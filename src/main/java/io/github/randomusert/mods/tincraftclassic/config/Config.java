package io.github.randomusert.mods.tincraftclassic.config;


// All config options defined here and default values provided.
// All values defined here are always the default config.
public class Config {
    // World gen for ores
    public WorldGeneration worldGeneration = new WorldGeneration();

    public static class WorldGeneration {
        public boolean spawnTinOre = true;

        public int tinOreVeinSize = 15;

        public int tinOreVeinsPerChunk = 10;
    }

    public Recipes recipes = new Recipes();

    public static class Recipes {
        public boolean smeltTinOreToTinIngot = true;
    }
}
