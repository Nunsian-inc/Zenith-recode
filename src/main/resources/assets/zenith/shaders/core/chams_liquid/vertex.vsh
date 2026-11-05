#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:fog.glsl>

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
layout(std140) uniform ZenithData {
    float iTime;
    vec4 PrimaryColor4;
    vec4 SecondaryColor4;
    float AlphaMul;
    float FresnelPower;
    float Freq;
    float ColorTransfer;
    float Brightness;
    float MirrorEnabled;
    vec2 ScreenSize;
};
// vec3 в std140 трактуется драйверами по-разному: тут он хранится как vec4,
// а имя остаётся прежним, чтобы не трогать тела шейдеров.
#define PrimaryColor PrimaryColor4.xyz
#define SecondaryColor SecondaryColor4.xyz


layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in vec3 Normal;

layout(location = 0) out float sphericalVertexDistance;
layout(location = 1) out float cylindricalVertexDistance;
layout(location = 2) out vec4 vertexColor;
layout(location = 3) out vec2 texCoord0;
layout(location = 4) out vec3 viewNormal;
layout(location = 5) out vec3 viewPos;
layout(location = 6) out vec3 modelPos;

void main() {
    vec4 mvPos = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * mvPos;

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    vertexColor = Color;
    texCoord0 = UV0;
    viewNormal = normalize(mat3(ModelViewMat) * Normal);
    viewPos = mvPos.xyz;
    modelPos = Position;
}
