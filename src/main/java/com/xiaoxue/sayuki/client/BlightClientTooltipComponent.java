package com.xiaoxue.sayuki.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Renders a blight tooltip line with a 12×12 item icon followed by the blight name (and optional ×N).
 */
public class BlightClientTooltipComponent implements ClientTooltipComponent {

    private final ItemStack icon;
    private final Component text;

    public BlightClientTooltipComponent(BlightIconTooltipData data) {
        this.icon = data.icon();
        Component name = Component.translatable("item.sayuki." + data.blightId());
        if (data.count() > 1) {
            this.text = name.copy().append(Component.literal(" §7×" + data.count()));
        } else {
            this.text = name;
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public int getWidth(Font font) {
        return 14 + 2 + font.width(text);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        // Render item icon scaled to ~12px
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(0.75F, 0.75F, 1.0F);
        guiGraphics.renderFakeItem(icon, 0, 0);
        pose.popPose();

        // Render text
        guiGraphics.drawString(font, text, x + 14 + 2, y + 3, 0xFFAAAAAA);
    }
}
