package io.github.randomusert.mods.tincraftclassic.tileentity;

import io.github.randomusert.mods.tincraftclassic.util.TCEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityGenerator extends TileEntity implements ITickable {

    private final TCEnergyStorage energyStorage = new TCEnergyStorage(100000, 1000);

    @Override
    public void update() {
        if (!world.isRemote) {
            this.energyStorage.generateEnergy(50);

            this.distributeEnergy();

            this.markDirty();
        }
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

    // Saving energy amount to NBT
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Energy", this.energyStorage.getEnergyStored());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.energyStorage.generateEnergy(compound.getInteger("Energy"));
    }


    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this.energyStorage);
        }
        return super.getCapability(capability, facing);
    }
}
