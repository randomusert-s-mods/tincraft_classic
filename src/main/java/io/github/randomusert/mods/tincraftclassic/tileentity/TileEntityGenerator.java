
package io.github.randomusert.mods.tincraftclassic.tileentity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import io.github.randomusert.mods.tincraftclassic.config.ConfigManager;
import io.github.randomusert.mods.tincraftclassic.util.TCEnergyStorage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityGenerator extends TileEntity
        implements ITickable, IGuiHolder<PosGuiData> {

    private static final int ENERGY_CAPACITY = 100000;
    private static final int ENERGY_TRANSFER_RATE = 1000;
    private static final int ENERGY_GENERATION_RATE = 50;

    private final TCEnergyStorage energyStorage =
            new TCEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER_RATE);

    private int progress = 0;

    // A newly placed generator must not have free burn time.
    private int burnTime = 0;
    private int currentItemBurnTime = 0;

    private final ItemStackHandler inv = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    @Override
    public void update() {
        if (world == null || world.isRemote) {
            return;
        }

        boolean dirty = false;

        // Only burn fuel when there is room to generate energy.
        if (energyStorage.getEnergyStored()
                < energyStorage.getMaxEnergyStored()) {

            if (burnTime > 0) {
                burnTime--;

                energyStorage.generateEnergy(ENERGY_GENERATION_RATE);
                dirty = true;

            } else {
                ItemStack fuelStack = inv.getStackInSlot(0);

                if (!fuelStack.isEmpty()) {
                    int itemBurnTime =
                            TileEntityFurnace.getItemBurnTime(fuelStack);

                    if (itemBurnTime > 0) {
                        burnTime = itemBurnTime;
                        currentItemBurnTime = itemBurnTime;

                        inv.extractItem(0, 1, false);
                        dirty = true;
                    }
                }
            }
        }

        // Send stored energy to neighboring compatible machines.
        if (energyStorage.getEnergyStored() > 0) {
            if (distributeEnergy()) {
                dirty = true;
            }
        }

        if (dirty) {
            markDirty();
        }
    }

    private boolean distributeEnergy() {
        boolean transferredAny = false;

        for (EnumFacing facing : EnumFacing.values()) {
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));

            if (neighbor == null) {
                continue;
            }

            IEnergyStorage neighborStorage = neighbor.getCapability(
                    CapabilityEnergy.ENERGY,
                    facing.getOpposite()
            );

            if (neighborStorage == null || !neighborStorage.canReceive()) {
                continue;
            }

            int energyToSend = energyStorage.extractEnergy(
                    ENERGY_TRANSFER_RATE, true);

            if (energyToSend <= 0) {
                continue;
            }

            int accepted = neighborStorage.receiveEnergy(
                    energyToSend, false);

            if (accepted > 0) {
                energyStorage.extractEnergy(accepted, false);
                transferredAny = true;
            }
        }

        return transferredAny;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger(
                "Energy", energyStorage.getEnergyStored());

        compound.setTag("Inventory", inv.serializeNBT());

        compound.setInteger("BurnTime", burnTime);
        compound.setInteger(
                "CurrentItemBurnTime", currentItemBurnTime);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("Inventory")) {
            inv.deserializeNBT(compound.getCompoundTag("Inventory"));
        }

        energyStorage.setEnergyStored(
                compound.getInteger("Energy"));

        burnTime = Math.max(0, compound.getInteger("BurnTime"));

        currentItemBurnTime = Math.max(
                0, compound.getInteger("CurrentItemBurnTime"));
    }

    @Override
    public boolean hasCapability(
            Capability<?> capability, EnumFacing facing) {

        if (capability == CapabilityEnergy.ENERGY
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(
            Capability<T> capability, EnumFacing facing) {

        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public ModularPanel buildUI(
            PosGuiData data,
            PanelSyncManager syncManager,
            UISettings settings) {

        // Read-only energy display: the client must not set server energy.
        IntSyncValue syncedEnergy = new IntSyncValue(
                () -> energyStorage.getEnergyStored()
        );

        syncManager.syncValue("generator_energy", syncedEnergy);

        ModularPanel panel = ModularPanel.defaultPanel("generator_ui");

        Widget<?> energyTextWidget = IKey.dynamic(
                () -> syncedEnergy.getIntValue()
                        + " / " + ENERGY_CAPACITY + " FE"
        ).asWidget().pos(45, 20);

        Widget<?> fuelSlot = new ItemSlot()
                .slot(new ModularSlot(inv, 0))
                .pos(55, 42);

        panel.bindPlayerInventory()
                .child(new ProgressWidget()
                        .size(20)
                        .leftRel(0.5f)
                        .topRelAnchor(0.25f, 0.5f)
                        .texture(GuiTextures.PROGRESS_ARROW, 20)
                        .value(new DoubleSyncValue(
                                () -> progress / 100.0,
                                val -> progress = (int) (val * 100)
                        )))
                .child(fuelSlot)
                .child(energyTextWidget);

        return panel;
    }
}
