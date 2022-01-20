package com.acidnom.savethespiders;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player {	
	Body mBody;
	Sprite mSprite;
	final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.05f, 0.8f);
	boolean isAlive;
		
	Player(final float pX, final float pY, PhysicsWorld pPhysicsWorld, TextureRegion pTextureRegion, Scene pScene){
		mSprite = new Sprite(pX, pY, pTextureRegion);
		mSprite.setScale(0.6f);
		mBody = PhysicsFactory.createCircleBody(pPhysicsWorld, mSprite, BodyType.DynamicBody, playerFixtureDef);
		pScene.attachChild(mSprite);
	    pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, mBody, true, true));
    }
}
