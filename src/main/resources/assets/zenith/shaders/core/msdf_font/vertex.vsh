#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    float Range;
    float Thickness;
    float Smoothness;
    float Outline;
    float OutlineThickness;
    vec4 OutlineColor;
    float EnableFadeout;
    float FadeoutStart;
    float FadeoutEnd;
    float MaxWidth;
    float TextPosX;
};

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

layout(location = 0) out vec2 TexCoord;
layout(location = 1) out vec4 FragColor;
layout(location = 2) out vec2 GlobalPos; // Передаем глобальные координаты

void main() {
    TexCoord = UV0;
    FragColor = Color;
    GlobalPos = Position.xy; // Передаем позицию вершины в мировых координатах

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}