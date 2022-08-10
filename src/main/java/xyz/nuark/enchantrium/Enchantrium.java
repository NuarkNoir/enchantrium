package xyz.nuark.enchantrium;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.nuark.enchantrium.setup.ClientSetup;
import xyz.nuark.enchantrium.setup.ModSetup;
import xyz.nuark.enchantrium.setup.Registration;

@Mod(Enchantrium.MOD_ID)
public class Enchantrium {
    public static final String MOD_ID = "enchantrium";
    public static final Logger LOGGER = LogUtils.getLogger();


    public Enchantrium() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModSetup.setup();
        Registration.register(eventBus);

        eventBus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> eventBus.addListener(ClientSetup::init));
    }
}
