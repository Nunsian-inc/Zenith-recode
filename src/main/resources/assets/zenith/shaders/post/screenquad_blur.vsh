#version 330
#extension GL_ARB_separate_shader_objects : require

// Полноэкранный квад с шагом выборки для размытия по одной оси — замена minecraft:post/blur.
// Блок ZenithPost обязан совпадать с блоком фрагментного шейдера поле в поле, иначе программа
// не слинкуется.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
layout(std140) uniform ZenithPost {
    vec2 BlurDir;
    float Radius;
};

layout(location = 0) out vec2 texCoord;
layout(location = 1) out vec2 sampleStep;

void main() {
    vec2 uv = vec2((gl_VertexIndex << 1) & 2, gl_VertexIndex & 2);
    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
    texCoord = uv;
    sampleStep = BlurDir / InSize;
}
