#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec2 Smoothness;
    float TextureSize;
};

// Плёнка шума поверх панелей HUD: раньше рисовалась отдельным вызовом на каждую панель,
// хотя текстура у всех одна и та же. Теперь все панели идут одной пачкой, а координата
// текстуры считается из размера фигуры — ровно те же числа, что процессор раскладывал
// по вершинам (u = смещение внутри панели / размер текстуры).
layout(location = 0) in vec2 fragRectCoord;
layout(location = 1) in vec2 fragSize;
layout(location = 2) in vec4 fragRadius;
layout(location = 3) in vec4 FragColor;

uniform sampler2D Sampler0;

layout(location = 0) out vec4 OutColor;

void main() {
    float alpha = ralpha(fragSize, fragRectCoord, fragRadius, Smoothness.x);
    vec2 texCoord = fragRectCoord * fragSize / TextureSize;

    vec4 whiteColor = vec4(1.0, 1.0, 1.0, alpha);
    vec4 finalColor = whiteColor * texture(Sampler0, texCoord) * FragColor;

    if (finalColor.a == 0.0) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
