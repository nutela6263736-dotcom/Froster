package com.froster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private static final int CARD = 0xE8172A3B;
    private static final int BORDER = 0xFF29485B;
    private static final int ON = 0xFF65E6C1;
    private static final int OFF = 0xFF8193A1;

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
                if (client.player != null) {
                    client.setScreen(new FrosterScreen());
                }
            }
        });

        System.out.println("[Froster] Client initialized!");
    }

    public static class FrosterScreen extends Screen {

        private int panelX;
        private int panelY;
        private int panelW;
        private int panelH;
        private int cardW;
        private int cardH;
        private int gap;

        public FrosterScreen() {
            super(Text.literal("Froster"));
        }

        @Override
        protected void init() {
            clearChildren();

            panelW = Math.min(520, width - 24);
            panelW = Math.max(220, panelW);
            panelH = 300;

            panelX = (width - panelW) / 2;
            panelY = (height - panelH) / 2;

            gap = 10;
            int innerW = panelW - 32;
            cardW = (innerW - gap) / 2;
            cardH = 76;

            int leftX = panelX + 16;
            int rightX = leftX + cardW + gap;
            int firstY = panelY + 91;

            addDrawableChild(
                ButtonWidget.builder(
                    toggleText("AMETHYST ESP", amethystEsp),
                    button -> {
                        amethystEsp = !amethystEsp;
                        button.setMessage(
                            toggleText("AMETHYST ESP", amethystEsp)
                        );
                    }
                ).dimensions(
                    leftX + 7, firstY + 36, cardW - 14, 27
                ).build()
            );

            addDrawableChild(
                ButtonWidget.builder(
                    toggleText("STORAGE ESP", storageEsp),
                    button -> {
                        storageEsp = !storageEsp;
                        button.setMessage(
                            toggleText("STORAGE ESP", storageEsp)
                        );
                    }
                ).dimensions(
                    rightX + 7, firstY + 36, cardW - 14, 27
                ).build()
            );

            int secondY = firstY + cardH + gap;

            addDrawableChild(
                ButtonWidget.builder(
                    toggleText("SPAWNER ESP", spawnerEsp),
                    button -> {
                        spawnerEsp = !spawnerEsp;
                        button.setMessage(
                            toggleText("SPAWNER ESP", spawnerEsp)
                        );
                    }
                ).dimensions(
                    leftX + 7, secondY + 36, cardW - 14, 27
                ).build()
            );

            addDrawableChild(
                ButtonWidget.builder(
                    Text.literal("CLOSE MENU"),
                    button -> close()
                ).dimensions(
                    rightX + 7, secondY + 36, cardW - 14, 27
                ).build()
            );
        }

        private static Text toggleText(String name, boolean enabled) {
            return Text.literal(
                name + "  [" + (enabled ? "ON" : "OFF") + "]"
            );
        }

        private void drawBorder(
            DrawContext context,
            int x,
            int y,
            int w,
            int h,
            int color
        ) {
            context.fill(x, y, x + w, y + 1, color);
            context.fill(x, y + h - 1, x + w, y + h, color);
            context.fill(x, y, x + 1, y + h, color);
            context.fill(x + w - 1, y, x + w, y + h, color);
        }

        private void drawCard(
            DrawContext context,
            int x,
            int y,
            String title,
            String subtitle,
            boolean enabled
        ) {
            context.fill(
                x, y, x + cardW, y + cardH, CARD
            );

            drawBorder(
                context, x, y, cardW, cardH, BORDER
            );

            context.fill(
                x + 1, y + 1, x + 3, y + cardH - 1, ICE
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(title),
                x + 10,
                y + 9,
                WHITE
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal(subtitle),
                x + 10,
                y + 23,
                MUTED
            );

            context.fill(
                x + cardW - 31,
                y + 9,
                x + cardW - 11,
                y + 15,
                enabled ? ON : OFF
            );
        }

        @Override
        public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
        ) {
            // No renderBackground() or blur call: prevents the
            // "Can only blur once per frame" crash.
            context.fill(
                0, 0, width, height, 0xB900060D
            );

            context.fill(
                panelX, panelY,
                panelX + panelW, panelY + panelH,
                PANEL
            );

            drawBorder(
                context,
                panelX, panelY, panelW, panelH,
                ICE
            );

            // Header accent.
            context.fill(
                panelX + 1, panelY + 1,
                panelX + panelW - 1, panelY + 3,
                ICE
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal("F R O S T E R"),
                panelX + 16,
                panelY + 15,
                ICE
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal("CLIENT SETTINGS"),
                panelX + 16,
                panelY + 32,
                WHITE
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal("VISUALS  /  FEATURE GRID"),
                panelX + 16,
                panelY + 61,
                MUTED
            );

            int leftX = panelX + 16;
            int rightX = leftX + cardW + gap;
            int firstY = panelY + 91;
            int secondY = firstY + cardH + gap;

            drawCard(
                context, leftX, firstY,
                "AMETHYST", "Ore highlighting",
                amethystEsp
            );

            drawCard(
                context, rightX, firstY,
                "STORAGE", "Container highlighting",
                storageEsp
            );

            drawCard(
                context, leftX, secondY,
                "SPAWNERS", "Spawner highlighting",
                spawnerEsp
            );

            drawCard(
                context, rightX, secondY,
                "EXIT", "Close this menu",
                false
            );

            context.drawTextWithShadow(
                textRenderer,
                Text.literal("RIGHT SHIFT  •  TOGGLE MENU"),
                panelX + 16,
                panelY + panelH - 19,
                MUTED
            );

            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}



