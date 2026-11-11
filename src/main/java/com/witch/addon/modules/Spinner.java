package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class Spinner extends Move {
    private final Setting<Integer> speed = intSet("speed", "Graus por tick.", 20, 1, 90);

    public Spinner() { super("spinner", "Gira a sua camera horizontalmente sem parar."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p != null) p.setYRot(p.getYRot() + speed.get());
    }
}
