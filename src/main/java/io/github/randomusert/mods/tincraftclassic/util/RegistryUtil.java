package io.github.randomusert.mods.tincraftclassic.util;

import io.github.randomusert.mods.tincraftclassic.Tincraftclassic;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class RegistryUtil {
    public static Item setItemName(final Item item, final String name) {
        item.setRegistryName(Tincraftclassic.MODID, name).setTranslationKey(Tincraftclassic.MODID + "." + name);
        return item;
    }

    public static Block setBlockName(final Block block, final String name) {
        block.setRegistryName(Tincraftclassic.MODID, name).setTranslationKey(Tincraftclassic.MODID + "." + name);
        return block;
    }
}
