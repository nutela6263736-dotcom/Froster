
package com.froster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.text.Text;

public class FrosterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[Froster] Loaded successfully!");

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if (client.player != null) {
                client.player.sendMessage(
                    Text.literal("§b[Froster] §fLoaded successfully!"),
                    false
                );
            }
        });
    }
}
