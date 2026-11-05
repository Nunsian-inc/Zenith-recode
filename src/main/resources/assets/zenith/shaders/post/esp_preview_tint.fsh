#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

// Проход только для превью в меню: плоский цвет вместо цвета модели.
//
// В мире силуэт приходит из ванильного entity_outline уже покрашенным — модуль подменяет
// сущности team color, и цепочка берёт цвет прямо из картинки. В карточке меню модель рисуется
// обычным рендером, и в буфере лежит скин; отдай его цепочке как есть, и свечение в превью
// пошло бы цветами кожи, а не цветом настройки.
//
// Выход тот же, что у esp_mask: цвет, домноженный на покрытие. Дальше превью идёт теми же
// файлами, что и мир, — и разойтись с ним не может.

uniform sampler2D InSampler;
layout(std140) uniform ZenithPost {
    vec4 Tint;
};

layout(location = 0) in vec2 texCoord;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 source = texture(InSampler, texCoord);
    float mask = clamp(max(source.a, max(source.r, max(source.g, source.b))), 0.0, 1.0);
    fragColor = vec4(Tint.rgb * mask, mask) * Tint.a;
}
