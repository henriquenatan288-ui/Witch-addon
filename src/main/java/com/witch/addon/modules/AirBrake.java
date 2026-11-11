package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AirBrake extends Move {
    public AirBrake() { super("air-brake", "Freia o movimento horizontal no ar quando voce solta as teclas."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || !airborne(p)) return;
        var o = mc.options;
        if (o.keyUp.isDown() || o.keyDown.isDown() || o.keyLeft.isDown() || o.keyRight.isDown()) return;
        var v = p.getDeltaMovement();
        vel(p, v.x * 0.5, v.y, v.z * 0.5);
    }
}
