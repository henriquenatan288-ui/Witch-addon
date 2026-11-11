package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WallJump extends Move {
    private boolean lastJump;

    public WallJump() { super("wall-jump", "Pula de novo ao encostar numa parede no ar."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null) return;
        boolean jump = mc.options.keyJump.isDown();
        if (jump && !lastJump && p.horizontalCollision && !p.onGround()) {
            var v = p.getDeltaMovement();
            vel(p, v.x, 0.42, v.z);
            p.fallDistance = 0;
        }
        lastJump = jump;
    }
}
