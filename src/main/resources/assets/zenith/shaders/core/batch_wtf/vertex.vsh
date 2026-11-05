#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec4 Radius;
    float Smoothness;
};

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

layout(location = 0) out vec2 FragCoord;
layout(location = 1) out vec2 TexCoord;
layout(location = 2) out vec2 FragSize;
layout(location = 3) out vec4 FragColor;

void main() {
    vec4 clipPos = ProjMat * ModelViewMat * vec4(Position, 1.0);
    gl_Position = clipPos;

    FragCoord = rvertexcoord(gl_VertexIndex);
    TexCoord = clipPos.xy * 0.5 + 0.5;
    FragSize = UV0;
    FragColor = Color;
}
