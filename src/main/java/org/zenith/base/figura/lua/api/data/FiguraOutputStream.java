package org.zenith.base.figura.lua.api.data;

import java.io.IOException;
import java.io.OutputStream;
import org.luaj.vm2.LuaError;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "OutputStream", value = "output_stream")
public class FiguraOutputStream extends OutputStream {
   private final Avatar parent;
   private final OutputStream destinationStream;

   public FiguraOutputStream(Avatar parent, OutputStream destinationStream) {
      this.destinationStream = destinationStream;
      this.parent = parent;
      parent.openOutputStreams.add(this);
   }

   @LuaWhitelist
   @LuaMethodDoc(value = "output_stream.write", overloads = @LuaMethodOverload(argumentNames = "b", argumentTypes = Integer.class))
   @Override
   public void write(int b) {
      try {
         this.destinationStream.write(b);
      } catch (IOException var3) {
         throw new LuaError(var3);
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("output_stream.flush")
   @Override
   public void flush() throws IOException {
      this.destinationStream.flush();
   }

   @LuaWhitelist
   @LuaMethodDoc("output_stream.close")
   @Override
   public void close() throws IOException {
      this.destinationStream.close();
      this.parent.openOutputStreams.remove(this);
   }

   @Override
   public String toString() {
      return "OutputStream";
   }
}
