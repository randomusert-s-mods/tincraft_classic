package io.github.randomusert.mods.tincraftclassic.tileentity;

import io.github.randomusert.mods.tincraftclassic.config.Config;
import io.github.randomusert.mods.tincraftclassic.config.ConfigManager;
import io.github.randomusert.mods.tincraftclassic.util.TCEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityHybridGenerator extends TileEntity implements ITickable {
    private final ItemStackHandler itemStorage = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };

    // Haetaan kapasiteetti ja siirtonopeus suoraan konfiguraatiosta
    private final TCEnergyStorage energyStorage = new TCEnergyStorage(ConfigManager.getConfig().generatorTileEntityConfig.generalGenCapacity, 1000);

    private int burnTimeRemaining = 0;

    @Override
    public void update() {
        if (world.isRemote) return;

        boolean changed = false;

        if (world.canSeeSky(pos.up()) && world.isDaytime()) {
            this.energyStorage.generateEnergy(ConfigManager.getConfig().generatorTileEntityConfig.passiveGenerationAmount);
            changed = true;
        }

        if (this.burnTimeRemaining > 0) {
            this.burnTimeRemaining--;
            this.energyStorage.generateEnergy(ConfigManager.getConfig().generatorTileEntityConfig.burnGenerationAmount);
            changed = true;
        } else {
            // Jos tuli on sammunut, yritetään ottaa uusi esine
            ItemStack fuel = itemStorage.getStackInSlot(0);
            if (!fuel.isEmpty() && isBrambleItem(fuel)) {
                // Kulutetaan 1 esine
                itemStorage.extractItem(0, 1, false);
                // Asetetaan paloaika konfiguraatiosta
                this.burnTimeRemaining = ConfigManager.getConfig().generatorTileEntityConfig.brambleBurnTime;
                changed = true;
            }
        }

        this.distributeEnergy();

        if (changed) {
            this.markDirty();
        }
    }

    private boolean isBrambleItem(ItemStack stack) {

        String registryName = stack.getItem().getRegistryName().toString();
        return registryName.contains("bramble");
    }

    private void distributeEnergy() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            if (neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                IEnergyStorage neighborStorage = neighbor.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                if (neighborStorage != null && neighborStorage.canReceive()) {
                    int energyToSend = this.energyStorage.extractEnergy(1000, true);
                    int accepted = neighborStorage.receiveEnergy(energyToSend, false);
                    this.energyStorage.extractEnergy(accepted, false);
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Energy", this.energyStorage.getEnergyStored());
        compound.setInteger("BurnTime", this.burnTimeRemaining);
        compound.setTag("Inventory", this.itemStorage.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energyStorage.generateEnergy(compound.getInteger("Energy"));
        this.burnTimeRemaining = compound.getInteger("BurnTime");
        this.itemStorage.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemStorage);
        }
        return super.getCapability(capability, facing);
    }
}
