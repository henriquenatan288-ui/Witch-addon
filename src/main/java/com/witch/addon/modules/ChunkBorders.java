package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Linhas nas bordas do chunk atual (cantos verticais e quadrados a cada 16 blocos). */
public class ChunkBorders extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> height = sg.add(new IntSetting.Builder()
        .name("height").description("Quantos blocos acima e abaixo de voce desenhar.").defaultValue(32).range(16, 128).sliderRange(16, 128).build());
    private final Setting<SettingColor> color = sg.add(new ColorSetting.Builder()
        .name("color").description("Cor das linhas.").defaultValue(new SettingColor(47, 123, 255, 255)).build());

    public ChunkBorders() {
        super(WitchAddon.CATEGORY, "chunk-borders", "Mostra as bordas do chunk em que voce esta.");
    }

    @EventHandler
    private void onRender(Render3DEvent e) {
        if (mc.player == null || mc.level == null) return;
        int cx = mc.player.blockPosition().getX() >> 4 << 4;
        int cz = mc.player.blockPosition().getZ() >> 4 << 4;
        int y = (int) Math.floor(mc.player.getY());
        int y0 = Math.max(mc.level.getMinY(), y - height.get());
        int y1 = Math.min(mc.level.getHeight(), y + height.get());
        double x0 = cx, x1 = cx + 16, z0 = cz, z1 = cz + 16;
        var c = color.get();
        // cantos verticais
        e.renderer.line(x0, y0, z0, x0, y1, z0, c);
        e.renderer.line(x1, y0, z0, x1, y1, z0, c);
        e.renderer.line(x0, y0, z1, x0, y1, z1, c);
        e.renderer.line(x1, y0, z1, x1, y1, z1, c);
        // quadrados a cada 16 blocos (alinhados ao mundo)
        for (int yy = (y0 + 15) >> 4 << 4; yy <= y1; yy += 16) {
            e.renderer.line(x0, yy, z0, x1, yy, z0, c);
            e.renderer.line(x1, yy, z0, x1, yy, z1, c);
            e.renderer.line(x1, yy, z1, x0, yy, z1, c);
            e.renderer.line(x0, yy, z1, x0, yy, z0, c);
        }
    }
}
