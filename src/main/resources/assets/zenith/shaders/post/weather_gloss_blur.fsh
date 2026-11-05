#version 330
#extension GL_ARB_separate_shader_objects : require
// 26.3 uses reversed-Z (cleared depth 0, near 1): every depth read below is flipped back with 1.0 - d,
// which equals the old depth exactly, so the sky test and linearDepth keep working unchanged.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    vec2 BlurDir;
    float NearPlane;
    float FarPlane;
};

// Гаусс по слою глянца, одно направление за проход. Это и есть сглаживание мокрого блеска:
// попиксельная крупа от квантованной глубины усредняется в ровное сияние, а заодно блеск
// получает мягкость настоящего отражения в шершавой мокрой поверхности.
//
// Размытие не переходит через края граней, и это главное в нём. Обычный гаусс тянул свет на
// несколько пикселей за силуэт — туда, где маска пуста и темнить нечего, — и каждый край
// блока обводился светлой каймой. Попытка лечить это тем, чтобы размывать только свет, а
// потемнение оставлять резким, дала ровно тот же результат наизнанку: свет по-прежнему уходил
// за край, но теперь без своего потемнения, и кайма стала ярче прежнего.
//
// Поэтому вес каждого отсчёта падает с разницей глубины: сосед на той же поверхности участвует
// полностью, сосед за обрывом — почти никак. Свет остаётся внутри грани, а внутри грани
// размывается свободно.

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(location = 0) in vec2 texCoord;
layout(location = 1) in vec2 oneTexel;

layout(location = 0) out vec4 fragColor;

float linearDepth(float depth) {
    float ndc = depth * 2.0 - 1.0;
    return 2.0 * NearPlane * FarPlane / (FarPlane + NearPlane - ndc * (FarPlane - NearPlane));
}

void main() {
    vec2 step = oneTexel * BlurDir;

    float offsets[4] = float[4](1.384615, -1.384615, 3.230769, -3.230769);
    float weights[4] = float[4](0.316216, 0.316216, 0.070270, 0.070270);

    // Сначала сам глянец: на сухой земле и на небе все пять отсчётов пусты, сумма выходит ровно
    // нулём при любых весах — и глубину там читать незачем. Таких пикселей на кадре большинство.
    vec4 center = texture(InSampler, texCoord);
    vec4 taps[4];
    vec4 mass = abs(center);
    for (int i = 0; i < 4; i++) {
        taps[i] = texture(InSampler, texCoord + step * offsets[i]);
        mass += abs(taps[i]);
    }
    if (dot(mass, vec4(1.0)) == 0.0) {
        fragColor = vec4(0.0);
        return;
    }

    float centerZ = linearDepth((1.0 - texture(DepthSampler, texCoord).r));

    // Допуск пропорционален расстоянию: вдали и сама глубина грубее, и полблока занимает
    // меньше пикселей, чем вблизи занимает четверть.
    float tolerance = max(0.05, centerZ * 0.02);

    vec4 sum = center * 0.227027;
    float total = 0.227027;

    for (int i = 0; i < 4; i++) {
        vec2 uv = texCoord + step * offsets[i];
        float z = linearDepth((1.0 - texture(DepthSampler, uv).r));
        float weight = weights[i] * max(0.0, 1.0 - abs(z - centerZ) / tolerance);
        sum += taps[i] * weight;
        total += weight;
    }

    fragColor = sum / max(total, 1.0e-4);
}
