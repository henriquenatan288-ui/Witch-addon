package com.witch.addon.ui;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.orbit.EventHandler;

/**
 * Deixa o fundo das caixas de categoria preto semi-transparente.
 * Aplicado uma vez por execucao, quando o tema ja foi carregado (so no tema Meteor).
 */
public class ThemeTweak {
    private static final int ALPHA = 140;
    private boolean applied;

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (applied) return;
        if (!(GuiThemes.get() instanceof MeteorGuiTheme t)) return;
        applied = true;
        // o objeto devolvido e o valor vivo da configuracao
        t.backgroundColor.get().set(0, 0, 0, ALPHA);
        t.backgroundColor.get(false, true, true).set(0, 0, 0, ALPHA);
        t.backgroundColor.get(true, true, true).set(0, 0, 0, ALPHA);
        // destaque: troca o roxo padrao do Meteor pelo azul do Witch (se voce ja escolheu outra cor, respeita)
        var acc = t.accentColor.get();
        if (acc.r == 145 && acc.g == 61 && acc.b == 226) acc.set(47, 123, 255, 255);
    }
}
