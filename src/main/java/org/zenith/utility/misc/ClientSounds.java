package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.InterfaceModule;

public class ClientSounds implements MinecraftAccess {
   public final SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:gui_open"));
   public final SoundEvent soundEvent2 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:gui_close"));
   public final SoundEvent soundEvent3 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:module_enable"));
   public final SoundEvent soundEvent4 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:module_disable"));
   public final SoundEvent soundEvent5 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:enable4"));
   public final SoundEvent soundEvent6 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:disable4"));
   public final SoundEvent soundEvent7 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:click_left"));
   public final SoundEvent soundEvent8 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:click_right"));
   public final SoundEvent soundEvent9 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:slider_step"));
   public final SoundEvent soundEvent10 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:category_switch"));
   public final SoundEvent soundEvent11 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:bind_set"));
   public final SoundEvent soundEvent12 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:bind_clear"));
   public final SoundEvent soundEvent13 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:input_confirm"));
   public final SoundEvent soundEvent14 = SoundEvent.createVariableRangeEvent(Identifier.parse("zenith:input_cancel"));

   public ClientSounds() {
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent.location(), this.soundEvent);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent2.location(), this.soundEvent2);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent3.location(), this.soundEvent3);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent4.location(), this.soundEvent4);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent5.location(), this.soundEvent5);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent6.location(), this.soundEvent6);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent7.location(), this.soundEvent7);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent8.location(), this.soundEvent8);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent9.location(), this.soundEvent9);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent10.location(), this.soundEvent10);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent11.location(), this.soundEvent11);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent12.location(), this.soundEvent12);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent13.location(), this.soundEvent13);
      Registry.register(BuiltInRegistries.SOUND_EVENT, this.soundEvent14.location(), this.soundEvent14);
   }

   public void method1561(SoundEvent soundEvent5) {
      this.method1576(soundEvent5, InterfaceModule.INSTANCE.volume.getValue3(), 1.0F);
   }

   public void method1562() {
      float f = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, f * 0.75F, 0.9F);
      this.method1576(this.soundEvent5, f * 0.45F, 1.5F);
   }

   public void method1563() {
      float f = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent8, f * 0.7F, 1.05F);
      this.method1576(this.soundEvent6, f * 0.4F, 0.85F);
   }

   public void method1564(int ordinal, int length) {
      float f = length > 1 ? (float)ordinal / (length - 1) : 0.0F;
      this.method1576(this.soundEvent10, InterfaceModule.INSTANCE.volume.getValue3() * 0.6F, 0.94F + f * 0.2F);
   }

   public void method1565(boolean enabled) {
      this.method1576(enabled ? this.soundEvent3 : this.soundEvent4, InterfaceModule.INSTANCE.volume.getValue3(), 1.0F);
   }

   public void method1566() {
      this.method1576(this.soundEvent11, InterfaceModule.INSTANCE.volume.getValue3() * 0.7F, 1.0F);
   }

   public void method1567() {
      this.method1576(this.soundEvent12, InterfaceModule.INSTANCE.volume.getValue3() * 0.65F, 1.0F);
   }

   public void method1568() {
      this.method1576(this.soundEvent13, InterfaceModule.INSTANCE.volume.getValue3() * 0.6F, 1.0F);
   }

   public void method1569() {
      this.method1576(this.soundEvent14, InterfaceModule.INSTANCE.volume.getValue3() * 0.55F, 1.0F);
   }

   public void method1570(boolean flag) {
      this.method1576(flag ? this.soundEvent8 : this.soundEvent7, InterfaceModule.INSTANCE.volume.getValue3() * 0.8F, 1.0F);
   }

   public void method1571(float progress) {
      this.method1576(this.soundEvent9, InterfaceModule.INSTANCE.volume.getValue3() * 0.5F, 0.92F + Mth.clamp(progress, 0.0F, 1.0F) * 0.28F);
   }

   public void method1572() {
      float f = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, f * 0.55F, 1.25F);
      this.method1576(this.soundEvent5, f * 0.35F, 1.6F);
   }

   public void method1573() {
      float f = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent5, f * 0.5F, 0.95F);
      this.method1576(this.soundEvent7, f * 0.3F, 0.8F);
   }

   public void method1574() {
      float f = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, f * 0.6F, 1.1F);
      this.method1576(this.soundEvent9, f * 0.35F, 1.35F);
   }

   public void method1575() {
      this.method1576(this.soundEvent9, InterfaceModule.INSTANCE.volume.getValue3() * 0.3F, 1.45F);
   }

   public void method1576(SoundEvent soundEvent5, float volume, float pitch) {
      mc.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent5, pitch, volume));
   }

   @Generated
   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   @Generated
   public SoundEvent getSoundEvent2() {
      return this.soundEvent2;
   }

   @Generated
   public SoundEvent getSoundEvent3() {
      return this.soundEvent3;
   }

   @Generated
   public SoundEvent getSoundEvent4() {
      return this.soundEvent4;
   }

   @Generated
   public SoundEvent getSoundEvent5() {
      return this.soundEvent5;
   }

   @Generated
   public SoundEvent getSoundEvent6() {
      return this.soundEvent6;
   }

   @Generated
   public SoundEvent getSoundEvent7() {
      return this.soundEvent7;
   }

   @Generated
   public SoundEvent getSoundEvent8() {
      return this.soundEvent8;
   }

   @Generated
   public SoundEvent getSoundEvent9() {
      return this.soundEvent9;
   }

   @Generated
   public SoundEvent getSoundEvent10() {
      return this.soundEvent10;
   }

   @Generated
   public SoundEvent getSoundEvent11() {
      return this.soundEvent11;
   }

   @Generated
   public SoundEvent getSoundEvent12() {
      return this.soundEvent12;
   }

   @Generated
   public SoundEvent getSoundEvent13() {
      return this.soundEvent13;
   }

   @Generated
   public SoundEvent getSoundEvent14() {
      return this.soundEvent14;
   }
}
