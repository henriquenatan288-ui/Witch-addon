package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Levitate extends Move {
    private final Setting<Integer> speed = intSet("speed", "Velocidade de subida.", 3, 1, 10);

    public Levitate() { super("levitate", "Segurando pulo no ar, voce sobe devagar."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || p.onGround() || p.isFallFlying() || !mc.options.keyJump.isDown()) return;
        var v = p.getDeltaMovement();
        vel(p, v.x, 0.03 * speed.get(), v.z);
        p.fallDistance = 0;
    }
}
