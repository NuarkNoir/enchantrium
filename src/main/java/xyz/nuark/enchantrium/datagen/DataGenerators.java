package xyz.nuark.enchantrium.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import xyz.nuark.enchantrium.Enchantrium;

@Mod.EventBusSubscriber(modid = Enchantrium.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new RecipesGenerator(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStatesGenerator(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
            generator.addProvider(new LanguageGenerator(generator, "en_us"));
        }
    }
}
