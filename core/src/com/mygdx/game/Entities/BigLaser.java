package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BigLaser extends Sprite{
    private final Sprite bigLaserSprite;
    private Vector2 bigLaserPosition = new Vector2();
    private final Boss boss;

    public BigLaser(Boss boss){
        bigLaserSprite = new Sprite(new Texture("ufo_laser.png"));
        bigLaserSprite.setPosition(500, 500);
        this.boss = boss;
    }

    public void update(float delta){
        bigLaserPosition.x = (boss.getPositionUfo().x + boss.getUfoBoss().getWidth() / 2) - bigLaserSprite.getWidth() / 2;
        bigLaserPosition.y = boss.getPositionUfo().y - bigLaserSprite.getHeight();
    }

    public void draw(SpriteBatch batch){
        bigLaserSprite.setPosition(bigLaserPosition.x, bigLaserPosition.y);
        bigLaserSprite.draw(batch);
    }
}
