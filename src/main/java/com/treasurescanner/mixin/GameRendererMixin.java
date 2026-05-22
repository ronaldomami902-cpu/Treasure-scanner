package com.treasurescanner.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // Mixin gerekli ama bu modda özel bir şey inject etmiyoruz
    // WorldRenderEvents ile hallediyoruz
}
