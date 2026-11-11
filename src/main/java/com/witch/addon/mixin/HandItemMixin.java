package com.witch.addon.mixin;

import com.witch.addon.modules.HeldItemGlow;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Passa a cor de contorno (5o argumento) ao desenhar o item na mao em primeira pessoa. */
@Mixin(ItemInHandRenderer.class)
public abstract class HandItemMixin {
    @ModifyArg(method = "renderItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"),
        index = 4, require = 0)
    private int witch$outline(int original) {
        int c = HeldItemGlow.handOutline();
        return c != 0 ? c : original;
    }
}
