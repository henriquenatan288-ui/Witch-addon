package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import org.joml.Vector3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Marca o ponto da sua ultima morte com um feixe vertical e a distancia. */
public class DeathMarker extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> clearDist = sg.add(new IntSetting.Builder()
        .name("clear-distance").description("Apaga a marca quando voce chega a essa distancia (0 = nunca).").defaultValue(4).range(0, 20).sliderRange(0, 20).build());
    private final Setting<SettingColor> color = sg.add(new ColorSetting.Builder()
        .name("color").description("Cor do feixe.").defaultValue(new SettingColor(255, 85, 85, 255)).build());

    private boolean wasDead;
    private boolean has;
    private double mx, my, mz;
    private String dim = "";
    private final Vector3d pos = new Vector3d();

    public DeathMarker() {
        super(WitchAddon.CATEGORY, "death-marker", "Marca onde voce morreu pela ultima vez.");
    }

    private String curDim() {
        return mc.level == null ? "" : mc.level.dimension().toString();
    }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        var p = mc.player;
        if (p == null || mc.level == null) return;
        boolean dead = p.isDeadOrDying();
        if (dead && !wasDead) {
            has = true;
            mx = p.getX();
            my = p.getY();
            mz = p.getZ();
            dim = curDim();
            info("Voce morreu em %d %d %d", (int) mx, (int) my, (int) mz);
        }
        wasDead = dead;
        if (has && !dead && clearDist.get() > 0 && dim.equals(curDim())) {
            double dx = p.getX() - mx, dy = p.getY() - my, dz = p.getZ() - mz;
            if (dx * dx + dy * dy + dz * dz < (double) clearDist.get() * clearDist.get()) has = false;
        }
    }

    private boolean show() {
        return has && mc.player != null && !mc.player.isDeadOrDying() && dim.equals(curDim());
    }

    @EventHandler
    private void onRender3D(Render3DEvent e) {
        if (!show()) return;
        var c = color.get();
        e.renderer.line(mx, my, mz, mx, my + 200, mz, c);
        e.renderer.box(mx - 0.4, my, mz - 0.4, mx + 0.4, my + 1.8, mz + 0.4,
            new meteordevelopment.meteorclient.utils.render.color.Color(c.r, c.g, c.b, 50), c, ShapeMode.Both, 0);
    }

    @EventHandler
    private void onRender2D(Render2DEvent e) {
        if (!show()) return;
        double d = Math.sqrt(Math.pow(mc.player.getX() - mx, 2) + Math.pow(mc.player.getY() - my, 2) + Math.pow(mc.player.getZ() - mz, 2));
        pos.set(mx, my + 2.2, mz);
        Label.draw(e.graphics, pos, 1.2, String.format("Morte  %.0fm", d), color.get(), true);
    }
}
