package com.bornander.platformer.screens;

import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bornander.libgdx.GameScreen;
import com.bornander.libgdx.Log;
import com.bornander.libgdx.RepeatingBackground;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.Assets;
import com.bornander.platformer.OnlineConnectivityCallback;
import com.bornander.platformer.PlatformerGame;
import com.bornander.platformer.persisted.Result;
import com.bornander.platformer.persisted.Settings;

public class MenuScreen extends GameScreen implements OnlineConnectivityCallback {
	
	private static final Random RND = new Random();
	private static final String[] MESSAGES = new String[] {
		"GO FAST",
		"TESTED BY EMMA",
		"TESTED BY OSKAR",
		"TESTED BY EMMA AND OSKAR",
		"TESTED BY OSKAR AND EMMA",
		"PROGRAMMED BY FREDRIK BORNANDER",
		"BUILT USING LIBGDX",
		"BOX2D FOR PHYSICS",
		"MUSIC BY SKETCHY LOGIC",
		"SOUND BY SUBSPACEAUDIO",
		"FONT BY KENNEY",
		"AS SEEN ON JGO",
		"WITH COINS!",
		"GRAPHICS BY GRAFXKID",
		"NO DOUBLE JUMPS!",
		"LEVELS DESIGNED USING TILED MAP EDITOR"
	};
	
	private static String getMessage() {
		return MESSAGES[RND.nextInt(MESSAGES.length)];
	}
	
	private final AsyncExecutor executor = new AsyncExecutor(4);
	
	private final Stage stage;
	private Timeout timeout = new Timeout(200);
	private final ScreenViewport viewport;
	private final RepeatingBackground background;
	
	private final Button googlePlaySignIn;
	private final Button googlePlaySignOut;	
	private final Button googlePlayLeaderboards;
    private final Button googlePlayAchivements;
	
	private final Table signedInButtonsTable;
	private final Table signedOutButtonsTable;
	
	private boolean signInIsOngoing = false;
	
	private final LevelActor[] levelActors;

	public MenuScreen(final PlatformerGame game) {
		super(game);
		background = new RepeatingBackground(Assets.instance.backgrounds.grass);
		
		IntMap<Result> results = Result.load();
		
		viewport = new ScreenViewport();
		viewport.apply();
		stage = new Stage(viewport) {
			@Override
			public boolean keyDown(int keyCode) {
				if (keyCode == Keys.BACK || keyCode == Keys.BACKSPACE) {
					Gdx.app.exit();
				}
				return super.keyDown(keyCode);
			}
		};
		//stage.setDebugAll(true);
		
		Label title = new Label("NINE LEVELS", Assets.instance.menu.largeMenuFontStyle);
		Label subTitle = new Label(getMessage(), Assets.instance.menu.smallMenuFontStyle);
		
		Table rootTable = new Table();
		
		title.setAlignment(Align.center);
		
		Button settings = new Button(Assets.instance.menu.skin, "settings");
		settings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.instance.sounds.playButton();
				game.setScreen(new SettingsScreen(game, 0, 0));
			}
		});
		

		googlePlaySignIn = new Button(Assets.instance.menu.skin, "signin");
		googlePlaySignIn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (signInIsOngoing)
					return;
				signInIsOngoing = true;
				Assets.instance.sounds.playButton();
				game.onlineConnectivity.signIn();
			}
		});
		
		
		googlePlaySignOut = new Button(Assets.instance.menu.skin, "signout");
		googlePlaySignOut.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				Assets.instance.sounds.playButton();
				game.onlineConnectivity.signOut();
			}
		});
		
		googlePlayLeaderboards = new Button(Assets.instance.menu.skin, "leaderboard");
		googlePlayLeaderboards.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.instance.sounds.playButton();
				game.onlineConnectivity.viewLeaderBoards();
			}
		});

        googlePlayAchivements = new Button(Assets.instance.menu.skin, "achievements");
        googlePlayAchivements.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Assets.instance.sounds.playButton();
                game.onlineConnectivity.viewAchievements();
            }
        });


        if (Gdx.app.getType() != ApplicationType.Desktop) {
	        settings.setScale(2);
	        googlePlaySignOut.setScale(2);
	        googlePlaySignIn.setScale(2);
	        googlePlayLeaderboards.setScale(2);
	        googlePlayAchivements.setScale(2);
	
	        settings.setTransform(true);
	        googlePlaySignOut.setTransform(true);
	        googlePlaySignIn.setTransform(true);
	        googlePlayLeaderboards.setTransform(true);
	        googlePlayAchivements.setTransform(true);
		}

        rootTable.setFillParent(true);
		rootTable.pad(10);
		
		rootTable.row().colspan(4);
		rootTable.add(title);
		
		rootTable.row().expand();
		rootTable.add();
		rootTable.add();
		rootTable.add();
		
		LevelActor l1 = getLevelButton(1, results);
		LevelActor l2 = getLevelButton(2, results);
		LevelActor l3 = getLevelButton(3, results);
		LevelActor l4 = getLevelButton(4, results);
		LevelActor l5 = getLevelButton(5, results);
		LevelActor l6 = getLevelButton(6, results);
		LevelActor l7 = getLevelButton(7, results);
		LevelActor l8 = getLevelButton(8, results);
		LevelActor l9 = getLevelButton(9, results);
		levelActors = new LevelActor[] { l1, l2, l3, l4, l5, l6, l7, l8, l9 }; 
		
		rootTable.row().pad(10);
		
		rootTable.add(l1).fillX(); rootTable.add(l2).fillX(); rootTable.add(l3).fillX();
		rootTable.row().pad(10);
		rootTable.add(l4).fillX(); rootTable.add(l5).fillX(); rootTable.add(l6).fillX();
		rootTable.row().pad(10);
		rootTable.add(l7).fillX(); rootTable.add(l8).fillX(); rootTable.add(l9).fillX();
		
		rootTable.row().expand();
		rootTable.add();
		rootTable.row().align(Align.bottom);

		Table controlButtonsTable = new Table();
		controlButtonsTable.row().align(Align.bottomLeft);
		
		controlButtonsTable.add(settings).align(Align.bottomLeft).expandX();
		controlButtonsTable.add().pad(settings.getWidth());
		
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			Stack stateStack = new Stack();
			
			signedInButtonsTable = new Table();
			signedInButtonsTable.row().align(Align.bottom);
			signedInButtonsTable.add(googlePlaySignOut).align(Align.bottom).expandX();
			signedInButtonsTable.add().pad(googlePlaySignOut.getWidth());
			signedInButtonsTable.add(googlePlayLeaderboards).align(Align.bottom).expandX();
	        signedInButtonsTable.add().pad(googlePlayLeaderboards.getWidth());
	        signedInButtonsTable.add(googlePlayAchivements).align(Align.bottom).expandX();
			
			signedOutButtonsTable = new Table();
			signedOutButtonsTable.align(Align.bottom);
			signedOutButtonsTable.row().align(Align.left);
			signedOutButtonsTable.add(googlePlaySignIn).align(Align.left).expandX();
			
			stateStack.add(signedInButtonsTable);
			stateStack.add(signedOutButtonsTable);
			
			controlButtonsTable.add(stateStack).align(Align.bottom);
			signedInButtonsTable.setVisible(game.onlineConnectivity.isSignedIn());
			signedOutButtonsTable.setVisible(!game.onlineConnectivity.isSignedIn());
		}
		else {
			signedInButtonsTable = null;
			signedOutButtonsTable = null;
			
		}
		rootTable.add(controlButtonsTable).colspan(2).expandX().align(Align.bottomLeft);
		rootTable.add(subTitle).colspan(3).align(Align.bottomRight);
		rootTable.pack();
		
		stage.addActor(rootTable);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(stage);
		
		attemptSilentSignIn();
		requestTopScores();
	}
	
	private void attemptSilentSignIn() {
		Log.info("attemptSilentSignIn");

		AsyncResult<Void> task = executor.submit(new AsyncTask<Void>() {
			public Void call() {
				Log.info("attemptSilentSignIn, executor.submit");

				Settings settings =Settings.load();
				if (settings.wasSignedIn()) {
					Log.info("was signed in");
					game.onlineConnectivity.signInSilently();
				}
				return null;
		   }
		 });
    }
	
	private void requestTopScores() {
        if (game.onlineConnectivity.isSignedIn()) {
            AsyncResult<Void> task = executor.submit(new AsyncTask<Void>() {
                public Void call() {
                    Settings settings = Settings.load();
                    if (settings.isScoreRequestDue()) {
                        settings.setLastScoreRequest();
                        settings.save();
                        game.onlineConnectivity.requestTopScores();
                    }
                    return null;
                }
            });
        }
    }

    private void requestAchievments() {
        if (game.onlineConnectivity.isSignedIn()) {
            AsyncResult<Void> task = executor.submit(new AsyncTask<Void>() {
                public Void call() {
                    Settings settings = Settings.load();
                    if (settings.isAchievementRequestDue()) {
                        settings.setLastAchievementRequest();
                        settings.save();
                        game.onlineConnectivity.requestAchievements();
                    }
                    return null;
                }
            });
        }
    }
	
	private LevelActor getLevelButton(final int index, final IntMap<Result> results)
	{
		LevelActor actor = new LevelActor(index, results.get(index));
		actor.layout();
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Assets.instance.sounds.playButton();
				game.setScreen(new LevelLoadScreen(game, index, LevelActor.NAMES[index-1], MenuScreen.this));
				event.handle();
			}
		});
		
		return actor;
	}
	
	@Override
	public void show() {
		super.show();
		Assets.instance.music.playMenu();
	}
	
	@Override
	public void update(float delta) {
		stage.act();
		timeout.update(delta);
	}

	@Override
	public void render() {
		clear();
		background.render();
		stage.draw();
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		super.dispose();

	}


	@Override
	public void onlineSignIn() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				if (signedInButtonsTable != null && signedOutButtonsTable != null) {
					signedInButtonsTable.setVisible(true);
					signedOutButtonsTable.setVisible(false);
				}
				requestTopScores();
				requestAchievments();
			}
		});
	}

	@Override
	public void onlineSignOut() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				signInIsOngoing = false;
				signedInButtonsTable.setVisible(false);
				signedOutButtonsTable.setVisible(true);
			}
		});
	}

	@Override
	public void onlineError() {
		signInIsOngoing = false;
	}

	@Override
	public void onlineTopScore(final int scoreIndex, long score) {
		Log.info("Top score received: %d", scoreIndex);
		IntMap<Result> results = Result.load();
		final Result result = results.get(scoreIndex);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				signInIsOngoing = false;
				levelActors[scoreIndex - 1].updateScores(result);
			}
		});
	}

    @Override
    public void onlineAchivementUnlocked(int index) {
    }
}