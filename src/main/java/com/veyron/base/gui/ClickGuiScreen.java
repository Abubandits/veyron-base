package com.veyron.base.gui;

import com.veyron.base.Veyron;
import com.veyron.base.font.FontManager;
import com.veyron.base.module.Category;
import com.veyron.base.module.Module;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * ClickGUI — 5 аккуратных зафиксированных колонок в 4K качестве:
 * <ul>
 *   <li>Combat (chevron v + sword)</li>
 *   <li>Movement (chevron v + runner)</li>
 *   <li>Visuals (chevron v + eye)</li>
 *   <li>Player (chevron v + badge)</li>
 *   <li>Misc (chevron v + wrench)</li>
 * </ul>
 *
 * Особенности:
 * <ul>
 *   <li>Полная фиксация — меню невозможно сдвинуть с места (закреплено строго по центру)</li>
 *   <li>Увеличенный удобный размер (125x310 px)</li>
 *   <li>4K 9-slice сглаживание карточек без пикселей</li>
 *   <li>Четкие иконки высокого разрешения из интернета</li>
 * </ul>
 */
public class ClickGuiScreen extends Screen {

    public static class CategoryPanel {
        public final Category category;
        public int x;
        public int y;
        public int width = 125;
        public int height = 310;
        public boolean collapsed = false;
        public int scroll = 0;

        public CategoryPanel(Category category) {
            this.category = category;
        }
    }

    private static final List<CategoryPanel> PANELS = new ArrayList<>();
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    // Увеличенные удобные размеры меню
    private static final int HEADER_H = 32;
    private static final int ROW_H = 22;

    private Module binding = null;

    public ClickGuiScreen() {
        super(Text.literal("Veyron"));
    }

    @Override
    protected void init() {
        if (PANELS.isEmpty()) {
            for (Category cat : Category.values()) {
                PANELS.add(new CategoryPanel(cat));
            }
        }

        if (lastWidth != this.width || lastHeight != this.height) {
            lastWidth = this.width;
            lastHeight = this.height;
            recalculatePositions();
        }
    }

    private void recalculatePositions() {
        int count = PANELS.size();
        if (count == 0) return;

        int gap = 12;
        int defaultPanelW = 125;
        int totalNeeded = defaultPanelW * count + gap * (count - 1); // 125*5 + 12*4 = 673

        int panelW = defaultPanelW;
        if (totalNeeded > this.width - 24) {
            panelW = Math.max(70, (this.width - 24 - gap * (count - 1)) / count);
        }

        int totalW = panelW * count + gap * (count - 1);
        int startX = (this.width - totalW) / 2;

        int panelH = Math.min(310, Math.max(180, this.height - 32));
        int startY = (this.height - panelH) / 2;

        for (int i = 0; i < count; i++) {
            CategoryPanel p = PANELS.get(i);
            p.x = startX + i * (panelW + gap);
            p.y = startY;
            p.width = panelW;
            p.height = panelH;
            p.collapsed = false;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ══════════════════════════════════════════════════════════════
    //  Render (4K 9-Slice — гладко, четко, 0 лагов)
    // ══════════════════════════════════════════════════════════════

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (CategoryPanel panel : PANELS) {
            renderPanel(ctx, panel, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderPanel(DrawContext ctx, CategoryPanel panel, int mx, int my) {
        int x = panel.x;
        int y = panel.y;
        int w = panel.width;
        int currentH = panel.collapsed ? HEADER_H : panel.height;

        // 1. Отрисовка 4K карточки через 9-slice (гладкие углы, шелковистая тень, тонкая рамка)
        RenderUtil.drawPanelCard(ctx, x, y, w, currentH);

        // 2. Шапка карточки
        renderHeader(ctx, panel, mx, my);

        // 3. Тело с модулями (если не свернуто)
        if (!panel.collapsed) {
            renderBody(ctx, panel, mx, my);
        }
    }

    private void renderHeader(DrawContext ctx, CategoryPanel panel, int mx, int my) {
        int x = panel.x;
        int y = panel.y;
        int w = panel.width;

        // Разделитель под шапкой с отступами по краям
        if (!panel.collapsed) {
            RenderUtil.rect(ctx, x + 8, y + HEADER_H - 1, w - 16, 1, 0x18FFFFFF);
        }

        // Шеврон вниз слева
        RenderUtil.drawTextureIcon(ctx, RenderUtil.ICON_CHEVRON, x + 10, y + 12, 9, 9);

        // Название категории белым шрифтом
        String title = panel.category.getDisplayName();
        FontManager.draw(ctx, title, x + 24, y + 11, 0xFFFFFFFF, false);

        // 4K PNG иконка из интернета справа
        Identifier icon = switch (panel.category) {
            case COMBAT -> RenderUtil.ICON_COMBAT;
            case MOVEMENT -> RenderUtil.ICON_MOVEMENT;
            case RENDER -> RenderUtil.ICON_VISUALS;
            case PLAYER -> RenderUtil.ICON_PLAYER;
            case MISC -> RenderUtil.ICON_MISC;
        };

        if (panel.category == Category.RENDER) {
            RenderUtil.drawTextureIcon(ctx, icon, x + w - 28, y + 10, 18, 13);
        } else {
            int iconSize = 16;
            RenderUtil.drawTextureIcon(ctx, icon, x + w - iconSize - 12, y + 8, iconSize, iconSize);
        }
    }

    private void renderBody(DrawContext ctx, CategoryPanel panel, int mx, int my) {
        List<Module> modules = Veyron.getInstance() != null && Veyron.getInstance().getModuleManager() != null
                ? Veyron.getInstance().getModuleManager().getByCategory(panel.category)
                : List.of();

        if (modules.isEmpty()) return;

        int bodyX = panel.x;
        int bodyY = panel.y + HEADER_H;
        int bodyW = panel.width;
        int bodyH = panel.height - HEADER_H;

        ctx.enableScissor(bodyX, bodyY, bodyX + bodyW, bodyY + bodyH - 4);

        int ry = bodyY + 4 - panel.scroll;
        for (Module module : modules) {
            if (ry + ROW_H > bodyY && ry < bodyY + bodyH) {
                boolean hovered = RenderUtil.hovered(mx, my, bodyX + 3, ry, bodyW - 6, ROW_H);

                if (hovered) {
                    RenderUtil.roundedRect(ctx, bodyX + 4, ry, bodyW - 8, ROW_H, 4, 0x14FFFFFF);
                }

                int nameColor = module.isEnabled() ? 0xFFFFFFFF : 0xFF8E8E9C;
                FontManager.draw(ctx, module.getName(), bodyX + 8, ry + 6, nameColor, false);

                if (binding == module) {
                    FontManager.draw(ctx, "[...]", bodyX + bodyW - 30, ry + 6, Theme.accent, false);
                } else if (module.getKey() != GLFW.GLFW_KEY_UNKNOWN && module.getKey() > 0) {
                    String kn = getKeyName(module.getKey());
                    int kw = FontManager.getWidth("[" + kn + "]");
                    FontManager.draw(ctx, "[" + kn + "]", bodyX + bodyW - 8 - kw, ry + 6, 0xFF6C6C7C, false);
                } else if (module.isEnabled()) {
                    RenderUtil.roundedRect(ctx, bodyX + bodyW - 16, ry + 8, 6, 6, 2, Theme.accent);
                }
            }
            ry += ROW_H;
        }

        ctx.disableScissor();
    }

    // ══════════════════════════════════════════════════════════════
    //  Input — Mouse (Перемещение панелей отключено)
    // ══════════════════════════════════════════════════════════════

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (super.mouseClicked(click, doubled)) return true;

        int mx = (int) click.x();
        int my = (int) click.y();
        int button = click.button();

        for (int i = PANELS.size() - 1; i >= 0; i--) {
            CategoryPanel panel = PANELS.get(i);
            int currentH = panel.collapsed ? HEADER_H : panel.height;

            if (RenderUtil.hovered(mx, my, panel.x, panel.y, panel.width, currentH)) {
                // Перемещение отключено — панели зафиксированы

                // Клик по модулям внутри панели
                if (!panel.collapsed && my >= panel.y + HEADER_H) {
                    if (handleBodyClick(panel, mx, my, button)) {
                        return true;
                    }
                }

                return true;
            }
        }

        return false;
    }

    private boolean handleBodyClick(CategoryPanel panel, int mx, int my, int button) {
        List<Module> modules = Veyron.getInstance() != null && Veyron.getInstance().getModuleManager() != null
                ? Veyron.getInstance().getModuleManager().getByCategory(panel.category)
                : List.of();

        if (modules.isEmpty()) return false;

        int bodyY = panel.y + HEADER_H;
        int ry = bodyY + 4 - panel.scroll;

        for (Module m : modules) {
            if (RenderUtil.hovered(mx, my, panel.x + 4, ry, panel.width - 8, ROW_H)) {
                if (button == 0) {
                    if (binding == m) {
                        binding = null;
                    } else {
                        m.toggle();
                    }
                    return true;
                } else if (button == 1) {
                    binding = (binding == m) ? null : m;
                    return true;
                } else if (button == 2) {
                    m.setKey(GLFW.GLFW_KEY_UNKNOWN);
                    return true;
                }
            }
            ry += ROW_H;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int mx = (int) mouseX;
        int my = (int) mouseY;

        for (CategoryPanel panel : PANELS) {
            if (!panel.collapsed && RenderUtil.hovered(mx, my, panel.x, panel.y + HEADER_H, panel.width, panel.height - HEADER_H)) {
                List<Module> mods = Veyron.getInstance() != null && Veyron.getInstance().getModuleManager() != null
                        ? Veyron.getInstance().getModuleManager().getByCategory(panel.category)
                        : List.of();
                int totalH = mods.size() * ROW_H;
                int maxScroll = Math.max(0, totalH - (panel.height - HEADER_H - 8));
                panel.scroll = Math.max(0, Math.min(maxScroll, panel.scroll - (int) (verticalAmount * 16)));
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key();

        if (binding != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                binding.setKey(GLFW.GLFW_KEY_UNKNOWN);
            } else {
                binding.setKey(keyCode);
            }
            binding = null;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }

        return super.keyPressed(input);
    }

    private static String getKeyName(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN || key <= 0) return "NONE";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null && !name.isBlank()) return name.toUpperCase();
        return switch (key) {
            case GLFW.GLFW_KEY_SPACE -> "SPC";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            default -> "K" + key;
        };
    }
}
