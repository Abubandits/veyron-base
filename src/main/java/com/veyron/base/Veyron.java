package com.veyron.base;

import com.veyron.base.event.EventBus;
import com.veyron.base.event.events.TickEvent;
import com.veyron.base.font.FontManager;
import com.veyron.base.account.AccountManager;
import com.veyron.base.gui.GuiOpener;
import com.veyron.base.key.KeyManager;
import com.veyron.base.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Veyron implements ClientModInitializer {
    public static final String MOD_ID = "veyron";
    public static final String NAME = "Veyron";

    private static Veyron instance;

    private final Logger logger = LoggerFactory.getLogger(NAME);
    private EventBus eventBus;
    private ModuleManager moduleManager;
    private AccountManager accountManager;

    @Override
    public void onInitializeClient() {
        instance = this;

        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager(eventBus);
        this.accountManager = new AccountManager();

        // Модулей по умолчанию нет - регистрируй свои в ModuleManager.load()
        moduleManager.load();

        KeyManager.init();
        FontManager.init();
        GuiOpener.init();
        try {
            accountManager.init(MinecraftClient.getInstance().runDirectory.toPath());
        } catch (Exception ignored) {
        }

        // Прокидываем тик клиента в нашу шину событий
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!FontManager.isLoaded()) FontManager.init();
            if (client.player == null || client.world == null) return;
            eventBus.post(new TickEvent());
        });

        logger.info("[{}] base loaded (1.21.11 Fabric/Yarn), modules: {}", NAME, moduleManager.getModules().size());
    }

    public static Veyron getInstance() {
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }
}
