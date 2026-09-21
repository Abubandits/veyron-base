package com.veyron.base.key;

import com.veyron.base.Veyron;
import com.veyron.base.module.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public final class KeyManager {
    private static final Set<Integer> down = new HashSet<>();
    private static boolean initialized = false;

    private KeyManager() {
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getWindow() == null) return;
            if (Veyron.getInstance() == null || Veyron.getInstance().getModuleManager() == null) return;

            for (Module module : Veyron.getInstance().getModuleManager().getModules()) {
                int key = module.getKey();
                if (key == GLFW.GLFW_KEY_UNKNOWN || key < 0) continue;
                if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) continue; // зарезервировано под ClickGui

                boolean pressed;
                try {
                    pressed = InputUtil.isKeyPressed(client.getWindow(), key);
                } catch (Exception ignored) {
                    continue;
                }

                if (pressed && !down.contains(key)) {
                    down.add(key);
                    // Тогглим только по нажатию (front edge), не по удержанию
                    // Мы уже на клиентском треде (END_CLIENT_TICK), поэтому напрямую
                    module.toggle();
                } else if (!pressed) {
                    down.remove(key);
                }
            }
        });
    }
}
