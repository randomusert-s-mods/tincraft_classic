package io.github.randomusert.mods.tincraftclassic;

import io.github.randomusert.mods.tincraftclassic.init.ModBlocks;
import io.github.randomusert.mods.tincraftclassic.item.ItemTinIngot;
import io.github.randomusert.mods.tincraftclassic.util.RegistryUtil;
import io.github.randomusert.mods.tincraftclassic.util.SmeltingRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = Tags.MOD_ID)
public class RegistrationHandler {
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final Item[] items = {
                new ItemTinIngot().setRegistryName(Tags.MOD_ID, "tin_ingot").setTranslationKey(Tags.MOD_ID + "." + "tin_ingot")
        };

        final Item[] itemBlocks = {
                new ItemBlock(ModBlocks.TIN_BLOCK).setRegistryName(ModBlocks.TIN_BLOCK.getRegistryName()),
                new ItemBlock(ModBlocks.TIN_ORE).setRegistryName(ModBlocks.TIN_ORE.getRegistryName())
        };

        event.getRegistry().registerAll(items);
        event.getRegistry().registerAll(itemBlocks);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Tincraftclassic.LOGGER.info("Registering blocks");
        final Block[] blocks = {
                RegistryUtil.setBlockName(new Block(Material.IRON), "tin_block").setCreativeTab(Tincraftclassic.TCC_TAB),
                RegistryUtil.setBlockName(new Block(Material.ROCK), "tin_ore").setCreativeTab(Tincraftclassic.TCC_TAB)
        };

        event.getRegistry().registerAll(blocks);

        Tincraftclassic.LOGGER.info("Blocks registered");
    }

    public static void initRegistries() {
        SmeltingRecipes.registerSmelting();
    }
}
