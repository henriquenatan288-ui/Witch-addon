package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LowHealthAlert extends Move {
    private final Setting<Integer> threshold = intSet("threshold", "Avisa quando a vida (em meios coracoes) ficar igual ou menor.", 6, 1, 19);
    private int cooldown;

    public LowHealthAlert() { super("low-health-alert", "Avisa no chat quando a vida esta baixa."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = mc.player;
        if (p == null || p.isDeadOrDying()) return;
        if (cooldown > 0) cooldown--;
        if (p.getHealth() <= threshold.get() && cooldown == 0) {
            warning("Vida baixa: %.1f", p.getHealth());
            cooldown = 100;
        }
    }
}
