package io.github.randomusert.mods.tincraftclassic.client;

import io.github.randomusert.mods.tincraftclassic.Tincraftclassic;
import io.github.randomusert.mods.tincraftclassic.init.ModBlocks;
import io.github.randomusert.mods.tincraftclassic.init.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = Tincraftclassic.MODID)
public class ModelRegistrationHandler {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerModel(ModItems.TIN_INGOT, 0);
        registerModel(Item.getItemFromBlock(ModBlocks.TIN_BLOCK), 0);
        registerModel(Item.getItemFromBlock(ModBlocks.TIN_ORE), 0);
    }

    private static void registerModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
