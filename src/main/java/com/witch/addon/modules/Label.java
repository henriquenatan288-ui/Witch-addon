package com.witch.addon.modules;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Vector3d;

/** Texto flutuante num ponto do mundo (mesmo metodo que o Meteor usa nas nametags). */
final class Label {
    private static final Color BG = new Color(0, 0, 0, 120);

    private Label() {}

    static void draw(GuiGraphicsExtractor g, Vector3d pos, double scale, String s, Color color, boolean background) {
        if (!NametagUtils.to2D(pos, scale)) return;
        TextRenderer text = TextRenderer.get();
        NametagUtils.begin(pos, g);
        text.beginBig(g);
        double w = text.getWidth(s);
        double x = -w / 2, y = -text.getHeight();
        if (background) {
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(x - 1, y - 1, w + 2, text.getHeight() + 2, BG);
            Renderer2D.COLOR.render();
        }
        text.render(s, x, y, color);
        text.end();
        NametagUtils.end(g);
    }
}
