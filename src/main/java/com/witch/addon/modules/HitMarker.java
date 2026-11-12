package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.LivingEntity;

/** Um X que pisca no centro da tela quando voce acerta um alvo. */
public class HitMarker extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> size = sg.add(new IntSetting.Builder()
        .name("size").description("Tamanho de cada braco do X (px).").defaultValue(7).range(3, 20).sliderRange(3, 20).build());
    private final Setting<Integer> duration = sg.add(new IntSetting.Builder()
        .name("duration").description("Duracao em ms.").defaultValue(260).range(100, 800).sliderRange(100, 800).build());
    private final Setting<SettingColor> color = sg.add(new ColorSetting.Builder()
        .name("color").description("Cor do X.").defaultValue(new SettingColor(255, 255, 255, 255)).build());

    private long hit = -100000;

    public HitMarker() {
        super(WitchAddon.CATEGORY, "hit-marker", "Mostra um X no centro da tela quando voce acerta alguem.");
    }

    @EventHandler
    private void onAttack(AttackEntityEvent e) {
        if (e.entity instanceof LivingEntity) hit = System.currentTimeMillis();
    }

    @EventHandler
    private void onRender2D(Render2DEvent e) {
        double u = (System.currentTimeMillis() - hit) / (double) duration.get();
        if (u < 0 || u >= 1) return;
        var c = color.get();
        int a = (int) Math.round(c.a * (1 - u * u));
        int col = (a << 24) | (c.r << 16) | (c.g << 8) | c.b;
        int cx = e.screenWidth / 2, cy = e.screenHeight / 2;
        int gap = 3 + (int) Math.round(u * 3), len = size.get();
        for (int i = gap; i < gap + len; i++) {
            e.graphics.fill(cx + i, cy + i, cx + i + 2, cy + i + 2, col);
            e.graphics.fill(cx - i - 1, cy + i, cx - i + 1, cy + i + 2, col);
            e.graphics.fill(cx + i, cy - i - 1, cx + i + 2, cy - i + 1, col);
            e.graphics.fill(cx - i - 1, cy - i - 1, cx - i + 1, cy - i + 1, col);
        }
    }
}
