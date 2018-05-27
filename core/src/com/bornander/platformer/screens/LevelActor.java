package com.bornander.platformer.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.bornander.platformer.Assets;
import com.bornander.platformer.persisted.Result;

public  class LevelActor extends Button {
    private final Label index;
    private final Label name;
    private final Label personalBestLabel;
    private final Label personalBestTime;
    private final Label worldBestLabel;
    private final Label worldBestTime;

    public static final String[] NAMES = new String[] {
            "MARIO",
            "ELLIE",
            "GORDON",
            "ALOY",
            "MAX",
            "LARA",
            "LINK",
            "FAITH",
            "NATHAN"
    };

    public LevelActor(int levelIndex, Result result) {
        ButtonStyle style = new ButtonStyle(Assets.instance.menu.button_up, Assets.instance.menu.button_down, null);
        setStyle(style);
        Label.LabelStyle largeStyle = new Label.LabelStyle(Assets.instance.fonts.levelButtonLarge, Color.WHITE);
        Label.LabelStyle smallStyle = new Label.LabelStyle(Assets.instance.fonts.levelButtonSmall, Color.WHITE);

        index = new Label("0" + levelIndex, largeStyle);
        index.setAlignment(Align.center);
        this.name = new Label(NAMES[levelIndex - 1], largeStyle);
        personalBestLabel = new Label("PERSONAL BEST:", smallStyle);
        personalBestTime = new Label(result.getPersonal(), smallStyle);

        worldBestLabel = new Label("WORLD BEST:", smallStyle);
        worldBestTime = new Label(result.getWorld(), smallStyle);


        Table inner = new Table();
        add(index).pad(8);
        add().expand();
        add(inner);

        inner.add(this.name).align(Align.center).colspan(3);
        inner.row();
        inner.add(personalBestLabel).align(Align.left);
        inner.add().width(16);
        inner.add(personalBestTime).align(Align.right);
        inner.row();
        inner.add(worldBestLabel).align(Align.left);
        inner.add().width(16);
        inner.add(worldBestTime).align(Align.right);

        add().expand();
        pad(6);
    }
    
	public void updateScores(Result result) {
		personalBestTime.setText(result.getPersonal());
		worldBestTime.setText(result.getWorld());
	}
    

    @Override
    public void layout() {
        super.layout();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}