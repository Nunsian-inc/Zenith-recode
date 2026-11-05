#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform ZenithData {
    vec2 Resolution;
    float Offset;
};

layout(location = 0) in vec2 TexCoord;
layout(location = 1) in vec4 FragColor;

uniform sampler2D Sampler0;   // 1.0/width, 1.0/height

layout(location = 0) out vec4 OutColor;

void main() {
    vec2 uv = TexCoord;
    vec2 hp = Resolution * Offset;

    // Kawase kernel: center (weight 4) + 4 diagonal samples
    vec3 sum = texture(Sampler0, uv).rgb * 4.0;
    sum += texture(Sampler0, uv + vec2(-hp.x, -hp.y)).rgb;
    sum += texture(Sampler0, uv + vec2( hp.x, -hp.y)).rgb;
    sum += texture(Sampler0, uv + vec2(-hp.x,  hp.y)).rgb;
    sum += texture(Sampler0, uv + vec2( hp.x,  hp.y)).rgb;

    OutColor = vec4(sum / 8.0, 1.0);
}
