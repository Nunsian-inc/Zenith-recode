#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec4 Radius;
    float Smoothness;
};

layout(location = 0) in vec2 fragRectCoord;
layout(location = 1) in vec2 fragSize;
layout(location = 2) in vec4 FragColor;

layout(location = 0) out vec4 OutColor;

void main() {
    float alpha = ralpha(fragSize, fragRectCoord, Radius, Smoothness);
    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (finalColor.a == 0.0) {
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
