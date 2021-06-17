package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Missile extends Sprite {
    final Sprite missileSprite;
    Vector2 missilePosition = new Vector2();
    public static final int SPEED = 7;
    public static final float MISSILE_LIFETIME = 5f;
    float missileTimer = 0;
    public boolean remove = false;
    float rotation;
    PlayerBoss player = new PlayerBoss();

    public Missile(float x, float y, float rotation, PlayerBoss player) {
        missilePosition.x = x;
        missilePosition.y = y;
        missileSprite = new Sprite(new Texture("missile.png"));
        missileSprite.setSize(missileSprite.getWidth()/2, missileSprite.getHeight()/2);
        missileSprite.setRotation(rotation);
        this.player = player;
    }

    public void update(float delta){
        missileTimer += delta;

        trackPlayer();
        removeMissile();
    }

    public void draw (SpriteBatch batch){
        missileSprite.setPosition(missilePosition.x, missilePosition.y);
        missileSprite.draw(batch);
    }

    public void removeMissile(){
        if (missilePosition.y > Gdx.graphics.getHeight() || missilePosition.x > Gdx.graphics.getWidth() ||
                missilePosition.y < 0 || missilePosition.x < 0){
            remove = true;
        }

        if(missileTimer >= MISSILE_LIFETIME){
            remove = true;
        }
    }

    public void trackPlayer(){
        double angle = Math.atan2(player.getPosY() - missilePosition.y, player.getPosX() - missilePosition.x);

        missileSprite.setPosition(missilePosition.x += SPEED * Math.cos(angle), missilePosition.y += SPEED * Math.sin(angle));

        missileSprite.setRotation((float)Math.toDegrees(angle) - 90f);

    }


}
