package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;

public class HotbarCycle extends Move {
    private final Setting<Integer> delay = intSet("delay", "Ticks entre cada troca de slot.", 20, 2, 100);
    private int t;

    public HotbarCycle() { super("hotbar-cycle", "Troca de slot da hotbar automaticamente, em ordem."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = me();
        if (p == null || ++t < delay.get()) return;
        t = 0;
        InvUtils.swap((p.getInventory().getSelectedSlot() + 1) % 9, false);
    }
}
