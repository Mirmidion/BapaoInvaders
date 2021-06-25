package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.Enums.BossDifficulty;

import java.util.ArrayList;
import java.util.Random;

public class Boss extends Sprite {

    //misc setup
    private final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private final int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private final PlayerBoss player;

    //boss setup
    private enum bossState {ENTRANCE, TP, MISSILES, LASER, TRACKING_LASER, EVADE}

    private bossState state;
    private Sprite ufoSprite;
    private Vector2 positionUfo;
    private float health;
    private final Texture healthBar = new Texture("healthBar.png");
    private final Sprite healthBarBorder = new Sprite(new Texture("healthbar_border.png"));
    private Sprite bossIcon;
    private BossDifficulty bossDifficulty;

    //teleport state
    private boolean visible;
    private Random random;

    //evade state
    private Vector2 dashTarget;
    private float evadeThresholdCounter;
    private boolean ufoTopRight;

    //laser state
    private int laserPhase;
    private int numberOfLaserAttacks;
    BigLaser ufoLaser;
    private boolean drawLaser;
    boolean canDoLaserAttack;

    //missile state
    private int missilesPhase;
    private float missileTimer;
    boolean drawMissile;
    private int missileCount;
    private ArrayList<Missile> missiles;
    private boolean canDoMissileAttack;

    //tracking laser
    private int trackingLaserPhase;
    private float trackingLaserTimer;
    private int laserCount;

    public Boss(PlayerBoss player, BossDifficulty difficulty) {
        this.player = player;
        this.bossDifficulty = difficulty;
        init();
    }

    private void init() {
        //boss setup
        ufoSprite = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));
        ufoSprite.setSize(ufoSprite.getWidth()/1.33f, ufoSprite.getHeight()/1.33f);
        positionUfo = new Vector2(SCREEN_WIDTH/2f - ufoSprite.getWidth()/2, SCREEN_HEIGHT);
        ufoSprite.setPosition(positionUfo.x, positionUfo.y);
        state = bossState.ENTRANCE;
        health = 100;
        healthBarBorder.setSize(healthBarBorder.getWidth()*1.5f, healthBarBorder.getHeight()*1.5f);
        healthBarBorder.setPosition(SCREEN_WIDTH/2 - healthBarBorder.getWidth()/2, SCREEN_HEIGHT - 100);
        bossIcon = new Sprite(new Texture(Gdx.files.internal("ufo_boss.png")));
        bossIcon.setSize(bossIcon.getWidth()/8f, bossIcon.getHeight()/8f);
        bossIcon.setPosition(healthBarBorder.getX() + healthBarBorder.getWidth() - 92,
                healthBarBorder.getY() + 18);


        //teleport state
        random = new Random();
        visible = true;

        //evade state
        dashTarget = new Vector2();
        evadeThresholdCounter = 0;

        //laser state
        laserPhase = 0;
        numberOfLaserAttacks = 0;
        drawLaser = false;
        ufoTopRight = true;
        canDoLaserAttack = true;
        ufoLaser = new BigLaser(this, bossDifficulty);

        //missile state
        missilesPhase = 0;
        drawMissile = false;
        missileCount = 0;
        missileTimer = 0;
        missiles = new ArrayList<>();
        canDoMissileAttack = false;

        //tracking laser
        trackingLaserPhase = 0;
        trackingLaserTimer = 0;
    }

    public void draw(SpriteBatch batch) {
        ufoSprite.draw(batch);
        if (drawLaser) {
            ufoLaser.draw(batch);
        }

        for (Missile missile : missiles) {
            missile.draw(batch);
        }

        drawHealthbar(batch);
    }

    public void update(float delta) {
        System.out.println(bossDifficulty);
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
                trackingLaser(delta);
                break;
            case EVADE:
                evade(delta);
                break;
        }


        if (drawLaser) {
            ufoLaser.update(delta);
            checkForLaserCollision();
        }

        if (missiles.size() >= 1) {
            updateMissiles(delta);
        }
    }

    private void decideState() {
        //als de afstand tussen de player en de boss < 100 pixels dan teleport state
        if (Math.abs((positionUfo.x + ufoSprite.getWidth() / 2) - (player.getPosX() + player.getTexture().getWidth() / 2f)) < 100 ||
                Math.abs((positionUfo.y + ufoSprite.getHeight() / 2) - (player.getPosY() + player.getTexture().getHeight() / 2f)) < 100) {
            state = bossState.TP;
        }

        //als de hoek tussen de player en de boss < 0 dus negatief (dat is > pi op de unit circle of > 180 graden), laser state
        else if ((Math.atan2(player.getPosY() - positionUfo.y, player.getPosX() - positionUfo.x) < 0) && numberOfLaserAttacks < 3) {
            numberOfLaserAttacks++;
            if (numberOfLaserAttacks == 3) {
                canDoMissileAttack = true;
            }
            laserPhase = 0;
            state = bossState.LASER;
        }

        //als de afstand tussen de speler en de boss op x of y > 500 pixels en de laserattack is vaker dan 3 keer geweest, missile state
        else if ((Math.abs((positionUfo.x + ufoSprite.getWidth() / 2) - (player.getPosX())) > 500 ||
                Math.abs((positionUfo.y + ufoSprite.getHeight() / 2) - (player.getPosY())) > 500) &&
                canDoMissileAttack) {
            canDoMissileAttack = false;
            numberOfLaserAttacks = 0;
            missilesPhase = 0;
            state = bossState.MISSILES;
        }

        //negatieve hoek tussen player en boss, afstand tussen player > 500 pixels op x of y, tracking laser state
        else if((Math.atan2(player.getPosY() - positionUfo.y, player.getPosX() - positionUfo.x) > 0)){
            trackingLaserPhase = 0;
            state = bossState.TRACKING_LASER;
        }

        //dit is de default state als er geen van de bovenste states werken
        else {
            //ufo breedte en hoogte bij x en y zorgt ervoor dat nieuwe positie niet buiten het scherm valt. 100 verkort de afstand van de dash
            dashTarget.set(new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoSprite.getWidth() / 2,
                    random.nextInt(SCREEN_HEIGHT - 100) - ufoSprite.getHeight() / 3));
            state = bossState.EVADE;
        }
    }

    public void drawHealthbar(SpriteBatch batch){
        if (health != 0) {
            batch.draw(healthBar, healthBarBorder.getX() + 22.5f, healthBarBorder.getY() + 16f,
                    health * 10.32f - 50, healthBarBorder.getHeight()/1.50f);
        }
        healthBarBorder.draw(batch);

        bossIcon.draw(batch);
    }

    /*-----------------------------------------------------------------------------------------------------------------------------------------------
    Bosstate functies
     ----------------------------------------------------------------------------------------------------------------------------------------------*/
    private void bossEntrance(float delta) {
        int ENTRANCE_ENDPOSITION = SCREEN_HEIGHT - 350;
        int ENTRANCE_SPEED = 75;

        //boss start boven en komt langzaam naar beneden
        if (ufoSprite.getY() > ENTRANCE_ENDPOSITION) {
            positionUfo.y -= ENTRANCE_SPEED * delta ;
            ufoSprite.setY(positionUfo.y);
        } else if (ufoSprite.getY() <= ENTRANCE_ENDPOSITION) {
            decideState();
        }
    }

    private void teleport(boolean isNewLocationRandom) {
        final float CHANGE_ALPHA = 0.04f;

        //als de boss zichbaar is, alpha naar 0, is de boss onzichtbaar, alpha naar 1
        if (visible) {
            ufoSprite.setAlpha(ufoSprite.getColor().a - CHANGE_ALPHA);
            if (ufoSprite.getColor().a <= 0.1)//kleiner dan 0.1 omdat hij anders reset naar 1.0
            {
                ufoSprite.setAlpha(0);
                positionUfo = teleportNewLocation(isNewLocationRandom);
                ufoSprite.setPosition(positionUfo.x, positionUfo.y);
                visible = false;
            }
        } else {
            ufoSprite.setAlpha(ufoSprite.getColor().a + CHANGE_ALPHA);
            if (ufoSprite.getColor().a >= 0.9)//groter dan 0.9 omdat hij anders reset naar 0.0
            {
                ufoSprite.setAlpha(1);
                visible = true;
                if (isNewLocationRandom) {
                    decideState();
                } else if (state == bossState.LASER) {
                    laserPhase = 1;
                } else if (state == bossState.TRACKING_LASER) {
                    trackingLaserPhase = 1;
                }
            }
        }
    }

    private Vector2 teleportNewLocation(boolean isNewLocationRandom) {
        Vector2 location;
        if (isNewLocationRandom) {
            location = new Vector2(random.nextInt(SCREEN_WIDTH - 100) - ufoSprite.getWidth() / 2,
                    random.nextInt(SCREEN_HEIGHT - 100) - ufoSprite.getHeight() / 3);
        } else {//40 is toegevoegt zodat de ufo meer in de hoek terecht komt

            int number;
            number = random.nextInt(2);
            ufoTopRight = number == 1;

            if (ufoTopRight) {
                location = new Vector2(SCREEN_WIDTH - ufoSprite.getWidth() / 2, SCREEN_HEIGHT - ufoSprite.getHeight());
            } else {
                location = new Vector2(0 - ufoSprite.getWidth() / 2, SCREEN_HEIGHT - ufoSprite.getHeight());
            }
        }
        return location;
    }

    private void evade(float delta) {
        final float DASH_THRESHOLD = bossDifficulty.getEvade_dash_threshold();
        final int UFO_SPEED = bossDifficulty.getEvade_ufo_speed();

        if (evadeThresholdCounter > DASH_THRESHOLD) {
            //dash

            //zoekt de hoek tussen boss en dashtarget
            double angle = Math.atan2(dashTarget.y - ufoSprite.getY(), dashTarget.x - ufoSprite.getX());
            //System.out.println("angle radians= " + angle);

            //beweeg naar de dashtarget toe
            ufoSprite.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y += UFO_SPEED * Math.sin(angle));
            //System.out.println("cos of angle = " + Math.cos(angle));
            //System.out.println("afstand = "+Math.abs(dashTarget.x - ufoBoss.getX()) + "speed * cos " + Math.abs(UFO_SPEED * Math.cos(angle)));

            //als de afstand tussen de boss en de target < ufo_speed * math.cos of math.sin, reset treshold en ga naar volgende state
            //als dit niet wordt gedaan dan is het mogelijk dat de sprite glitched
            if (Math.abs(dashTarget.x - ufoSprite.getX()) < Math.abs(UFO_SPEED * Math.cos(angle)) ||
                    Math.abs(dashTarget.y - ufoSprite.getY()) < Math.abs(UFO_SPEED * Math.sin(angle))) {
                evadeThresholdCounter = 0;
                decideState();
            }
        } else {
            //hoover

            final double HOOVER_AMPLITUDE = 1.7;
            final double HOOVER_SPEEDX = 0.0075;
            final double HOOVER_SPEEDY = 0.0053;

            //snelheid waarmee naar de treshold gewerkt wordt
            evadeThresholdCounter += delta;

            //de volgende twee regels zorgen voor (pseudo)random beweging (hoovering)
            //de speed zijn voor x en y verschillend want... het is een ufo
            positionUfo.x += HOOVER_AMPLITUDE * (float) Math.cos(TimeUtils.millis() * HOOVER_SPEEDX);
            positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY);
            ufoSprite.setPosition(positionUfo.x, positionUfo.y);
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
                drawLaser = true;
                break;
            case 2:
                drawLaser = false;
                decideState();
                break;
        }
    }

    private void checkForLaserCollision(){
        if(player.getPlayerHitbox().overlaps(ufoLaser.getLaserHitbox())){
            if(!player.isInvulnerable()) {
                player.setInvulnerable(true);
                player.setHealth(-ufoLaser.getLaserDamage());
            }
        }
    }

    private void moveUfoLaser() {
        final int UFO_SPEED = bossDifficulty.getLaser_ufo_speed();
        final double HOOVER_AMPLITUDE = 5;
        final double HOOVER_SPEEDY = 0.005;

        if (ufoTopRight) {
            ufoSprite.setPosition(
                    positionUfo.x += UFO_SPEED * Math.cos(Math.PI),
                    positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY));
            if (positionUfo.x < 0) {
                laserPhase = 2;
            }
        } else {
            ufoSprite.setPosition(
                    positionUfo.x += UFO_SPEED * Math.cos(0),
                    positionUfo.y += HOOVER_AMPLITUDE * (float) Math.sin(TimeUtils.millis() * HOOVER_SPEEDY));
            if (positionUfo.x > SCREEN_WIDTH - ufoSprite.getWidth()) {
                laserPhase = 2;
            }
        }
        positionUfo.y += Math.sin(TimeUtils.millis());
    }

    private void missiles(float delta) {
        final int UFO_SPEED = bossDifficulty.getMissiles_ufo_speed();

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
        final float TIME_BETWEEN_MISSILES = bossDifficulty.getMissiles_time_between();
        final float MAX_MISSILES_COUNT = bossDifficulty.getMissiles_max_count();

        missileTimer += delta;
        if (missileCount < MAX_MISSILES_COUNT && missileTimer >= TIME_BETWEEN_MISSILES) {
            //40f is de hoogte waarop de kanonen van de ufo zitten.
            //de 72.5f is de hoogte van de missile. Nu is het beginpunt van de missile bij het kanon van de ufo
            missiles.add(new Missile(positionUfo.x, positionUfo.y + ufoSprite.getHeight() / 2 - 40f,
                    90f, player, bossDifficulty));
            missiles.add(new Missile(positionUfo.x + ufoSprite.getWidth() - 72.5f, positionUfo.y + ufoSprite.getHeight() / 2 - 40f,
                    270f, player, bossDifficulty));
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
            if(missile.getDrawHitboxes()) {
                missile.drawHitbox();
            }
            //System.out.println("missile updatin");
            if (missile.remove) {
                missilesToRemove.add(missile);
            }

            if(player.getPlayerHitbox().overlaps(missile.getMissileHitbox())){
                missilesToRemove.add(missile);
                if(!player.isInvulnerable()){
                    player.setInvulnerable(true);
                    player.setHealth(-missile.getMissileDamage());
                }
            }
        }
        missiles.removeAll(missilesToRemove);
    }

    private void moveToMiddle(int UFO_SPEED) {
        Vector2 middle = new Vector2(SCREEN_WIDTH / 2f - ufoSprite.getWidth() / 2, SCREEN_HEIGHT / 2f - ufoSprite.getHeight() / 3);

        double angle = Math.atan2(middle.y - positionUfo.y, middle.x - positionUfo.x);

        ufoSprite.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y += UFO_SPEED * Math.sin(angle));

        if (Math.abs(middle.x - ufoSprite.getX()) < Math.abs(UFO_SPEED * Math.cos(angle)) ||
                Math.abs(middle.y - ufoSprite.getY()) < Math.abs(UFO_SPEED * Math.sin(angle))) {
            missilesPhase = 1;
        }
    }

    private void trackingLaser(float delta) {
        switch (trackingLaserPhase) {
            case 0:
                teleport(false);
                break;
            case 1:
                moveUfoTrackingLaser(delta);
                break;
            case 2:
                laserCount = 0;
                decideState();
                break;
        }
    }

    public void moveUfoTrackingLaser(float delta) {
        final int UFO_SPEED = bossDifficulty.getTrackinglaser_ufo_speed();
        final float TIME_BETWEEN_LASER = bossDifficulty.getTrackinglaser_time_between();
        final float MAX_LASER_COUNT = bossDifficulty.getTrackinglaser_max_count();

        trackingLaserTimer += delta;
        if (laserCount < MAX_LASER_COUNT && trackingLaserTimer >= TIME_BETWEEN_LASER) {
            if (trackingLaserTimer >= 1.5 * TIME_BETWEEN_LASER) {
                drawLaser = false;
                trackingLaserTimer = 0;
                laserCount++;
            } else {
                drawLaser = true;
            }
        } else {
            double angle = Math.atan2(player.getPosY() - (ufoSprite.getY() - ufoSprite.getHeight() / 2), player.getPosX() - (ufoSprite.getX() + ufoSprite.getWidth() / 2));

            ufoSprite.setPosition(positionUfo.x += UFO_SPEED * Math.cos(angle), positionUfo.y);
        }

        if(laserCount >= MAX_LASER_COUNT){
            trackingLaserPhase = 2;
        }
    }


    public Sprite getUfoSprite() {
        return ufoSprite;
    }

    public Vector2 getPositionUfo() {
        return positionUfo;
    }

    public Rectangle getBossHitbox(){
        return new Rectangle(positionUfo.x + 52f, positionUfo.y,
                ufoSprite.getWidth()/1.20f, ufoSprite.getHeight()/1.10f);
    }

    public void setHealth(float health) {
        this.health = Math.max(Math.min(this.health + health, 100), 0);
    }

}
