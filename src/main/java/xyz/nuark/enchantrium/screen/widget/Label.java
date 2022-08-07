package xyz.nuark.enchantrium.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class Label implements Widget {
    private Component text;
    private final int x;
    private final int y;
    private final int color;
    private final boolean centered;

    public Label(Component p_120736_, int p_120737_, int p_120738_, int p_120739_, boolean centered) {
        this.text = p_120736_.copy();
        this.x = p_120737_;
        this.y = p_120738_;
        this.color = p_120739_;
        this.centered = centered;
    }

    public void render(@NotNull PoseStack p_175036_, int p_175037_, int p_175038_, float p_175039_) {
        if (centered) {
            GuiComponent.drawCenteredString(p_175036_, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
        } else {
            GuiComponent.drawString(p_175036_, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
        }
    }

    public void setText(Component p_175041_) {
        this.text = p_175041_;
    }

    public Component getText() {
        return this.text;
    }
}
