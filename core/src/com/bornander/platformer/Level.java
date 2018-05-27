package com.bornander.platformer;

import static com.bornander.libgdx.FontRenderer.drawGlyphs;
import static com.bornander.libgdx.MapUtils.getEnum;
import static com.bornander.libgdx.MapUtils.getString;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bornander.libgdx.Collidable;
import com.bornander.libgdx.Contactable;
import com.bornander.libgdx.FixedStepWorldUpdater;
import com.bornander.libgdx.InvalidCaseException;
import com.bornander.libgdx.LabelledFloat;
import com.bornander.libgdx.LabelledInt;
import com.bornander.libgdx.Timeout;
import com.bornander.platformer.persisted.Settings;
import com.bornander.platformer.screens.PlayScreen;

public class Level {
	private enum State {
		INIT,
		PLAYING,
		WON,
		LOST,
		DONE
	}

	private final static int[] LAYER_SET_A = new int[] { 0, 1 };
	private final static int[] LAYER_SET_B = new int[] { 2 };
	
	private final TiledMap map;
	private final OrthographicCamera camera;
	private final OrthographicCamera backgroundCamera;
	private final OrthographicCamera hudCamera;
	private final OrthogonalTiledMapRenderer mapRenderer;
    private final int tileSizePixels = 16;
    private final float unitScale = 1.0f / (float)tileSizePixels;
    private final Background background; 
    
    private final Vector2 gravity = new Vector2(0, -25);
    public final World world;
    private final Box2DDebugRenderer worldDebugRenderer = new Box2DDebugRenderer();
    private final FPSLogger fpsLogger = new FPSLogger();
    private final FixedStepWorldUpdater worldUpdater;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    
    
    private final Array<GameActor> actors = new Array<GameActor>();
    private Array<GameActor> newActorStore = new Array<GameActor>(32);
     
    private final LabelledInt coinsLabel  = new LabelledInt("CX", 3);
    private final LabelledFloat timeLabel = new LabelledFloat("T:", 6, 2);
    
    private final Timeout introEnterTextTimeOut = new Timeout(1f);
    private final Timeout introWaitTimeOut = new Timeout(1, introEnterTextTimeOut);
    private final Timeout introExitTextTimeOut = new Timeout(0.5f, introWaitTimeOut);
    private final Timeout effectTimeOut = new Timeout(0);
    private State state = State.INIT; 
    
    private int coinCount = 0;
    private int capturedCoins = 0;
    
    private GlyphLayout nameGlyphLayout = new GlyphLayout(); 
    private GlyphLayout coinTargetGlyphLayout = new GlyphLayout(); 
    
    private final Array<Terrain> terrains;
    private final SpriteBatch batch = new SpriteBatch();
    private GameActor cameraTarget = null;
    
    private Controller controller;
    
    private float elapsedTime;
    
    private PlayerSpawner playerSpawner;
    
    private PlayScreen playScreen;
    
	public Level(TiledMap map, String name) {
		this.map = map;
		
		hudCamera = createHudCamera();
		camera = createCamera();
		backgroundCamera = createCamera();
		controller = new Controller(false, Settings.load().getControllerFactor());
		
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, unitScale);
		mapRenderer.setView(camera);
		String backgroundString = getString(map.getProperties(), "background", "grass");
		background = new Background(Assets.instance.backgrounds.get(backgroundString), unitScale);
		
		if (backgroundString.equals("grass"))
			Assets.instance.music.playGrass();
		else 
			Assets.instance.music.playSnow();
		
		world = new World(gravity, true);
		worldUpdater = new FixedStepWorldUpdater(world);
		
		TiledMapTileLayer terrainLayer = (TiledMapTileLayer)map.getLayers().get("tiles.terrain");
		terrains = Terrain.buildTerrain(world, terrainLayer);
		
		MapLayer entitiesLayer = map.getLayers().get("entities");
		for(MapObject mapObject : entitiesLayer.getObjects()) {
			GameActorType entityType = getEnum(mapObject, "type", GameActorType.NONE);
			switch (entityType) {
			case PLAYER_SPAWNER:
				actors.add(playerSpawner = new PlayerSpawner((RectangleMapObject)mapObject, world, unitScale, controller));
				break;
			case COIN:
				actors.add(new Coin(this, world, (EllipseMapObject)mapObject, unitScale));
				++coinCount;
				break;
			case COIN_PATH:
				{
					CoinPath path = new CoinPath(this, world, (PolygonMapObject)mapObject, unitScale);
					actors.add(path);
					coinCount += path.getCoinCount(); 
				}
				break;
			case SLIME:
				actors.add(new Slime(world, (RectangleMapObject)mapObject, unitScale));
				break;
			case PUMPKIN_SPAWNER:
				actors.add(new PumpkinSpawner(world, camera, (RectangleMapObject)mapObject, unitScale));
				break;
			case BIRD:
				actors.add(new Bird(world, (RectangleMapObject)mapObject, unitScale));
				break;
			case MASKED_BLOB:
				actors.add(new MaskedBlob(world, (RectangleMapObject)mapObject, unitScale));
				break;
			case BRICK:
				actors.add(new Brick(this, world, (RectangleMapObject)mapObject, unitScale));
				coinCount += 4;
				break;
			default:
				throw new InvalidCaseException(entityType);
			}
		}
		
		world.setContactListener(new ContactListener() {
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				
				Object userObjectA = fixtureA.getUserData();
				Object userObjectB = fixtureB.getUserData();
				
				boolean disableContact = false;
				if (userObjectA instanceof Collidable)
					disableContact |= ((Collidable)userObjectA).collides(userObjectB);

				if (userObjectB instanceof Collidable)
					disableContact |= ((Collidable)userObjectB).collides(userObjectA);
				
				contact.setEnabled(contact.isEnabled() && !disableContact);
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				contact.setEnabled(true);
			}
			
			@Override
			public void endContact(Contact contact) {
			}
			
			@Override
			public void beginContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				Object userObjectA = fixtureA.getUserData();
				Object userObjectB = fixtureB.getUserData();
				
				if (userObjectA instanceof Contactable)
					((Contactable)userObjectA).handleContact(fixtureA, userObjectB);

				if (userObjectB instanceof Contactable)
					((Contactable)userObjectB).handleContact(fixtureB, userObjectA);			
			}
		});
		

		capturedCoins = coinCount;
		coinsLabel.setValue(capturedCoins);
		setMessage(name, coinCount);
	}
	
    private static OrthographicCamera createCamera() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float verticalHeight = 8.0f;
        float viewportWidth = verticalHeight / aspectRatio;

        OrthographicCamera camera = new OrthographicCamera(viewportWidth, verticalHeight);
        camera.position.set(viewportWidth / 2.0f, verticalHeight / 2.0f, 1.0f);
        camera.update();
        return camera;
    }
    
    private static OrthographicCamera createHudCamera() {
        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float verticalHeight = 600.0f;
        float viewportWidth = verticalHeight / aspectRatio;

        OrthographicCamera camera = new OrthographicCamera(viewportWidth, verticalHeight);
        camera.position.set(viewportWidth / 2.0f, verticalHeight / 2.0f, 1.0f);
        camera.update();
        return camera;
    }
    
    public void setMessage(String name, int coinCount) {
    	float dw = camera.viewportWidth;
		nameGlyphLayout.setText(Assets.instance.fonts.levelH1, name, Color.WHITE, dw, Align.topLeft, false);		    	
		coinTargetGlyphLayout.setText(Assets.instance.fonts.levelH2, String.format("COLLECT [ORANGE]%d [WHITE]COINS!", coinCount), Color.WHITE, dw, Align.topLeft, false);
    }
    
    private void centerCamera() {
    	cameraTarget = GameActor.getActor(GameActorType.PLAYER, actors);
    	if (cameraTarget == null)
    		cameraTarget = GameActor.getActor(GameActorType.PLAYER_SPAWNER, actors);
    		
    	camera.position.x = Math.max(cameraTarget.getPosition().x, camera.viewportWidth / 2.0f);
    	camera.update();
    	mapRenderer.setView(camera);    	
    }
    
    
    public void update(float delta) {
    	introEnterTextTimeOut.update(delta);
    	introWaitTimeOut.update(delta);
    	introExitTextTimeOut.update(delta);
    	effectTimeOut.update(delta);
    	controller.update(delta);
    	switch(state) {
    	case INIT:
    		updateWorld(delta);
    		if (introEnterTextTimeOut.hasElapsed())
    			state = State.PLAYING;
    		break;
    	case PLAYING:
    		updatePlaying(delta);
    		break;
    	case WON:
    		updateWon(delta);
    		if (effectTimeOut.hasElapsed()) {
    			playScreen.won(elapsedTime);
    			state = State.DONE;
    		}
    		break;
    	case LOST:
    		updateWorld(delta);
    		if (effectTimeOut.hasElapsed())
    		{
    			playScreen.lost();
    			state = State.DONE;
    		}
    		break;
    	case DONE:
    		break;
    		
    	default:
    		throw new InvalidCaseException(state);
    	}
    	
    	centerCamera();
    }
    
    private void updatePlaying(float delta) {
    	if (playerSpawner.hasSpawnedPlayer()) 
    	{
        	elapsedTime += delta;
    	}
    	
    	updateWorld(delta);
    	
    	if (playerSpawner.hasSpawnedPlayer()) {
    		Player player = playerSpawner.getPlayer();
    		if (player.isDead()) {
    			effectTimeOut.reset(1);
    			controller.fadeOut();
    			state = State.LOST;
    		}
    	}
    }
    
    private void updateWon(float delta) {
    	updateWorld(delta);    	
    }    

	private void updateWorld(float delta) {
		for(GameActor newActor : newActorStore)
    		actors.add(newActor);
    	
    	actors.sort(GameActor.RenderSorter);
    	newActorStore.clear();
    	
    	worldUpdater.update(delta);
    	// TODO: Update actors according to fixed timestep.
    	for(int i = 0; i < actors.size; ++i) {
    		actors.get(i).update(delta, actors, newActorStore);
    	}
	}

	
	public void render() {
    	//fpsLogger.log();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    	
    	if (state == State.DONE ) {
    		Gdx.gl.glClearColor(0, 0, 0, 1);
    		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    		return;
    	}

    	backgroundCamera.position.x = camera.position.x * 0.5f;
    	backgroundCamera.update();
    	
    	batch.setProjectionMatrix(backgroundCamera.combined);
    	batch.begin(); 
    	{
    		background.render(batch, backgroundCamera);
    	}
		batch.end();
		
		batch.setProjectionMatrix(camera.combined);
		
		
		mapRenderer.render(LAYER_SET_A);
		
		batch.begin(); 
		{
			for(GameActor actor : actors){
				actor.render(batch);
			}
    	}
		
		batch.end();
		
		mapRenderer.render(LAYER_SET_B);
		
		timeLabel.setValue(Math.min(99.0f, elapsedTime));
		
		batch.setProjectionMatrix(hudCamera.combined);
		batch.begin(); 
		{
			Assets.instance.fonts.gameHud.draw(batch, coinsLabel, 0, hudCamera.viewportHeight);
			Assets.instance.fonts.gameHud.draw(batch, timeLabel,  0, hudCamera.viewportHeight, hudCamera.viewportWidth, Align.right, false);
			
			if (!introExitTextTimeOut.hasElapsed()) {
				float x1, x2;
				
				if (!introEnterTextTimeOut.hasElapsed()) {
					float f = introEnterTextTimeOut.getElapsed();
					float sx1 = -nameGlyphLayout.width; 
					float ex1 = (hudCamera.viewportWidth) / 2.0f;
					x1 = Interpolation.sineOut.apply(sx1, ex1, f);

					float sx2 = hudCamera.viewportWidth; 
					float ex2 = (hudCamera.viewportWidth) / 2.0f;
					x2 = Interpolation.sineOut.apply(sx2, ex2, f);
					
					
					
				}
				else {
					float f = introExitTextTimeOut.getElapsed();
					float sx1 = (hudCamera.viewportWidth) / 2.0f;
					float ex1 = hudCamera.viewportWidth + nameGlyphLayout.width;
					x1 = Interpolation.sineIn.apply(sx1, ex1, f);
					
					float sx2 = (hudCamera.viewportWidth) / 2.0f;
					float ex2 = -coinTargetGlyphLayout.width;
					x2 = Interpolation.sineIn.apply(sx2, ex2, f);
					
					
				}
				drawGlyphs(batch, x1, hudCamera.viewportHeight / 2.0f + nameGlyphLayout.height, Align.center, Align.center, Assets.instance.fonts.levelH1, nameGlyphLayout);
				drawGlyphs(batch, x2, hudCamera.viewportHeight / 2.0f - coinTargetGlyphLayout.height, Align.center, Align.center, Assets.instance.fonts.levelH2, coinTargetGlyphLayout);
			}
		}
		batch.end();
		
		
		GameActor player = GameActor.getActor(GameActorType.PLAYER, actors);
		
		if (state == State.WON) {
			renderWonEffect(player);
		}
		
		if (state == State.LOST) {
			renderLostEffect();
		}
    	
		controller.render();
		
		//worldDebugRenderer.render(world, camera.combined);    	
    }
	
	private void renderLostEffect() {
		shapeRenderer.setProjectionMatrix(hudCamera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		float w = hudCamera.viewportWidth;
		float h = hudCamera.viewportHeight;
		
		float d = (w / 2.0f) * effectTimeOut.getElapsed();
		
		shapeRenderer.box(0, 0, 0, d, h, 0);
		shapeRenderer.box(w-d, 0, 0, d, h, 0);
		
		shapeRenderer.end();
		
	}

	@SuppressWarnings("static-access")
	private void renderWonEffect(GameActor player) {
		Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
		Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(0, 0, 0, effectTimeOut.getElapsed() );
		
		float cx = camera.position.x - camera.viewportWidth / 2.0f;
		float cy = camera.position.y - camera.viewportHeight / 2.0f;
		shapeRenderer.rect(cx, cy, camera.viewportWidth, camera.viewportHeight);
		
		final int rays = 7;
		final float raySpread = 360.0f / rays;
		final float rayWidth = 360.0f / (rays * 2);
		for(int ray = 0; ray < rays; ++ray) {
			shapeRenderer.setColor(1.0f, 1.0f, 1.0f, Math.min(1f, effectTimeOut.getElapsed()*3));
			shapeRenderer.arc(player.getPosition().x, player.getPosition().y, 2*Math.max(camera.viewportWidth, camera.viewportHeight), effectTimeOut.getElapsed()*90 + ray * raySpread, rayWidth);
		}
		
		shapeRenderer.setColor(0, 0, 0, Math.max(0, Math.min(1.0f,effectTimeOut.getElapsed()*2-1 )));
		shapeRenderer.rect(cx, cy, camera.viewportWidth, camera.viewportHeight);
		
		shapeRenderer.end();
		Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		player.render(batch);
		batch.end();

		Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
		Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, Math.max(0, Math.min(1.0f,effectTimeOut.getElapsed()*2-1 )));
		shapeRenderer.rect(cx, cy, camera.viewportWidth, camera.viewportHeight);
		shapeRenderer.end();
		
		Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
	}

	public void grabCoin() {
		Assets.instance.sounds.playCoin();
		--capturedCoins;
		coinsLabel.setValue(capturedCoins);
		if (capturedCoins == 0) {
			Assets.instance.sounds.playLevelWon();
			state = State.WON;
			Player player = (Player)GameActor.getActor(GameActorType.PLAYER, actors);
			player.markAsWon();
			effectTimeOut.reset(3.0f);
			controller.fadeOut();
		}
	}

	public void setPlayScreen(PlayScreen playScreen) {
		this.playScreen = playScreen;
	}
}