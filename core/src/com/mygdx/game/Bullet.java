package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Bullet{
    private int posX;
    private int posY;
    private Texture laser = new Texture(Gdx.files.internal("laser.png"));
    private boolean friendly;
    boolean exists = true;

    public Bullet (int x, int y, boolean friendly, Player player){
        this.posX = x + player.getPosX();
        this.posY = y + player.getPosY();
        this.friendly = friendly;
    }

    public void setPosX(int posX, Player player, int width) {
        if (this.posX+posX > width || this.posX+posX < 0){
            player.bulletRemove(this);
        }
        else{
            this.posX += posX;
        }
    }

    public void setPosY(int posY, Player player, int height) {
        if (this.posY+posY > height || this.posY+posY < 0){
            this.exists = false;
        }
        else{
            this.posY += posY;
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Texture getLaser() {
        return laser;
    }
}
