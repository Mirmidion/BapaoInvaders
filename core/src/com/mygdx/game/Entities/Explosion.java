package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {

    public static final float FRAME_LENGTH = 0.1f;
    public static final int SIZE = 96;

    private Animation<TextureRegion> animation;
    float x, y;
    float statetime;
    private static int sparkColor = 0;

    public boolean remove = false;

    public Explosion (float x, float y, int type){
        this.x = x;
        this.y = y;
        statetime = 0;

        if(type == 1){
            animation = new Animation<>(FRAME_LENGTH, TextureRegion.split(new Texture("explosion.png"), SIZE, SIZE)[0]);
        }
        else if(type == 2){
            animation = new Animation<>(FRAME_LENGTH, TextureRegion.split(new Texture("sparks.png"),
                    46, 46)[(sparkColor > 6) ? sparkColor-=sparkColor : sparkColor]);
        }
        sparkColor++;
    }

    public void update (float delta){
        statetime += delta;
        if(animation.isAnimationFinished(statetime)){
            remove = true;
        }
    }

    public void draw (SpriteBatch batch){
        batch.draw(animation.getKeyFrame(statetime), x, y);
    }
}
