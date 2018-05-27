package com.bornander.platformer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bornander.libgdx.Log;
import com.bornander.platformer.OnlineConnectivity;
import com.bornander.platformer.OnlineConnectivityCallback;
import com.bornander.platformer.PlatformerGame;

public class DesktopLauncher implements OnlineConnectivity {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		float f = 0.50f;
		
		float w = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		float h = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		
		//config.addIcon("graphics/app_icon.png", FileType.Internal);
		config.title = "Nine Levels";
		
		//w = 16;
		//h = 9;
		
		config.width = (int)(w * f);
		config.height = (int)(h * f);
		//config.width = (int)(w * f);
		//config.height = (int)(h * f);
		config.resizable = false;
		config.foregroundFPS = 60;
		config.vSyncEnabled = false;
        DesktopLauncher launcher = new DesktopLauncher();
		PlatformerGame game = new PlatformerGame(launcher);
		game.onlineConnectivity.setCallback(game);
		new LwjglApplication(game, config);
	}

    OnlineConnectivityCallback callback;
	private boolean signedIn = false;

    @Override
    public void setCallback(OnlineConnectivityCallback callback) {
        this.callback = callback;
    }

    @Override
	public boolean isOnlineCapable()
	{
		return true;
	}
	

	@Override
	public boolean isSignedIn() {
		return signedIn;
	}

	@Override
	public void signIn() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				signedIn = true;
				callback.onlineSignIn();
			}
		});
		thread.start();
	}
	
	@Override
	public void signInSilently() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				signedIn = true;
				callback.onlineSignIn();
			}
		});
		thread.start();		
	}
	

	@Override
	public void signOut() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				signedIn = false;
				callback.onlineSignOut();
			}
		});
		thread.start();
		
	}

    @Override
    public void submitScore(int scoreIndex, long score) {
    }

	@Override
	public void unlockAchievement(int achievementIndex) {
	}

	@Override
	public void requestTopScores() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 1; i <= 9; ++i)
				{	
					Log.info("Generating top score for index %d", i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					callback.onlineTopScore(i, 1000 * (2*i + 60));
				}
			}
		});
		thread.start();		
	}

	@Override
	public void requestAchievements() {
	}

	@Override
	public void viewLeaderBoards() {
	}

	@Override
	public void viewAchievements() {
	}
}
