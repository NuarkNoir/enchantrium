package xyz.nuark.enchantrium.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.network.Networking;
import xyz.nuark.enchantrium.network.message.PacketEnchantItem;
import xyz.nuark.enchantrium.screen.widget.Label;

public class EnchanterScreen extends AbstractContainerScreen<EnchanterMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Enchantrium.MOD_ID, "textures/gui/enchanter_gui.png");

    private Label currentEnchantmentLabel;
    private Button enchantBtn;
    private Button nextBtn;
    private Button prevBtn;
    private Button increaseLevelBtn;
    private Button decreaseLevelBtn;

    public EnchanterScreen(EnchanterMenu enchanterMenu, Inventory inventory, Component title) {
        super(enchanterMenu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        currentEnchantmentLabel = new Label(
                new TextComponent(""),
                width / 2, height / 2 + 80, 0xFFFFFF, true
        );

        enchantBtn = new Button(
                width / 2 - 50, height / 2 + 100, 100, 20,
                new TextComponent("Enchant"),
                button -> clickButton(0)
        );
        nextBtn = new Button(
                width / 2 + 60, height / 2 + 100, 20, 20,
                new TextComponent("\u2192"),
                button -> clickButton(1)
        );
        prevBtn = new Button(
                width / 2 - 80, height / 2 + 100, 20, 20,
                new TextComponent("\u2190"),
                button -> clickButton(2)
        );

        increaseLevelBtn = new Button(
                width / 2 + 90, height / 2 + 100, 20, 10,
                new TextComponent("\u2191"),
                button -> clickButton(3)
        );
        decreaseLevelBtn = new Button(
                width / 2 + 90, height / 2 + 110, 20, 10,
                new TextComponent("\u2193"),
                button -> clickButton(4)
        );

        addRenderableWidget(enchantBtn);
        addRenderableWidget(nextBtn);
        addRenderableWidget(prevBtn);
        addRenderableWidget(increaseLevelBtn);
        addRenderableWidget(decreaseLevelBtn);
        addRenderableOnly(currentEnchantmentLabel);
    }

    private void clickButton(int buttonId) {
        assert Minecraft.getInstance().player != null;
         menu.clickMenuButton(Minecraft.getInstance().player, buttonId);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);

        var enchantmentInstance = menu.getCurrentEnchantment();
        if (enchantmentInstance != null) {
            var enchantment = enchantmentInstance.enchantment;
            var level = enchantmentInstance.level;
            Component description;
            if (level == 0) {
                description = new TranslatableComponent(enchantment.getDescriptionId());
            } else {
                description = enchantment.getFullname(level);
            }
            currentEnchantmentLabel.setText(description);
        } else {
            currentEnchantmentLabel.setText(new TextComponent("No enchantment selected"));
        }

        enchantBtn.active = enchantmentInstance != null;
        nextBtn.active = enchantmentInstance != null;
        prevBtn.active = enchantmentInstance != null;
        increaseLevelBtn.active = enchantmentInstance != null;
        decreaseLevelBtn.active = enchantmentInstance != null;
    }
}
