# World gen configuring
In the latest version (0.1.1) We include a custom config system for configuring world gen.

## tin ore
in `config/tincraft.json` you will see following:

```JSON

{
  "worldGeneration": {
    "spawnTinOre": true,
    "tinOreVeinSize": 15,
    "tinOreVeinsPerChunk": 10
  }
}

```

Here's what each option does.

`spawnTinOre: true`: Enables the tin ore generation

`tinOreVeinSize: 15`: the size of the Ore Vein

`tinOreVeinsPerChunk: 10`: the amount of the Ore Veins that the game tries to generate
## please note

There's limits. like doing:
```JSON

{
  "worldGeneration": {
    "spawnTinOre": true,
    "tinOreVeinSize": 100,
    "tinOreVeinsPerChunk": 20
  }
}

```

Will most certainly crash your game. Please make sure that the ore gen config is reasonable as i will not do much if you configured it so high