package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Caixa em Ancient Debris proximo, atraves de parede (varredura barata por secao). */
public class AncientFinder extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> range = sg.add(new IntSetting.Builder()
        .name("range").description("Raio de busca em blocos.").defaultValue(32).range(8, 64).sliderRange(8, 64).build());
    private final Setting<SettingColor> side = sg.add(new ColorSetting.Builder()
        .name("side-color").description("Cor do preenchimento.").defaultValue(new SettingColor(255, 150, 40, 40)).build());
    private final Setting<SettingColor> line = sg.add(new ColorSetting.Builder()
        .name("line-color").description("Cor das linhas.").defaultValue(new SettingColor(255, 150, 40, 255)).build());

    private volatile List<BlockPos> found = List.of();
    private int cooldown;

    public AncientFinder() {
        super(WitchAddon.CATEGORY, "ancient-finder", "Mostra Ancient Debris por perto atraves de paredes.");
    }

    @Override
    public void onDeactivate() { found = List.of(); }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (--cooldown > 0) return;
        cooldown = 40;
        if (mc.player == null || mc.level == null) { found = List.of(); return; }
        BlockPos c = mc.player.blockPosition();
        final List<BlockPos> hits = new ArrayList<>();
        SectionScan.scan(mc.level, c.getX(), c.getY(), c.getZ(), range.get(), Math.min(range.get(), 48),
            st -> st.is(Blocks.ANCIENT_DEBRIS), (x, y, z, st) -> hits.add(new BlockPos(x, y, z)));
        found = hits.size() > 200 ? new ArrayList<>(hits.subList(0, 200)) : hits;
    }

    @EventHandler
    private void onRender(Render3DEvent e) {
        for (BlockPos p : found) e.renderer.box(p, side.get(), line.get(), ShapeMode.Both, 0);
    }
}
