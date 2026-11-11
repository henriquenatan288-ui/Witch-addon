package com.witch.addon.modules;

import com.witch.addon.WitchAddon;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.HumanoidArm;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Contorno brilhante e pulsante em volta do item da mao (slot selecionado da hotbar e mao secundaria). */
public class HeldItemGlow extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();
    private final Setting<SettingColor> color = sg.add(new ColorSetting.Builder()
        .name("color").description("Cor do brilho.").defaultValue(new SettingColor(47, 123, 255, 255)).build());
    private final Setting<Integer> spread = sg.add(new IntSetting.Builder()
        .name("spread").description("Tamanho do brilho em pixels.").defaultValue(5).range(2, 12).sliderRange(2, 12).build());
    private final Setting<Boolean> pulse = sg.add(new BoolSetting.Builder()
        .name("pulse").description("Brilho que pulsa.").defaultValue(true).build());
    private final Setting<Boolean> handOutline = sg.add(new BoolSetting.Builder()
        .name("hand-outline").description("Contorno brilhante no item 3D da mao (experimental).").defaultValue(true).build());
    private final Setting<Boolean> hotbar = sg.add(new BoolSetting.Builder()
        .name("hotbar-glow").description("Brilho retangular no slot da hotbar.").defaultValue(false).build());
    private final Setting<Boolean> offhand = sg.add(new BoolSetting.Builder()
        .name("offhand").description("Tambem na mao secundaria.").defaultValue(true).build());

    private static HeldItemGlow instance;

    /** Cor ARGB do contorno do item na mao (0 = desligado). Usado pelo mixin. */
    public static int handOutline() {
        HeldItemGlow m = instance;
        if (m == null || !m.isActive() || !m.handOutline.get()) return 0;
        SettingColor c = m.color.get();
        double k = m.pulse.get() ? 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0) : 0.0;
        int r = (int) Math.round(c.r + (255 - c.r) * k * 0.5);
        int g = (int) Math.round(c.g + (255 - c.g) * k * 0.5);
        int b = (int) Math.round(c.b + (255 - c.b) * k * 0.5);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public HeldItemGlow() {
        super(WitchAddon.CATEGORY, "held-item-glow", "Contorno brilhante no item que voce segura.");
        instance = this;
    }

    @EventHandler
    private void onRender2D(Render2DEvent e) {
        var p = mc.player;
        if (p == null || !hotbar.get()) return;
        int cx = e.screenWidth / 2;
        int y = e.screenHeight - 19;
        double k = pulse.get() ? 0.65 + 0.35 * Math.sin(System.currentTimeMillis() / 260.0) : 1.0;

        if (!p.getMainHandItem().isEmpty()) {
            int slot = p.getInventory().getSelectedSlot();
            glow(e.graphics, cx - 90 + slot * 20 + 2, y, k);
        }
        if (offhand.get() && !p.getOffhandItem().isEmpty()) {
            int x = p.getMainArm() == HumanoidArm.RIGHT ? cx - 91 - 26 : cx + 91 + 10;
            glow(e.graphics, x, y, k);
        }
    }

    /** Aneis de 1 px com opacidade caindo para fora, mais uma borda nitida. */
    private void glow(GuiGraphicsExtractor g, int x, int y, double k) {
        SettingColor c = color.get();
        int n = spread.get();
        for (int i = 0; i <= n; i++) {
            double f = i == 0 ? 1.0 : Math.pow(1.0 - (double) i / (n + 1), 2) * 0.55;
            int a = (int) Math.round(255 * f * k * (c.a / 255.0));
            int col = (Math.max(0, Math.min(255, a)) << 24) | (c.r << 16) | (c.g << 8) | c.b;
            int x0 = x - 1 - i, y0 = y - 1 - i, x1 = x + 17 + i, y1 = y + 17 + i;
            g.fill(x0, y0, x1, y0 + 1, col);       // topo
            g.fill(x0, y1 - 1, x1, y1, col);       // base
            g.fill(x0, y0 + 1, x0 + 1, y1 - 1, col); // esquerda
            g.fill(x1 - 1, y0 + 1, x1, y1 - 1, col); // direita
        }
    }
}
