package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends Sprite {

    //misc setup
    private enum bossState {ENTRANCE, TP, MISSILES, LASER, TRACKING_LASER, EVADE}

    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private bossState state;
    private final PlayerBoss player;

    //boss setup
    private Sprite ufoBoss;
    private Vector2 positionUfo;

    //teleport state
    private boolean visible;
    private Random random;

    //evade state
    private Vector2 dashTarget;
    private float evadeThresholdCounter;
    private boolean ufoTopRight;

    //laser state
    private int laserPhase;
    private int numberOfConsecutiveLaserAttacks;
    private Sprite ufoLaser;
    private boolean drawLaser;
    boolean canDoLaserAttack;

    //missile state
    private int missilesPhase;
    float missileTimer = 0;
    boolean drawMissile;
    int missileCount;
    ArrayList<Missile> missiles;
    private int numberOfMissileAttacks;
    boolean canDoMissileAttack;

    public Boss(PlayerBoss player) {
        this.player = player;
        init();
    }

    private void init() {
        ufoBoss = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));
        ufoLaser = new Sprite(new Texture(Gdx.files.internal("ufo_laser.png")));

        //boss setup
        ufoBoss.setSize(ufoBoss.getWidth() / 1.33f, ufoBoss.getHeight() / 1.33f);
        positionUfo = new Vector2(SCREEN_WIDTH / 2f - ufoBoss.getWidth() / 2, SCREEN_HEIGHT);
        ufoBoss.setPosition(positionUfo.x, positionUfo.y);
        state = bossState.ENTRANCE;

        //teleport state
        random = new Random();
        visible = true;

        //evade state
        dashTarget = new Vector2();
        evadeThresholdCounter = 0;

        //laser state
        laserPhase = 0;
        numberOfConsecutiveLaserAttacks = 0;
        drawLaser = false;
        ufoTopRight = true;
        canDoLaserAttack = true;

        //missile state
        missilesPhase = 0;
        drawMissile = false;
        missileCount = 0;
        missiles = new ArrayList<>();
        numberOfMissileAttacks = 0;
        canDoMissileAttack = false;
    }

    public void draw(SpriteBatch batch) {
        ufoBoss.draw(batch);
        if (drawLaser) {
            ufoLaser.draw(batch);
        } else {
            ufoLaser.setAlpha(0);
        }

        for (Missile missile : missiles) {
            missile.draw(batch);
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
            case MISSILES:
                missiles(delta);
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

        if (missiles.size() >= 1) {
            updateMissiles(delta);
        }

    }

    private void decideState() {
        //als de afstand tussen de player en de boss < 100 pixels dan teleport state
        if (Math.abs((positionUfo.x + ufoBoss.getWidth() / 2) - (player.getPosX() + player.getTexture().getWidth() / 2f)) < 100 ||
                Math.abs((positionUfo.y + ufoBoss.getHeight() / 2) - (player.getPosY() + player.getTexture().getHeight() / 2f)) < 100) {
            state = bossState.TP;
        } //als de hoek tussen de player en de boss < 0 dus negatief (dat is > pi op de unit circle of > 180 graden), laser state
        else if ((Math.atan2(player.getPosY() - positionUfo.y, player.getPosX() - positionUfo.x) < 0) && numberOfConsecutiveLaserAttacks < 3) {
            numberOfConsecutiveLaserAttacks++;
            if(numberOfConsecutiveLaserAttacks == 3){
                canDoMissileAttack = true;
            }
            laserPhase = 0;
            state = bossState.LASER;
        } else if ((Math.abs((positionUfo.x + ufoBoss.getWidth() / 2) - (player.getPosX() + player.getTexture().getWidth() / 2f)) > 300 ||
                Math.abs((positionUfo.y + ufoBoss.getHeight() / 2) - (player.getPosY() + player.getTexture().getHeight() / 2f)) > 300) &&
                canDoMissileAttack) {
            canDoMissileAttack = false;
            numberOfConsecutiveLaserAttacks = 0;
            missilesPhase = 0;
            state = bossState.MISSILES;

        } else {
            //ufo breedte en hoogte bij x en y zorgt ervoor dat nieuwe positie niet buiten het scherm valt. 100 verkort de afstand van de dash
            dashTarget.set(new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoBoss.getWidth() / 2,
                    random.nextInt(SCREEN_HEIGHT - 100) - ufoBoss.getHeight() / 3));
            state = bossState.EVADE;
        }
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------
    Bosstate functies
     ----------------------------------------------------------------------------------------------------------------------------------------------*/
    private void bossEntrance(float delta) {
        int ENTRANCE_ENDPOSITION = SCREEN_HEIGHT - 350;
        int ENTRANCE_SPEED = 75;

        //boss start boven en komt langzaam naar beneden
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
            location = new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoBoss.getWidth() / 2,
                    random.nextInt(SCREEN_HEIGHT - 100) - ufoBoss.getHeight() / 3);
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
        ufoLaser.setPosition((positionUfo.x + ufoBoss.getWidth() / 2) - ufoLaser.getWidth() / 2, positionUfo.y - ufoLaser.getHeight());
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

    private void missiles(float delta) {
        final int UFO_SPEED = 20;

        switch (missilesPhase) {
            case 0:
                moveToMiddle(UFO_SPEED);
                break;
            case 1:
                shootMissiles(delta);
                break;
            case 2:
                missileCount = 0;
                decideState();
                break;
        }
    }

    private void shootMissiles(float delta) {
        final float TIME_BETWEEN_MISSILES = 1f;
        final float MAX_MISSILES_COUNT = 10;

        missileTimer += delta;
        if (missileCount < MAX_MISSILES_COUNT && missileTimer >= TIME_BETWEEN_MISSILES) {
            //40f is de hoogte waarop de kanonen van de ufo zitten. Omdat bij de 2e missile er een rotatie van 270 wordt gedaan,
            //is het startpunt omgekeerd en dus moet de breedte (33) van de missile er ook af (33 + 40 = 77)
            //de 72.5f is de hoogte van de missile. Nu is het beginpunt van de missile bij het kanon van de ufo
            missiles.add(new Missile(positionUfo.x, positionUfo.y + ufoBoss.getHeight() / 2 - 40f, 90f, player));
            missiles.add(new Missile(positionUfo.x + ufoBoss.getWidth() - 72.5f, positionUfo.y + ufoBoss.getHeight() / 2 - 77f, 270f, player));
            missileCount += 2;
            missileTimer = 0;
        } else if (missileCount >= MAX_MISSILES_COUNT) {
            missilesPhase = 2;
            System.out.println(missilesPhase);
        }
    }

    private void updateMissiles(float delta) {
        ArrayList<Missile> missilesToRemove = new ArrayList<>();
        for (Missile missile : missiles) {
            missile.update(delta);
            //System.out.println("missile updatin");
            if (missile.remove) {
                missilesToRemove.add(missile);
            }
        }
        missiles.removeAll(missilesToRemove);
    }

    private void moveToMiddle(int UFO_SPEED) {
        Vector2 middle = new Vector2(SCREEN_WIDTH / 2f - ufoBoss.getWidth() / 2, SCREEN_HEIGHT / 2f - ufoBoss.getHeight() / 3);

        double angle = Math.atan2(middle.y - positionUfo.y, middle.x - positionUfo.x);

        ufoBoss.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y += UFO_SPEED * Math.sin(angle));

        if (Math.abs(middle.x - ufoBoss.getX()) < Math.abs(UFO_SPEED * Math.cos(angle)) ||
                Math.abs(middle.y - ufoBoss.getY()) < Math.abs(UFO_SPEED * Math.sin(angle))) {
            missilesPhase = 1;
        }
    }


    private void trackingLaser() {


    }


}
