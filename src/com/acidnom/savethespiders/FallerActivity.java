package com.acidnom.savethespiders;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.widget.EditText;

public class FallerActivity extends BaseGameActivity implements IAccelerometerListener, IOnMenuItemClickListener {
	
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	protected static final int MENU_UNPAUSE = 0;
	protected static final int MENU_QUIT = 1;
	protected static final int MENU_HIGHSCORES = 2;
	protected static final int MENU_REPLAY = 3;
	
	protected MenuScene mMenuScene;
	protected MenuScene mGameOverMenuScene;
	private int timeElapsed = 0;
	private boolean alive = true;
	
	// DO NOT INCLUDE A PRIVATE ENGINE IN DECLARATIONS
	private Camera mCamera;
	private Scene mScene;
	private PhysicsWorld mPhysicsWorld1;
	private PhysicsWorld mPhysicsWorld2;
	private Context mContext;
	private FallerActivity me;
	private HighScoreTable hsTable;
	private EditText nameEditText;
	
	private BitmapTextureAtlas playerTextureAtlas;
	private TextureRegion playerTextureRegion;
	private BitmapTextureAtlas wallTextureAtlas;
	private TextureRegion wallTextureRegion;
	
	private BitmapTextureAtlas unpauseTextureAtlas;
	private TextureRegion unpauseTextureRegion;
	private BitmapTextureAtlas quitTextureAtlas;
	private TextureRegion quitTextureRegion;
	private BitmapTextureAtlas highscoresTextureAtlas;
	private TextureRegion highscoresTextureRegion;
	private BitmapTextureAtlas replayTextureAtlas;
	private TextureRegion replayTextureRegion;
	private BitmapTextureAtlas mFontTexture;
	private TextureRegion bgTextureRegion;
	private BitmapTextureAtlas bgTextureAtlas;
	private Font mFont;
	
	private Wall[] mWall;
	int contorLeft, contorRight;
	
	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera( 0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions mEngineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		//engineOptions.getTouchOptions().setRunOnUpdateThread(true); - ONLY FOR TOUCH EVENTS
		mEngineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(mEngineOptions);
	}
	
	@Override
	public void onLoadResources() {
		this.playerTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.playerTextureAtlas, this, "gfx/player.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.playerTextureAtlas);
		this.wallTextureAtlas = new BitmapTextureAtlas(16, 16, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.wallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wallTextureAtlas, this, "gfx/wall.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.wallTextureAtlas);
		this.unpauseTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.unpauseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.unpauseTextureAtlas, this, "gfx/unpause.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.unpauseTextureAtlas);
		this.quitTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.quitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.quitTextureAtlas, this, "gfx/quit.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.quitTextureAtlas);
		this.highscoresTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.highscoresTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.highscoresTextureAtlas, this, "gfx/highscores.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.highscoresTextureAtlas);
		this.replayTextureAtlas = new BitmapTextureAtlas(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.replayTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.replayTextureAtlas, this, "gfx/replay.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.replayTextureAtlas);
		this.bgTextureAtlas = new BitmapTextureAtlas(512,1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.bgTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bgTextureAtlas, this, "gfx/gamebg.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.bgTextureAtlas);
		
		this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 16, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.mScene = new Scene();
		
		mContext = this;
		me = this;
		hsTable = new HighScoreTable(this);
		nameEditText = new EditText(this);
		
		Sprite bg = new Sprite(0, 0, this.bgTextureRegion);
		this.mScene.attachChild(bg);
		
		this.mMenuScene = this.createMenuScene();
		this.mGameOverMenuScene = this.createGameOverMenuScene(); 
		
		this.mScene.setBackground(new ColorBackground(0f, 0f, 0f));
		this.mPhysicsWorld1 = new PhysicsWorld(new Vector2(0, 10), false);
		this.mPhysicsWorld2 = new PhysicsWorld(new Vector2(0, -10), false);
		final Player player1 = new Player(120f, 360f, mPhysicsWorld1, playerTextureRegion, mScene);
		final Player player2 = new Player(360f, 360f, mPhysicsWorld2, playerTextureRegion, mScene);
		player1.isAlive = true;
		player2.isAlive = true;
		
		final Random random = new Random();
		mWall = new Wall[300];
		
		final ChangeableText elapsedText = new ChangeableText(10, 10, this.mFont, "Time:0s", "Seconds elapsed: XXXXXs".length());
		mScene.attachChild(elapsedText);

		mScene.registerUpdateHandler(new TimerHandler(1f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				timeElapsed++;
				elapsedText.setText("Time:" + timeElapsed+"s");
			}
		}));
		
		final Shape ground1 = new Rectangle(0, 0, 4, CAMERA_HEIGHT);
		final Shape ground2 = new Rectangle(238, 0, 4, CAMERA_HEIGHT);
		final Shape ground3 = new Rectangle(476, 0, 4, CAMERA_HEIGHT);
		final Shape ground4 = new Rectangle(0, 0, 4, CAMERA_HEIGHT);
		final Shape ground5 = new Rectangle(238, 0, 4, CAMERA_HEIGHT);
		final Shape ground6 = new Rectangle(476, 0, 4, CAMERA_HEIGHT);
		final FixtureDef groundFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld1, ground1, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld1, ground2, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld1, ground3, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld2, ground4, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld2, ground5, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld2, ground6, BodyType.StaticBody, groundFixtureDef);
		this.mScene.attachChild(ground1);
		this.mScene.attachChild(ground2);
		this.mScene.attachChild(ground3);
		this.mScene.attachChild(ground4);
		this.mScene.attachChild(ground5);
		this.mScene.attachChild(ground6);
		
		final Shape top1 = new Rectangle(0, -1, CAMERA_WIDTH, 1);
		final Shape bottom1 = new Rectangle(0, CAMERA_HEIGHT+1, CAMERA_WIDTH, 1);
		final Shape top2 = new Rectangle(0, -1, CAMERA_WIDTH, 1);
		final Shape bottom2 = new Rectangle(0, CAMERA_HEIGHT+1, CAMERA_WIDTH, 1);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld1, top1, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld2, top2, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld1, bottom1, BodyType.StaticBody, groundFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld2, bottom2, BodyType.StaticBody, groundFixtureDef);
		this.mScene.attachChild(top1);
		this.mScene.attachChild(top2);
		this.mScene.attachChild(bottom1);
		this.mScene.attachChild(bottom2);
		
		//create walls
		for (int i=0; i<=149; i++){
			mWall[i] = new Wall(-100f, -100f, wallTextureRegion, mPhysicsWorld1, mScene, 0f);
		}
		for (int i=150; i<=299; i++){
			mWall[i] = new Wall(-100f, -100f, wallTextureRegion, mPhysicsWorld2, mScene, 0f);
		}
		
		contorLeft = 0;
		contorRight = 150;
		
		for (int j=0; j<=5; j++){
			int rand= random.nextInt(11);
			rand = rand + 2;
			for (int i=0; i<=14;i++){
				if (i!=rand && i!=(rand+1)){
					mWall[contorLeft].moveWall(16f * i, 140f * j, -1f);
					contorLeft++;
					if (contorLeft == 149) 
						contorLeft = 0;
				}
			}
		}
		
		for (int j=0; j<=5; j++){
		int rand = random.nextInt(11);
		rand = rand + 2;
		for (int i=0; i<=14;i++){
			if (i!=rand && i!=(rand+1)){
				mWall[contorRight].moveWall(240 + 16f * i, 20f + 120f * j, 1f);
				contorRight++;
				if (contorRight == 299) 
					contorRight = 150;
				}
			}
		}
		
		mScene.registerUpdateHandler(new TimerHandler(4.0f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				int rand= random.nextInt(12);
				rand = rand + 1;
				for (int i=0; i<=14;i++){
					if (i!=rand && i!=(rand+1)){
						mWall[contorLeft].moveWall(16f * i, 730f, -1f);
						contorLeft++;
						if (contorLeft == 149) 
							contorLeft = 0;
					}
				}
				rand= random.nextInt(13);
				rand = rand + 1;
				for (int i=0; i<=14;i++){
					if (i!=rand && i!=(rand+1)){
						mWall[contorRight].moveWall(240f + 16f * i, -10f, 1f);
						contorRight++;
						if (contorRight == 299) 
							contorRight = 150;
					}
				}
			}
		}));
		
		mScene.registerUpdateHandler(new IUpdateHandler() {
			@Override 
			public void reset() {
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed){
				if (!alive) return; //this way it wills top after one display of enter hs 
				if (player1.mSprite.collidesWith(top1) || player2.mSprite.collidesWith(top2) || player1.mSprite.collidesWith(bottom1) || player2.mSprite.collidesWith(bottom2)) {
					gameover();
					alive = false;
				}
			}
		});
		
		this.mScene.registerUpdateHandler(this.mPhysicsWorld1);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld2);
		return this.mScene;
	}
	
	private void gameover()
	{// JUST ONE ARG, not4
		mScene.setChildScene(mGameOverMenuScene);
		if (timeElapsed > hsTable.getLowestHighScore())
		{//display high score window
			 mScene.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {   
	          	    @Override
	          	    public void onTimePassed(final TimerHandler pTimerHandler){   
	          	    	
	          	    	me.runOnUiThread(new Runnable() /*can't create handler inside thread that has not called looper.prepare: this is the solution I used*/
		                {
	                        @Override
	                        public void run()
	                        {
	                        	me.showDialog(0);
	                        }
		                });
	          	        mScene.unregisterUpdateHandler(pTimerHandler);
	          	    }
	          	}));
		}
	}

	@Override
	public void onLoadComplete() {
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
	public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
		final Vector2 gravity1 = Vector2Pool.obtain(pAccelerometerData.getX(), 10);
		final Vector2 gravity2 = Vector2Pool.obtain(pAccelerometerData.getX(), -10);
		this.mPhysicsWorld1.setGravity(gravity1);
		this.mPhysicsWorld2.setGravity(gravity2);
		Vector2Pool.recycle(gravity1);
		Vector2Pool.recycle(gravity2);
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mScene.hasChildScene()) {
				this.mMenuScene.back();
			} else {
				this.mScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_UNPAUSE:
				this.mMenuScene.back();
				return true;
			case MENU_QUIT:
				this.finish();
				return true;
			case MENU_HIGHSCORES:
				startActivity(new Intent(mContext, HighScoreChart.class));
				return true;
			case MENU_REPLAY:
				Intent intent = getIntent();
				finish();
				startActivity(intent);
				return true;
			default:
				return false;
		}
	}

	protected MenuScene createMenuScene() {
		MenuScene mMenuScene = new MenuScene(this.mCamera);

		final SpriteMenuItem unpauseMenuItem = new SpriteMenuItem(MENU_UNPAUSE, this.unpauseTextureRegion);
		unpauseMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(unpauseMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.quitTextureRegion);
		quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(quitMenuItem);

		mMenuScene.buildAnimations();
		mMenuScene.setBackgroundEnabled(false);
		mMenuScene.setOnMenuItemClickListener(this);
		return mMenuScene;
	}
	
	protected MenuScene createGameOverMenuScene() {
		MenuScene mMenuScene = new MenuScene(this.mCamera);

		final SpriteMenuItem highscoresMenuItem = new SpriteMenuItem(MENU_HIGHSCORES, this.highscoresTextureRegion);
		highscoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(highscoresMenuItem);

		final SpriteMenuItem replayMenuItem = new SpriteMenuItem(MENU_REPLAY, this.replayTextureRegion);
		replayMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mMenuScene.addMenuItem(replayMenuItem);

		mMenuScene.buildAnimations();
		mMenuScene.setBackgroundEnabled(false);
		mMenuScene.setOnMenuItemClickListener(this);
		return mMenuScene;
	}
	
	@Override
	protected Dialog onCreateDialog(final int pID) {
		switch(pID) {
			case 0:
				return new AlertDialog.Builder(FallerActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("HIGH SCORE! Name:")
				.setCancelable(true)
				.setView(nameEditText)
				.setPositiveButton("Add", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
						hsTable.addNewHighScore(nameEditText.getText().toString(), timeElapsed);
					}
				})
				.setNegativeButton("Cancel", new OnClickListener() {
					@Override
					public void onClick(final DialogInterface pDialog, final int pWhich) {
					}
				})
				.create();
			default:
				return super.onCreateDialog(pID);
		}
	}

}