package com.witch.addon.mixin;

import com.witch.addon.ui.Round;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorWindow;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Caixa da categoria: branca, contorno azul, cantos redondos. */
@Mixin(WMeteorWindow.class)
public abstract class WindowMixin {
    @Inject(method = "onRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void witch$render(GuiRenderer renderer, double mouseX, double mouseY, double delta, CallbackInfo ci) {
        WWidget w = (WWidget) (Object) this;
        Round.box(renderer, w.x, w.y, w.width, w.height);
        ci.cancel();
    }
}
