package com.witch.addon.mixin;

import com.witch.addon.ui.Round;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Barra do titulo da categoria: faixa azul com os cantos de cima redondos, por dentro do contorno. */
@Mixin(targets = "meteordevelopment.meteorclient.gui.themes.meteor.widgets.WMeteorWindow$WMeteorHeader")
public abstract class HeaderMixin {
    @Inject(method = "onRender", at = @At("HEAD"), cancellable = true, require = 0)
    private void witch$render(GuiRenderer renderer, double mouseX, double mouseY, double delta, CallbackInfo ci) {
        WWidget w = (WWidget) (Object) this;
        Color c = GuiThemes.get() instanceof MeteorGuiTheme t ? t.accentColor.get() : Round.BLUE;
        Round.rect(renderer, w.x + 2, w.y + 2, w.width - 4, w.height - 2, 5, c, true, false);
        ci.cancel();
    }
}
