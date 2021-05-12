package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Entities.Bullet;

public class Enemy extends Ship{

    //For the random position
    //targetX is in the range of 50 and 1870 - width of the sprite
    //targetY is in the range of 400 and 1050 - height of the sprite
    private float targetX;
    private float targetY;
    private int limitXLeft;
    private int limitXRight;
    private int enemyClass;
    private float speed = 1;


    // 1 == right	-1 == left
    private int directionOfMoving = -1;

    // Finite state machine:


    public Enemy(int enemyClass, int x, int y, int limitXLeft, int limitXRight){
        this.enemyClass = enemyClass;
        if (enemyClass==1) {
            // Cruiser (average speed and damage)
                this.shipSprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 100;
            }
        // Falcon (very agile, not much damage)
        else if (enemyClass == 2){
                this.shipSprite = new Texture("Falcon.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1.5f;
                this.health = 50;
        }
        // Fighter (little above average damage, average speed)
        else if (enemyClass == 3) {
                this.shipSprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 150;
            }
            // Tank (high health, high damage but slow)
           else if (enemyClass == 4) {
            this.shipSprite = new Texture("Enemyship.png");
            this.limitXLeft = limitXLeft;
            this.limitXRight = limitXRight;
            this.posX = x;
            this.posY = y;
            this.speed = 1;
            this.health = 300;
        }

    }

    public void moveEnemy(){
        posX += 2 * directionOfMoving * 0.5f * this.speed;
        posY += -1 * 0.1f ;
        if (posX <= limitXLeft || posX >= limitXRight){
            this.directionOfMoving *= -1;
        }
        if (/*posX == (limitXRight-limitXLeft)/2f+limitXLeft */ MathUtils.random(-100,100) > 99){
            this.shoot();
        }
    }

    public void shoot(){
        Bullet.allBullets.add(new Bullet(shipSprite.getWidth()-((gun == 1)?40:104),shipSprite.getHeight()-140, false, this));
        this.gun *= -1;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }



    public int getEnemyClass() {
        return enemyClass;
    }

    public void refreshTextures(){
        if (this.enemyClass == 1){
            this.shipSprite = new Texture("Enemyship.png");
        }
        else if (this.enemyClass == 2){
            this.shipSprite = new Texture("Falcon.png");
        }
        else if (this.enemyClass == 3){
            this.shipSprite = new Texture("Enemyship.png");
        }
        else if (this.enemyClass == 4){
            this.shipSprite = new Texture("Enemyship.png");
        }
    }
}
