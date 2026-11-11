package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class LowGravity extends Move {
    private final Setting<Integer> strength = intSet("strength", "Forca da reducao de gravidade.", 4, 1, 10);

    public LowGravity() { super("low-gravity", "Gravidade menor durante a queda."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || !airborne(p)) return;
        var v = p.getDeltaMovement();
        if (v.y < 0) vel(p, v.x, v.y + 0.007 * strength.get(), v.z);
    }
}
