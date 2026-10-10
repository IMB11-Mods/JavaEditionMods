package dev.imb11.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

final class InventoryPanels {
    private static final Identifier PANEL_SPRITE = Identifier.withDefaultNamespace("recipe_book/overlay_recipe");
    private static final Identifier INVENTORY_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");

    private InventoryPanels() {
    }

    static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL_SPRITE, x, y, width, height);
    }

    static void inset(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x + 1, y + 1, 8.0F, 84.0F, width - 2, height - 2, 1, 1, 256, 256);
        insetBorder(graphics, x, y, width, height);
    }

    static void insetBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x, y, 8.0F, 83.0F, width - 1, 1, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x, y + 1, 7.0F, 84.0F, 1, height - 2, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x + width - 1, y + 1, 24.0F, 84.0F, 1, height - 1, 1, 1, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, x, y + height - 1, 8.0F, 100.0F, width - 1, 1, 1, 1, 256, 256);
    }
}
