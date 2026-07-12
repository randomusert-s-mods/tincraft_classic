package io.github.randomusert.mods.tincraftclassic.util;

import io.github.randomusert.mods.tincraftclassic.init.ModBlocks;
import io.github.randomusert.mods.tincraftclassic.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SmeltingRecipes {
    public static void registerSmelting() {
        GameRegistry.addSmelting(new ItemStack(ModBlocks.TIN_ORE, 1, 0), new ItemStack(ModItems.TIN_INGOT, 1), 1.0f);
    }
}
