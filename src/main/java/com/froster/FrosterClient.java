package com.froster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
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
                if (client.currentScreen instanceof FrosterScreen froster) {
                    froster.close();
                } else {
                    client.setScreen(new FrosterScreen(client.currentScreen));
                }
            }
        });

        ScreenEvents.AFTER_INIT.register(
            (client, screen, scaledWidth, scaledHeight) -> {
                if (screen instanceof TitleScreen) {
                    screen.addDrawableChild(
                        ButtonWidget.builder(
                            Text.literal("FROSTER"),
                            button -> client.setScreen(
                                new FrosterScreen(screen)
                            )
                        ).dimensions(
                            8, scaledHeight - 30, 90, 20
                        ).build()
                    );
                }
            }
        );

        System.out.println("[Froster] Client initialized!");
    }

    public static class FrosterScreen extends Screen {
        private final Screen parent;

        private int panelX, panelY, panelW, panelH;
        private int cardW, cardH, gap;
        private int storageX, storageY;
        private boolean storageExpanded = false;

        public FrosterScreen(Screen parent) {
            super(Text.literal("Froster"));
            this.parent = parent;
        }

        private static Text toggleText(String name, boolean enabled) {
            return Text.literal(name + "   " + (enabled ? "ON" : "OFF"));
        }

        private void calculateLayout() {
            panelW = Math.max(220, Math.min(600, width - 20));
            panelH = Math.max(190, Math.min(
                storageExpanded ? 360 : 320, height - 12
            ));

            panelX = (width - panelW) / 2;
            panelY = (height - panelH) / 2;

            gap = 10;
            cardW = (panelW - 42) / 2;
            cardH = 76;

            storageX = panelX + 16 + cardW + gap;
            storageY = panelY + 91;
        }

        @Override
        protected void init() {
            clearChildren();
            calculateLayout();

            int leftX = panelX + 16;
            int rightX = leftX + cardW + gap;
            int firstY = panelY + 91;
            int secondY = firstY + cardH + gap;
            int buttonH = 25;

            addDrawableChild(ButtonWidget.builder(
                toggleText("AMETHYST ESP", amethystEsp),
                button -> {
                    amethystEsp = !amethystEsp;
                    button.setMessage(toggleText("AMETHYST ESP", amethystEsp));
                }
            ).dimensions(
                leftX + 7, firstY + 43, cardW - 14, buttonH
            ).build());

            addDrawableChild(ButtonWidget.builder(
                toggleText("STORAGE ESP", storageEsp),
                button -> {
                    storageEsp = !storageEsp;
                    button.setMessage(toggleText("STORAGE ESP", storageEsp));
                }
            ).dimensions(
                rightX + 7, firstY + 43, cardW - 14, buttonH
            ).build());

            if (storageExpanded) {
                addDrawableChild(ButtonWidget.builder(
                    toggleText("SPAWNER ESP", spawnerEsp),
                    button -> {
                        spawnerEsp = !spawnerEsp;
                        button.setMessage(toggleText("SPAWNER ESP", spawnerEsp));
                    }
                ).dimensions(
                    rightX + 7, secondY + 43, cardW - 14, buttonH
                ).build());
            } else {
                addDrawableChild(ButtonWidget.builder(
                    Text.literal("MORE MODULES SOON"),
                    button -> {}
                ).dimensions(
                    leftX + 7, secondY + 43, cardW - 14, buttonH
                ).build());

                addDrawableChild(ButtonWidget.builder(
                    Text.literal("MODULE SLOT"),
                    button -> {}
                ).dimensions(
                    rightX + 7, secondY + 43, cardW - 14, buttonH
                ).build());
            }
        }

        @Override
        public void close() {
            if (client != null) {
                client.setScreen(parent);
            }
        }

        private void drawBorder(
            DrawContext context, int x, int y,
            int w, int h, int color
        ) {
            context.fill(x, y, x + w, y + 1, color);
            context.fill(x, y + h - 1, x + w, y + h, color);
            context.fill(x, y, x + 1, y + h, color);
            context.fill(x + w - 1, y, x + w, y + h, color);
        }

        private void drawCard(
            DrawContext context, int x, int y,
            String title, String subtitle,
            boolean enabled, boolean selected
        ) {
            context.fill(x, y, x + cardW, y + cardH, CARD);
            drawBorder(
                context, x, y, cardW, cardH,
                selected ? ICE : BORDER
            );
            context.fill(x + 1, y + 1, x + 3, y + cardH - 1, ICE);

            context.drawTextWithShadow(
                textRenderer, Text.literal(title),
                x + 10, y + 8, WHITE
            );
            context.drawTextWithShadow(
                textRenderer, Text.literal(subtitle),
                x + 10, y + 23, MUTED
            );
            context.fill(
                x + cardW - 29, y + 9,
                x + cardW - 11, y + 14,
                enabled ? ON : OFF
            );
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            double mouseX = click.x();
            double mouseY = click.y();

            if (click.button() == 1
                && mouseX >= storageX
                && mouseX < storageX + cardW
                && mouseY >= storageY
                && mouseY < storageY + cardH) {
                storageExpanded = !storageExpanded;
                init();
                return true;
            }

            return super.mouseClicked(click, doubled);
        }

        @Override
        public void render(
            DrawContext context, int mouseX,
            int mouseY, float delta
        ) {
            // Custom background: no blur calls.
            context.fill(0, 0, width, height, 0xB900060D);
            context.fill(
                panelX, panelY,
                panelX + panelW, panelY + panelH, PANEL
            );
            drawBorder(
                context, panelX, panelY,
                panelW, panelH, ICE
            );
            context.fill(
                panelX + 1, panelY + 1,
                panelX + panelW - 1, panelY + 3, ICE
            );

            context.drawTextWithShadow(
                textRenderer, Text.literal("F R O S T E R"),
                panelX + 16, panelY + 14, ICE
            );
            context.drawTextWithShadow(
                textRenderer, Text.literal("CLIENT MODULES"),
                panelX + 16, panelY + 32, WHITE
            );
            context.drawTextWithShadow(
                textRenderer,
                Text.literal("ICE THEME / RIGHT SHIFT TO CLOSE"),
                panelX + 16, panelY + 61, MUTED
            );

            int leftX = panelX + 16;
            int firstY = panelY + 91;
            int secondY = firstY + cardH + gap;

            drawCard(
                context, leftX, firstY,
                "AMETHYST ESP", "Ore highlighting",
                amethystEsp, false
            );
            drawCard(
                context, storageX, storageY,
                "STORAGE ESP",
                storageExpanded
                    ? "Right-click to collapse"
                    : "Right-click for Spawner ESP",
                storageEsp, storageExpanded
            );

            if (storageExpanded) {
                drawCard(
                    context, storageX, secondY,
                    "SPAWNER ESP", "Spawner highlighting",
                    spawnerEsp, false
                );
            } else {
                drawCard(
                    context, leftX, secondY,
                    "MODULE SLOT 04", "Future module slot",
                    false, false
                );
                drawCard(
                    context, storageX, secondY,
                    "MORE MODULES", "Ready for expansion",
                    false, false
                );
            }

            context.drawTextWithShadow(
                textRenderer,
                Text.literal("RIGHT SHIFT  •  OPEN / CLOSE"),
                panelX + 16, panelY + panelH - 18, MUTED
            );

            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}



