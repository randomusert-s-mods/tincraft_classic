package io.github.randomusert.mods.tincraftclassic.util;

import net.minecraftforge.energy.EnergyStorage;

public class TCEnergyStorage extends EnergyStorage {

    public TCEnergyStorage(int capacity, int maxExtract) {
        super(capacity, 0, maxExtract);
    }

    public int generateEnergy(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int generated = Math.min(amount, capacity - energy);
        energy += generated;

        return generated;
    }

    public void setEnergyStored(int amount) {
        energy = Math.max(0, Math.min(capacity, amount));
    }
}
