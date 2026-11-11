package com.witch.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ArmorWarning extends Move {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final Setting<Integer> percent = intSet("percent", "Avisa quando a durabilidade restante for menor que isso (%).", 15, 1, 90);
    private int cooldown;

    public ArmorWarning() { super("armor-warning", "Avisa no chat quando uma peca de armadura esta quase quebrando."); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = mc.player;
        if (p == null) return;
        if (cooldown > 0) { cooldown--; return; }
        for (EquipmentSlot s : SLOTS) {
            ItemStack st = p.getItemBySlot(s);
            if (st.isEmpty() || !st.isDamageableItem()) continue;
            int left = st.getMaxDamage() - st.getDamageValue();
            if (left * 100 < st.getMaxDamage() * percent.get()) {
                warning("Armadura quase quebrando: %s (%d%%)", st.getHoverName().getString(), left * 100 / st.getMaxDamage());
                cooldown = 200;
                return;
            }
        }
    }
}
