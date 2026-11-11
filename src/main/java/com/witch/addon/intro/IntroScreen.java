package com.witch.addon.intro;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;

/**
 * Animacao de entrada: W branco em fundo preto gira 180 graus (virada em torno do eixo vertical),
 * e arrastado para o canto superior direito encolhendo, e ao chegar saem dois morcegos dele.
 * Desenhada so com fill (sem textura): o W e uma grade de pixels calculada uma vez.
 */
public class IntroScreen extends Screen {
    // linha do tempo (ms)
    private static final long FLIP_START = 450, FLIP_END = 1250;
    private static final long MOVE_END = 2300;
    private static final long BAT_END = 4000;
    private static final long END = 4400;

    private static final int GW = 25, GH = 19;
    private static final boolean[][] W_CELLS = buildW();

    private final Screen next;
    private long last = -1;
    private double clock;   // ms de animacao; avanca no maximo 50 ms por quadro (aguenta quedas de FPS)
    private boolean done;
    private boolean batSound;

    public IntroScreen(Screen next) {
        super(Component.literal("Witch"));
        this.next = next;
    }

    private static boolean[][] buildW() {
        // W = linha quebrada (0,0)-(6,18)-(12,6)-(18,18)-(24,0) com espessura
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

    private static double ease(double t) {
        t = Math.max(0, Math.min(1, t));
        return t * t * (3 - 2 * t);
    }

    private static int argb(int rgb, double a) {
        int al = (int) Math.round(255 * Math.max(0, Math.min(1, a)));
        return (al << 24) | (rgb & 0xFFFFFF);
    }

    private void finish() {
        if (done) return;
        done = true;
        Minecraft.getInstance().gui.setScreen(next);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        finish();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        finish();
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        long now = System.currentTimeMillis();
        if (last < 0) last = now;
        // enquanto a tela de carregamento do jogo cobre tudo, a animacao espera (senao ela passa escondida)
        if (Minecraft.getInstance().getOverlay() != null) {
            last = now;
            g.fill(0, 0, width, height, 0xFF000000);
            return;
        }
        clock += Math.min(now - last, 50);
        last = now;
        long t = (long) clock;
        if (t >= END) {
            finish();
            return;
        }

        // fundo do menu (panorama) por baixo; o preto cobre ate o W comecar a ser arrastado
        super.extractRenderState(g, mx, my, delta);
        double move = ease((t - FLIP_END) / (double) (MOVE_END - FLIP_END));
        double blackA = t < FLIP_END ? 1.0 : 1.0 - move;
        g.fill(0, 0, width, height, argb(0x000000, blackA));

        // posicao e tamanho do W
        double base = Math.min(width, height) * 0.42;           // altura inicial em px
        double scale = 1.0 - 0.78 * move;                        // encolhe enquanto e arrastado
        double cx = width / 2.0 + (width - 28 - width / 2.0) * move;
        double cy = height / 2.0 + (26 - height / 2.0) * move;

        // giro de 180: largura = |cos(angulo)|
        double flip = ease((t - FLIP_START) / (double) (FLIP_END - FLIP_START));
        double xs = Math.abs(Math.cos(Math.PI * flip));
        double wA = t < 250 ? t / 250.0 : (t > BAT_END ? 1.0 - (t - BAT_END) / (double) (END - BAT_END) : 1.0);

        double cell = base * scale / GH;
        double totalW = GW * cell * xs, totalH = GH * cell;
        double x0 = cx - totalW / 2, y0 = cy - totalH / 2;
        int color = argb(0xFFFFFF, wA);
        for (int gx = 0; gx < GW; gx++) {
            for (int gy = 0; gy < GH; gy++) {
                if (!W_CELLS[gx][gy]) continue;
                int ax = (int) Math.round(x0 + gx * cell * xs), bx = (int) Math.round(x0 + (gx + 1) * cell * xs);
                int ay = (int) Math.round(y0 + gy * cell), by = (int) Math.round(y0 + (gy + 1) * cell);
                if (bx <= ax) bx = ax + 1;
                if (by <= ay) by = ay + 1;
                g.fill(ax, ay, bx, by, color);
            }
        }

        // morcegos: saem do W quando ele chega ao canto
        if (t >= MOVE_END - 150) {
            if (!batSound) {
                batSound = true;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BAT_TAKEOFF, 1.0f, 1.0f));
            }
            double bt = (t - (MOVE_END - 150)) / (double) (BAT_END - MOVE_END + 150);
            drawBat(g, cx, cy, bt, 0, t);
            drawBat(g, cx, cy, Math.max(0, bt - 0.12), 1, t);
        }
    }

    /** Morcego de pixels: corpo + asas que batem, voando em curva do canto para o meio da tela. */
    private void drawBat(GuiGraphicsExtractor g, double sx, double sy, double bt, int which, long t) {
        if (bt <= 0 || bt >= 1) return;
        double e = ease(bt);
        double tx = which == 0 ? width * 0.22 : width * 0.55;
        double ty = which == 0 ? height * 0.78 : height * 0.88;
        double x = sx + (tx - sx) * e + Math.sin(bt * 9 + which * 2) * 10;
        double y = sy + (ty - sy) * e + Math.sin(bt * 7 + which) * 14;
        double size = (2 + 4 * Math.min(1, bt * 3)) * Math.max(1, height / 360.0);   // cresce ao sair
        double a = bt > 0.85 ? (1 - bt) / 0.15 : 1.0;
        int u = (int) Math.max(2, Math.round(size));
        int body = argb(0x2B1B4D, a), wing = argb(0x3A2766, a), eye = argb(0xFF4040, a);
        double flap = Math.sin(t / 55.0 + which * 1.7);                 // -1..1
        int ix = (int) Math.round(x), iy = (int) Math.round(y);
        // asas
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 1; i <= 6; i++) {
                int wx = ix + side * (u + i * u);
                int wy = iy + (int) Math.round(flap * i * u * 0.55) - (i > 4 ? u : 0);
                int h = u * (i < 3 ? 3 : 2);
                g.fill(Math.min(wx, wx + u * side), wy, Math.max(wx, wx + u * side), wy + h, wing);
            }
        }
        // corpo, orelhas e olhos
        g.fill(ix - u, iy - u, ix + u, iy + 2 * u, body);
        g.fill(ix - u, iy - 2 * u, ix - u / 2 + 1, iy - u, body);
        g.fill(ix + u / 2, iy - 2 * u, ix + u, iy - u, body);
        g.fill(ix - u + 1, iy - u / 2, ix - u / 2 + 1, iy, eye);
        g.fill(ix + u / 2, iy - u / 2, ix + u - 1, iy, eye);
    }
}
