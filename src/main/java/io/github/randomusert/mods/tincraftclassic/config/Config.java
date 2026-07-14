package io.github.randomusert.mods.tincraftclassic.config;


// All config options defined here and default values provided
public class Config {
    // World gen for ores
    public WorldGeneration worldGeneration = new WorldGeneration();

    public static class WorldGeneration {
        public boolean spawnTinOre = true;

        public int tinOreVeinSize = 15;

        public int tinOreVeinsPerChunk = 10;
    }
}
