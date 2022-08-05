package xyz.nuark.enchantrium;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.nuark.enchantrium.block.ModBlocks;
import xyz.nuark.enchantrium.block.entity.ModBlockEntities;
import xyz.nuark.enchantrium.item.ModItems;
import xyz.nuark.enchantrium.screen.ModMenuTypes;

@Mod(Enchantrium.MOD_ID)
public class Enchantrium {
    public static final String MOD_ID = "enchantrium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Enchantrium() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
