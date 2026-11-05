package org.zenith.base.figura.lua.api.data;

import org.luaj.vm2.LuaError;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(value = "future", name = "Future")
public class FiguraFuture<T> {
   private boolean isDone;
   private boolean hasError;
   private LuaError errorObject;
   private T value;

   public void handle(T value, Throwable error) {
      if (error != null) {
         this.error(error);
      } else {
         this.complete(value);
      }
   }

   public void complete(T value) {
      if (!this.isDone) {
         this.value = value;
         this.isDone = true;
      }
   }

   public void error(Throwable t) {
      if (!this.isDone) {
         this.hasError = true;
         this.isDone = true;
         LuaError var10001;
         if (t instanceof LuaError e) {
            var10001 = e;
         } else {
            var10001 = new LuaError(t);
         }

         this.errorObject = var10001;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "future.is_done", overloads = @LuaMethodOverload(returnType = Boolean.class))
   public boolean isDone() {
      return this.isDone;
   }

   @LuaMethodDoc(value = "future.has_error", overloads = @LuaMethodOverload(returnType = Boolean.class))
   public boolean hasError() {
      return this.hasError;
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "future.get_value", overloads = @LuaMethodOverload(returnType = Object.class))
   public T getValue() {
      return this.value;
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "future.get_or_error", overloads = @LuaMethodOverload(returnType = Object.class))
   public T getOrError() {
      if (this.errorObject != null) {
         throw this.errorObject;
      } else {
         return this.value;
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("future.throw_error")
   public void throwError() {
      if (this.errorObject != null) {
         throw this.errorObject;
      }
   }

   @Override
   public String toString() {
      return "Future(isDone=%s)".formatted(this.isDone);
   }
}
