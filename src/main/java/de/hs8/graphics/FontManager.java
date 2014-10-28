package de.hs8.graphics;

/*
    created by hen
 */


import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.List;

public class FontManager {

    // List<PFont> pfonts = new ArrayList<PFont>();
    List<Font> awtfonts = new ArrayList<Font>();
    int fontCount = 1;
    FontRenderContext frc;

    public FontManager(final FontRenderContext frc, final String fontName,
                       final Integer... sizes) {

        this.frc = frc;
        final Font refFont = new Font(fontName, Font.PLAIN, sizes[0]);
        awtfonts.add(refFont);

        for (int i = 1; i < sizes.length; i++) {
            final Font deriveFont = refFont.deriveFont((float) sizes[i]);
            awtfonts.add(deriveFont);
            // System.out.println(deriveFont);
        }

        // for (Font awtf : awtfonts) {
        // pfonts.add(new PFont(awtf, true));
        // }
        fontCount = sizes.length - 1;

        // for (PFont f : pfonts) {
        // System.out.println(f.getSize());
        // }
        //

    }

    private int getSlot(final float size) {
        int slot = (int) Math.floor(size * fontCount + .5);
        if (slot > fontCount)
            slot = fontCount;
        if (slot < 0)
            slot = 0;
        return slot;
    }

    // public PFont getFont(float size){
    //
    // return pfonts.get(getSlot(size));
    //
    // }

    public Font getAWTFont(final float size) {
        return awtfonts.get(getSlot(size));
    }

    public Shape getShape(final String text, final float size) {
        final Font font = awtfonts.get(getSlot(size));
        final GlyphVector gv = font.createGlyphVector(frc, text);
        final Shape glyph = gv.getOutline(0, 0);
        return glyph;
    }

}
