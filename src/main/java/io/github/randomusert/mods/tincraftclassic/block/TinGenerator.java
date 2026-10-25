package io.github.randomusert.mods.tincraftclassic.block;

import io.github.randomusert.mods.tincraftclassic.tileentity.TileEntityGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import com.cleanroommc.modularui.factory.GuiFactories;

public class TinGenerator extends Block implements ITileEntityProvider {
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityGenerator();
    }

    public TinGenerator() {
        super(Material.IRON, MapColor.IRON);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityGenerator();
    }

    @Override 
    public boolean onBlockActivated(World worldIn, net.minecraft.util.math.BlockPos pos, IBlockState state, net.minecraft.entity.player.EntityPlayer playerIn, net.minecraft.util.EnumHand hand, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ) {
         if (!worldIn.isRemote) {
            GuiFactories.tileEntity().open(playerIn, pos);
        }
        return true;
    }

}
