package xyz.nuark.enchantrium.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.nuark.enchantrium.Enchantrium;
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
    private Button switchUnbreakableBtn;

    public EnchanterScreen(EnchanterMenu enchanterMenu, Inventory inventory, Component title) {
        super(enchanterMenu, inventory, title);

        imageWidth = 176;
        imageHeight = 232;
    }

    @Override
    protected void init() {
        super.init();

        int sx = width / 2 - imageWidth / 2;
        int sy = height / 2 - imageHeight / 2;

        currentEnchantmentLabel = new Label(
                new TextComponent(""),
                sx + 7, sy + 51, 0xFFFFFF, false
        );

        enchantBtn = new Button(
                sx + 76, sy + 18, 93, 20,
                new TextComponent("Enchant"),
                button -> clickButton(0)
        );

        prevBtn = new Button(
                sx + 7, sy + 71, 78, 20,
                new TextComponent("Previous"),
                button -> clickButton(1)
        );
        nextBtn = new Button(
                sx + 91, sy + 71, 78, 20,
                new TextComponent("Next"),
                button -> clickButton(2)
        );

        decreaseLevelBtn = new Button(
                sx + 7, sy + 97, 78, 20,
                new TextComponent("Decrease"),
                button -> clickButton(3)
        );
        increaseLevelBtn = new Button(
                sx + 91, sy + 97, 78, 20,
                new TextComponent("Increase"),
                button -> clickButton(4)
        );

        switchUnbreakableBtn = new Button(
                sx + 7, sy + 123, 162, 20,
                new TextComponent(""),
                button -> clickButton(5)
        );

        addRenderableWidget(enchantBtn);
        addRenderableWidget(prevBtn);
        addRenderableWidget(nextBtn);
        addRenderableWidget(decreaseLevelBtn);
        addRenderableWidget(increaseLevelBtn);
        addRenderableWidget(switchUnbreakableBtn);
        addRenderableOnly(new Label(
                new TextComponent("Selected Enchantment:"),
                sx + 7, sy + 41, 0xFFFFFF, false
        ));
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
    protected void renderLabels(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
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
            MutableComponent description;
            if (level == 0) {
                description = new TranslatableComponent(enchantment.getDescriptionId());
            } else {
                description = new TranslatableComponent(enchantment.getDescriptionId());
                description.withStyle(ChatFormatting.GREEN);
                if (level != 1 || enchantment.getMaxLevel() != 1) {
                    description.append(" ").append(new TranslatableComponent("enchantment.level." + level));
                }
            }
            currentEnchantmentLabel.setText(description);
        } else {
            currentEnchantmentLabel.setText(new TextComponent("No enchantment selected"));
        }

        var enchantmentRequirements = menu.getEnchantmentRequirements();

        if (Minecraft.getInstance().player != null) {
            enchantBtn.active = enchantmentInstance != null && menu.requirementsMet(Minecraft.getInstance().player, enchantmentRequirements);
        } else {
            enchantBtn.active = false;
        }
        nextBtn.active = enchantmentInstance != null;
        prevBtn.active = enchantmentInstance != null;
        increaseLevelBtn.active = enchantmentInstance != null;
        decreaseLevelBtn.active = enchantmentInstance != null;
        switchUnbreakableBtn.active = enchantmentInstance != null;

        switchUnbreakableBtn.setMessage(new TextComponent(menu.unbreakable() ? "Make unbreakable" : "Make breakable"));

        if (enchantmentInstance != null && enchantBtn.isHoveredOrFocused()) {
            renderTooltip(
                    pPoseStack,
                    new TextComponent("Requirements: ")
                            .append(String.valueOf(enchantmentRequirements.lapis()))
                            .append(new TextComponent(" lapis").withStyle(ChatFormatting.BLUE))
                            .append(", ")
                            .append(String.valueOf(enchantmentRequirements.netherite()))
                            .append(new TextComponent(" netherite").withStyle(ChatFormatting.DARK_PURPLE))
                            .append(", ")
                            .append(String.valueOf(enchantmentRequirements.levels()))
                            .append(new TextComponent(" levels").withStyle(ChatFormatting.YELLOW)),
                    mouseX, mouseY
            );
        }
    }
}
