package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Missile extends Sprite {
    final Sprite missileSprite;
    Vector2 missilePosition = new Vector2();
    public static final int MISSILE_SPEED = 7;
    public static final float MISSILE_LIFETIME = 5f;
    float missileTimer = 0;
    public boolean remove = false;
    PlayerBoss player;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static final int MISSILE_DAMAGE = 20;
    private static boolean drawHitboxes = false;

    public Missile(float x, float y, float rotation, PlayerBoss player) {
        missilePosition.x = x;
        missilePosition.y = y;
        missileSprite = new Sprite(new Texture("missile.png"));
        missileSprite.setSize(missileSprite.getWidth() / 2, missileSprite.getHeight() / 2);
        missileSprite.setRotation(rotation);
        this.player = player;
    }

    public void update(float delta) {
        missileTimer += delta;

        trackPlayer();
        removeMissile();
    }

    public void draw(SpriteBatch batch) {
        missileSprite.setPosition(missilePosition.x, missilePosition.y);
        missileSprite.draw(batch);
    }

    public void removeMissile() {
        if (missilePosition.y > Gdx.graphics.getHeight() || missilePosition.x > Gdx.graphics.getWidth() ||
                missilePosition.y < 0 || missilePosition.x < 0) {
            remove = true;
        }

        if (missileTimer >= MISSILE_LIFETIME) {
            remove = true;
        }
    }

    public void drawHitbox() {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        float missileRotation = missileSprite.getRotation();
        System.out.println(missileRotation);
        if (missileRotation >= -45.0 && missileRotation <= 45.0) {
            shapeRenderer.rect(missilePosition.x + missileSprite.getWidth() / 4, missilePosition.y - missileSprite.getHeight() / 3f,
                    missileSprite.getWidth() / 1.5f, missileSprite.getHeight() / 1.5f);
        } else if (missileRotation > -135.0 && missileRotation <= -45.0) {
            shapeRenderer.rect(missilePosition.x - missileSprite.getWidth(), missilePosition.y + missileSprite.getHeight() / 1.4f,
                    missileSprite.getHeight() / 1.5f, missileSprite.getWidth() / 1.5f);
        } else if ((missileRotation > -270.0 && missileRotation <= -225.0) || missileRotation > 45.0 && missileRotation <= 90.0) {
            shapeRenderer.rect(missilePosition.x + missileSprite.getWidth() * 1.5f, missilePosition.y + missileSprite.getWidth() / 2.2f,
                    missileSprite.getHeight() / 1.5f, missileSprite.getWidth() / 1.5f);
        }
        else {
            shapeRenderer.rect(missilePosition.x + missileSprite.getWidth(), missilePosition.y + missileSprite.getHeight() / 1.5f,
                    missileSprite.getWidth() / 1.5f, missileSprite.getHeight() / 1.5f);
        }
        shapeRenderer.end();
    }

    public void trackPlayer() {
        double angle = Math.atan2(player.getPosY() - missilePosition.y, player.getPosX() - missilePosition.x);
        missilePosition.x += (MISSILE_SPEED * Math.cos(angle));
        missilePosition.y += (MISSILE_SPEED * Math.sin(angle));

        missileSprite.setPosition(missilePosition.x, missilePosition.y);

        missileSprite.setRotation((float) Math.toDegrees(angle) - 90f);
    }

    public Rectangle getMissileHitbox() {
        float missileRotation = missileSprite.getRotation();
        if (missileRotation >= -45.0 && missileRotation <= 45.0) {
            return new Rectangle(missilePosition.x + missileSprite.getWidth() / 4, missilePosition.y - missileSprite.getHeight() / 3f,
                    missileSprite.getWidth() / 1.5f, missileSprite.getHeight() / 1.5f);
        } else if (missileRotation > -135.0 && missileRotation <= -45.0) {
            return new Rectangle(missilePosition.x - missileSprite.getWidth(), missilePosition.y + missileSprite.getHeight() / 1.4f,
                    missileSprite.getHeight() / 1.5f, missileSprite.getWidth() / 1.5f);
        } else if ((missileRotation > -270.0 && missileRotation <= -225.0) || missileRotation > 45.0 && missileRotation <= 90.0) {
            return new Rectangle(missilePosition.x + missileSprite.getWidth(), missilePosition.y + missileSprite.getWidth() / 2.2f,
                    missileSprite.getHeight() / 1.5f, missileSprite.getWidth() / 1.5f);
        } else {
            return new Rectangle(missilePosition.x + missileSprite.getWidth(), missilePosition.y + missileSprite.getHeight() / 1.5f,
                    missileSprite.getWidth() / 1.5f, missileSprite.getHeight() / 1.5f);
        }

    }

    public static int getMissileDamage() {
        return MISSILE_DAMAGE;
    }

    public boolean getDrawHitboxes() {
        return drawHitboxes;
    }

    public static void setDrawHitboxes(boolean drawHitboxes) {
        Missile.drawHitboxes = drawHitboxes;
    }
}
