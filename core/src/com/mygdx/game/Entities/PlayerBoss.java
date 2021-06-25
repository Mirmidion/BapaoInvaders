package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

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
    private ArrayList<Laser> lasers;
    private final float TIME_BETWEEN_LASERS = 0.3f;
    private float laserTimer;
    public enum playerFacing {UP, DOWN, LEFT, RIGHT}
    private playerFacing facing;
    private Boss boss;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final Texture healthBar = new Texture("healthBar.png");
    private final Sprite healthBarBorder = new Sprite(new Texture("healthbar_border.png"));
    private boolean drawHitboxes = false;
    private Sprite playerIcon;


    public PlayerBoss() {
        init();
    }

    private void init() {
        playerTexture = new Texture(Gdx.files.internal("Playership.png"));
        playerSprite = new Sprite(playerTexture);
        shield = new Texture(Gdx.files.internal("shield.png"));
        playerPos = new Vector2(SCREEN_WIDTH / 2f - playerSprite.getWidth() / 2, SCREEN_HEIGHT / 10f);
        playerSprite.setPosition(playerPos.x, playerPos.y);
        health = 10;
        invulnerable = false;
        rotate = false;
        playerRotation = playerSprite.getRotation();
        timeInvulnerable = 0;
        currentRotation = 0;
        lasers = new ArrayList<>();
        laserTimer = 0;
        facing = playerFacing.UP;
        healthBarBorder.setPosition(SCREEN_WIDTH - healthBarBorder.getWidth() - 100, 50);
        playerIcon = new Sprite(playerTexture);
        playerIcon.setSize(playerTexture.getWidth()/4f, playerTexture.getHeight()/4f);
        playerIcon.setPosition(healthBarBorder.getX() + healthBarBorder.getWidth() - 44,
                healthBarBorder.getY() + 10);
    }

    public void update(float delta) {
        float shipRotation = playerSprite.getRotation();
        checkInvulnerable(delta);
        handleInput(shipRotation);
        updateLaser(delta, shipRotation);

        if(drawHitboxes){
            drawHitboxes();
        }
    }

    //'teken' het schip en de attributen op het scherm
    public void draw(SpriteBatch batch) {
        playerSprite.setPosition(playerPos.x, playerPos.y);
        playerSprite.draw(batch);
        drawShield(batch);
        rotate();
        drawHealthbar(batch);

        //draw lasers
        for (Laser laser : lasers) {
            laser.draw(batch);
        }
    }

    public void drawHitboxes(){
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        switch(facing){
            case UP:
            case DOWN:
                shapeRenderer.rect(playerPos.x+playerSprite.getWidth()*0.25f, playerPos.y, playerSprite.getWidth()/2, playerSprite.getHeight());
                break;
            case LEFT:
            case RIGHT:
                shapeRenderer.rect(playerPos.x - playerSprite.getHeight()*0.10f, playerPos.y+playerSprite.getHeight()*0.30f, playerSprite.getHeight(), playerSprite.getWidth()/2);
                break;
        }

        //draw boss hitbox
        shapeRenderer.rect(boss.getPositionUfo().x + 52f, boss.getPositionUfo().y,
                boss.getUfoSprite().getWidth()/1.20f, boss.getUfoSprite().getHeight()/1.10f);
        shapeRenderer.end();
    }

    public void shoot() {
        if(facing == playerFacing.UP || facing == playerFacing.DOWN) {
            lasers.add(new Laser(playerPos.x + 10, playerPos.y + 80, this, facing));
            lasers.add(new Laser(playerPos.x + playerSprite.getWidth() - 10, playerPos.y + 80, this, facing));
        }
        else if (facing == playerFacing.LEFT || facing == playerFacing.RIGHT){
            lasers.add(new Laser(playerPos.x + 80, playerPos.y + 10, this, facing));
            lasers.add(new Laser(playerPos.x + 80, playerPos.y + playerSprite.getWidth() - 10, this, facing));
        }
    }

    public void updateLaser(float delta, float shipRotation) {
        laserTimer += delta;

        if(Math.abs(shipRotation) == 0|| Math.abs(shipRotation) == 360){
            facing = playerFacing.UP;
        }
        else if(shipRotation == 90 || shipRotation == -270){
            facing = playerFacing.LEFT;
        }
        else if(shipRotation == -90 || shipRotation == 270){
            facing = playerFacing.RIGHT;
        }
        else if(Math.abs(shipRotation) == 180){
            facing = playerFacing.DOWN;
        }

        ArrayList<Laser> lasersToRemove = new ArrayList<>();
        for (Laser laser : lasers) {
            laser.update(delta);
            if (laser.getRemove()) {
                lasersToRemove.add(laser);
            }
            if(laser.getLaserHitbox().overlaps(boss.getBossHitbox())){
                boss.setHealth(-Laser.getLaserDamage());
                lasersToRemove.add(laser);
            }
        }
        lasers.removeAll(lasersToRemove);
    }

    public void drawHealthbar(SpriteBatch batch){
        if (health != 0) {
            batch.draw(healthBar, healthBarBorder.getX() + 15, healthBarBorder.getY() + 11,
                    health * 6.88f - 50, healthBarBorder.getHeight()/1.5f);
        }
        healthBarBorder.draw(batch);

        playerIcon.draw(batch);
    }

    private void rotate() {
        if (rotate) {
            playerRotation += 10;
            if (rotateClockwise) {
                playerSprite.setRotation(-playerRotation - currentRotation);
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
        }
    }

    public void handleInput(float shipRotation) {
        final float SHIP_SPEED = 10f;
        final float EXTRA_SPEED = 3f;

        //movement
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

        //rotate
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            rotate = true;
            rotateClockwise = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            rotate = true;
            rotateClockwise = true;
        }

        //shoot
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && laserTimer >= TIME_BETWEEN_LASERS) {
            laserTimer = 0;
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
            batch.draw(shield, playerPos.x - 40f, playerPos.y - 20f, shield.getWidth() * 0.4f, shield.getHeight() * 0.4f);

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

    public void setAdvancedMovement(boolean advancedMovement) {
        this.advancedMovement = advancedMovement;
    }

    public Sprite getSprite() {
        return playerSprite;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public Rectangle getPlayerHitbox(){
        switch(facing){
            case UP:
            case DOWN:
                return new Rectangle(playerPos.x+playerSprite.getWidth()*0.25f, playerPos.y,
                        playerSprite.getWidth()/2, playerSprite.getHeight());
            case LEFT:
            case RIGHT:
                return new Rectangle(playerPos.x - playerSprite.getHeight()*0.10f, playerPos.y+playerSprite.getHeight()*0.30f,
                        playerSprite.getHeight(), playerSprite.getWidth()/2);
        }
        return null;
    }

    public boolean getDrawHitboxes() {
        return drawHitboxes;
    }

    public void setDrawHitboxes(boolean drawHitboxes) {
        this.drawHitboxes = drawHitboxes;
    }
}
