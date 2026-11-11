package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class Glide extends Move {
    private final Setting<Integer> fall = intSet("fall-speed", "Velocidade maxima de queda (menor = mais lento).", 3, 1, 10);

    public Glide() { super("glide", "Cai devagar, planando."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || !airborne(p)) return;
        var v = p.getDeltaMovement();
        double lim = -0.02 * fall.get();
        if (v.y < lim) vel(p, v.x, lim, v.z);
    }
}
