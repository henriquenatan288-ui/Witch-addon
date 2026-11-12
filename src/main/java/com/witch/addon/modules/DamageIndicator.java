package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Numeros de dano subindo e sumindo sobre quem leva dano por perto. */
public class DamageIndicator extends Module {
    private static final int LIFE_MS = 1100;

    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> range = sg.add(new IntSetting.Builder()
        .name("range").description("Alcance em blocos.").defaultValue(32).range(8, 64).sliderRange(8, 64).build());
    private final Setting<Double> scale = sg.add(new DoubleSetting.Builder()
        .name("scale").description("Tamanho do texto.").defaultValue(1.3).range(0.6, 3).sliderRange(0.6, 3).build());
    private final Setting<Boolean> self = sg.add(new BoolSetting.Builder()
        .name("self").description("Mostrar o seu proprio dano.").defaultValue(true).build());
    private final Setting<Boolean> heal = sg.add(new BoolSetting.Builder()
        .name("show-heal").description("Mostrar cura em verde.").defaultValue(false).build());

    private record Pop(double x, double y, double z, String text, boolean good, long born) {}

    private final Map<Integer, Float> last = new HashMap<>();
    private final List<Pop> pops = new ArrayList<>();
    private final Vector3d pos = new Vector3d();

    public DamageIndicator() {
        super(WitchAddon.CATEGORY, "damage-indicator", "Mostra numeros de dano flutuando sobre os alvos.");
    }

    @Override
    public void onDeactivate() {
        last.clear();
        pops.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (mc.player == null || mc.level == null) return;
        double r2 = (double) range.get() * range.get();
        long now = System.currentTimeMillis();
        for (Entity en : mc.level.entitiesForRendering()) {
            if (!(en instanceof LivingEntity le) || le.isRemoved()) continue;
            if (le == mc.player && !self.get()) continue;
            if (le.distanceToSqr(mc.player) > r2) continue;
            float hp = le.getHealth();
            Float prev = last.put(le.getId(), hp);
            if (prev == null || Math.abs(prev - hp) < 0.01f) continue;
            if (hp < prev) {
                pops.add(new Pop(le.getX(), le.getY() + le.getBbHeight() + 0.2, le.getZ(), String.format("-%.1f", prev - hp), false, now));
            } else if (heal.get()) {
                pops.add(new Pop(le.getX(), le.getY() + le.getBbHeight() + 0.2, le.getZ(), String.format("+%.1f", hp - prev), true, now));
            }
        }
        if (last.size() > 400) last.keySet().removeIf(id -> mc.level.getEntity(id) == null);
        pops.removeIf(p -> now - p.born > LIFE_MS);
        if (pops.size() > 60) pops.subList(0, pops.size() - 60).clear();
    }

    @EventHandler
    private void onRender2D(Render2DEvent e) {
        long now = System.currentTimeMillis();
        for (Pop p : pops) {
            double u = (now - p.born) / (double) LIFE_MS;
            if (u < 0 || u >= 1) continue;
            pos.set(p.x, p.y + u * 0.9, p.z);
            int a = (int) Math.round(255 * Math.min(1.0, (1 - u) * 1.6));
            Color c = p.good ? new Color(90, 220, 120, a) : new Color(255, 85, 85, a);
            Label.draw(e.graphics, pos, scale.get(), p.text, c, false);
        }
    }
}
