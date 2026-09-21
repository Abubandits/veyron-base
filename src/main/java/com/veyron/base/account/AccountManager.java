package com.veyron.base.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.veyron.base.mixin.accessor.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AccountManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Veyron/Accounts");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<Account> accounts = new ArrayList<>();
    private String current;
    private Path file;

    public void init(Path runDir) {
        this.file = runDir.resolve("config").resolve("veyron_accounts.json");
        load();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (current == null && mc != null && mc.getSession() != null) {
            current = mc.getSession().getUsername();
        }
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public String getCurrent() {
        return current;
    }

    public boolean add(String name) {
        if (name == null) return false;
        name = name.trim();
        if (name.length() < 3 || name.length() > 16) return false;
        if (!name.matches("[A-Za-z0-9_]+")) return false;
        for (Account a : accounts) {
            if (a.getName().equalsIgnoreCase(name)) return false;
        }
        accounts.add(new Account(name));
        save();
        return true;
    }

    public boolean remove(String name) {
        boolean ok = accounts.removeIf(a -> a.getName().equalsIgnoreCase(name));
        if (ok) save();
        return ok;
    }

    /** Офлайн-логин: меняет сессию клиента (для пираток/локалки). Возвращает текст результата. */
    public String login(String name) {
        if (name == null || name.isBlank()) return "Empty name";
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return "No client";
        try {
            UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            Session session = new Session(name, uuid, "", Optional.empty(), Optional.empty());
            ((MinecraftClientAccessor) mc).veyron$setSession(session);
            this.current = name;
            return "Logged in as " + name + " (offline)";
        } catch (Throwable t) {
            LOGGER.warn("login failed", t);
            return "Login failed: " + t.getMessage();
        }
    }

    private void load() {
        try {
            if (file != null && Files.exists(file)) {
                try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    Type type = new TypeToken<List<Account>>() {
                    }.getType();
                    List<Account> list = GSON.fromJson(r, type);
                    if (list != null) {
                        accounts.clear();
                        accounts.addAll(list);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("accounts load failed", e);
        }
    }

    private void save() {
        try {
            if (file != null) {
                Files.createDirectories(file.getParent());
                try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    GSON.toJson(accounts, w);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("accounts save failed", e);
        }
    }
}
