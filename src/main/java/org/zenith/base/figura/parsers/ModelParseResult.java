package org.zenith.base.figura.parsers;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public record ModelParseResult(CompoundTag textures, List<CompoundTag> animationList, CompoundTag modelNbt) {
}
