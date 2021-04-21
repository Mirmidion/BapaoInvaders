package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Bullet;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    private int posX = 0;
    private int posY = 100;
    private Texture playerSprite = new Texture(Gdx.files.internal("Playership.png"));
    private int gun = 1;
    private long time = 0;
    private int health = 100;

    public Player (int width){
        this.posX = width/2-playerSprite.getWidth()/2;
    }

    public void setPosX(int x, int width){
        if (posX + x > width - playerSprite.getWidth()){
            posX = width - playerSprite.getWidth();
        }
        else if (posX + x < 0){
            posX = 0;
        }
        else {
            posX += x;
        }
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public Texture getPlayerSprite() {
        return playerSprite;
    }

    public void shoot(){
        if (TimeUtils.millis() - time > 500){
            Bullet.allBullets.add(new Bullet(playerSprite.getWidth()-((gun == 1)?40:104),playerSprite.getHeight()-32, true, this));
            time = TimeUtils.millis();
            gun *= -1;
        }
    }

    public void bulletRemove(Bullet bulletToRemove) {

    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public void resetPosition(int width){
        this.posX = width/2-playerSprite.getWidth()/2;
    }

}