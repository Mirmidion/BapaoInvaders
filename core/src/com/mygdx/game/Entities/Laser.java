package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Sprite {
    private final TextureAtlas textureAtlas;
    private Sprite laserSprite;
    private Vector2 laserPosition = new Vector2();
    private boolean remove = false;
    private static final int SPEED = 700;
    private static final float LASER_DAMAGE = 0.5f;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static boolean drawHitboxes = false;

    private enum laserDirection {UP, DOWN, LEFT, RIGHT}

    private laserDirection direction;

    public Laser(float x, float y, PlayerBoss.playerFacing facing) {
        laserPosition.x = x;
        laserPosition.y = y;
        textureAtlas = new TextureAtlas("Lasers.pack");

        laserSprite = new Sprite(textureAtlas.findRegion("TinyBlueLaser"));

        laserSprite.setSize(laserSprite.getWidth() * 2, laserSprite.getHeight() * 2);
        if (facing == PlayerBoss.playerFacing.UP) {
            direction = laserDirection.UP;
        } else if (facing == PlayerBoss.playerFacing.DOWN) {
            direction = laserDirection.DOWN;
        } else if (facing == PlayerBoss.playerFacing.LEFT) {
            direction = laserDirection.LEFT;
        } else if (facing == PlayerBoss.playerFacing.RIGHT) {
            direction = laserDirection.RIGHT;
        }
    }

    public void update(float delta) {
        updateMovement(delta);
        removeLaser();

        if(drawHitboxes){
            drawHitbox();
        }
    }

    public void draw(SpriteBatch batch) {
        laserSprite.setPosition(laserPosition.x, laserPosition.y);
        laserSprite.draw(batch);
    }

    public void drawHitbox(){
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.rect(laserPosition.x, laserPosition.y, laserSprite.getWidth(), laserSprite.getHeight());
        shapeRenderer.end();
    }

    private void updateMovement(float delta) {
        if (direction == laserDirection.UP) {
            laserPosition.y += SPEED * delta;
        } else if (direction == laserDirection.DOWN) {
            laserPosition.y -= SPEED * delta;
        } else if (direction == laserDirection.LEFT) {
            laserPosition.x -= SPEED * delta;
        } else if (direction == laserDirection.RIGHT) {
            laserPosition.x += SPEED * delta;
        }
    }

    public void removeLaser() {
        if (laserPosition.y > Gdx.graphics.getHeight() || laserPosition.x > Gdx.graphics.getWidth() ||
                laserPosition.y < 0 || laserPosition.x < 0) {
            remove = true;
        }
    }

    public boolean getRemove() {
        return remove;
    }

    public Rectangle getLaserHitbox(){
        if(direction == laserDirection.UP || direction == laserDirection.DOWN) {
            return new Rectangle(laserPosition.x, laserPosition.y, laserSprite.getWidth(), laserSprite.getHeight());
        }
        else {
            return new Rectangle(laserPosition.x, laserPosition.y, laserSprite.getHeight(), laserSprite.getWidth());
        }
    }

    public static void setDrawHitboxes(boolean drawHitboxes) {
        Laser.drawHitboxes = drawHitboxes;
    }

    public static float getLaserDamage() {
        return LASER_DAMAGE;
    }

    public Vector2 getLaserPosition() {
        return laserPosition;
    }
}
