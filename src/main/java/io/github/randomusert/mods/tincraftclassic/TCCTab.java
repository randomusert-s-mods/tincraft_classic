package io.github.randomusert.mods.tincraftclassic;

import io.github.randomusert.mods.tincraftclassic.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TCCTab extends CreativeTabs {
    public TCCTab() {
        super(Tincraftclassic.MODID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.TIN_INGOT);
    }
}
