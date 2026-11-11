package com.witch.test;

import com.witch.addon.TestHooks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/** Roda o jogo sem tela no CI com Meteor + addon e tira screenshots. */
public class AddonGametest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.waitTicks(40);
        context.takeScreenshot("a0-title");
        // animacao de entrada, chamada de forma deterministica
        context.runOnClient(mc -> TestHooks.playIntro());
        for (int i = 1; i <= 14; i++) {
            context.waitTicks(4);
            context.takeScreenshot("a" + (i < 10 ? "0" : "") + i + "-intro");
        }
        context.waitTicks(40);
        context.takeScreenshot("b-title");

        // Meteor GUI com caixas novas
        context.runOnClient(mc -> TestHooks.openMeteorGui());
        context.waitTicks(40);
        context.takeScreenshot("c-meteor-gui");
        context.runOnClient(mc -> mc.gui.setScreen(null));

        try (var sp = context.worldBuilder().create()) {
            sp.getClientLevel().waitForChunksRender();
            context.waitTicks(20);
            sp.getServer().runCommand("give @p diamond_sword");
            context.waitTicks(10);
            context.runOnClient(mc -> {
                TestHooks.toggle("held-item-glow");
                TestHooks.toggle("farm-esp");
                TestHooks.toggle("chunk-borders");
            });
            context.waitTicks(40);
            context.takeScreenshot("d-world-hud");
            context.runOnClient(mc -> {
                TestHooks.toggle("toggle-sneak");
                TestHooks.toggle("spinner");
                TestHooks.toggle("hotbar-cycle");
                TestHooks.toggle("glide");
                TestHooks.toggle("pitch-lock");
            });
            context.waitTicks(60);
            context.takeScreenshot("f-newmods");
            context.runOnClient(mc -> TestHooks.openMeteorGui());
            context.waitTicks(40);
            context.takeScreenshot("e-gui-in-world");
            context.runOnClient(mc -> mc.gui.setScreen(null));
            context.waitTicks(5);
        }
    }
}
