package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.player.LocalPlayer;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Base dos modulos de movimento simples (fisica do cliente). */
abstract class Move extends Module {
    protected final SettingGroup sg = settings.getDefaultGroup();

    Move(String name, String desc) {
        super(WitchAddon.CATEGORY, name, desc);
    }

    protected Setting<Integer> intSet(String name, String desc, int def, int min, int max) {
        return sg.add(new IntSetting.Builder().name(name).description(desc).defaultValue(def).range(min, max).sliderRange(min, max).build());
    }

    /** Jogador usavel (vivo, fora de espectador, sem tela aberta) ou null. */
    protected LocalPlayer me() {
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null || p.isSpectator() || p.isDeadOrDying() || mc.gui.screen() != null) return null;
        return p;
    }

    protected static boolean airborne(LocalPlayer p) {
        return !p.onGround() && !p.isInWater() && !p.isInLava() && !p.isFallFlying();
    }

    protected static void vel(LocalPlayer p, double x, double y, double z) {
        p.setDeltaMovement(x, y, z);
    }
}
