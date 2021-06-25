package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BigLaser extends Sprite{
    private final Sprite bigLaserSprite;
    private Vector2 bigLaserPosition = new Vector2();
    private final Boss boss;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static boolean drawHitboxes = false;
    private final int LASER_DAMAGE = 20;


    public BigLaser(Boss boss){
        bigLaserSprite = new Sprite(new Texture("ufo_laser.png"));
        bigLaserSprite.setPosition(500, 500);
        this.boss = boss;
    }

    public void update(float delta){
        bigLaserPosition.x = (boss.getPositionUfo().x + boss.getUfoSprite().getWidth() / 2) - bigLaserSprite.getWidth() / 2;
        bigLaserPosition.y = boss.getPositionUfo().y - bigLaserSprite.getHeight();
        if(drawHitboxes) {
            drawHitbox();
        }
    }

    public void draw(SpriteBatch batch){
        bigLaserSprite.setPosition(bigLaserPosition.x, bigLaserPosition.y);
        bigLaserSprite.draw(batch);
    }

    public void drawHitbox(){
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.rect(boss.ufoLaser.getBigLaserPosition().x + boss.ufoLaser.getBigLaserSprite().getWidth() * 0.05f, boss.ufoLaser.getBigLaserPosition().y,
                boss.ufoLaser.getBigLaserSprite().getWidth()/1.1f, boss.ufoLaser.getBigLaserSprite().getHeight());
        shapeRenderer.end();
    }

    public Rectangle getLaserHitbox(){
        return new Rectangle(bigLaserPosition.x + bigLaserSprite.getWidth() * 0.05f, bigLaserPosition.y,
                bigLaserSprite.getWidth()/1.1f, bigLaserSprite.getHeight());
    }

    public Vector2 getBigLaserPosition() {
        return bigLaserPosition;
    }

    public Sprite getBigLaserSprite() {
        return bigLaserSprite;
    }

    public static void setDrawHitboxes(boolean drawHitboxes) {
        BigLaser.drawHitboxes = drawHitboxes;
    }

    public int getLaserDamage() {
        return LASER_DAMAGE;
    }
}
