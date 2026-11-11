package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import com.witch.addon.intro.IntroScreen;
import meteordevelopment.meteorclient.systems.modules.Module;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Ligar toca a animacao de entrada de novo (bom para testar sem reiniciar o jogo). */
public class Intro extends Module {
    public Intro() {
        super(WitchAddon.CATEGORY, "intro", "Toca a animacao de entrada do Witch (desliga sozinho).");
    }

    @Override
    public void onActivate() {
        toggle();
        mc.execute(() -> mc.gui.setScreen(new IntroScreen(mc.gui.screen())));
    }
}
