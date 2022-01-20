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

public class Wall {
	
	private Body mBody;
	Sprite mSprite;
	private FixtureDef mFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.05f, 1f);
	private PhysicsWorld mPhysicsWorld;
	
	Wall(float pX, float pY, TextureRegion pTextureRegion, PhysicsWorld pPhysicsWorld, Scene pScene, float vel){
		mPhysicsWorld = pPhysicsWorld;
		mSprite = new Sprite(pX, pY, pTextureRegion);
		mBody = PhysicsFactory.createCircleBody(pPhysicsWorld, mSprite, BodyType.KinematicBody, mFixtureDef);
		pScene.attachChild(mSprite);
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, mBody, true, true));
		mBody.setLinearVelocity(0, vel);
	}
	
	public void moveWall (float pX, float pY, float vel){
		mBody.setTransform((pX+8) / 32f, (pY+8) / 32f, 0f); 
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mSprite, mBody, true, true));
		mBody.setLinearVelocity(0, vel);
	}
	
}