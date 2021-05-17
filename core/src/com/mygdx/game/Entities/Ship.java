package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;

import java.io.Serializable;

public abstract class Ship implements Serializable {
    //Position
    float posX;
    float posY;

    //Which gun will shoot next
    int gun = 1;

    //Health of ship
    int health;

    //Sprite, not serializable
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
