
package com.froster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

public class FrosterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[Froster] Client initialized!");

        ClientPlayConnectionEvents.JOIN.register(
            (handler, sender, client) -> {
                client.execute(() -> {
                    if (client.player != null) {
                        client.player.sendMessage(
                            Text.literal("§b[Froster] §fLoaded successfully!"),
                            false
                        );
                    }
                });
            }
        );
    }
}
