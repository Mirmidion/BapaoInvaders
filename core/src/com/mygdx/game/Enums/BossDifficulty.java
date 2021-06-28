package com.mygdx.game.Enums;

public enum BossDifficulty {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    BossDifficulty(int number) {
        switch (number){
            case 1:
                //boss
                boss_damage_reduction = 1f;

                //evade
                evade_dash_threshold = 3f;
                evade_ufo_speed = 10;

                //laser
                laser_ufo_speed = 9;
                laser_damage = 10;

                //missiles
                missiles_ufo_speed = 10;
                missiles_time_between = 5f;
                missiles_max_count = 4;

                missile_speed = 3;
                missile_damage = 10;
                missile_lifetime = 3f;
                missile_time_before_first = missiles_time_between/2f;

                //tracking laser
                trackinglaser_ufo_speed = 9;
                trackinglaser_time_between = 3f;
                trackinglaser_max_count = 3;
                break;
            case 2:
                //boss
                boss_damage_reduction = 2f;

                //evade
                evade_dash_threshold = 2f;
                evade_ufo_speed = 15;

                //laser
                laser_ufo_speed = 11;
                laser_damage = 15;

                //missiles
                missiles_ufo_speed = 15;
                missiles_time_between = 3f;
                missiles_max_count = 6;

                missile_speed = 5;
                missile_damage = 15;
                missile_lifetime = 4f;
                missile_time_before_first = missiles_time_between/2f;

                //tracking laser
                trackinglaser_ufo_speed = 11;
                trackinglaser_time_between = 2f;
                trackinglaser_max_count = 5;
                break;
            case 3:
                //boss
                boss_damage_reduction = 3f;

                //evade
                evade_dash_threshold = 1f;
                evade_ufo_speed = 20;

                //laser
                laser_ufo_speed = 13;
                laser_damage = 20;

                //missiles
                missiles_ufo_speed = 20;
                missiles_time_between = 1f;
                missiles_max_count = 8;

                missile_speed = 7;
                missile_damage = 20;
                missile_lifetime = 5f;
                missile_time_before_first = missiles_time_between/2f;

                //tracking laser
                trackinglaser_ufo_speed = 18;
                trackinglaser_time_between = 1f;
                trackinglaser_max_count = 7;
                break;
        }
    }

    //boss
    private float boss_damage_reduction; //hoe hoger dit getal des te meer damage is reduced.

    //evade
    private float evade_dash_threshold;
    private int evade_ufo_speed;

    //laser
    private int laser_ufo_speed;
    private int laser_damage;

    //missiles
    private int missiles_ufo_speed;
    private float missiles_time_between;
    private float missiles_max_count;

    private int missile_speed;
    private int missile_damage;
    private float missile_lifetime;
    private float missile_time_before_first;

    //tracking laser
    private int trackinglaser_ufo_speed;
    private float trackinglaser_time_between;
    private float trackinglaser_max_count;

    public float getBoss_damage_reduction() {
        return boss_damage_reduction;
    }

    public float getEvade_dash_threshold() {
        return evade_dash_threshold;
    }

    public int getEvade_ufo_speed() {
        return evade_ufo_speed;
    }

    public int getLaser_ufo_speed() {
        return laser_ufo_speed;
    }

    public int getLaser_damage() {
        return laser_damage;
    }

    public int getMissiles_ufo_speed() {
        return missiles_ufo_speed;
    }

    public float getMissiles_time_between() {
        return missiles_time_between;
    }

    public float getMissiles_max_count() {
        return missiles_max_count;
    }

    public int getMissile_speed() {
        return missile_speed;
    }

    public int getMissile_damage() {
        return missile_damage;
    }

    public float getMissile_lifetime() {
        return missile_lifetime;
    }

    public float getMissile_time_before_first() {
        return missile_time_before_first;
    }

    public int getTrackinglaser_ufo_speed() {
        return trackinglaser_ufo_speed;
    }

    public float getTrackinglaser_time_between() {
        return trackinglaser_time_between;
    }

    public float getTrackinglaser_max_count() {
        return trackinglaser_max_count;
    }
}
