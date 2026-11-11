package com.witch.addon;

import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;

/** So existe na branch visual-test: ajudantes para o teste automatico. */
public final class TestHooks {
    private TestHooks() {}

    public static void openMeteorGui() {
        Tabs.get().get(0).openScreen(GuiThemes.get());
    }

    public static void playIntro() {
        var mc = net.minecraft.client.Minecraft.getInstance();
        mc.gui.setScreen(new com.witch.addon.intro.IntroScreen(mc.gui.screen()));
    }

    public static void toggle(String name) {
        Module m = Modules.get().get(name);
        if (m != null && !m.isActive()) m.toggle();
        else WitchAddon.LOG.info("TEST toggle: modulo nao achado ou ja ativo: {}", name);
    }
}
