package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Marca no mundo de onde veio cada som (passos, blocos, mobs), por alguns segundos. */
public class SoundEsp extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> range = sg.add(new IntSetting.Builder()
        .name("range").description("Alcance em blocos.").defaultValue(48).range(8, 128).sliderRange(8, 128).build());
    private final Setting<Integer> duration = sg.add(new IntSetting.Builder()
        .name("duration").description("Quanto tempo a marca fica (ms).").defaultValue(2500).range(500, 8000).sliderRange(500, 8000).build());
    private final Setting<Boolean> steps = sg.add(new BoolSetting.Builder()
        .name("steps").description("Incluir passos.").defaultValue(true).build());
    private final Setting<Boolean> names = sg.add(new BoolSetting.Builder()
        .name("names").description("Mostrar o nome do som.").defaultValue(true).build());
    private final Setting<SettingColor> side = sg.add(new ColorSetting.Builder()
        .name("side-color").description("Preenchimento.").defaultValue(new SettingColor(96, 146, 232, 70)).build());
    private final Setting<SettingColor> line = sg.add(new ColorSetting.Builder()
        .name("line-color").description("Linhas.").defaultValue(new SettingColor(96, 146, 232, 255)).build());

    private record Mark(double x, double y, double z, String name, long born) {}

    private final List<Mark> marks = new ArrayList<>();
    private final Vector3d pos = new Vector3d();

    public SoundEsp() {
        super(WitchAddon.CATEGORY, "sound-esp", "Marca de onde vieram os sons ao redor.");
    }

    @Override
    public void onDeactivate() { marks.clear(); }

    @EventHandler
    private void onSound(PlaySoundEvent e) {
        SoundInstance s = e.sound;
        if (mc.player == null || s == null) return;
        String id = s.getIdentifier().getPath();
        if (!steps.get() && id.contains("step")) return;
        double dx = s.getX() - mc.player.getX(), dy = s.getY() - mc.player.getY(), dz = s.getZ() - mc.player.getZ();
        double d2 = dx * dx + dy * dy + dz * dz;
        if (d2 > (double) range.get() * range.get() || d2 < 1.5) return;   // ignora o que sai de voce
        String[] parts = id.split("\\.");
        String shortName = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : id;
        synchronized (marks) {
            marks.add(new Mark(s.getX(), s.getY(), s.getZ(), shortName, System.currentTimeMillis()));
            if (marks.size() > 80) marks.remove(0);
        }
    }

    private List<Mark> live() {
        long now = System.currentTimeMillis();
        synchronized (marks) {
            marks.removeIf(m -> now - m.born > duration.get());
            return new ArrayList<>(marks);
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent e) {
        long now = System.currentTimeMillis();
        for (Mark m : live()) {
            double u = (now - m.born) / (double) duration.get();
            double k = 1 - u;
            double h = 0.18 + 0.1 * u;
            Color sc = new Color(side.get().r, side.get().g, side.get().b, (int) (side.get().a * k));
            Color lc = new Color(line.get().r, line.get().g, line.get().b, (int) (line.get().a * k));
            e.renderer.box(m.x - h, m.y - h, m.z - h, m.x + h, m.y + h, m.z + h, sc, lc, ShapeMode.Both, 0);
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent e) {
        if (!names.get()) return;
        long now = System.currentTimeMillis();
        for (Mark m : live()) {
            double k = 1 - (now - m.born) / (double) duration.get();
            pos.set(m.x, m.y + 0.35, m.z);
            Label.draw(e.graphics, pos, 1.0, m.name, new Color(235, 242, 255, (int) (255 * Math.max(0, k))), false);
        }
    }
}
