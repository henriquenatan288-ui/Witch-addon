package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class PitchLock extends Move {
    private final Setting<Integer> pitch = intSet("pitch", "Angulo vertical fixo (-90 a 90).", 0, -90, 90);

    public PitchLock() { super("pitch-lock", "Trava a direcao vertical."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p != null) p.setXRot(pitch.get());
    }
}
