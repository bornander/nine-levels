package com.bornander.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.RepeatingBackground;
import com.bornander.platformer.Assets;
import com.bornander.platformer.Controller;
import com.bornander.platformer.PlatformerGame;
import com.bornander.platformer.persisted.Settings;

public class SettingsScreen extends GameScreen {
	
	private final Stage stage;
	private Controller controller;
	
	private final ScreenViewport viewport;
	private final RepeatingBackground background;
	private final Settings settings;

	public SettingsScreen(final PlatformerGame game, float lx, float hx) {
		super(game);
		
		
		settings = Settings.load();
		
		viewport = new ScreenViewport();
		viewport.apply();
		background = new RepeatingBackground(Assets.instance.backgrounds.grass);
		
		stage = new Stage(viewport) {
			@Override
			public boolean keyDown(int keyCode) {
				if (keyCode == Keys.BACK || keyCode == Keys.BACKSPACE) {
					game.setScreen(new MenuScreen(game));
				}
				return super.keyDown(keyCode);
			}
		};
		
		Table rootTable = new Table();
		
		Label title = new Label("SETTINGS", Assets.instance.menu.largeMenuFontStyle);
		title.setAlignment(Align.center);
		Label audio = new Label("AUDIO", Assets.instance.menu.mediumMenuFontStyle);
		Label controls = new Label("CONTROLS", Assets.instance.menu.mediumMenuFontStyle);
		audio.setAlignment(Align.left);
		controls.setAlignment(Align.left);
		
		
		CheckBox sound = new CheckBox(" SOUND", Assets.instance.menu.checkStyle);
		CheckBox music = new CheckBox(" MUSIC", Assets.instance.menu.checkStyle);
		sound.align(Align.left);
		music.align(Align.left);
		sound.setChecked(settings.sound);
		music.setChecked(settings.music);
		sound.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Assets.instance.sounds.playButton();
				settings.toogleSound(); 
				Assets.instance.sounds.setEnabled(settings.sound);
			}
		});
		music.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Assets.instance.sounds.playButton();
				settings.toogleMusic(); 
				Assets.instance.music.setEnabled(settings.music);
				if (settings.music)
					Assets.instance.music.playMenu();
			}
		});
		
		final CheckBox controllerS = new CheckBox(" SMALL", Assets.instance.menu.radioStyle);
		final CheckBox controllerM = new CheckBox(" MEDIUM", Assets.instance.menu.radioStyle);
		final CheckBox controllerL = new CheckBox(" LARGE", Assets.instance.menu.radioStyle);
		final CheckBox controllerVL = new CheckBox(" VERY LARGE", Assets.instance.menu.radioStyle);
		ButtonGroup<CheckBox> controllerGroup = new ButtonGroup<CheckBox>(controllerS, controllerM, controllerL, controllerVL);
		
		controllerS.setChecked(settings.isControllerSmall());
		controllerM.setChecked(settings.isControllerMedium());
		controllerL.setChecked(settings.isControllerLarge());
		controllerVL.setChecked(settings.isControllerVeryLarge());
		
		
		controllerS.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (controllerS.isChecked()) {
					Assets.instance.sounds.playButton();
					settings.setControllerSize(1);
				}
					
				controller = new Controller(true, settings.getControllerFactor());
			}
		});
		controllerM.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (controllerM.isChecked()) {
					Assets.instance.sounds.playButton();
					settings.setControllerSize(2);
				}
					
				controller = new Controller(true, settings.getControllerFactor());
			}
		});
		controllerL.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (controllerL.isChecked()) {
					Assets.instance.sounds.playButton();
					settings.setControllerSize(3);
				}
				controller = new Controller(true, settings.getControllerFactor());
			}
		});
		
		controllerVL.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (controllerVL.isChecked()) {
					Assets.instance.sounds.playButton();
					settings.setControllerSize(4);
				}
				controller = new Controller(true, settings.getControllerFactor());
			}
		});		

		Button back = new Button(Assets.instance.menu.skin, "back");
		back.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				settings.save();
				Assets.instance.sounds.playButton();
				game.setScreen(new MenuScreen(game));
			}
		});
		
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			back.setScale(2);
			back.setTransform(true);
		}
		back.align(Align.left);
		
		
		controllerS.align(Align.left);
		controllerM.align(Align.left);
		controllerL.align(Align.left);
		controllerVL.align(Align.left);
		
		
		rootTable.setFillParent(true);
		rootTable.pad(10);
		
		rootTable.row().colspan(4);
		rootTable.add(title);
		
		rootTable.row().height(32);
		rootTable.add();
		
		rootTable.row().expandX();
		rootTable.add().fillX();
		rootTable.add(audio).fillX();
		
		rootTable.add(controls).fillX();
		rootTable.add().fillX();
		
		rootTable.row().pad(10);
		rootTable.add().fillX(); rootTable.add(sound).fillX();	rootTable.add(controllerS).fillX();rootTable.add().fillX();
		rootTable.row().pad(10);
		rootTable.add().fillX(); rootTable.add(music).fillX();	rootTable.add(controllerM).fillX();rootTable.add().fillX(); 
		rootTable.row().pad(10);
		rootTable.add().fillX(); rootTable.add().fillX();		rootTable.add(controllerL).fillX();rootTable.add().fillX();
		rootTable.row().pad(10);
		rootTable.add().fillX(); rootTable.add().fillX();		rootTable.add(controllerVL).fillX();rootTable.add().fillX();
		
		rootTable.row().expand().fill();
		rootTable.add().fill().expand();
		rootTable.row();
		rootTable.add(back).align(Align.left);

		stage.addActor(rootTable);

		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);
	}
	
	@Override
	public void update(float delta) {
		stage.act(delta);
		if (controller != null)
			controller.update(delta);
	}

	@Override
	public void render() {
		clear();
		background.render();
		if (controller != null)
			controller.render();
		
		stage.draw();
	}
}