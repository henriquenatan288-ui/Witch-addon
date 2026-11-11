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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Caixa nos blocos de farm de redstone (piston, dispenser, observer, hopper), atraves de parede. */
public class FarmEsp extends Module {
    private static final Set<Block> BLOCKS = Set.of(
        Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.DISPENSER, Blocks.DROPPER, Blocks.OBSERVER, Blocks.HOPPER);
    private static final int MAX = 300;

    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<Integer> range = sg.add(new IntSetting.Builder()
        .name("range").description("Raio de busca em blocos.").defaultValue(16).range(8, 48).sliderRange(8, 48).build());
    private final Setting<ShapeMode> shape = sg.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").description("Como as caixas sao desenhadas.").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> side = sg.add(new ColorSetting.Builder()
        .name("side-color").description("Cor do preenchimento.").defaultValue(new SettingColor(47, 123, 255, 40)).build());
    private final Setting<SettingColor> line = sg.add(new ColorSetting.Builder()
        .name("line-color").description("Cor das linhas.").defaultValue(new SettingColor(47, 123, 255, 255)).build());

    private volatile List<BlockPos> found = List.of();
    private int cooldown;

    public FarmEsp() {
        super(WitchAddon.CATEGORY, "farm-esp", "Mostra pistoes, dispensers, observers e hoppers atraves de paredes.");
    }

    @Override
    public void onDeactivate() {
        found = List.of();
    }

    @EventHandler
    private void onTick(TickEvent.Post e) {
        if (--cooldown > 0) return;
        cooldown = range.get() > 24 ? 60 : 20;
        if (mc.player == null || mc.level == null) {
            found = List.of();
            return;
        }
        BlockPos c = mc.player.blockPosition();
        int r = range.get(), vr = Math.min(r, 24);
        final List<BlockPos> hits = new ArrayList<>();
        SectionScan.scan(mc.level, c.getX(), c.getY(), c.getZ(), r, vr,
            st -> BLOCKS.contains(st.getBlock()), (x, y, z, st) -> hits.add(new BlockPos(x, y, z)));
        hits.sort((a, b) -> Double.compare(a.distSqr(c), b.distSqr(c)));
        found = hits.size() > MAX ? new ArrayList<>(hits.subList(0, MAX)) : hits;
    }

    @EventHandler
    private void onRender(Render3DEvent e) {
        for (BlockPos p : found) {
            e.renderer.box(p, side.get(), line.get(), shape.get(), 0);
        }
    }
}
