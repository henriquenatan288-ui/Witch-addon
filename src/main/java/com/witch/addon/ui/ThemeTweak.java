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
        // caixas brancas: texto escuro e modulo ativo em azul claro (se voce nao mexeu nas cores padrao)
        var tx = t.textColor.get();
        if ((tx.r == 255 && tx.g == 255 && tx.b == 255) || (tx.r == 15 && tx.g == 25 && tx.b == 60)) tx.set(38, 50, 72, 255);
        var mb = t.moduleBackground.get();
        if ((mb.r == 50 && mb.g == 50 && mb.b == 50) || (mb.r == 190 && mb.g == 214 && mb.b == 255)) mb.set(214, 227, 250, 255);
        // destaque: troca o roxo padrao do Meteor pelo azul do Witch (se voce ja escolheu outra cor, respeita)
        var acc = t.accentColor.get();
        if ((acc.r == 145 && acc.g == 61 && acc.b == 226) || (acc.r == 47 && acc.g == 123 && acc.b == 255)) acc.set(70, 120, 215, 255);
    }
}
