package io.github.randomusert.mods.tincraftclassic;

import io.github.randomusert.mods.tincraftclassic.config.ConfigManager;
import io.github.randomusert.mods.tincraftclassic.init.*;
import io.github.randomusert.mods.tincraftclassic.world.gen.TCCWorldGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Tincraftclassic {
    public static final String MODID = Tags.MOD_ID;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static final CreativeTabs TCC_TAB = new TCCTab();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
        ConfigManager.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RegistrationHandler.initRegistries();


        if (ConfigManager.getConfig().worldGeneration.spawnTinOre) {
            GameRegistry.registerWorldGenerator(new TCCWorldGen(), 0);
        } else {
            LOGGER.info("Config: Tin Ore spawning disabled");
        }
    }

}
