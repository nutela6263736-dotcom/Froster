
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
        public FrosterScreen() {
            super(Text.literal("Froster"));
        }

        @Override
        protected void init() {
            int center = width / 2;
            int top = height / 2 - 65;

            addDrawableChild(ButtonWidget.builder(
                toggleText("Amethyst ESP", amethystEsp),
                button -> {
                    amethystEsp = !amethystEsp;
                    button.setMessage(toggleText("Amethyst ESP", amethystEsp));
                }
            ).dimensions(center - 100, top, 200, 20).build());

            addDrawableChild(ButtonWidget.builder(
                toggleText("Storage ESP", storageEsp),
                button -> {
                    storageEsp = !storageEsp;
                    button.setMessage(toggleText("Storage ESP", storageEsp));
                }
            ).dimensions(center - 100, top + 25, 200, 20).build());

            addDrawableChild(ButtonWidget.builder(
                toggleText("Spawner ESP", spawnerEsp),
                button -> {
                    spawnerEsp = !spawnerEsp;
                    button.setMessage(toggleText("Spawner ESP", spawnerEsp));
                }
            ).dimensions(center - 100, top + 50, 200, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                button -> close()
            ).dimensions(center - 100, top + 80, 200, 20).build());
        }

        private static Text toggleText(String name, boolean enabled) {
            return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
        }

        @Override
        public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
        ) {
            // Draw a plain background; don't trigger Minecraft's blur again.
            context.fill(0, 0, width, height, 0xE0101420);
            context.drawCenteredTextWithShadow(
                textRenderer,
                "FROSTER",
                width / 2,
                height / 2 - 90,
                0x55FFFF
            );
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() {
            return false;
        }
    }
}
