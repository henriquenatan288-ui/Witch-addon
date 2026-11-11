package com.witch.addon.hud;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.function.Supplier;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Elementos de HUD simples do Witch: uma linha de texto sobre fundo azul escuro. */
public class WitchHud extends HudElement {
    private static final Color BG = new Color(14, 22, 48, 190);
    private static final Color FG = new Color(235, 242, 255, 255);

    public static final HudElementInfo<WitchHud> WATERMARK = new HudElementInfo<>(WitchAddon.HUD_GROUP, "witch-watermark",
        "Marca do Witch.", () -> new WitchHud(WATERMARK, () -> "Witch Addon"));
    public static final HudElementInfo<WitchHud> DAY = new HudElementInfo<>(WitchAddon.HUD_GROUP, "day-counter",
        "Dia do mundo.", () -> new WitchHud(DAY, () -> mc.level == null ? "Dia -" : "Dia " + (mc.level.getDayTime() / 24000L)));
    public static final HudElementInfo<WitchHud> DIRECTION = new HudElementInfo<>(WitchAddon.HUD_GROUP, "direction",
        "Direcao que voce olha.", () -> new WitchHud(DIRECTION, () -> mc.player == null ? "-" : mc.player.getDirection().getName()));
    public static final HudElementInfo<WitchHud> NETHER = new HudElementInfo<>(WitchAddon.HUD_GROUP, "nether-coords",
        "Coordenadas convertidas Overworld/Nether.", () -> new WitchHud(NETHER, () -> {
            if (mc.player == null) return "-";
            boolean nether = mc.level != null && mc.level.dimension().toString().contains("the_nether");
            double k = nether ? 8.0 : 1.0 / 8.0;
            return String.format("%s: %d %d", nether ? "Overworld" : "Nether", (int) (mc.player.getX() * k), (int) (mc.player.getZ() * k));
        }));

    private final Supplier<String> text;

    public WitchHud(HudElementInfo<WitchHud> info, Supplier<String> text) {
        super(info);
        this.text = text;
    }

    @Override
    public void render(HudRenderer r) {
        String s = text.get();
        double w = r.textWidth(s, true) + 8, h = r.textHeight(true) + 6;
        setSize(w, h);
        r.quad(x, y, w, h, BG);
        r.quad(x, y, 2, h, new Color(47, 123, 255, 255));
        r.text(s, x + 5, y + 3, FG, true);
    }
}
