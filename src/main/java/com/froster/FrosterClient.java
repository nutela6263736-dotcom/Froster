package com.froster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FrosterClient implements ClientModInitializer {

    public static boolean amethystEsp = false;
    public static boolean storageEsp = false;
    public static boolean spawnerEsp = false;

    private KeyBinding openGui;

    private static final int ICE = 0xFF76E8FF;
    private static final int WHITE = 0xFFEAFBFF;
    private static final int MUTED = 0xFF91AAB8;
    private static final int PANEL = 0xF00B1421;
    private static final int ROW = 0xE8172A3B;
    private static final int BORDER = 0xFF29485B;
    private static final int CHECK = 0xFF65E6C1;

    @Override
    public void onInitializeClient() {
        openGui = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.froster.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) {
                if (client.currentScreen instanceof FrosterScreen) {
                    client.setScreen(
                        client.currentScreen instanceof FrosterScreen
                            ? ((FrosterScreen) client.currentScreen).parent
                            : null
                    );
                } else {
                    client.setScreen(
                        new FrosterScreen(client.currentScreen)
                    );
                }
            }
        });

        System.out.println("[Froster] Client initialized!");
    }

    public static class FrosterScreen extends Screen {
        private final Screen parent;

        private int px, py, pw, ph;
        private int rowX, rowY, rowW;
        private int childY;
        private boolean expanded = false;

        public FrosterScreen(Screen parent) {
            super(Text.literal("Froster"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            pw = Math.min(440, width - 20);
            pw = Math.max(200, pw);
            ph = Math.min(330, height - 12);
            ph = Math.max(170, ph);

            px = (width - pw) / 2;
            py = (height - ph) / 2;

            rowX = px + 12;
            rowY = py + 76;
            rowW = pw - 24;
            childY = rowY + 43;
        }

        @Override
        public void close() {
            if (client != null) {
                client.setScreen(parent);
            }
        }

        private void border(
            DrawContext c, int x, int y,
            int w, int h, int color
        ) {
            c.fill(x, y, x + w, y + 1, color);
            c.fill(x, y + h - 1, x + w, y + h, color);
            c.fill(x, y, x + 1, y + h, color);
            c.fill(x + w - 1, y, x + w, y + h, color);
        }

        private void drawCheck(
            DrawContext c, int x, int y, boolean enabled
        ) {
            c.fill(x, y, x + 13, y + 13, 0xFF142A3B);
            border(c, x, y, 13, 13, enabled ? CHECK : BORDER);

            if (enabled) {
                c.drawText(
                    textRenderer,
                    Text.literal("✓"),
                    x + 2, y + 1, CHECK,
                    false
                );
            }
        }

        private void drawRow(
            DrawContext c, int x, int y, int w,
            String label, boolean enabled, boolean selected
        ) {
            c.fill(x, y, x + w, y + 29, ROW);
            border(c, x, y, w, 29, selected ? ICE : BORDER);

            c.drawTextWithShadow(
                textRenderer,
                Text.literal(label),
                x + 9, y + 10, WHITE
            );

            drawCheck(c, x + w - 23, y + 8, enabled);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            double mx = click.x();
            double my = click.y();

            // Right-click the Storage ESP parent to expand/collapse.
            if (mx >= rowX && mx < rowX + rowW
                && my >= rowY && my < rowY + 29
                && click.button() == 1) {
                expanded = !expanded;
                return true;
            }

            // Left-click Storage ESP toggles its own checkbox.
            if (mx >= rowX && mx < rowX + rowW
                && my >= rowY && my < rowY + 29
                && click.button() == 0) {
                storageEsp = !storageEsp;
                return true;
            }

            // Child modules are compact text rows.
            if (expanded && click.button() == 0) {
                if (mx >= rowX + 12 && mx < rowX + rowW
                    && my >= childY && my < childY + 25) {
                    amethystEsp = !amethystEsp;
                    return true;
                }

                if (mx >= rowX + 12 && mx < rowX + rowW
                    && my >= childY + 29
                    && my < childY + 54) {
                    spawnerEsp = !spawnerEsp;
                    return true;
                }
            }

            return super.mouseClicked(click, doubled);
        }

        @Override
        public void render(
            DrawContext c, int mouseX, int mouseY, float delta
        ) {
            // Plain custom background; no blur calls.
            c.fill(0, 0, width, height, 0xB900060D);
            c.fill(px, py, px + pw, py + ph, PANEL);
            border(c, px, py, pw, ph, ICE);
            c.fill(px + 1, py + 1, px + pw - 1, py + 3, ICE);

            c.drawTextWithShadow(
                textRenderer, Text.literal("F R O S T E R"),
                px + 14, py + 13, ICE
            );
            c.drawTextWithShadow(
                textRenderer, Text.literal("MODULES"),
                px + 14, py + 30, WHITE
            );
            c.drawTextWithShadow(
                textRenderer, Text.literal("RIGHT SHIFT  /  CLOSE"),
                px + 14, py + 49, MUTED
            );

            // Parent row.
            drawRow(
                c, rowX, rowY, rowW,
                (expanded ? "▾  " : "▸  ") + "Storage ESP",
                storageEsp, expanded
            );

            if (expanded) {
                drawRow(
                    c, rowX + 12, childY, rowW - 12,
                    "Amethyst ESP", amethystEsp, false
                );

                drawRow(
                    c, rowX + 12, childY + 29, rowW - 12,
                    "Spawner ESP", spawnerEsp, false
                );
            }

            c.drawTextWithShadow(
                textRenderer,
                Text.literal("ICE CLIENT  •  BUILDING FROSTER"),
                px + 14, py + ph - 18, MUTED
            );

            super.render(c, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}



