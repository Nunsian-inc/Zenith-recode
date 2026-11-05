#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
    float CornerSmoothness;
};

layout(location = 0) in vec2 FragCoord;
layout(location = 1) in vec4 FragColor;

layout(location = 0) out vec4 OutColor;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r, float smoothness) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    vec2 q_clamped = max(q, 0.0);

    float len = pow(pow(q_clamped.x, smoothness) + pow(q_clamped.y, smoothness), 1.0/smoothness);

    return min(max(q.x, q.y), 0.0) + len - r.x;
}

void main() {
    vec2 center = Size * 0.5;

    float distance = roundedBoxSDF(
        center - (FragCoord * Size),
        center - 1.0,
        Radius,
        CornerSmoothness
    );

    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);
    vec4 finalColor = vec4(FragColor.rgb, FragColor.a * alpha);

    if (finalColor.a == 0.0) { // alpha test
        discard;
    }

    OutColor = finalColor * ColorModulator;
}