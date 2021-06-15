package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.TimeUtils;

public class Player extends Ship {

    private final Sprite playerSprite = new Sprite(new Texture(Gdx.files.internal("Playership.png")));
    private final Texture shield = new Texture(Gdx.files.internal("shield.png"));
    private long time = 0;
    private boolean invulnerable = false;

    private float timeInvulnerable = 0;


    public Player(int width) {
        shipSprite = new Texture(Gdx.files.internal("Playership.png"));
        posX = width / 2f - shipSprite.getWidth() / 2f;
        health = 100;
        posY = 100;
    }

    public void setPosX(int x, int width) {
        if (posX + x > width - shipSprite.getWidth()) {
            posX = width - shipSprite.getWidth();
        } else if (posX + x < 0) {
            posX = 0;
        } else {
            posX += x;
        }
    }

    public void shoot() {
        System.out.println(time);
        if (TimeUtils.millis() - time > 500) {
            Bullet.getAllBullets().add(new Bullet(shipSprite.getWidth() - ((gun == 1) ? 40 : 104), shipSprite.getHeight() - 32, this));
            time = TimeUtils.millis();
            gun *= -1;
        }
    }

    public void resetPosition(int width) {
        this.posX = width / 2f - shipSprite.getWidth() / 2f;
    }

    public void update(float delta) {
        if (invulnerable) {
            timeInvulnerable += delta;
        }
        float invulnerableTime = 3;
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

    @Override
    public float getPosY() {
        return posY;
    }

    @Override
    public float getPosX() {
        return posX;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public Texture getTexture() {
        return shipSprite;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }
}
