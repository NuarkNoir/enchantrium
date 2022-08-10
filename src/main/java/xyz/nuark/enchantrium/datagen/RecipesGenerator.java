package xyz.nuark.enchantrium.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import xyz.nuark.enchantrium.block.ModBlocks;

import java.util.function.Consumer;

public class RecipesGenerator extends RecipeProvider {
    public RecipesGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModBlocks.ENCHANTER.get())
                .pattern("xxx")
                .pattern("x#x")
                .pattern("#y#")
                .define('x', Tags.Items.GEMS_DIAMOND)
                .define('#', Tags.Items.STORAGE_BLOCKS_COPPER)
                .define('y', Items.ENCHANTING_TABLE)
                .unlockedBy("enchanting", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENCHANTING_TABLE))
                .save(consumer);
    }
}
