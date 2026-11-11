package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class FastFall extends Move {
    private final Setting<Integer> mult = intSet("multiplier", "Quanto acelera a queda.", 3, 1, 10);

    public FastFall() { super("fast-fall", "Cai mais rapido."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || !airborne(p)) return;
        var v = p.getDeltaMovement();
        if (v.y < -0.1) vel(p, v.x, Math.max(-3.0, v.y * (1.0 + 0.1 * mult.get())), v.z);
    }
}
