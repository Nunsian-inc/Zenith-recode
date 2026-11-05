#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec2 Smoothness;
};

// Единый батч фигур: заливки, контуры и уголки лежат в одном буфере.
// Раньше у каждого вида был свой шейдер и свой вызов, а всё, что различается от фигуры к
// фигуре, жило в uniform — из-за этого пачка сбрасывалась почти на каждой фигуре. Теперь
// различия приезжают вершиной.
layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;    // размер фигуры в пикселях
layout(location = 3) in vec4 Radii;  // скругления: верхнее левое, нижнее левое, верхнее правое, нижнее правое
layout(location = 4) in vec2 Params; // x — толщина контура, y — вид фигуры

layout(location = 0) out vec2 fragRectCoord;
layout(location = 1) out vec2 fragSize;
layout(location = 2) out vec4 fragRadius;
layout(location = 3) out vec4 FragColor;
layout(location = 4) out float fragThickness;
layout(location = 5) out float fragMode;

void main() {
    fragRectCoord = rvertexcoord(gl_VertexIndex);
    fragSize = UV0;
    fragRadius = Radii;
    fragThickness = Params.x;
    fragMode = Params.y;
    FragColor = Color;

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
