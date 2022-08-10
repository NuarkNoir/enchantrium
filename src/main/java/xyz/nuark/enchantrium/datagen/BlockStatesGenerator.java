package xyz.nuark.enchantrium.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.block.ModBlocks;

public class BlockStatesGenerator extends BlockStateProvider {
    public BlockStatesGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Enchantrium.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerEnchanter();
    }

    private void registerEnchanter() {
        var block = ModBlocks.ENCHANTER.get();
        var side = modLoc("block/enchanter_side");
        var top = modLoc("block/enchanter_top");
        simpleBlock(block, models().cube(block.getRegistryName().getPath(), side, top, side, side, side, side));
    }
}
