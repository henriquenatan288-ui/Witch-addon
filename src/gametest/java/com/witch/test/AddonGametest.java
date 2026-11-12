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
        for (int i = 1; i <= 3; i++) {
            context.waitTicks(5);
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
            context.runOnClient(mc -> {
                TestHooks.toggle("hit-marker");
                TestHooks.toggle("damage-indicator");
                TestHooks.toggle("sound-esp");
                TestHooks.toggle("death-marker");
            });
            sp.getServer().runCommand("summon minecraft:zombie ~ ~ ~5 {NoAI:1b,PersistenceRequired:1b}");
            context.waitTicks(20);
            sp.getServer().runCommand("damage @e[type=zombie,limit=1] 6");
            sp.getServer().runCommand("playsound minecraft:block.chest.open block @a ~3 ~ ~4 1 1");
            context.runOnClient(mc -> TestHooks.attackZombie());
            context.waitTicks(2);
            context.takeScreenshot("g-damage-hit-sound");
            context.waitTicks(10);
            context.takeScreenshot("g2-damage-later");
            // morte: vai para 15 blocos ao norte, morre, renasce olhando para la
            sp.getServer().runCommand("tp @p ~ ~ ~-15");
            context.waitTicks(10);
            sp.getServer().runCommand("kill @p");
            context.waitTicks(20);
            context.runOnClient(mc -> TestHooks.respawn());
            context.waitTicks(40);
            sp.getServer().runCommand("tp @p ~ ~ ~ 180 0");
            context.waitTicks(20);
            context.takeScreenshot("h-death-marker");
        }
    }
}
