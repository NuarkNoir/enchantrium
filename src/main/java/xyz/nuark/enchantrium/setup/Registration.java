package xyz.nuark.enchantrium.setup;

import net.minecraftforge.eventbus.api.IEventBus;
import xyz.nuark.enchantrium.block.ModBlocks;
import xyz.nuark.enchantrium.block.entity.ModBlockEntities;
import xyz.nuark.enchantrium.item.ModItems;
import xyz.nuark.enchantrium.screen.ModMenuTypes;

public class Registration {
    public static void register(IEventBus eventBus) {
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
    }
}
