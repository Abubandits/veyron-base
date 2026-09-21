package com.veyron.base.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Font;
import java.io.InputStream;

/**
 * Менеджер кастомного шрифта "SF Pro Display Bold" (assets/veyron/font/bold.otf).
 *
 * Работает ТОЛЬКО для модификации Veyron через ID veyron:bold
 * и НЕ затрагивает стандартный шрифт самого Minecraft (чат, меню, настройки).
 */
public final class FontManager {

    /** Идентификатор кастомного шрифта мода (veyron:bold) */
    public static final Identifier FONT_ID = Identifier.of("veyron", "bold");
    private static final Style VEYRON_FONT_STYLE = Style.EMPTY.withFont(new StyleSpriteSource.Font(FONT_ID));

    private static Font awtBold;
    private static boolean loaded = false;

    private FontManager() {
    }

    public static void init() {
        if (loaded) return;
        try (InputStream in = FontManager.class.getResourceAsStream("/assets/veyron/font/bold.ttf")) {
            if (in != null) {
                awtBold = Font.createFont(Font.TRUETYPE_FONT, in);
                loaded = true;
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static Font getAwt(float size) {
        if (awtBold == null) return new Font(Font.SANS_SERIF, Font.BOLD, (int) size);
        return awtBold.deriveFont(Font.BOLD, size);
    }

    /**
     * Создает Text со шрифтом SF Pro Display Bold (работает только в Veyron).
     */
    public static Text literal(String text) {
        return Text.literal(text).fillStyle(VEYRON_FONT_STYLE);
    }

    /**
     * Отрисовка текста шрифтом SF Pro Display Bold в GUI мода с безопасной защитой от краша.
     */
    public static void draw(DrawContext context, String text, int x, int y, int color, boolean shadow) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null || context == null) return;
        try {
            context.drawText(mc.textRenderer, literal(text), x, y, color, shadow);
        } catch (Throwable t) {
            context.drawText(mc.textRenderer, text, x, y, color, shadow);
        }
    }

    public static void draw(DrawContext context, String text, float x, float y, int color, boolean shadow) {
        draw(context, text, (int) x, (int) y, color, shadow);
    }

    public static int getWidth(String text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 0;
        try {
            return mc.textRenderer.getWidth(literal(text));
        } catch (Throwable t) {
            return mc.textRenderer.getWidth(text);
        }
    }

    public static int getHeight() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.textRenderer == null) return 9;
        return mc.textRenderer.fontHeight;
    }
}
