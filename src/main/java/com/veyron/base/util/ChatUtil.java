package com.veyron.base.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class ChatUtil {
    private static final String PREFIX = "§8[§bVeyron§8] §7";

    private ChatUtil() {
    }

    public static void send(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.literal(PREFIX + message));
    }

    public static void sendRaw(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud == null) return;
        mc.inGameHud.getChatHud().addMessage(Text.literal(message));
    }
}
