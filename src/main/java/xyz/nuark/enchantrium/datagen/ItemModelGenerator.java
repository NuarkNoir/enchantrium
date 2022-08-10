package xyz.nuark.enchantrium.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.item.ModItems;

public class ItemModelGenerator extends ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, Enchantrium.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.TEST_ITEM.get());
        withExistingParent(ModItems.ENCHANTER_ITEM.get().getRegistryName().getPath(), modLoc("block/enchanter"));
    }
}
