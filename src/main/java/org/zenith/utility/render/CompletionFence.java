package org.zenith.utility.render;

import com.mojang.renderpearl.api.commands.GpuFence;
import com.mojang.renderpearl.backend.opengl.GlFence;
import org.lwjgl.opengl.GL11;

public final class CompletionFence implements GpuFence {
   private static CompletionFence.Entry entry;
   private final CompletionFence.Entry entry2;
   private boolean closed;

   private CompletionFence(CompletionFence.Entry entry) {
      this.entry2 = entry;
   }

   public static GpuFence method5163() {
      CompletionFence.Entry entry2 = entry;
      if (entry2 == null) {
         entry2 = new CompletionFence.Entry();
         entry = entry2;
      }

      entry2.intValue++;
      return new CompletionFence(entry2);
   }

   public static void endFrame() {
      CompletionFence.Entry entry2 = entry;
      if (entry2 != null) {
         entry = null;
         if (entry2.intValue > 0) {
            entry2.glGpuFence = new GlFence(GlBridge.encoder());
         }
      }
   }

   @Override
   public boolean awaitCompletion(long timeoutNS) {
      if (this.closed) {
         return true;
      } else {
         CompletionFence.Entry entry2 = this.entry2;
         if (entry2.glGpuFence == null) {
            if (entry == entry2) {
               entry = null;
            }

            entry2.glGpuFence = new GlFence(GlBridge.encoder());
            GL11.glFlush();
         }

         return entry2.glGpuFence.awaitCompletion(timeoutNS);
      }
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
         CompletionFence.Entry entry = this.entry2;
         if (--entry.intValue == 0 && entry.glGpuFence != null) {
            entry.glGpuFence.close();
            entry.glGpuFence = null;
         }
      }
   }

   private static final class Entry {
      private GlFence glGpuFence;
      private int intValue;
   }
}
