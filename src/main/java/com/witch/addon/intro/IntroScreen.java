package com.witch.addon.intro;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix3x2fStack;

/**
 * Animacao de entrada: W branco em fundo preto entra com brilho, gira 180 graus, segue uma curva ate o canto
 * superior direito encolhendo (com rastro), solta faiscas e dois morcegos voam dele.
 * Tudo com fill + matriz de pose (posicao com subpixel, sem tremer). O relogio avanca no maximo 50 ms por quadro.
 */
public class IntroScreen extends Screen {
    // linha do tempo (ms)
    private static final double IN_END = 700;
    private static final double FLIP_START = 800, FLIP_END = 1700;
    private static final double MOVE_START = 1700, MOVE_END = 2900;
    private static final double BAT_START = 2800, BAT_DUR = 2100;
    private static final double W_FADE = 4500, END = 5100;

    private static final int GW = 25, GH = 19;
    private static final boolean[][] W_CELLS = buildW();
    private static final int BLUE = 0x6092E8;

    private final Screen next;
    private long last = -1;
    private double clock;
    private boolean done, batSound;

    public IntroScreen(Screen next) {
        super(Component.literal("Witch"));
        this.next = next;
    }

    // ---------- geometria do W ----------
    private static boolean[][] buildW() {
        double[][] p = {{0, 0}, {6, 18}, {12, 6}, {18, 18}, {24, 0}};
        boolean[][] c = new boolean[GW][GH];
        for (int x = 0; x < GW; x++) {
            for (int y = 0; y < GH; y++) {
                double best = 1e9;
                for (int i = 0; i < 4; i++) best = Math.min(best, dist(x, y, p[i][0], p[i][1], p[i + 1][0], p[i + 1][1]));
                c[x][y] = best <= 1.6;
            }
        }
        return c;
    }

    private static double dist(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay;
        double t = Math.max(0, Math.min(1, ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)));
        return Math.hypot(px - (ax + t * dx), py - (ay + t * dy));
    }

    // ---------- easings ----------
    private static double clamp01(double t) { return Math.max(0, Math.min(1, t)); }
    private static double inOutCubic(double t) { t = clamp01(t); return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2; }
    private static double outCubic(double t) { t = clamp01(t); return 1 - Math.pow(1 - t, 3); }
    private static double outBack(double t) { t = clamp01(t); double c1 = 1.70158, c3 = c1 + 1; return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2); }
    private static double lerp(double a, double b, double t) { return a + (b - a) * t; }

    private static int argb(int rgb, double a) {
        int al = (int) Math.round(255 * clamp01(a));
        return (al << 24) | (rgb & 0xFFFFFF);
    }

    // ---------- ciclo da tela ----------
    private void finish() {
        if (done) return;
        done = true;
        Minecraft.getInstance().gui.setScreen(next);
    }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void onClose() { finish(); }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        finish();
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        long now = System.currentTimeMillis();
        if (last < 0) last = now;
        // enquanto a tela de carregamento do jogo cobre tudo, a animacao espera
        if (Minecraft.getInstance().gui.overlay() != null) {
            last = now;
            g.fill(0, 0, width, height, 0xFF000000);
            return;
        }
        clock += Math.min(now - last, 50);
        last = now;
        double t = clock;
        if (t >= END) {
            finish();
            return;
        }

        super.extractRenderState(g, mx, my, delta);   // menu (panorama) por baixo

        double fs = Math.max(1.0, height / 360.0);    // fator de escala da interface
        double move = inOutCubic((t - MOVE_START) / (MOVE_END - MOVE_START));

        // fundo preto some enquanto o W viaja
        double blackA = t < MOVE_START ? 1.0 : 1.0 - move;
        g.fill(0, 0, width, height, argb(0x000000, blackA));

        // trajetoria em curva (Bezier quadratica) do centro ate o canto superior direito
        double p0x = width / 2.0, p0y = height / 2.0;
        double p2x = width - 30 * fs, p2y = 28 * fs;
        double p1x = width * 0.50, p1y = height * 0.04;
        double[] pos = bez(p0x, p0y, p1x, p1y, p2x, p2y, move);

        double baseH = Math.min(width, height) * 0.40;
        double scale = lerp(1.0, 0.20, move);
        double intro = t < IN_END ? outBack(t / IN_END) : 1.0;
        double bob = t >= IN_END && t < MOVE_START ? Math.sin((t - IN_END) / (MOVE_START - IN_END) * Math.PI) * -6 * fs : 0;
        double flip = inOutCubic((t - FLIP_START) / (FLIP_END - FLIP_START));
        double xs = Math.max(0.02, Math.abs(Math.cos(Math.PI * flip)));
        double alpha = t < IN_END ? clamp01(t / (IN_END * 0.7)) : (t > W_FADE ? 1.0 - (t - W_FADE) / (END - W_FADE) : 1.0);

        double cell = baseH * scale * (0.85 + 0.15 * intro) / GH;

        // rastro: copias fracas do W em posicoes anteriores da trajetoria
        if (move > 0 && move < 1) {
            for (int k = 4; k >= 1; k--) {
                double mm = move - k * 0.05;
                if (mm <= 0) continue;
                double[] gp = bez(p0x, p0y, p1x, p1y, p2x, p2y, mm);
                drawW(g, gp[0], gp[1], baseH * lerp(1.0, 0.20, mm) / GH, 1.0, argb(0xFFFFFF, 0.16 / k * alpha), 0, 0);
            }
        }
        // brilho pulsante + W
        double pulse = 0.6 + 0.4 * Math.sin(t / 240.0);
        drawW(g, pos[0], pos[1] + bob, cell, xs, argb(0xFFFFFF, alpha), argb(BLUE, 0.20 * alpha * pulse), 3);

        // faiscas na chegada
        double arrive = t - MOVE_END;
        if (arrive > -100 && arrive < 1100) drawSparks(g, p2x, p2y, Math.max(0, arrive) / 1000.0, fs, alpha);

        // morcegos
        if (t >= BAT_START) {
            if (!batSound) {
                batSound = true;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BAT_TAKEOFF, 1.0f, 1.0f));
            }
            drawBat(g, 0, (t - BAT_START) / BAT_DUR, p2x, p2y, fs, t);
            drawBat(g, 1, (t - BAT_START - 220) / BAT_DUR, p2x, p2y, fs, t);
        }
    }

    private static double[] bez(double ax, double ay, double bx, double by, double cx, double cy, double t) {
        double u = 1 - t;
        return new double[]{u * u * ax + 2 * u * t * bx + t * t * cx, u * u * ay + 2 * u * t * by + t * t * cy};
    }

    private static double[] cubic(double[] p, double t) {
        double u = 1 - t;
        double a = u * u * u, b = 3 * u * u * t, c = 3 * u * t * t, d = t * t * t;
        return new double[]{a * p[0] + b * p[2] + c * p[4] + d * p[6], a * p[1] + b * p[3] + c * p[5] + d * p[7]};
    }

    /** Desenha o W centrado em (cx, cy). Cada celula = 4x4 unidades locais, para o brilho caber em quartos de celula. */
    private void drawW(GuiGraphicsExtractor g, double cx, double cy, double cell, double xs, int color, int glow, int glowLayers) {
        Matrix3x2fStack m = g.pose();
        m.pushMatrix();
        m.translate((float) cx, (float) cy);
        m.scale((float) (cell * xs / 4.0), (float) (cell / 4.0));
        m.translate(-GW * 2f, -GH * 2f);       // centro do W (GW*4/2, GH*4/2)
        if (glowLayers > 0 && (glow >>> 24) > 0) {
            for (int l = glowLayers; l >= 1; l--) {
                int e = l * 3;
                int c = (((glow >>> 24) / (l + 1)) << 24) | (glow & 0xFFFFFF);
                for (int x = 0; x < GW; x++)
                    for (int y = 0; y < GH; y++)
                        if (W_CELLS[x][y]) g.fill(x * 4 - e, y * 4 - e, x * 4 + 4 + e, y * 4 + 4 + e, c);
            }
        }
        for (int x = 0; x < GW; x++)
            for (int y = 0; y < GH; y++)
                if (W_CELLS[x][y]) g.fill(x * 4, y * 4, x * 4 + 4, y * 4 + 4, color);
        m.popMatrix();
    }

    private void drawSparks(GuiGraphicsExtractor g, double cx, double cy, double u, double fs, double alpha) {
        for (int j = 0; j < 18; j++) {
            double ang = j * 2.399963;
            double speed = (38 + (j * 37) % 60) * fs;
            double d = speed * outCubic(u);
            double life = 1.0 - clamp01(u * (0.9 + (j % 4) * 0.08));
            if (life <= 0) continue;
            int s = Math.max(1, (int) Math.round((j % 3 == 0 ? 3 : 2) * fs * life + 0.5));
            int x = (int) Math.round(cx + Math.cos(ang) * d), y = (int) Math.round(cy + Math.sin(ang) * d);
            g.fill(x - s, y - s, x + s, y + s, argb(j % 2 == 0 ? 0xFFFFFF : BLUE, life * alpha));
        }
    }

    /** Morcego de pixels: voa em curva suave saindo do W, inclina para o lado do movimento e bate as asas. */
    private void drawBat(GuiGraphicsExtractor g, int which, double bt, double sx, double sy, double fs, double t) {
        if (bt <= 0 || bt >= 1) return;
        double dir = which == 0 ? -1 : 1;
        double[] path = which == 0
            ? new double[]{sx, sy, sx - width * 0.10, sy + height * 0.35, width * 0.55, height * 0.10, -width * 0.12, height * 0.62}
            : new double[]{sx, sy, sx + width * 0.02, sy + height * 0.45, width * 0.30, height * 0.45, width * 0.30, height * 1.15};
        double e = inOutCubic(bt * 0.98 + 0.02 * bt);
        double[] p = cubic(path, e);
        double[] q = cubic(path, Math.min(1, e + 0.02));
        double vx = q[0] - p[0], vy = q[1] - p[1];
        double sp = Math.max(1e-6, Math.hypot(vx, vy));
        double lean = Math.max(-1, Math.min(1, vx / sp)) * 0.35;
        double size = fs * lerp(0.9, 3.4, outCubic(bt * 2.5));
        double flap = Math.sin(t / 70.0 + which * 1.7);
        double a = clamp01(bt < 0.08 ? bt / 0.08 : 1.0);
        double bob = Math.sin(bt * 14 + which) * 4 * fs;

        // sombra azul fraca
        Matrix3x2fStack m = g.pose();
        m.pushMatrix();
        m.translate((float) p[0], (float) (p[1] + bob));
        m.rotate((float) lean);
        m.scale((float) size, (float) size);

        int body = argb(0x2B1B4D, a), wing = argb(0x3F2A73, a), memb = argb(0x57409A, a * 0.85), eye = argb(0xFF4040, a);
        for (int side = -1; side <= 1; side += 2) {
            m.pushMatrix();
            m.translate((float) (side * 1.5), -0.5f);
            m.scale((float) side, 1f);
            m.rotate((float) (-(0.15 + flap * 0.65)));
            g.fill(0, -1, 5, 2, wing);
            g.fill(1, 1, 5, 3, memb);
            m.translate(5f, 0f);
            m.rotate((float) (-(flap * 0.55 + 0.1)));
            g.fill(0, -1, 5, 1, wing);
            g.fill(0, 1, 4, 3, memb);
            g.fill(5, 0, 7, 1, wing);
            m.popMatrix();
        }
        g.fill(-1, -2, 2, 3, body);           // corpo
        g.fill(-1, -3, 0, -2, body);          // orelhas
        g.fill(1, -3, 2, -2, body);
        g.fill(-1, -1, 0, 0, eye);            // olhos
        g.fill(1, -1, 2, 0, eye);
        m.popMatrix();
    }
}
