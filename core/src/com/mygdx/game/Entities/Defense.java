package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Defense {
    private int health;
    private Texture texture1 = new Texture(Gdx.files.internal("Bapao1.png"));
    private Texture texture = new Texture(Gdx.files.internal("Bapao.png"));
    private int posY;
    private int posX;

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
        return (health > 50)? texture : texture1;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }
}
