package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends Ship {

    private enum bossState {ENTRANCE, TP, ROCKETS, LASER}

    private Sprite ufoBoss;
    private Vector2 positionUfo;
    private Player player;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HIGHT = 1080;
    private bossState state;
    private boolean visible;
    private ArrayList<Vector2> tpPos;
    private Random random;

    public Boss(Player player) {
        this.player = player;
        init();
    }

    private void init() {
        ufoBoss = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));

        //plaatst de boss in het midden
        positionUfo = new Vector2(SCREEN_WIDTH / 2 - ufoBoss.getWidth() / 2, SCREEN_HIGHT);
        ufoBoss.setPosition(positionUfo.x, positionUfo.y);
        state = bossState.ENTRANCE;
        visible = true;
        tpPos = new ArrayList<Vector2>();
        tpPos.add(new Vector2(1920 / 2 - 80, 1080 - 200));
        tpPos.add(new Vector2(1920 / 2 - 210, 1080 - 800));
        tpPos.add(new Vector2(1920 / 2 - 400, 1080 - 500));
        tpPos.add(new Vector2(1920 / 2 - 800, 1080 - 200));
        random = new Random();
    }

    public void render(SpriteBatch batch) {
        ufoBoss.draw(batch);
    }

    public void update(float delta) {
        switch (state) {
            case ENTRANCE:
                bossEntrance(delta);
                break;
            case TP:
                teleport(delta);
                break;
            case ROCKETS:
                rockets();
                break;
            case LASER:
                laser();
                break;
        }
    }

    private void decideState() {
        state = bossState.TP;

    }

    private void bossEntrance(float delta) {
        int ENTRANCE_ENDPOSITION = 750;
        int ENTRANCE_SPEED = 75;
        if (ufoBoss.getY() > ENTRANCE_ENDPOSITION) {
            ufoBoss.setY(ufoBoss.getY() - ENTRANCE_SPEED * delta);
        }
        else if(ufoBoss.getY() <= ENTRANCE_ENDPOSITION)
        {
            decideState();
        }
    }

    private void teleport(float delta) {
        if (visible) {
            ufoBoss.setAlpha(ufoBoss.getColor().a -= 0.015);
        }
        else {
            ufoBoss.setAlpha(ufoBoss.getColor().a += 0.015);
        }

        if (ufoBoss.getColor().a <= 0) {
            ufoBoss.setAlpha(0);
            visible = false;
            positionUfo = teleportNewLocation();
            ufoBoss.setPosition(positionUfo.x, positionUfo.y);
        } else if(ufoBoss.getColor().a >= 1f)
        {
            ufoBoss.setAlpha(1f);
            visible = true;
            decideState();
        }


    }

    private void rockets() {

    }

    private void laser() {

    }

    private Vector2 teleportNewLocation() {
        int num = random.nextInt(4);
        return tpPos.get(num);

    }

    @Override
    public float getPosY() {
        return 0;
    }

    @Override
    public float getPosX() {
        return 0;
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public Texture getSprite() {
        return null;
    }
}
