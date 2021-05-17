package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class Bullet{

    //Position
    private final float posX;
    private float posY;

    //Laser texture
    private final Texture laser = new Texture(Gdx.files.internal("laser.png"));

    //Is it from the player or the enemy?
    private final boolean friendly;

    //Speed of how fast it travels
    private float bulletSpeed;

    //Damage it deals
    private int damage;

    //Is it in screen space?
    private boolean exists = true;

    //All bullets in the current level
    static ArrayList<Bullet> allBullets = new ArrayList<>();

    public Bullet (int x, int y, boolean friendly, Player player){
        this.posX = x + player.getPosX();
        this.posY = y + player.getPosY();
        this.friendly = friendly;
        this.damage = 50;
        this.bulletSpeed = 1;
    }

    public Bullet (int x, int y, boolean friendly, Enemy enemy){
        this.posX = x + enemy.getPosX();
        this.posY = y + enemy.getPosY();
        this.friendly = friendly;
        if (enemy.getEnemyClass()==1) {
                this.damage = 25;
                this.bulletSpeed = 1;
            }
        else if (enemy.getEnemyClass()==2) {
                this.damage = 10;
                this.bulletSpeed = 1.5f;
            }
        else if (enemy.getEnemyClass()==3) {
                this.damage = 34;
                this.bulletSpeed = 0.85f;
            }
        else if (enemy.getEnemyClass()==4) {
                this.damage = 50;
                this.bulletSpeed = 0.5f;

        }
    }

    public void setPosY(float posY, int height) {
        if (this.posY+posY > height || this.posY+posY < 0){
            this.exists = false;
        }
        else{
            this.posY += posY* this.bulletSpeed * ((this.friendly)?1:-1) ;
        }
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public Texture getLaser() {
        return laser;
    }

    public boolean getFriendly(){
        return friendly;
    }

    public static void setAllBullets(ArrayList<Bullet> allBullets) {
        Bullet.allBullets = allBullets;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public static ArrayList<Bullet> getAllBullets() {
        return allBullets;
    }

    public int getDamage() {
        return damage;
    }
}
