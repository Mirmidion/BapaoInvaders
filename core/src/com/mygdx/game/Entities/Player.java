package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Bullet;

import java.util.ArrayList;
import java.util.Iterator;

public class Player extends Ship{

    private long time = 0;
    private boolean invulnerable = false;
    private long lastTimeInvulnerable = 0;

    public Player (int width){

        shipSprite = new Texture(Gdx.files.internal("Playership.png"));
        posX = width/2f-shipSprite.getWidth()/2f;
        health = 100;
        posY = 100;
    }

    public void setPosX(int x, int width){
        if (posX + x > width - shipSprite.getWidth()){
            posX = width - shipSprite.getWidth();
        }
        else if (posX + x < 0){
            posX = 0;
        }
        else {
            posX += x;
        }

    }

    public void shoot(){
        if (TimeUtils.millis() - time > 500){
            Bullet.allBullets.add(new Bullet(shipSprite.getWidth()-((gun == 1)?40:104),shipSprite.getHeight()-32, true, this));
            time = TimeUtils.millis();
            gun *= -1;
        }
    }

    public void bulletRemove(Bullet bulletToRemove) {

    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void invulnerableTime(){
        if (TimeUtils.millis() - lastTimeInvulnerable >= 3000){
            this.setInvulnerable(false);
            this.lastTimeInvulnerable = TimeUtils.millis();
        }
    }

    public void setLastTimeInvulnerable() {
        this.lastTimeInvulnerable = TimeUtils.millis();
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public void resetPosition(int width){
        this.posX = width/2f-shipSprite.getWidth()/2f;
    }
}
