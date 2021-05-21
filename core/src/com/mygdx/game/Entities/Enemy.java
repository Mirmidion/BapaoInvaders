package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Enums.States;

import java.util.ArrayList;
import java.util.LinkedList;

public class Enemy extends Ship{

    //For the random position
    //targetX is in the range of 50 and 1870 - width of the sprite
    //targetY is in the range of 400 and 1050 - height of the sprite
    private float targetX;
    private float targetY;

    //Variables to store a temporary position
    private float tempX;
    private float tempY;

    //Needed when enemies enter the level, how far they can go left and right
    private final int limitXLeft;
    private final int limitXRight;

    //Type of enemy
    private final int enemyClass;

    //The speed of flying
    private float speed = 1;

    //Different areas to fly in
    private Rectangle targetArea;
    private final Rectangle flyArea;
    private final Rectangle hideArea;
    private final Rectangle protectArea;

    //Max health to make a health bar
    private int maxHealth;

    //Can it take damage? (Only when in screenspace)
    private boolean invulnerable = true;


    // 1 == right	-1 == left
    private int directionOfMoving = -1;

    //The current state of the enemy
    private States currentState;

    //Time of the previous state change
    private long previousStateChange = 0;

    //Was it hit?
    private boolean gotHit = false;

    //Time after it got hit
    private long timeAfterHit = 0;

    //Home planet of the enemy
    private final Planet homePlanet;

    //All vulnerable enemies in the current level
    static ArrayList<Enemy> vulnerableEnemies = new ArrayList<>();




    public Enemy(int enemyClass, int x, int y, int limitXLeft, int limitXRight, Planet homePlanet){
        this.enemyClass = enemyClass;
        this.homePlanet = homePlanet;
        this.posX = x;
        this.posY = y;
        this.limitXLeft = limitXLeft;
        this.limitXRight = limitXRight;

        if (enemyClass==1) {
            // Cruiser (average speed and damage)
            this.shipSprite = new Texture("Enemyship.png");
            this.speed = 1;
            this.health = 100;
            this.maxHealth = 100;
        }
        // Falcon (very agile, not much damage)
        else if (enemyClass == 2){
            this.shipSprite = new Texture("Falcon.png");
            this.speed = 1.5f;
            this.health = 50;
            this.maxHealth = 50;
        }
        // Fighter (little above average damage, average speed)
        else if (enemyClass == 3) {
            this.shipSprite = new Texture("Enemyship.png");
            this.speed = 1;
            this.health = 150;
            this.maxHealth = 150;
        }
        // Tank (high health, high damage but slow)
        else if (enemyClass == 4) {
            this.shipSprite = new Texture("Enemyship.png");
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
        if (getSprite() == null) {
            refreshTextures();
        }

        this.currentState.update(this,player);

        if (TimeUtils.millis() - previousStateChange > 500) {
            this.currentState = currentState.changeState(this, player);
        }
    }

    public void moveEnemy(){
        if (posY > 1000) {
            posX += 2 * directionOfMoving * 0.5f * this.speed;
            posY += -1 * 0.1f;
            if (posX <= limitXLeft || posX >= limitXRight) {
                this.directionOfMoving *= -1;
            }
            this.invulnerable = false;
        }
        else {
            this.invulnerable = true;
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
        Rectangle currentPosition = new Rectangle(this.getPosX()-40, this.getPosY()-40, this.getSprite().getWidth()+80, this.getSprite().getHeight()+80);
        Rectangle targetPosition = new Rectangle(this.getTargetX()-40, this.getTargetY()-40, this.getSprite().getWidth()+80, this.getSprite().getHeight()+80);
        if (overlaps(currentPosition, targetPosition)){
            calculateNewPosition();
        }
    }

    public void tryNewPosition(){
        this.tempX = MathUtils.random(this.targetArea.x, this.targetArea.x+this.targetArea.width);
        this.tempY = MathUtils.random(this.targetArea.y,  this.targetArea.y+this.targetArea.height);
    }

    public void shoot(){
        Bullet.getAllBullets().add(new Bullet(shipSprite.getWidth()-((gun == 1)?40:104),shipSprite.getHeight()-140, this));
        this.gun *= -1;
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
        Rectangle viewDistance = new Rectangle(this.posX,this.posY-300,140,300);
        for (Bullet bullet : Bullet.getAllBullets()){
            Rectangle bulletRect = new Rectangle(bullet.getPosX(), bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
            if (overlaps(bulletRect, viewDistance) && bullet.getFriendly()){
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
        else return gotHit;
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

    public void avoidBullet(){
        int bulletFriendlyCount = 0;
        for (Bullet bullet : Bullet.getAllBullets()){
            if (!bullet.getFriendly()){
                bulletFriendlyCount++;
            }
        }
        if (bulletFriendlyCount != Bullet.getAllBullets().size()) {
            for (int i = 0; i < 4; i++) {
                this.tryNewPosition();
                Rectangle enemyRect = new Rectangle(this.tempX - 40, 0, this.shipSprite.getWidth() + 80, 1000);
                Rectangle bulletRect = new Rectangle(Bullet.getAllBullets().get(0).getPosX(), Bullet.getAllBullets().get(0).getPosY(), Bullet.getAllBullets().get(0).getLaser().getWidth(), 1000);
                for (Bullet bullet : Bullet.getAllBullets()) {
                    if (!bullet.getFriendly()) {
                        bulletRect = new Rectangle(bullet.getPosX(), bullet.getPosY(), bullet.getLaser().getWidth(), 1000);

                        if (this.overlaps(enemyRect, bulletRect)) {
                            break;
                        }
                    }
                }
                if (!this.overlaps(enemyRect, bulletRect)) {
                    this.targetX = this.tempX;
                    this.targetY = this.tempY;
                    break;
                }
            }
        }
    }

    public boolean overlaps (Rectangle r, Rectangle r2){
        return (r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y);
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

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public int getEnemyClass() {
        return enemyClass;
    }

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
        return this.shipSprite;
    }

    public Rectangle getFlyArea() {
        return flyArea;
    }

    public Rectangle getHideArea() {
        return hideArea;
    }

    public Rectangle getProtectArea() {
        return protectArea;
    }
}
