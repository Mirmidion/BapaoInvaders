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
    private final PlayerBoss player;
    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private bossState state;
    private boolean visible;
    private Random random;
    private Vector2 dashTarget;
    private float evadeThresholdCounter;
    private boolean ufoTopRight;
    private int laserPhase;
    private int numberOfConsecutiveLaserAttacks;
    private Sprite ufoLaser;
    private boolean drawLaser;

    public Boss(PlayerBoss player) {
        this.player = player;
        init();
    }

    private void init() {
        ufoBoss = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));
        ufoLaser = new Sprite(new Texture(Gdx.files.internal("ufo_laser.png")));

        //plaatst de boss in het midden
        ufoBoss.setSize(ufoBoss.getWidth()/1.25f, ufoBoss.getHeight()/1.25f);
        positionUfo = new Vector2(SCREEN_WIDTH / 2f - ufoBoss.getWidth()/2, SCREEN_HEIGHT);
        ufoBoss.setPosition(positionUfo.x, positionUfo.y);
        state = bossState.ENTRANCE;
        random = new Random();
        visible = true;
        dashTarget = new Vector2();
        evadeThresholdCounter = 0;
        laserPhase = 0;
        numberOfConsecutiveLaserAttacks = 0;
        drawLaser = false;
        ufoTopRight = true;
    }

    public void render(SpriteBatch batch) {
        ufoBoss.draw(batch);
        if (drawLaser) {
            ufoLaser.draw(batch);
        } else {
            ufoLaser.setAlpha(0);
        }
    }

    public void update(float delta) {
        switch (state) {
            case ENTRANCE:
                bossEntrance(delta);
                break;
            case TP:
                teleport(true);
                break;
            case ROCKETS:
                missiles();
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

    private void decideState() {
        //als de afstand tussen de player en de boss < 100 pixels dan teleport state
        if (Math.abs((positionUfo.x + ufoBoss.getWidth() / 2) - (player.getPosX() + player.getTexture().getWidth()/2f)) < 100 ||
                Math.abs((positionUfo.y + ufoBoss.getHeight() / 2) - (player.getPosY() + player.getTexture().getHeight()/2f)) < 100) {
            state = bossState.TP;
        } //als de hoek tussen de player en de boss < 0 dus negatief (dat is > pi op de unit circle of > 180 graden), laser state
        else if (Math.atan2(player.getPosY() - positionUfo.y, player.getPosX() - positionUfo.x) < 0 && numberOfConsecutiveLaserAttacks < 3) {
            numberOfConsecutiveLaserAttacks++;
            laserPhase = 0;
            state = bossState.LASER;
        } else {
            //ufo breedte en hoogte bij x en y zorgt ervoor dat nieuwe positie niet buiten het scherm valt. 100 verkort de afstand van de dash
            dashTarget.set(new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoBoss.getWidth() / 2, random.nextInt(SCREEN_HEIGHT - 100) - ufoBoss.getHeight() / 3));
            state = bossState.EVADE;
        }
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------
    Bosstate functies
     ----------------------------------------------------------------------------------------------------------------------------------------------*/
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

    private void teleport(boolean isNewLocationRandom) {
        final float CHANGE_ALPHA = 0.04f;

        //als de boss zichbaar is, alpha naar 0, is de boss onzichtbaar, alpha naar 1
        if (visible) {
            ufoBoss.setAlpha(ufoBoss.getColor().a - CHANGE_ALPHA);
            if (ufoBoss.getColor().a <= 0.1)//kleiner dan 0.1 omdat hij anders reset naar 1.0
            {
                ufoBoss.setAlpha(0);
                positionUfo = teleportNewLocation(isNewLocationRandom);
                ufoBoss.setPosition(positionUfo.x, positionUfo.y);
                visible = false;
            }
        } else {
            ufoBoss.setAlpha(ufoBoss.getColor().a + CHANGE_ALPHA);
            if (ufoBoss.getColor().a >= 0.9)//groter dan 0.9 omdat hij anders reset naar 0.0
            {
                ufoBoss.setAlpha(1);
                visible = true;
                if (isNewLocationRandom) {
                    decideState();
                } else {
                    laserPhase = 1;
                }
            }
        }
    }

    private Vector2 teleportNewLocation(boolean isNewLocationRandom) {
        Vector2 location;
        if (isNewLocationRandom) {
            location = new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoBoss.getWidth() / 2, random.nextInt(SCREEN_HEIGHT - 100) - ufoBoss.getHeight() / 3);
        } else {//40 is toegevoegt zodat de ufo meer in de hoek terecht komt

            int number;
            number = random.nextInt(2);
            ufoTopRight = number == 1;

            if (ufoTopRight) {
                location = new Vector2(SCREEN_WIDTH - ufoBoss.getWidth() / 2, SCREEN_HEIGHT - ufoBoss.getHeight());
            } else {
                location = new Vector2(0 - ufoBoss.getWidth() / 2, SCREEN_HEIGHT - ufoBoss.getHeight());
            }
        }
        return location;
    }

    private void evade(float delta) {
        final int DASH_THRESHOLD = 300;
        final int UFO_SPEED = 20;

        if (evadeThresholdCounter > DASH_THRESHOLD) {
            //dash

            //zoekt de hoek tussen boss en dashtarget
            double angle = Math.atan2(dashTarget.y - ufoBoss.getY(), dashTarget.x - ufoBoss.getX());
            //System.out.println("angle radians= " + angle);

            //beweeg naar de dashtarget toe
            ufoBoss.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y += UFO_SPEED * Math.sin(angle));
            //System.out.println("cos of angle = " + Math.cos(angle));
            //System.out.println("afstand = "+Math.abs(dashTarget.x - ufoBoss.getX()) + "speed * cos " + Math.abs(UFO_SPEED * Math.cos(angle)));

            //als de afstand tussen de boss en de target < ufo_speed * math.cos of math.sin, reset treshold en ga naar volgende state
            //als dit niet wordt gedaan dan is het mogelijk dat de sprite glitched
            if (Math.abs(dashTarget.x - ufoBoss.getX()) < Math.abs(UFO_SPEED * Math.cos(angle)) ||
                    Math.abs(dashTarget.y - ufoBoss.getY()) < Math.abs(UFO_SPEED * Math.sin(angle))) {
                evadeThresholdCounter = 0;
                decideState();
            }
        } else {
            //hoover

            final double HOOVER_AMPLITUDE = 1.7;
            final double HOOVER_SPEEDX = 0.0075;
            final double HOOVER_SPEEDY = 0.0053;
            final int EVADETHRESHOLD_SPEED = 200;

            //tijd dat het duurt voor de eerstvolgende dash
            evadeThresholdCounter += EVADETHRESHOLD_SPEED * delta;

            //de volgende twee regels zorgen voor (pseudo)random beweging (hoovering)
            //de speed zijn voor x en y verschillend want... het is een ufo
            positionUfo.x += HOOVER_AMPLITUDE * (float) Math.cos(TimeUtils.millis() * HOOVER_SPEEDX);
            positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY);
            ufoBoss.setPosition(positionUfo.x, positionUfo.y);
            // System.out.println(evadeThreshold);

        }
    }

    private void laser() {
        switch (laserPhase) {
            case 0:
                teleport(false);
                break;
            case 1:
                moveUfoLaser();
                shootLaser();
                break;
            case 2:
                drawLaser = false;
                decideState();
                break;
        }
    }

    private void shootLaser() {
        drawLaser = true;
        ufoLaser.setAlpha(1);
        ufoLaser.setPosition((positionUfo.x + ufoBoss.getWidth() / 2) - ufoLaser.getWidth()/2, positionUfo.y - ufoLaser.getHeight());
    }

    private void moveUfoLaser() {
        final int UFO_SPEED = 13;
        final double HOOVER_AMPLITUDE = 5;
        final double HOOVER_SPEEDY = 0.005;

        if (ufoTopRight) {
            ufoBoss.setPosition(
                    positionUfo.x += UFO_SPEED * Math.cos(Math.PI),
                    positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY));
            if (positionUfo.x < 0) {
                laserPhase = 2;
            }
        } else {
            ufoBoss.setPosition(
                    positionUfo.x += UFO_SPEED * Math.cos(0),
                    positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY));
            if (positionUfo.x > SCREEN_WIDTH - ufoBoss.getWidth()) {
                laserPhase = 2;
            }
        }
        positionUfo.y += Math.sin(TimeUtils.millis());
    }

    private void missiles() {
        





    }



    private void trackingLaser() {


    }




}
