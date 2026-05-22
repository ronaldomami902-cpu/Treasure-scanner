package com.treasurescanner;

import com.treasurescanner.scanner.BlockScanner;
import com.treasurescanner.renderer.ESPRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TreasureScannerMod implements ClientModInitializer {

    public static final String MOD_ID = "treasurescanner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Mod açık/kapalı (R tuşu ile toggle)
    public static boolean enabled = false;

    // Taranan bloklar listesi
    public static List<BlockScanner.FoundBlock> foundBlocks = List.of();

    // Her kaç tick'te bir tarama yapılsın (20 tick = 1 saniye)
    private static final int SCAN_INTERVAL = 40; // 2 saniyede bir
    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Treasure Scanner başlatıldı! R tuşuna bas açmak için.");

        // Her tick'te çalışır
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // R tuşu kontrolü - KeyBinding yerine basit kontrol
            // Not: Gerçek key binding için ayrı kayıt gerekir, bu basit versiyon
            
            tickCounter++;
            if (enabled && tickCounter >= SCAN_INTERVAL) {
                tickCounter = 0;
                // Arka planda tara
                BlockPos playerPos = client.player.getBlockPos();
                foundBlocks = BlockScanner.scan(client.world, playerPos);
                LOGGER.info("Tarama tamamlandı: {} blok bulundu", foundBlocks.size());
            }
        });

        // Dünya render edilirken ESP çiz
        WorldRenderEvents.LAST.register(context -> {
            if (!enabled || foundBlocks.isEmpty()) return;
            if (MinecraftClient.getInstance().player == null) return;
            ESPRenderer.render(context, foundBlocks);
        });

        // Keyboard input eventi - R tuşu
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
        });

        registerKeyBinding();
    }

    private void registerKeyBinding() {
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(
            new net.minecraft.client.option.KeyBinding(
                "key.treasurescanner.toggle",
                net.minecraft.client.util.InputUtil.Type.KEYSYM,
                org.lwjgl.glfw.GLFW.GLFW_KEY_R,
                "category.treasurescanner"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            net.minecraft.client.option.KeyBinding[] keyBindings = client.options.allKeys;
            for (net.minecraft.client.option.KeyBinding kb : keyBindings) {
                if (kb.getTranslationKey().equals("key.treasurescanner.toggle")) {
                    while (kb.wasPressed()) {
                        enabled = !enabled;
                        String status = enabled ? "§aAÇIK" : "§cKAPALI";
                        client.player.sendMessage(
                            net.minecraft.text.Text.literal("§6[TreasureScanner] §r" + status),
                            true
                        );
                        if (!enabled) {
                            foundBlocks = List.of();
                        } else {
                            // Hemen bir tarama yap
                            tickCounter = SCAN_INTERVAL;
                        }
                    }
                    break;
                }
            }
        });
    }
}
