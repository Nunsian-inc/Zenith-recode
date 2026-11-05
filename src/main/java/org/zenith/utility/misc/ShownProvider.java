package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ShownProvider {
   public static final ShownProvider INSTANCE = new ShownProvider(
      "anthropic",
      "Claude",
      "Anthropic",
      ShownProvider.Type.ANTHROPIC,
      "https://api.anthropic.com/v1/messages",
      List.of("claude-opus-5-5", "claude-sonnet-5-5", "claude-haiku-4-5", "claude-fable-5-1"),
      true,
      "https://console.anthropic.com/settings/keys",
      false
   );
   public static final ShownProvider INSTANCE2 = new ShownProvider(
      "openai",
      "ChatGPT",
      "OpenAI",
      ShownProvider.Type.OPENAI,
      "https://api.openai.com/v1/chat/completions",
      List.of("gpt-5", "gpt-5-mini", "gpt-4.1", "o4-mini"),
      true,
      "https://platform.openai.com/api-keys",
      false
   );
   public static final ShownProvider INSTANCE3 = new ShownProvider(
      "gemini",
      "Gemini",
      "Google",
      ShownProvider.Type.GEMINI,
      "https://generativelanguage.googleapis.com/v1beta/models/",
      List.of("gemini-2.5-pro", "gemini-2.5-flash"),
      true,
      "https://aistudio.google.com/apikey",
      false
   );
   public static final ShownProvider INSTANCE4 = new ShownProvider(
      "deepseek",
      "DeepSeek",
      "DeepSeek",
      ShownProvider.Type.OPENAI,
      "https://api.deepseek.com/chat/completions",
      List.of("deepseek-chat", "deepseek-reasoner"),
      true,
      "https://platform.deepseek.com/api_keys",
      false
   );
   public static final ShownProvider INSTANCE5 = new ShownProvider(
      "xai",
      "Grok",
      "xAI",
      ShownProvider.Type.OPENAI,
      "https://api.x.ai/v1/chat/completions",
      List.of("grok-4", "grok-3-mini"),
      true,
      "https://console.x.ai",
      false
   );
   public static final ShownProvider INSTANCE6 = new ShownProvider(
      "openrouter",
      "OpenRouter",
      "OpenRouter",
      ShownProvider.Type.OPENAI,
      "https://openrouter.ai/api/v1/chat/completions",
      List.of("openrouter/auto", "anthropic/claude-opus-5-5", "openai/gpt-5", "google/gemini-2.5-pro"),
      true,
      "https://openrouter.ai/keys",
      false
   );
   public static final ShownProvider INSTANCE7 = new ShownProvider(
      "mistral",
      "Mistral",
      "Mistral AI",
      ShownProvider.Type.OPENAI,
      "https://api.mistral.ai/v1/chat/completions",
      List.of("mistral-large-latest", "codestral-latest"),
      true,
      "https://console.mistral.ai/api-keys",
      false
   );
   public static final ShownProvider INSTANCE8 = new ShownProvider(
      "groq",
      "Groq",
      "Groq",
      ShownProvider.Type.OPENAI,
      "https://api.groq.com/openai/v1/chat/completions",
      List.of("llama-3.3-70b-versatile", "qwen-qwq-32b"),
      true,
      "https://console.groq.com/keys",
      false
   );
   public static final ShownProvider INSTANCE9 = new ShownProvider(
      "ollama",
      "Ollama",
      "локально",
      ShownProvider.Type.OPENAI,
      "http://127.0.0.1:11434/v1/chat/completions",
      List.of("qwen2.5-coder:14b", "llama3.1:8b"),
      false,
      "https://ollama.com/download",
      false
   );
   private static final List<ShownProvider> items = List.of(INSTANCE, INSTANCE2, INSTANCE3, INSTANCE4, INSTANCE5, INSTANCE6, INSTANCE7, INSTANCE8, INSTANCE9);
   private static final List<ShownProvider> items2 = new CopyOnWriteArrayList<>();
   private final String text;
   private final String text2;
   private final String text3;
   private final ShownProvider.Type type;
   private final String text4;
   private final List<String> items3;
   private final boolean flag;
   private final String text5;
   private final boolean flag2;

   private ShownProvider(
      String text, String str, String str2, ShownProvider.Type type, String clean, List<String> strings, boolean flag, String clean2, boolean flag2
   ) {
      this.text = text;
      this.text2 = str;
      this.text3 = str2;
      this.type = type;
      this.text4 = clean;
      this.items3 = strings;
      this.flag = flag;
      this.text5 = clean2;
      this.flag2 = flag2;
   }

   public static ShownProvider method7332(String string, String str, String clean, String clean2) {
      return new ShownProvider(
         string, str, "свой API", ShownProvider.Type.OPENAI, clean, List.of(clean2 != null && !clean2.isBlank() ? clean2.strip() : "default"), false, clean, true
      );
   }

   public static void method7333(ShownProvider shownProvider) {
      items2.removeIf(shownProvider2 -> shownProvider2.text.equals(shownProvider.text));
      items2.add(shownProvider);
   }

   public static void method7334(ShownProvider shownProvider) {
      items2.removeIf(shownProvider2 -> shownProvider2.text.equals(shownProvider.text));
   }

   public static List<ShownProvider> method7335() {
      return new ArrayList<>(items2);
   }

   public static ShownProvider[] method7336() {
      var shownProviders = new ArrayList<>(items);
      shownProviders.addAll(items2);
      return (ShownProvider[])shownProviders.toArray(new ShownProvider[0]);
   }

   public String getText() {
      return this.text;
   }

   public String title() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public ShownProvider.Type getType() {
      return this.type;
   }

   public String getText4() {
      return this.text4;
   }

   public List<String> getItems3() {
      return this.items3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText5() {
      return this.text5;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public static ShownProvider method7337(String string) {
      for (ShownProvider shownProvider : method7336()) {
         if (shownProvider.text.equalsIgnoreCase(string)) {
            return shownProvider;
         }
      }

      return INSTANCE;
   }

   @Override
   public boolean equals(Object shownProvider) {
      return shownProvider instanceof ShownProvider shownProvider2 && shownProvider2.text.equals(this.text);
   }

   @Override
   public int hashCode() {
      return this.text.hashCode();
   }

   public static enum Type {
      ANTHROPIC,
      OPENAI,
      GEMINI;
   }
}
