package io.github.randomusert.mods.tincraftclassic.util;

import net.minecraftforge.energy.EnergyStorage;

public class TCEnergyStorage extends EnergyStorage {
    public TCEnergyStorage(int capacity, int maxExtract) {
        super(capacity, 0, maxExtract);
    }

    public int generateEnergy(int amount) {
        this.energy += amount;
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
        return this.energy;
    }
}
