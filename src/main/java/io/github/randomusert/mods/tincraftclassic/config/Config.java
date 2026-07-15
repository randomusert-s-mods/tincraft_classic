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

    // Config for generators
    public GeneratorTileEntityConfig  generatorTileEntityConfig = new GeneratorTileEntityConfig();

    public  static class GeneratorTileEntityConfig {
        public int generalGenCapacity = 1000;

        public int passiveGenerationAmount = 20;

        public int burnGenerationAmount = 50;

        public int brambleBurnTime = 200;
    }
}
