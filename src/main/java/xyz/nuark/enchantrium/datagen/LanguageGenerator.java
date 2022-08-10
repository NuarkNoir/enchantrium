package xyz.nuark.enchantrium.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.block.ModBlocks;
import xyz.nuark.enchantrium.item.ModItems;

public class LanguageGenerator extends LanguageProvider {
    public LanguageGenerator(DataGenerator gen, String locale) {
        super(gen, Enchantrium.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + Enchantrium.MOD_ID, "Enchantrium");

        add(ModBlocks.ENCHANTER.get(), "Enchanter");
        add(ModItems.TEST_ITEM.get(), "Test Item :)");
    }
}
