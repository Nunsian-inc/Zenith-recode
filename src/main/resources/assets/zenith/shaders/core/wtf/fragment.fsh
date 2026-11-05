#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
};

layout(location = 0) in vec2 FragCoord; // normalized fragment coord relative to the primitive
layout(location = 1) in vec2 TexCoord;
layout(location = 2) in vec4 FragColor;

uniform sampler2D Sampler0;

layout(location = 0) out vec4 OutColor;

void main() {
    vec4 texColor = texture(Sampler0, TexCoord);

    vec2 center = Size * 0.5;
    float distance = roundedBoxSDF(center - (FragCoord * Size), center - 1.0, Radius);

    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);

    vec4 finalColor = texColor * FragColor * ColorModulator;
    finalColor.a *= alpha;

    if (finalColor.a <= 0.0) {
        discard;
    }

    OutColor = finalColor;
}