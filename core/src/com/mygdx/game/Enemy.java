package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

public class Enemy{
    private float posX;
    private float posY;
    private int limitXLeft;
    private int limitXRight;
    private int enemyClass;
    private int speed = 1;
    private int gun = 1;
    private Texture enemySprite;
    static ArrayList<Bullet> allEnemyBullets = new ArrayList<Bullet>();
    private int health;

    // 1 == right	-1 == left
    private int directionOfMoving = -1;

    public Enemy(int enemyClass, int x, int y, int limitXLeft, int limitXRight){
        this.enemyClass = enemyClass;
        switch (enemyClass) {
            // Cruiser (average speed and damage)
            case 1: {
                this.enemySprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 100;
            }
            // Falcon (very agile, not much damage)
            case 2: {
                this.enemySprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 50;
            }
            // Fighter (little above average damage, average speed)
            case 3: {
                this.enemySprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 150;
            }
            // Tank (high health, high damage but slow)
            case 4: {
                this.enemySprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 300;
            }
            default:{
                this.enemySprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 100;
            }
        }
    }

    public void moveEnemy(){
        posX += 2 * directionOfMoving * 0.5f;
        posY += -1 * 0.1f;
        if (posX <= limitXLeft || posX >= limitXRight){
            directionOfMoving *= -1;
        }
        if (/*posX == (limitXRight-limitXLeft)/2f+limitXLeft */ MathUtils.random(-100,100) > 99){
            this.shoot();
        }
    }

    public void shoot(){
        Bullet.allBullets.add(new Bullet(enemySprite.getWidth()-((gun == 1)?40:104),enemySprite.getHeight()-140, false, this));
        this.gun *= -1;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public Texture getEnemySprite() {
        return enemySprite;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public int getHealth() {
        return health;
    }

    public int getEnemyClass() {
        return enemyClass;
    }
}
