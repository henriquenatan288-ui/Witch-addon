package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ToggleSneak extends Move {
    public ToggleSneak() { super("toggle-sneak", "Fica agachado sem precisar segurar a tecla."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (mc.player != null) mc.options.keyShift.setDown(true);
    }

    @Override
    public void onDeactivate() {
        mc.options.keyShift.setDown(false);
    }
}
