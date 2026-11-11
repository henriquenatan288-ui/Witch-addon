package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class YawLock extends Move {
    private final Setting<Integer> step = intSet("step", "Graus por passo (a direcao gruda no multiplo mais proximo).", 45, 15, 90);

    public YawLock() { super("yaw-lock", "Trava a direcao horizontal em multiplos de um angulo."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null) return;
        float s = step.get();
        p.setYRot(Math.round(p.getYRot() / s) * s);
    }
}
