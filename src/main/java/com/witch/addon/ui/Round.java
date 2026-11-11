package com.witch.addon.ui;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Retangulos de cantos arredondados feitos com linhas de quad (o renderer do Meteor so tem quad reto). */
public final class Round {
    public static final Color BLUE = new Color(47, 123, 255, 255);
    public static final Color WHITE = new Color(255, 255, 255, 240);

    private Round() {}

    public static void rect(GuiRenderer r, double x, double y, double w, double h, double rad, Color c, boolean top, boolean bottom) {
        if (w <= 0 || h <= 0) return;
        rad = Math.min(rad, Math.min(w, h) / 2);
        int n = (int) Math.ceil(rad);
        double ty = top ? n : 0, by = bottom ? n : 0;
        if (h - ty - by > 0) r.quad(x, y + ty, w, h - ty - by, c);
        for (int i = 0; i < n; i++) {
            double dy = rad - (i + 0.5);
            double inset = rad - Math.sqrt(Math.max(0, rad * rad - dy * dy));
            if (top) r.quad(x + inset, y + i, w - 2 * inset, 1, c);
            if (bottom) r.quad(x + inset, y + h - 1 - i, w - 2 * inset, 1, c);
        }
    }

    /** Caixa branca com contorno azul e cantos redondos. */
    public static void box(GuiRenderer r, double x, double y, double w, double h) {
        rect(r, x, y, w, h, 7, BLUE, true, true);
        rect(r, x + 2, y + 2, w - 4, h - 4, 5, WHITE, true, true);
    }
}
