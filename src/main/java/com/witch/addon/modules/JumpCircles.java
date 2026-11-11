package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Anel que cresce e some no chao onde voce pulou. */
public class JumpCircles extends Module {
    private static final int LIFE = 20;

    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Double> radius = sg.add(new DoubleSetting.Builder()
        .name("radius").description("Raio final do anel.").defaultValue(1.6).range(0.5, 4).sliderRange(0.5, 4).build());
    private final Setting<SettingColor> color = sg.add(new ColorSetting.Builder()
        .name("color").description("Cor do anel.").defaultValue(new SettingColor(47, 123, 255, 255)).build());

    private record Ring(double x, double y, double z, int born) {}

    private final List<Ring> rings = new ArrayList<>();
    private boolean wasGround = true;
    private int ticks;

    public JumpCircles() {
        super(WitchAddon.CATEGORY, "jump-circles", "Desenha um anel no chao quando voce pula.");
    }

    @Override
    public void onDeactivate() { rings.clear(); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        ticks++;
        var p = mc.player;
        if (p == null) { rings.clear(); return; }
        boolean g = p.onGround();
        if (wasGround && !g && p.getDeltaMovement().y > 0.1) rings.add(new Ring(p.getX(), p.getY() + 0.02, p.getZ(), ticks));
        wasGround = g;
        rings.removeIf(r -> ticks - r.born > LIFE);
    }

    @EventHandler
    private void onRender(Render3DEvent e) {
        for (Ring r : rings) {
            double t = Math.min(1.0, (ticks - r.born) / (double) LIFE);
            double rad = radius.get() * t;
            int a = (int) (color.get().a * (1.0 - t));
            Color c = new Color(color.get().r, color.get().g, color.get().b, Math.max(0, a));
            int seg = 32;
            for (int i = 0; i < seg; i++) {
                double a0 = Math.PI * 2 * i / seg, a1 = Math.PI * 2 * (i + 1) / seg;
                e.renderer.line(r.x + Math.cos(a0) * rad, r.y, r.z + Math.sin(a0) * rad,
                    r.x + Math.cos(a1) * rad, r.y, r.z + Math.sin(a1) * rad, c);
            }
        }
    }
}
