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

layout(location = 0) out vec2 fragRectCoord;
layout(location = 1) out vec2 fragSize;
layout(location = 2) out vec4 FragColor;

void main() {

    fragRectCoord = rvertexcoord(gl_VertexIndex);
    fragSize = UV0;
    FragColor = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
