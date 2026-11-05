#version 330
#extension GL_ARB_separate_shader_objects : require

#include <zenith:common.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    vec2 Resolution;
    float Offset;
    float Saturation;
    float TintIntensity;
    vec4 TintColor4;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define TintColor TintColor4.xyz


layout(location = 0) in vec3 Position;
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
