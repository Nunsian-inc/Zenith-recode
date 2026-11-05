#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
    float Progress;
    float Fade;
    float StripeWidth;
};

layout(location = 0) in vec2 FragCoord;
layout(location = 1) in vec4 FragColor;

layout(location = 0) out vec4 OutColor;

void main() {
    vec2 center = Size * 0.5;
    float distance = roundedBoxSDF(center - (FragCoord * Size), center - 1.0, Radius);
    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);

    float diag = (FragCoord.x + FragCoord.y) * 0.5;
    float stripeWidth = StripeWidth;
    float fade = Fade;

    float stripeMask = smoothstep(Progress - stripeWidth * 0.5 - fade, Progress - stripeWidth * 0.5, diag)
                     * (1.0 - smoothstep(Progress + stripeWidth * 0.5, Progress + stripeWidth * 0.5 + fade, diag));

    vec4 stripeColor = FragColor * ColorModulator;

    vec4 finalColor = vec4(stripeColor.rgb, stripeColor.a * alpha * stripeMask);

    if (finalColor.a < 0.01) discard;
    OutColor = finalColor;
}
