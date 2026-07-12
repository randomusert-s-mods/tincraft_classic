package io.github.randomusert.mods.tincraftclassic;

import io.github.randomusert.mods.tincraftclassic.item.ItemTinIngot;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class RegistrationHandler {
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final Item[] items = {
                new ItemTinIngot().setRegistryName(Tags.MOD_ID, "tin_ingot").setTranslationKey(Tags.MOD_ID + "." + "tin_ingot")
        };

        event.getRegistry().registerAll(items);
    }
}
