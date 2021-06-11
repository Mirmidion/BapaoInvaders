package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerBoss extends Ship {

    private Sprite playerSprite;
    private Texture shield;
    private boolean invulnerable;
    private Vector2 playerPos;
    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private float playerRotation;
    private float timeInvulnerable;
    private boolean rotateClockwise;
    private float currentRotation;
    private boolean rotate;

    public PlayerBoss() {
        init();
    }

    private void init() {
        playerSprite = new Sprite(new Texture(Gdx.files.internal("Playership.png")));
        shield = new Texture(Gdx.files.internal("shield.png"));
        playerPos = new Vector2(SCREEN_WIDTH / 2 - playerSprite.getWidth() / 2, SCREEN_HEIGHT / 10);
        playerSprite.setPosition(playerPos.x, playerPos.y);
        health = 100;
        invulnerable = false;
        rotate = false;
        playerRotation = playerSprite.getRotation();
        timeInvulnerable = 0;
        currentRotation = 0;
    }


    public void shoot() {

    }

    public void update(float delta) {
        checkInvulnerable(delta);
        handleInput(delta);
    }

    //'teken' het schip in de wereld en teken een shield als die geraakt wordt
    public void draw(SpriteBatch batch) {
        playerSprite.draw(batch);
        playerSprite.setPosition(playerPos.x, playerPos.y);
        drawShield(batch);
        rotate();
        // System.out.println(playerRotation);
    }

    private void rotate() {
        if (rotate) {
            playerRotation += 10;
            if (rotateClockwise) {
                playerSprite.setRotation(-playerRotation - currentRotation);
                //System.out.println(playerRotation);
            } else {
                playerSprite.setRotation(playerRotation - currentRotation);
            }
        }

        if (playerRotation >= 90.0f) {
            rotate = false;
            playerRotation = 0;
            if (rotateClockwise) {
                currentRotation += 90;
            } else {
                currentRotation -= 90;
            }
            if (currentRotation == -360) {
                currentRotation = 0;
            } else if (currentRotation == 360) {
                currentRotation = 0;
            }
            //System.out.println(currentRotation);
        }
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerPos.y += 10f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerPos.x -= 10f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerPos.y -= 10f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerPos.x += 10f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            rotate = true;
            rotateClockwise = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            rotate = true;
            rotateClockwise = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shoot();
        }
    }

    private void checkInvulnerable(float delta) {
        if (invulnerable) {
            timeInvulnerable += delta;
        }
        float invulnerableTime = 3;
        if (timeInvulnerable - invulnerableTime >= 0) {
            invulnerable = false;
            timeInvulnerable = 0;
        }
    }

    private void drawShield(SpriteBatch batch) {
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
    public Texture getSprite() {
        return shipSprite;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }
}
