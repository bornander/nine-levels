package com.bornander.libgdx.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Joystick extends Actor {
	
	public static class JoystickStyle
	{
		public Drawable up;
		public Drawable down;
		public Drawable background;
		
		public JoystickStyle(Drawable up, Drawable down, Drawable background) {
			this.up = up;
			this.down = down;
			this.background = background;
		}
		
		public JoystickStyle(JoystickStyle style) {
			this.up = style.up;
			this.down = style.down;
			this.background = style.background;
		}
		
		public JoystickStyle() {
		}
	}
	
    private Drawable background;
    private Drawable up;
    private Drawable down;

    public boolean isPressed = false;
    public final Vector2 state = new Vector2();

    public Joystick(Drawable up, Drawable down, Drawable background) {
    	this.up = up;
    	this.down = down;
    	this.background = background;
        
        addListener(new InputListener() {
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float w = getWidth();
                state.set(MathUtils.clamp(x, 0, w) / w - 0.5f, 0.0f).scl(2);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isPressed = true;
                float w = getWidth();
                state.set(MathUtils.clamp(x, 0, w) / w - 0.5f, 0.0f).scl(2);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                isPressed = false;
                state.setZero();
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) { return false; }

            @Override
            public boolean keyUp(InputEvent event, int keycode) { return false; }
        });
    }
    
    public Joystick(JoystickStyle style) {
    	this(style.up, style.down, style.background);
    }
    
    public Joystick(Skin skin, String styleName) {
    	this(skin.get(styleName, JoystickStyle.class));
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();
        
        background.draw(batch, x, y, w, h);
        
        if (isPressed)
        	down.draw(batch, x + w/2.0f + state.x * (w-h)/ 2.0f - h /2.0f, y, h, h);
        else
        	up.draw(batch, x + w/2.0f + state.x * (w-h)/ 2.0f - h /2.0f, y, h, h);
    }
}