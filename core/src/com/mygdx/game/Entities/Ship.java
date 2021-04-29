package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;

import java.io.Serializable;

public abstract class Ship implements Serializable {
    float posX;
    float posY;
    int gun = 1;
    int health;
    transient Texture shipSprite;

    public float getPosY() {
        return posY;
    }

    public float getPosX() {
        return posX;
    }

    public int getHealth() {
        return this.health;
    }

    public Texture getSprite() {
        return shipSprite;
    }

}
