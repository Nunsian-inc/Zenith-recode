#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Radius;
    float Smoothness;
    vec4 TopLeftColor;
    vec4 BottomLeftColor;
    vec4 TopRightColor;
    vec4 BottomRightColor;
};

layout(location = 0) in vec2 FragCoord; // normalized fragment coord relative to the primitive
layout(location = 1) in vec4 FragColor;

// Четыре цвета для углов градиента

layout(location = 0) out vec4 OutColor;

vec4 bilinearInterpolation(vec2 uv) {
    // Интерполяция по горизонтали для верхней строки
    vec4 topColor = mix(TopLeftColor, TopRightColor, uv.x);

    // Интерполяция по горизонтали для нижней строки
    vec4 bottomColor = mix(BottomLeftColor, BottomRightColor, uv.x);

    // Интерполяция по вертикали между верхней и нижней строками
    return mix(topColor, bottomColor, uv.y);
}

void main() {
    vec2 center = Size * 0.5;
    vec2 uv = FragCoord; // UV координаты от 0 до 1

    // Вычисляем цвет градиента для текущего фрагмента
    vec4 gradientColor = bilinearInterpolation(uv);

    float distance = roundedBoxSDF(center - (FragCoord * Size), center - 1.0, Radius);
    float alpha = 1.0 - smoothstep(1.0 - Smoothness, 1.0, distance);

    vec4 finalColor = vec4(gradientColor.rgb, gradientColor.a * alpha);

    if (finalColor.a == 0.0) { // alpha test
        discard;
    }

    OutColor = finalColor * ColorModulator;
}
