package com.veyron.base.gui;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class GuiOpener {
    private static boolean initialized = false;
    private static boolean wasDown = false;

    private GuiOpener() {
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        // Работает и в игре, и в MainMenu: тик идет всегда
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null || client.getWindow() == null) return;

            boolean down;
            try {
                down = InputUtil.isKeyPressed(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
            } catch (Exception ignored) {
                return;
            }

            if (down && !wasDown) {
                wasDown = true;
                onRightShift(client);
            } else if (!down) {
                wasDown = false;
            }
        });
    }

    private static void onRightShift(MinecraftClient client) {
        Screen current = client.currentScreen;
        if (current instanceof ClickGuiScreen) {
            client.setScreen(null);
        } else if (current == null) {
            client.setScreen(new ClickGuiScreen());
        }
        // если открыт другой скрин (чат, инвентарь) - не мешаем
    }

    public static void open() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null) mc.setScreen(new ClickGuiScreen());
    }
}
