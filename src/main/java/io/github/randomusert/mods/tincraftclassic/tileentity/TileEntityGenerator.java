package io.github.randomusert.mods.tincraftclassic.tileentity;

import io.github.randomusert.mods.tincraftclassic.util.TCEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

public class TileEntityGenerator extends TileEntity implements ITickable, IGuiHolder<PosGuiData> {

    private static final int ENERGY_CAPACITY = 100000;
    private static final int ENERGY_TRANSFER_RATE = 1000;
    private static final int ENERGY_GENERATION_RATE = 50;

    private final TCEnergyStorage energyStorage = new TCEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER_RATE);

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        energyStorage.generateEnergy(ENERGY_GENERATION_RATE);
        distributeEnergy();
        markDirty();
    }

    private void distributeEnergy() {
        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            if (neighbor == null) {
                continue;
            }

            EnumFacing oppositeFacing = facing.getOpposite();
            IEnergyStorage neighborStorage = neighbor.getCapability(CapabilityEnergy.ENERGY, oppositeFacing);
            if (neighborStorage == null || !neighborStorage.canReceive()) {
                continue;
            }

            int energyToSend = energyStorage.extractEnergy(ENERGY_TRANSFER_RATE, true);
            int accepted = neighborStorage.receiveEnergy(energyToSend, false);
            energyStorage.extractEnergy(accepted, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Energy", energyStorage.getEnergyStored());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energyStorage.setEnergyStored(compound.getInteger("Energy"));
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
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ModularPanel buildUI(PosGuiData arg0, PanelSyncManager arg1, UISettings arg2) {
        ModularPanel panel = ModularPanel.defaultPanel("generator_ui");
        panel.bindPlayerInventory();
        return panel;
    }
}
