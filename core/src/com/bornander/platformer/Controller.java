package com.bornander.platformer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bornander.libgdx.scene2d.Joystick;

public class Controller {
	
	private enum GameButton { LEFT, RIGHT, JUMP, DROP }
	
	private final Stage stage;
	private final ObjectMap<GameButton, Button> buttons = new ObjectMap<Controller.GameButton, Button>(4);
	private final ObjectMap<GameButton, Boolean> state = new ObjectMap<GameButton, Boolean>(4);
	
	private final boolean useOnScreenController;
	private final Joystick joystick;
	
	public Controller(boolean displayOnly, float scale) {
		float factor =  scale * Gdx.graphics.getHeight() / 100.0f; 
		useOnScreenController = Gdx.app.getType() != ApplicationType.Desktop || displayOnly;

		ScreenViewport viewport = new ScreenViewport();

		stage = new Stage(viewport);
		
		
		Skin skin = new Skin(Gdx.files.internal("graphics/sheets/controller.skin"));
		float pad = factor;
		Table table = new Table(skin);
		table.setFillParent(true);
		table.bottom();
		
		joystick = new Joystick(skin, "default");

		buttons.put(GameButton.JUMP, new Button(skin, "jump"));
		buttons.put(GameButton.DROP, new Button(skin, "drop"));

		table.add(joystick).size(factor * 30, factor * 10).pad(pad);
		table.add().expandX();
		
		table.add(buttons.get(GameButton.DROP)).size(factor * 10).pad(pad);
		table.add(buttons.get(GameButton.JUMP)).size(factor * 10).pad(pad);
		stage.addActor(table);
		
		if (displayOnly) {
			stage.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeOut(1.5f)));
		}
		else {
			stage.addAction(Actions.sequence(Actions.alpha(0), Actions.delay(1.5f), Actions.fadeIn(0.5f)));
		}
		
		if (!displayOnly) {
			Gdx.input.setInputProcessor(new InputProcessor(){
			
			private boolean processPress(int keycode, boolean targetState) {
				switch(keycode) {
				case Keys.LEFT:
					state.put(GameButton.LEFT, targetState);
					break;
				case Keys.RIGHT:
					state.put(GameButton.RIGHT, targetState);
					break;
				case Keys.SPACE:
				case Keys.UP: 
					state.put(GameButton.JUMP, targetState); 
					break;
				case Keys.DOWN: 
					state.put(GameButton.DROP, targetState); 
					break;
				}
				
				return true;
			}

			@Override
			public boolean keyDown(int keycode) { return processPress(keycode, true); }

			@Override
			public boolean keyUp(int keycode) { return processPress(keycode, false); }

			@Override
			public boolean keyTyped(char character) { return false; }

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return stage.touchDown(screenX, screenY, pointer, button); }

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) { return stage.touchUp(screenX, screenY, pointer, button); }

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) { return stage.touchDragged(screenX, screenY, pointer); }

			@Override
			public boolean mouseMoved(int screenX, int screenY) { 
				return stage.mouseMoved(screenX, screenY); 
				}

			@Override
			public boolean scrolled(int amount) { return stage.scrolled(amount); }
			});
		}
	}
	
	public boolean isLeftPressed() {
		return state.get(GameButton.LEFT, false);
	}

	public boolean isRightPressed() {
		return state.get(GameButton.RIGHT, false);
	}
	
	public boolean isJumpPressed() {
		return state.get(GameButton.JUMP, false);
	}
	
	public boolean isDropPressed() {
		return state.get(GameButton.DROP, false);
	}
	
	public void render() {
		if (useOnScreenController)
			stage.draw();
	}

	public void update(float deltaTime) {
		stage.act(deltaTime);
 		if (useOnScreenController) {
 			state.put(GameButton.JUMP, buttons.get(GameButton.JUMP).isPressed());
 			state.put(GameButton.DROP, buttons.get(GameButton.DROP).isPressed());
 			
 			state.put(GameButton.LEFT, joystick.state.x < -0.1f);
 			state.put(GameButton.RIGHT, joystick.state.x > 0.1f);
		}
	}

	public void fadeOut() {
		stage.addAction(Actions.fadeOut(0.5f));
	}
}
