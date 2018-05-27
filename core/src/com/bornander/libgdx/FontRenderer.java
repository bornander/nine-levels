package com.bornander.libgdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;

public class FontRenderer {

    public static void drawGlyphs(Batch batch, float x, float y, int hAlign, int vAlign, BitmapFont font, GlyphLayout glyphLayout) {
    	int padding = 2; // TODO: Make this a parameter
		float tw = glyphLayout.width;
		float th = glyphLayout.height;
		
		float tx;
		float ty;
		switch(hAlign) {
		case Align.center: tx = x - (tw + 2 * padding) / 2.0f; break;
		case Align.right:  tx = x - (tw + padding); break;
		case Align.left:   tx = x + padding; break;
		default:
			throw new InvalidCaseException(hAlign);
		}
		
		switch(vAlign) {
		case Align.center:	ty = y + (th + 2 * padding) / 2.0f; break;
		case Align.top:		ty = y - padding; break;
		case Align.bottom:	ty = y + th + padding; break;
		default:
			throw new InvalidCaseException(hAlign);
		}
		
		font.draw(batch, glyphLayout, tx, ty);
    }		
}
