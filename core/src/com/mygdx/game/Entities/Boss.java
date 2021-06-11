package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class Boss extends Sprite {

    private enum bossState {ENTRANCE, TP, ROCKETS, LASER, TRACKING_LASER, EVADE}

    private Sprite ufoBoss;
    private Vector2 positionUfo;
    private PlayerBoss player;
    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private bossState state;
    private boolean visible;
    private Random random;
    private Vector2 dashTarget;
    private double angle;
    private float evadeTreshold;

    public Boss(PlayerBoss player) {
        this.player = player;
        init();
    }

    private void init() {
        ufoBoss = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));

        //plaatst de boss in het midden
        positionUfo = new Vector2(SCREEN_WIDTH / 2 - ufoBoss.getWidth() / 2, SCREEN_HEIGHT);
        ufoBoss.setPosition(positionUfo.x, positionUfo.y);
        state = bossState.ENTRANCE;
        random = new Random();
        visible = true;
        dashTarget = new Vector2();
        evadeTreshold = 0;
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
                teleport();
                break;
            case ROCKETS:
                rockets();
                break;
            case LASER:
                laser();
                break;
            case TRACKING_LASER:
                trackingLaser();
                break;
            case EVADE:
                evade(delta);
                break;
        }
    }

    private void evade(float delta) {
        final int MAX_TRESHOLD = 300;
        final int UFO_SPEED = 20;

        if (evadeTreshold > MAX_TRESHOLD) {
            //dash

            //zoekt de hoek tussen boss en dashtarget
            angle = Math.atan2(dashTarget.y - ufoBoss.getY(), dashTarget.x - ufoBoss.getX());
            //System.out.println("angle degrees= " + Math.toDegrees(angle) + "angle radians= " + angle);


            ufoBoss.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y += UFO_SPEED * Math.sin(angle));
            //System.out.println("cos of angle = " + Math.cos(angle));

            //als de afstand tussen de boss en de target < 10pixels, reset treshold en ga naar volgende state
            if (Math.abs(dashTarget.x - ufoBoss.getX()) < 10 || Math.abs(dashTarget.y - ufoBoss.getY()) < 10) {
                evadeTreshold = 0;
                decideState();
            }
        } else {
            //hoover

            final double HOOVER_AMPLITUDE = 1.7;
            final double HOOVER_SPEEDX = 0.0075;
            final double HOOVER_SPEEDY = 0.0053;
            final int EVADETRESHOLD_SPEED = 100;

            //tijd dat het duurt voor de eerstvolgende dash
            evadeTreshold += EVADETRESHOLD_SPEED * delta;

            //de volgende twee regels zorgen (pseudo)random beweging (hoovering)
            //de speed zijn voor x en y verschillend want... het is een ufo
            positionUfo.x += HOOVER_AMPLITUDE * (float) Math.cos(TimeUtils.millis() * HOOVER_SPEEDX);
            positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY);
            ufoBoss.setPosition(positionUfo.x, positionUfo.y);
            // System.out.println(evadeTreshold);
        }
    }

    private void trackingLaser() {
    }

    private void decideState() {
//        if(Math.abs(player.getPosX() - ufoBoss.getX()) > 1000 || Math.abs(player.getPosY() - ufoBoss.getY()) > 1000){
//            state = bossState.TP;
//        }
//        else
//        {
        state = bossState.EVADE;
        //ufo breedte en hoogte bij x en y zorgt ervoor dat nieuwe positie niet buiten het scherm valt. 100 verkort de afstand van de dash
        dashTarget.set(new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoBoss.getWidth() / 2, random.nextInt(SCREEN_HEIGHT - 100) - ufoBoss.getHeight() / 3));
        //}
    }

    private void bossEntrance(float delta) {
        int ENTRANCE_ENDPOSITION = SCREEN_HEIGHT - 350;
        int ENTRANCE_SPEED = 75;

        //boss start boven en komt langzaam naar beneden.
        if (ufoBoss.getY() > ENTRANCE_ENDPOSITION) {
            ufoBoss.setY(ufoBoss.getY() - ENTRANCE_SPEED * delta);
        } else if (ufoBoss.getY() <= ENTRANCE_ENDPOSITION) {
            decideState();
        }
    }

    private void teleport() {
        final float CHANGE_ALPHA = 0.04f;

        //als de boss zichbaar is, alpha naar 0, is de boss onzichtbaar, alpha naar 1
        if (visible) {
            ufoBoss.setAlpha(ufoBoss.getColor().a - CHANGE_ALPHA);
            if (ufoBoss.getColor().a <= 0.1)//kleiner dan 0.1 omdat hij anders reset naar 1.0
            {
                ufoBoss.setAlpha(0);
                positionUfo = teleportNewLocation();
                ufoBoss.setPosition(positionUfo.x, positionUfo.y);
                visible = false;
            }
        } else {
            ufoBoss.setAlpha(ufoBoss.getColor().a + CHANGE_ALPHA);
            if (ufoBoss.getColor().a >= 0.9)//groter dan 0.9 omdat hij anders reset naar 0.0
            {
                ufoBoss.setAlpha(1);
                visible = true;
            }
        }
    }

    private void rockets() {

    }

    private void laser() {


    }

    private Vector2 teleportNewLocation() {
        return new Vector2(random.nextInt(SCREEN_WIDTH - 200), random.nextInt(SCREEN_HEIGHT - 200));
    }


}
