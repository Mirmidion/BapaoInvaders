package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Entities.Bullet;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    private int posX = 0;
    private int posY = 100;
    private Sprite playerSprite = new Sprite(new Texture(Gdx.files.internal("Playership.png")));
    private Texture shield = new Texture(Gdx.files.internal("shield.png"));
    // private Texture playerSprite = new Texture(Gdx.files.internal("Playership.png"));
    private int gun = 1;
    private long time = 0;
    private int health = 100;
    private boolean invulnerable = false;
    private float timeInvulnerable = 0;
    private float invulnerableTime = 3;

    public Player(int width) {
        this.posX = (int) (width / 2 - playerSprite.getWidth() / 2);
    }

    public void setPosX(int x, int width) {
        if (posX + x > width - playerSprite.getWidth()) {
            posX = (int) (width - playerSprite.getWidth());
        } else if (posX + x < 0) {
            posX = 0;
        } else {
            posX += x;
        }
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public void shoot() {
        System.out.println(time);
        if (TimeUtils.millis() - time > 500) {
            Bullet.allBullets.add(new Bullet((int) playerSprite.getWidth() - ((gun == 1) ? 40 : 104), (int) playerSprite.getHeight() - 32, true, this));
            time = TimeUtils.millis();
            gun *= -1;
        }
    }

    public void bulletRemove(Bullet bulletToRemove) {

    }

    public boolean isInvulnerable() {

        return this.invulnerable;

    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public void resetPosition(int width) {
        this.posX = (int) (width / 2 - playerSprite.getWidth() / 2);
    }

    //update de tijd dat het schip invulnerable is en na 3 seconden zet terug op invulnerable
    public void update(float delta) {
        if (invulnerable) {
            timeInvulnerable += delta;
        }
        if (timeInvulnerable - invulnerableTime >= 0) {
            invulnerable = false;
            timeInvulnerable = 0;

        }
    }

    //'teken' het schip in de wereld en teken een shield als die geraakt wordt
    public void draw(Batch batch) {
        playerSprite.draw(batch);
        playerSprite.setPosition(posX, posY);
        if (invulnerable) {
            batch.draw(shield, posX - 40f, posY - 20f, shield.getWidth() * 0.4f, shield.getHeight() * 0.4f);

            //dit stukje zorgt ervoor dat de laatste seconde van de invulnerability state, het schip knippert
            if (timeInvulnerable - 2 >= 0) {
                boolean even = true;
                for (float i = 2; i < 3; i += 0.10) {
                    if (timeInvulnerable - i >= 0) {
                        if (even) {
                            playerSprite.setAlpha(0.5f);
                        } else {
                            playerSprite.setAlpha(1f);
                        }
                        even = !even;
                    }
                }
            }
        }

        if (!invulnerable) {
            playerSprite.setAlpha(1f);
        }

    }


}
