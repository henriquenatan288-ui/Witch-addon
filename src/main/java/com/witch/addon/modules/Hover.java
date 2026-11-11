package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Hover extends Move {
    public Hover() { super("hover", "Segurando pulo + agachar no ar, voce fica parado."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || p.onGround() || p.isFallFlying()) return;
        if (mc.options.keyShift.isDown() && mc.options.keyJump.isDown()) {
            var v = p.getDeltaMovement();
            vel(p, v.x, 0, v.z);
            p.fallDistance = 0;
        }
    }
}
