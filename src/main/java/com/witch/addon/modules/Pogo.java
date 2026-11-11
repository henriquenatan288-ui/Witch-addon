package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Pogo extends Move {
    private final Setting<Integer> height = intSet("height", "Multiplicador da altura do pulo.", 2, 1, 10);

    public Pogo() { super("pogo", "Segurando pulo, voce quica mais alto."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || !p.onGround() || p.isInWater()) return;
        if (mc.options.keyJump.isDown()) {
            var v = p.getDeltaMovement();
            vel(p, v.x, 0.42 * height.get(), v.z);
        }
    }
}
