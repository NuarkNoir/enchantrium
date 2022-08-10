package xyz.nuark.enchantrium.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.nuark.enchantrium.screen.EnchanterScreen;
import xyz.nuark.enchantrium.screen.ModMenuTypes;

public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ENCHANTER_MENU.get(), EnchanterScreen::new);
        });
    }
}
