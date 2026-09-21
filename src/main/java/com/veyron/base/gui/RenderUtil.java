package com.veyron.base.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public final class RenderUtil {
    private RenderUtil() {
    }

    // ══════════════════════════════════════════════════════════════
    //  4K Texture Assets & Icons
    // ══════════════════════════════════════════════════════════════

    public static final Identifier PANEL_CARD   = Identifier.of("veyron", "textures/gui/panel_card.png");

    public static final Identifier ICON_COMBAT   = Identifier.of("veyron", "textures/gui/icons/combat.png");
    public static final Identifier ICON_MOVEMENT = Identifier.of("veyron", "textures/gui/icons/movement.png");
    public static final Identifier ICON_VISUALS  = Identifier.of("veyron", "textures/gui/icons/visuals.png");
    public static final Identifier ICON_PLAYER   = Identifier.of("veyron", "textures/gui/icons/player.png");
    public static final Identifier ICON_MISC     = Identifier.of("veyron", "textures/gui/icons/misc.png");
    public static final Identifier ICON_CHEVRON  = Identifier.of("veyron", "textures/gui/icons/chevron.png");

    /**
     * Отрисовка 4K текстурной иконки с аппаратным билинейным сглаживанием OpenGL.
     */
    public static void drawTextureIcon(DrawContext ctx, Identifier id, int x, int y, int w, int h) {
        var p = RenderPipelines.GUI_TEXTURED;
        ctx.drawTexture(p, id, x, y, 0f, 0f, w, h, w, h);
    }

    /**
     * Отрисовка 4K карточки панели через 9-slice текстурирование (256x256).
     * Дает идеально гладкие скругленные углы, шелковистую мягкую тень и тонкую рамку
     * без единого пикселя и без малейших лагов (9 квадов на аппаратном конвейере).
     */
    public static void drawPanelCard(DrawContext ctx, int x, int y, int w, int h) {
        int shadowPad = 6;
        int drawX = x - shadowPad;
        int drawY = y - shadowPad;
        int drawW = w + shadowPad * 2;
        int drawH = h + shadowPad * 2;

        int corner = 14;
        int texCorner = 44;
        int texMid = 256 - texCorner * 2; // 168

        int midW = drawW - corner * 2;
        int midH = drawH - corner * 2;

        var p = RenderPipelines.GUI_TEXTURED;

        // 4 Угла
        ctx.drawTexture(p, PANEL_CARD, drawX, drawY, 0f, 0f, corner, corner, texCorner, texCorner, 256, 256);
        ctx.drawTexture(p, PANEL_CARD, drawX + drawW - corner, drawY, (float) (256 - texCorner), 0f, corner, corner, texCorner, texCorner, 256, 256);
        ctx.drawTexture(p, PANEL_CARD, drawX, drawY + drawH - corner, 0f, (float) (256 - texCorner), corner, corner, texCorner, texCorner, 256, 256);
        ctx.drawTexture(p, PANEL_CARD, drawX + drawW - corner, drawY + drawH - corner, (float) (256 - texCorner), (float) (256 - texCorner), corner, corner, texCorner, texCorner, 256, 256);

        // 4 Стороны
        if (midW > 0) {
            ctx.drawTexture(p, PANEL_CARD, drawX + corner, drawY, (float) texCorner, 0f, midW, corner, texMid, texCorner, 256, 256);
            ctx.drawTexture(p, PANEL_CARD, drawX + corner, drawY + drawH - corner, (float) texCorner, (float) (256 - texCorner), midW, corner, texMid, texCorner, 256, 256);
        }
        if (midH > 0) {
            ctx.drawTexture(p, PANEL_CARD, drawX, drawY + corner, 0f, (float) texCorner, corner, midH, texCorner, texMid, 256, 256);
            ctx.drawTexture(p, PANEL_CARD, drawX + drawW - corner, drawY + corner, (float) (256 - texCorner), (float) texCorner, corner, midH, texCorner, texMid, 256, 256);
        }

        // Центр карточки
        if (midW > 0 && midH > 0) {
            ctx.drawTexture(p, PANEL_CARD, drawX + corner, drawY + corner, (float) texCorner, (float) texCorner, midW, midH, texMid, texMid, 256, 256);
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  Shapes
    // ══════════════════════════════════════════════════════════════

    public static void rect(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    /**
     * Быстрый скругленный прямоугольник (для строк модулей и мелких элементов).
     */
    public static void roundedRect(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
        int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));

        if (r <= 0) {
            ctx.fill(x, y, x + w, y + h, color);
            return;
        }

        ctx.fill(x + r, y, x + w - r, y + h, color);
        ctx.fill(x, y + r, x + r, y + h - r, color);
        ctx.fill(x + w - r, y + r, x + w, y + h - r, color);

        for (int dy = 0; dy < r; dy++) {
            int dx = (int) Math.sqrt((double) r * r - (double) (r - dy) * (r - dy));
            ctx.fill(x + r - dx, y + dy, x + r, y + dy + 1, color);
            ctx.fill(x + w - r, y + dy, x + w - r + dx, y + dy + 1, color);
            ctx.fill(x + r - dx, y + h - dy - 1, x + r, y + h - dy, color);
            ctx.fill(x + w - r, y + h - dy - 1, x + w - r + dx, y + h - dy, color);
        }
    }

    public static boolean hovered(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}
