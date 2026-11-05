#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec4 Tab;
    float TabRadius;
    float MainHeight;
    float Joint;
    vec4 Radius;
    float Smoothness;
    float CornerSmoothness;
    float FresnelPower;
    float FresnelAlpha;
    float BaseAlpha;
    float FresnelInvert;
    float FresnelMix;
    float DistortStrength;
    float Time;
    vec4 GlareColor4;
    float GlareAlpha;
    float GlareSpeed;
    float GlareMode;
    float GlareTexture;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define GlareColor GlareColor4.xyz


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
    // Экранная координата берётся из позиции в клипе — ровно та же величина, что раньше
    // считалась на процессоре для каждой панели отдельно (x/ширина экрана и так далее).
    TexCoord = clipPos.xy * 0.5 + 0.5;
    FragSize = UV0;
    FragColor = Color;
}
