package com.witch.addon.intro;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Toca a animacao uma vez por execucao, na primeira vez que o menu inicial abre. */
public class IntroHook {
    private boolean played;

    @EventHandler
    private void onOpenScreen(OpenScreenEvent e) {
        if (played || !(e.screen instanceof TitleScreen title)) return;
        played = true;
        WitchAddon.LOG.info("Intro: menu inicial detectado (evento), tocando animacao");
        e.setCancelled(true);
        mc.execute(() -> mc.gui.setScreen(new IntroScreen(title)));
    }

    /** Plano B: se o evento nao pegou (menu aberto antes do addon ouvir), pega pelo tick. */
    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (played) return;
        Screen cur = mc.gui.screen();
        if (cur instanceof TitleScreen title) {
            played = true;
            WitchAddon.LOG.info("Intro: menu inicial detectado (tick), tocando animacao");
            mc.gui.setScreen(new IntroScreen(title));
        }
    }
}
