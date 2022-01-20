package com.acidnom.savethespiders;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Menu extends BaseGameActivity implements  IAccelerometerListener, IOnSceneTouchListener {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;

	private BitmapTextureAtlas newgameButtonTextureAtlas;
	private BitmapTextureAtlas highscoresButtonTextureAtlas;
	private BitmapTextureAtlas helpaboutButtonTextureAtlas;
	private BitmapTextureAtlas quitButtonTextureAtlas;
	private BitmapTextureAtlas bgTextureAtlas;
	
	private TextureRegion newgameButtonTextureRegion;
	private TextureRegion highscoresButtonTextureRegion;
	private TextureRegion helpaboutButtonTextureRegion;
	private TextureRegion quitButtonTextureRegion;
	private TextureRegion bgTextureRegion;
	
	public static Context gameContext;
	private Scene mScene;
	private PhysicsWorld mPhysicsWorld;
	private Context context ;

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		
		context = this;
		
		/* Textures. */
		this.newgameButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.highscoresButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.helpaboutButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.quitButtonTextureAtlas = new BitmapTextureAtlas(256,128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.bgTextureAtlas = new BitmapTextureAtlas(512,1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/* TextureRegions. */
		this.newgameButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.newgameButtonTextureAtlas, this, "newgame.png", 0, 0);
		this.highscoresButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.highscoresButtonTextureAtlas, this, "highscores.png", 0, 0);
		this.helpaboutButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.helpaboutButtonTextureAtlas, this, "helpabout.png", 0, 0);
		this.quitButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.quitButtonTextureAtlas, this, "quit.png", 0, 0);
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bgTextureAtlas, this, "background.png", 0, 0);
	
		this.mEngine.getTextureManager().loadTexture(this.bgTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.newgameButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.highscoresButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.helpaboutButtonTextureAtlas);
		this.mEngine.getTextureManager().loadTexture(this.quitButtonTextureAtlas);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		this.mScene.setBackground(new ColorBackground(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.LIGHT_NO_MOON), false);

		final Shape ground = new Rectangle(3, CAMERA_HEIGHT - 3, CAMERA_WIDTH-6, 1);
		final Shape roof = new Rectangle(3, 3, CAMERA_WIDTH-6, 1);
		final Shape left = new Rectangle(3, 3, 1, CAMERA_HEIGHT-6);
		final Shape right = new Rectangle(CAMERA_WIDTH - 3, 3, 1, CAMERA_HEIGHT-6);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		Sprite bg = new Sprite(0, 0, this.bgTextureRegion);
		this.mScene.attachChild(bg);

		int centerX = (CAMERA_WIDTH - 2 * this.newgameButtonTextureRegion.getWidth()) / 3;
		int centerY = CAMERA_HEIGHT / 20 * 13;
		final Sprite newgameButton = new Sprite(centerX, centerY, this.newgameButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					startActivity(new Intent(context, FallerActivity.class));
				}
				return true;
			}
		};
		mScene.attachChild(newgameButton);
		mScene.registerTouchArea(newgameButton);

		centerX = (CAMERA_WIDTH - 2 * this.highscoresButtonTextureRegion.getWidth()) / 3 * 2 + this.highscoresButtonTextureRegion.getWidth();
		centerY = CAMERA_HEIGHT / 20 * 13;
		final Sprite highscoresButton = new Sprite(centerX, centerY, this.highscoresButtonTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					startActivity(new Intent(context, HighScoreChart.class));
				}
				return true;
			}
		};
		mScene.attachChild(highscoresButton);
		mScene.registerTouchArea(highscoresButton);
		
		centerX = (CAMERA_WIDTH - 2 * this.helpaboutButtonTextureRegion.getWidth()) / 3;
		centerY = CAMERA_HEIGHT / 20 * 15;
		final Sprite helpaboutButton = new Sprite(centerX, centerY, this.helpaboutButtonTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					Intent helpIntent = new Intent (context, Help.class);
					startActivity(helpIntent);
				}
				return true;
			}
		};
		mScene.attachChild(helpaboutButton);
		mScene.registerTouchArea(helpaboutButton);
		
		centerX = (CAMERA_WIDTH - 2 * this.quitButtonTextureRegion.getWidth()) / 3 * 2 + this.quitButtonTextureRegion.getWidth();
		centerY = CAMERA_HEIGHT / 20 * 15;
		final Sprite quitButton = new Sprite(centerX, centerY, this.quitButtonTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					finish();
				}
				return true;
			}
		};
		mScene.attachChild(quitButton);
		mScene.registerTouchArea(quitButton);
		
		mScene.setTouchAreaBindingEnabled(true);
		
		gameContext = context;
		
		return this.mScene;
	}

	@Override
	public void onLoadComplete() {
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getX(), pAccelerometerData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerometerSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerometerSensor();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		this.disableAccelerometerSensor();
	}
}