package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Defense extends Ship{

    //Different textures
    private final Texture shipSpriteBroken = new Texture(Gdx.files.internal("Bapao1.png"));


    public Defense(int posX, int posY){
        this.health = 100;
        this.posX = posX;
        this.posY = posY;
        shipSprite = new Texture(Gdx.files.internal("Bapao.png"));
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public float getPosY() {
        return posY;
    }

    @Override
    public float getPosX() {
        return posX;
    }

    @Override
    public Texture getSprite() {
        return (health > 50)? shipSprite : shipSpriteBroken;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }
}
