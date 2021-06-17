package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PlayerBoss extends Ship {

    private Sprite playerSprite;
    private Texture playerTexture;
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
    private boolean advancedMovement;

    public PlayerBoss() {
        init();
    }

    private void init() {
        playerTexture = new Texture(Gdx.files.internal("Playership.png"));
        playerSprite = new Sprite(playerTexture);
        shield = new Texture(Gdx.files.internal("shield.png"));
        playerPos = new Vector2(SCREEN_WIDTH / 2f - playerSprite.getWidth() / 2, SCREEN_HEIGHT / 10f);
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
        handleInput();
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

    public void handleInput() {
        final float SHIP_SPEED = 10f;
        final float EXTRA_SPEED = 3f;
        float shipRotation = playerSprite.getRotation();
        //System.out.println(playerSprite.getRotation());

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (advancedMovement) {
                moveUp(shipRotation, SHIP_SPEED, EXTRA_SPEED);
            } else {
                moveUpSimple(SHIP_SPEED, EXTRA_SPEED);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (advancedMovement) {
                moveLeft(shipRotation, SHIP_SPEED);
            } else {
                moveLeftSimple(SHIP_SPEED, EXTRA_SPEED);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (advancedMovement) {
                moveDown(shipRotation, SHIP_SPEED);
            } else {
                moveDownSimple(SHIP_SPEED, EXTRA_SPEED);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (advancedMovement) {
                moveRight(shipRotation, SHIP_SPEED);
            } else {
                moveRightSimple(SHIP_SPEED, EXTRA_SPEED);
            }
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

    private void moveUpSimple(float SHIP_SPEED, float EXTRA_SPEED) {
        if (playerPos.y < 0) {
            playerPos.y = 0;
        } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
            playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() + SHIP_SPEED;
        } else if (Math.abs(playerSprite.getRotation()) == 0 || Math.abs(playerSprite.getRotation()) == 360) {
            playerPos.y += (SHIP_SPEED + EXTRA_SPEED);
        } else {
            playerPos.y += SHIP_SPEED;
        }
    }

    private void moveDownSimple(float SHIP_SPEED, float EXTRA_SPEED) {
        if (playerPos.y < 0) {
            playerPos.y = 0 - SHIP_SPEED;
        } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
            playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() - SHIP_SPEED;
        } else if (Math.abs(playerSprite.getRotation()) == 180) {
            playerPos.y -= (SHIP_SPEED + EXTRA_SPEED);
        } else {
            playerPos.y -= SHIP_SPEED;
        }
    }

    private void moveLeftSimple(float SHIP_SPEED, float EXTRA_SPEED) {
        if (playerPos.x < 0) {
            playerPos.x = 0 - SHIP_SPEED;// - ship_speed anders valt buiten scherm
        } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
            playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() - SHIP_SPEED;
        } else if (playerSprite.getRotation() == -270 || playerSprite.getRotation() == 90) {
            playerPos.x -= (SHIP_SPEED + EXTRA_SPEED);
        } else {
            playerPos.x -= SHIP_SPEED;
        }
    }

    private void moveRightSimple(float SHIP_SPEED, float EXTRA_SPEED) {
        if (playerPos.x < 0) {
            playerPos.x = 0;
        } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
            playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() + SHIP_SPEED;
        } else if (playerSprite.getRotation() == 270 || playerSprite.getRotation() == -90) {
            playerPos.x += (SHIP_SPEED + EXTRA_SPEED);
        } else {
            playerPos.x += SHIP_SPEED;
        }
    }

    private void moveUp(float shipRotation, float SHIP_SPEED, float EXTRA_SPEED) {
        if (Math.abs(shipRotation) == 0 || Math.abs(shipRotation) == 360) {
            if (playerPos.y < 0) {
                playerPos.y = 0;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() + SHIP_SPEED;
            } else {
                playerPos.y += (SHIP_SPEED + EXTRA_SPEED);
            }
        } else if (shipRotation == 90 || shipRotation == -270) {
            if (playerPos.x < 0) {
                playerPos.x = 0 - SHIP_SPEED;// - ship_speed anders valt buiten scherm
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() - SHIP_SPEED;
            } else {
                playerPos.x -= (SHIP_SPEED + EXTRA_SPEED);
            }
        } else if (Math.abs(shipRotation) == 180) {
            if (playerPos.y < 0) {
                playerPos.y = 0 - SHIP_SPEED;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() - SHIP_SPEED;
            } else {
                playerPos.y -= (SHIP_SPEED + EXTRA_SPEED);
            }
        } else if (shipRotation == -90 || shipRotation == 270) {
            if (playerPos.x < 0) {
                playerPos.x = 0;
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() + SHIP_SPEED;
            } else {
                playerPos.x += (SHIP_SPEED + EXTRA_SPEED);
            }
        }
    }

    private void moveDown(float shipRotation, float SHIP_SPEED) {
        if (Math.abs(shipRotation) == 0 || Math.abs(shipRotation) == 360) {
            if (playerPos.y < 0) {
                playerPos.y = 0 - SHIP_SPEED;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() - SHIP_SPEED;
            } else {
                playerPos.y -= SHIP_SPEED;
            }
        } else if (shipRotation == 90 || shipRotation == -270) {
            if (playerPos.x < 0) {
                playerPos.x = 0;
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() + SHIP_SPEED;
            } else {
                playerPos.x += SHIP_SPEED;
            }
        } else if (Math.abs(shipRotation) == 180) {
            if (playerPos.y < 0) {
                playerPos.y = 0;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() + SHIP_SPEED;
            } else {
                playerPos.y += SHIP_SPEED;
            }
        } else if (shipRotation == -90 || shipRotation == 270) {
            if (playerPos.x < 0) {
                playerPos.x = 0 - SHIP_SPEED;// - ship_speed anders valt buiten scherm
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() - SHIP_SPEED;
            } else {
                playerPos.x -= SHIP_SPEED;
            }
        }
    }

    private void moveRight(float shipRotation, float SHIP_SPEED) {
        if (Math.abs(shipRotation) == 0 || Math.abs(shipRotation) == 360) {
            if (playerPos.x < 0) {
                playerPos.x = 0;
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() + SHIP_SPEED;
            } else {
                playerPos.x += SHIP_SPEED;
            }
        } else if (shipRotation == 90 || shipRotation == -270) {
            if (playerPos.y < 0) {
                playerPos.y = 0;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() + SHIP_SPEED;
            } else {
                playerPos.y += SHIP_SPEED;
            }
        } else if (shipRotation == -90 || shipRotation == 270) {
            if (playerPos.y < 0) {
                playerPos.y = 0 - SHIP_SPEED;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() - SHIP_SPEED;
            } else {
                playerPos.y -= SHIP_SPEED;
            }
        } else if (Math.abs(shipRotation) == 180) {
            if (playerPos.x < 0) {
                playerPos.x = 0 - SHIP_SPEED;// - ship_speed anders valt buiten scherm
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() - SHIP_SPEED;
            } else {
                playerPos.x -= SHIP_SPEED;
            }
        }
    }

    private void moveLeft(float shipRotation, float SHIP_SPEED) {
        if (Math.abs(shipRotation) == 0 || Math.abs(shipRotation) == 360) {
            if (playerPos.x < 0) {
                playerPos.x = 0 - SHIP_SPEED;// - ship_speed anders valt buiten scherm
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() - SHIP_SPEED;
            } else {
                playerPos.x -= SHIP_SPEED;
            }
        } else if (shipRotation == 90 || shipRotation == -270) {
            if (playerPos.y < 0) {
                playerPos.y = 0 - SHIP_SPEED;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() - SHIP_SPEED;
            } else {
                playerPos.y -= SHIP_SPEED;
            }
        } else if (shipRotation == -90 || shipRotation == 270) {
            if (playerPos.y < 0) {
                playerPos.y = 0;
            } else if (playerPos.y > SCREEN_HEIGHT - playerSprite.getHeight()) {
                playerPos.y = SCREEN_HEIGHT - playerSprite.getHeight() + SHIP_SPEED;
            } else {
                playerPos.y += SHIP_SPEED;
            }
        } else if (Math.abs(shipRotation) == 180) {
            if (playerPos.x < 0) {
                playerPos.x = 0;
            } else if (playerPos.x > SCREEN_WIDTH - playerSprite.getWidth()) {
                playerPos.x = SCREEN_WIDTH - playerSprite.getWidth() + SHIP_SPEED;
            } else {
                playerPos.x += SHIP_SPEED;
            }
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
        return playerPos.y + playerSprite.getHeight() / 2;
    }

    @Override
    public float getPosX() {
        return playerPos.x + playerSprite.getWidth() / 2;
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
        return playerTexture;
    }

    public void setHealth(int health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

    public void setAdvancedMovement(boolean advancedMovement)
    {
        this.advancedMovement = advancedMovement;
    }
}
