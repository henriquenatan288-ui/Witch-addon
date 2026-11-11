package com.witch.addon.intro;

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screens.TitleScreen;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Toca a animacao uma vez por execucao, na primeira vez que o menu inicial abre. */
public class IntroHook {
    private boolean played;

    @EventHandler
    private void onOpenScreen(OpenScreenEvent e) {
        if (played || !(e.screen instanceof TitleScreen)) return;
        played = true;
        TitleScreen title = (TitleScreen) e.screen;
        e.setCancelled(true);
        mc.execute(() -> mc.gui.setScreen(new IntroScreen(title)));
    }
}
