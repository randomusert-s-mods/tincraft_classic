package io.github.randomusert.mods.tincraftclassic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.randomusert.mods.tincraftclassic.Tincraftclassic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ConfigManager {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Config config = new Config();

    private static String json = gson.toJson(config);

    public static void load() {


        File configFile = new File("config/tincraft.json");

        if (configFile.exists() && !configFile.isDirectory()) {
            Tincraftclassic.LOGGER.debug("Config file found. loading values from config");


            try {
                FileReader reader = new FileReader(configFile);
                config = gson.fromJson(reader, Config.class);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Tincraftclassic.LOGGER.debug("Creating config");
            createDefaultConfig();
        }
    }

    public static void createDefaultConfig() {
      Config defaultConfig = new Config();
      String defaultConfigJson = gson.toJson(defaultConfig);

      try {
          FileWriter defConfigWriter = new FileWriter("config/tincraft.json");

          defConfigWriter.write(defaultConfigJson);
          defConfigWriter.close();
      } catch (IOException e) {
          e.printStackTrace();
      }

    }

    // Gets the config from Config
    public static Config getConfig() {
        return config;
    }
}
