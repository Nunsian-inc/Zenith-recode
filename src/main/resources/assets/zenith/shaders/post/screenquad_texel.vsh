#version 330
#extension GL_ARB_separate_shader_objects : require

// Полноэкранный квад с размером текселя.
//
// В 1.21.4 эти проходы брали вершинный шейдер minecraft:post/sobel, который отдавал texCoord и
// oneTexel. В 1.21.11 таких шейдеров в ванили не осталось — есть только core/screenquad, а он
// даёт одни координаты. Размер текселя теперь считается из блока SamplerInfo, который движок
// заполняет сам.

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(location = 0) out vec2 texCoord;
layout(location = 1) out vec2 oneTexel;

void main() {
    vec2 uv = vec2((gl_VertexIndex << 1) & 2, gl_VertexIndex & 2);
    gl_Position = vec4(uv * 2.0 - 1.0, 0.0, 1.0);
    texCoord = uv;
    oneTexel = 1.0 / InSize;
}
