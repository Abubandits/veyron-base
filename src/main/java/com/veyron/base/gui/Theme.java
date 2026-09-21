package com.veyron.base.gui;

public final class Theme {
    private Theme() {
    }

    // ── Window ──
    public static int panelBg      = 0xF014141C;
    public static int panelBgSolid = 0xFF14141C;
    public static int sidebarBg    = 0xFF111119;
    public static int headerBg     = 0xFF18182A;
    public static int footerBg     = 0xFF111119;

    // ── Rows ──
    public static int rowBg        = 0xFF1A1A24;
    public static int rowBgHover   = 0xFF222230;

    // ── Sidebar ──
    public static int sidebarSelected = 0x339D9DFF;
    public static int sidebarHover    = 0xFF1C1C28;

    // ── Dock (legacy, used by icons) ──
    public static int dockBg = 0xE61A1A22;

    // ── Borders ──
    public static int border    = 0xFF2A2A38;
    public static int borderDim = 0xFF222230;

    // ── Text ──
    public static int text      = 0xFFE8E8EE;
    public static int textDim   = 0xFF9A9AA8;
    public static int textMuted = 0xFF5A5A68;

    // ── Toggle switch ──
    public static int toggleOn   = 0xFF9D9DFF;
    public static int toggleOff  = 0xFF3A3A48;
    public static int toggleKnob = 0xFFE8E8EE;

    // ── Accent ──
    public static int accent     = 0xFF9D9DFF;
    public static int accentDim  = 0x559D9DFF;
    public static int accentDark = 0xFF6B6BCC;

    // ── Overlay ──
    public static int overlayBg = 0xCC000000;

    // ── Presets ──
    public static final int[] PRESETS = {
            0xFF9D9DFF, // lavender (default)
            0xFF6CA8FF, // blue
            0xFF6FE3A5, // green
            0xFFFF7A7A, // red
            0xFFFFB35C, // orange
            0xFFFF8AC2, // pink
    };

    public static void setAccent(int color) {
        accent = color;
        accentDim = (0x55 << 24) | (color & 0x00FFFFFF);
        accentDark = darken(color, 0.7f);
        toggleOn = color;
        sidebarSelected = (0x33 << 24) | (color & 0x00FFFFFF);
    }

    private static int darken(int color, float factor) {
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }
}
