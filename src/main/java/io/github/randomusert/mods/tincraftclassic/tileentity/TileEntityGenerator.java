package io.github.randomusert.mods.tincraftclassic.tileentity;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
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

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import net.minecraftforge.items.CapabilityItemHandler; // Added import
import net.minecraftforge.items.ItemStackHandler;


import com.cleanroommc.modularui.widgets.*;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;

public class TileEntityGenerator extends TileEntity implements ITickable, IGuiHolder<PosGuiData> {

    private static final int ENERGY_CAPACITY = 100000;
    private static final int ENERGY_TRANSFER_RATE = 1000;
    private static final int ENERGY_GENERATION_RATE = 50;

    private final TCEnergyStorage energyStorage = new TCEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER_RATE);

    private int progress = 0;
    private int burnTime = ConfigManager.getConfig().generatorTileEntityConfig.maxBurnTime;
    private int curentItemBurnTime = 0;

    private final ItemStackHandler inv = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    };

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        boolean isBurning = this.burnTime > 0;
        boolean dirty = false;

        if (this.burnTime > 0) {
            this.burnTime--;

            if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                energyStorage.generateEnergy(ENERGY_GENERATION_RATE);
                dirty = true;
            }
        }

        if (this.burnTime <= 0) {
            ItemStack fuelStack = inv.getStackInSlot(0);

            if (!fuelStack.isEmpty()) {
                int itemBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);

                if (itemBurnTime > 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                    this.burnTime = itemBurnTime;
                    this.curentItemBurnTime = itemBurnTime;

                    inv.extractItem(0, 1, false);
                    dirty = true;
                }
            }
        }

        if (energyStorage.getEnergyStored() > 0) {
            distributeEnergy();
            dirty = true;
        }

        if (dirty) {
            markDirty();
        }
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
        compound.setTag("Inventory", inv.serializeNBT());
        compound.setInteger("currentItemBurnTime", curentItemBurnTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Inventory")) {
            inv.deserializeNBT(compound.getCompoundTag("Inventory"));
        }

        compound.getInteger("currentItemBurnTime");
        energyStorage.setEnergyStored(compound.getInteger("Energy"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyStorage);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        IntSyncValue syncedEnergy = new IntSyncValue(
                () -> this.energyStorage.getEnergyStored(),
                val -> this.energyStorage.setEnergyStored(val)
        );

        syncManager.syncValue("generator_energy", syncedEnergy);

        ModularPanel panel = ModularPanel.defaultPanel("generator_ui");

        Widget<?> energyTextWidget = IKey.dynamic(() ->
                syncedEnergy.getIntValue() + " / " + ENERGY_CAPACITY + " FE"
        ).asWidget().pos(20, 20);

        // 4. Rakennetaan käyttöliittymän sisältö ilman, että ketjutuksen palautusarvo sotkee return-lausetta
        panel.bindPlayerInventory()
                .child(new ProgressWidget()
                        .size(20)
                        .leftRel(0.5f).topRelAnchor(0.25f, 0.5f)
                        .texture(GuiTextures.PROGRESS_ARROW, 20)
                        .value(new DoubleSyncValue(() -> this.progress / 100.0, val -> this.progress = (int) (val * 100))))
                .child(new ItemSlot().slot(new ModularSlot(inv, 0)))
                .child(energyTextWidget);

        return panel;
    }
}
