package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Defense {

    //Health of the defense
    private int health;

    //Different textures
    private final Texture textureBroken = new Texture(Gdx.files.internal("Bapao1.png"));
    private final Texture texture = new Texture(Gdx.files.internal("Bapao.png"));

    //Position
    private final int posY;
    private final int posX;

    public Defense(int posX, int posY){
        this.health = 100;
        this.posX = posX;
        this.posY = posY;
    }

    public int getHealth() {
        return health;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public Texture getTexture() {
        return (health > 50)? texture : textureBroken;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }
}
