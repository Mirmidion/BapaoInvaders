package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Bullet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Enemy extends Ship{

    //For the random position
    //targetX is in the range of 50 and 1870 - width of the sprite
    //targetY is in the range of 400 and 1050 - height of the sprite
    private Rectangle targetArea;
    private Rectangle flyArea;
    private Rectangle hideArea;
    private Rectangle protectArea;
    private float targetX;
    private float targetY;
    private int limitXLeft;
    private int limitXRight;
    private int enemyClass;
    private float speed = 1;

    private int maxHealth;


    // 1 == right	-1 == left
    private int directionOfMoving = -1;

    States currentState;
    private long previousStateChange = 0;

    private boolean gotHit = false;
    private long timeAfterHit = 0;

    private Planet homePlanet;

    static LinkedList<Enemy> vulnerableEnemies = new LinkedList<>();

    // Finite state machine:
    private enum States{
        //Enemy moves and shoots randomly
        MoveRandom{

            @Override
            public States changeState(Enemy enemy, Player player){

                if (enemy.playerInView(player)){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    return Attack;
                }
                else if (enemy.bulletInView()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    enemy.avoidBullet();
                    return Avoid;
                }
                else if (enemy.iGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.hideArea);
                    enemy.calculateNewPosition();
                    return Hide;
                }
                else if (enemy.enemyGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.protectArea);
                    enemy.calculateNewPosition();
                    return Protect;
                }
                return MoveRandom;
            }

        },
        //Enemy tries to attack the player
        Attack{

            @Override
            public States changeState(Enemy enemy, Player player){

                if (!enemy.playerInView(player)){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    return MoveRandom;
                }
                else if (enemy.bulletInView()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    enemy.avoidBullet();
                    return Avoid;
                }
                return Attack;
            }
        },
        Avoid{

            @Override
            public States changeState(Enemy enemy, Player player){

                if (!enemy.bulletInView()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    return MoveRandom;
                }
                else if (enemy.iGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.hideArea);
                    enemy.calculateNewPosition();
                    return Hide;
                }
                else if (enemy.enemyGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.protectArea);
                    enemy.calculateNewPosition();
                    return Protect;
                }
                return Avoid;
            }
        },
        Hide{

            @Override
            public States changeState(Enemy enemy, Player player){

                if (enemy.bulletInView()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.avoidBullet();
                    return Avoid;
                }
                else if (enemy.enemyIsNearDeath(enemy)){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.protectArea);
                    enemy.calculateNewPosition();
                    return Protect;
                }
                return Hide;
            }
        },
        Protect{

            @Override
            public States changeState(Enemy enemy, Player player){

                //if (enemy.bulletInView()){
                //    enemy.setPreviousStateChange(TimeUtils.millis());
                //    enemy.setTargetArea(enemy.flyArea);
                //    enemy.calculateNewPosition();
                //    return Avoid;
                //}
                if (enemy.iGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.hideArea);
                    enemy.calculateNewPosition();
                    return Hide;
                }
                else if (!enemy.enemyGotHit()){
                    enemy.setPreviousStateChange(TimeUtils.millis());
                    enemy.setTargetArea(enemy.flyArea);
                    enemy.calculateNewPosition();
                    return MoveRandom;
                }
                return Protect;
            }
        };

        public abstract States changeState(Enemy enemy, Player player);

    }


    public Enemy(int enemyClass, int x, int y, int limitXLeft, int limitXRight, Planet homePlanet){
        this.enemyClass = enemyClass;
        this.homePlanet = homePlanet;
        if (enemyClass==1) {
            // Cruiser (average speed and damage)
                this.shipSprite = new Texture("Enemyship.png");
                this.limitXLeft = limitXLeft;
                this.limitXRight = limitXRight;
                this.posX = x;
                this.posY = y;
                this.speed = 1;
                this.health = 100;
                this.maxHealth = 100;
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
                this.maxHealth = 50;
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
                this.maxHealth = 150;
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
            this.maxHealth = 300;
        }

           this.flyArea = new Rectangle(50,400,1870-this.shipSprite.getWidth(), 650-this.shipSprite.getHeight());
           this.hideArea = new Rectangle(50,700,1870-this.shipSprite.getWidth(),250-this.shipSprite.getHeight());
           this.protectArea = new Rectangle(50,400,1870-this.shipSprite.getWidth(),350-this.shipSprite.getHeight());
           this.targetArea = new Rectangle(50,400,1870-this.shipSprite.getWidth(), 650-this.shipSprite.getHeight());

           this.currentState = States.MoveRandom;
           this.calculateNewPosition();
    }

    public void update(Player player){
        if (currentState == States.MoveRandom){
            System.out.println("random");
            if (this.posX == targetX && this.posY == targetY){
                this.calculateNewPosition();
            }
            else{
                this.moveEnemy();
            }
        }
        else if (currentState == States.Attack){
            System.out.println("attack");
            Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getSprite().getWidth(), player.getSprite().getHeight());
            Rectangle enemyAttackRect = new Rectangle(this.getPosX()-30, 0, this.shipSprite.getWidth()+30, 1080);
            if (MathUtils.random(-100,100) > 98 && overlaps(playerRect, enemyAttackRect)){

                this.shoot();
            }
            this.moveEnemy();
        }
        else if (currentState == States.Hide){
            System.out.println("hide");
            this.moveEnemy();
        }
        else if (currentState == States.Protect){
            System.out.println("protect");
            this.moveEnemy();
        }
        else if (currentState == States.Avoid){
            System.out.println("avoid");
            //todo Set target position to area without bullets (try 3 times, else just use the last one calculated):
            if (this.posX == targetX && this.posY == targetY){
                this.avoidBullet();
            }
            //
            this.moveEnemy();
        }
        //if (TimeUtils.millis() - previousStateChange > 3000) {
            currentState = currentState.changeState(this, player);
        //}
    }

    public void moveEnemy(){
        if (posY > 1000) {
            posX += 2 * directionOfMoving * 0.5f * this.speed;
            posY += -1 * 0.1f;
            if (posX <= limitXLeft || posX >= limitXRight) {
                this.directionOfMoving *= -1;
            }
        }
        else {
            float distanceX = targetX - posX;
            float distanceY = targetY - posY;
            posX += distanceX / 200;
            posY += distanceY / 200;
            Rectangle zone = new Rectangle(this.posX - 20, this.posY - 20, this.shipSprite.getWidth()+40, this.shipSprite.getHeight()+40);
            Rectangle target = new Rectangle(targetX-2, targetY-2, 5, 5);
            if (overlaps(zone, target)){
                this.calculateNewPosition();
            }
        }
    }

    public void calculateNewPosition(){
        this.targetX = MathUtils.random(this.targetArea.x, this.targetArea.x+this.targetArea.width);
        this.targetY = MathUtils.random(this.targetArea.y,  this.targetArea.y+this.targetArea.height);
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

    public boolean bulletInView(){
        //Enemy view distance for bullets = (140, 300)
        Rectangle viewDistance = new Rectangle(this.posX,this.posY-shipSprite.getHeight(),140,300);
        for (Bullet bullet : Bullet.getAllBullets()){
            Rectangle bulletRect = new Rectangle(bullet.getPosX(), bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
            if (overlaps(bulletRect, viewDistance)){
                return true;
            }
        }
        return false;
    }

    public boolean playerInView(Player player){
        //Enemy view distance for player = (140, 400)
        Rectangle viewDistance = new Rectangle(this.posX,this.posY-400,140,400);
        Rectangle playerRect = new Rectangle(player.getPosX(), player.getPosY(), player.getSprite().getWidth(),player.getSprite().getHeight());

        return overlaps(viewDistance, playerRect);
    }

    public boolean enemyGotHit(){
        for (Enemy enemy : homePlanet.getEnemyWaves()){
            if(enemy.isGotHit() && TimeUtils.millis() - enemy.getTimeAfterHit() < 5000 && enemy != this){
                return true;
            }
            else if (TimeUtils.millis() - enemy.getTimeAfterHit() > 5000){
                enemy.setGotHit(false);
            }
        }
        return false;
    }

    public boolean iGotHit(){
        if (gotHit && TimeUtils.millis() - timeAfterHit > 3000){
            gotHit = false;
            for (Enemy enemy : homePlanet.getEnemyWaves()){
                if (!enemyGotHit()){
                    enemy.setCurrentState(States.Protect);
                    if (homePlanet.getEnemyWaves().indexOf(enemy)>4){
                        break;
                    }
                }
            }
            return true;
        }
        else if (gotHit){
            return true;
        }
        return false;
    }

    public boolean enemyIsNearDeath(Enemy enemyHurt){
        for (Enemy enemy : homePlanet.getEnemyWaves()){
            if (enemy.getHealth() <= 50 && !vulnerableEnemies.contains(this) && enemyHurt != this){
                vulnerableEnemies.add(this);
                return true;
            }
        }
        return false;
    }

    public void setGotHit() {
        gotHit = true;
        this.timeAfterHit = TimeUtils.millis();
    }

    public boolean isGotHit() {
        return gotHit;
    }

    public long getTimeAfterHit() {
        return timeAfterHit;
    }

    public void setGotHit(boolean gotHit) {
        this.gotHit = gotHit;

    }

    public boolean overlaps (Rectangle r, Rectangle r2){
        return (r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y);
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    public void setPreviousStateChange(long previousStateChange) {
        this.previousStateChange = previousStateChange;
    }

    public void setTargetArea(Rectangle targetArea) {
        this.targetArea = targetArea;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setCurrentState(States currentState) {
        this.currentState = currentState;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void avoidBullet(){
        outerloop:
        for (int i = 0; i < 6; i++){
            this.calculateNewPosition();
            Rectangle enemyRect = new Rectangle(this.targetX-20, 0, this.shipSprite.getWidth()+40, 1000);
            for (Bullet bullet : Bullet.allBullets){
                if (bullet.getFriendly()){
                    continue;
                }
                else {

                    Rectangle bulletRect = new Rectangle(bullet.getPosX(), bullet.getPosY(), bullet.getLaser().getWidth(), 1000);

                    if (!this.overlaps(enemyRect, bulletRect)) {
                        break outerloop;

                    }
                }
            }
        }
    }
}
