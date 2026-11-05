#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec2 Size;
    vec4 Tab;
    float TabRadius;
    float MainHeight;
    float Joint;
    vec4 Radius;
    float Smoothness;
    float CornerSmoothness;
    float GlobalAlpha;
    float FresnelPower;
    vec4 FresnelColor4;
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
#define FresnelColor FresnelColor4.xyz
#define GlareColor GlareColor4.xyz


layout(location = 0) in vec3 Position; // POSITION_TEXTURE_COLOR vertex attributes
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;

layout(location = 0) out vec2 FragCoord;
layout(location = 1) out vec2 TexCoord;
layout(location = 2) out vec4 FragColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    FragCoord = rvertexcoord(gl_VertexIndex);
    TexCoord = UV0;
    FragColor = Color;
} 