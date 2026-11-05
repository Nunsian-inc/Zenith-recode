#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec2 Resolution;
    float Offset;
};

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;

layout(location = 0) out vec2 TexCoord;
layout(location = 1) out vec4 FragColor;

const vec2[4] FULLSCREEN_POSITIONS = vec2[](
    vec2(-1.0,  1.0),
    vec2(-1.0, -1.0),
    vec2( 1.0, -1.0),
    vec2( 1.0,  1.0)
);

void main() {
    vec2 position = FULLSCREEN_POSITIONS[gl_VertexIndex % 4];
    gl_Position = vec4(position, 0.0, 1.0);
    TexCoord = position * 0.5 + 0.5;
    FragColor = Color;
}
