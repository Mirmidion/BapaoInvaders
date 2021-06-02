package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Planet implements Serializable {

    //Difficulty of the waves
    private int difficulty = 0;

    //Waves consist of arraylist<int> that contain amount and types of enemies
    //The ArrayList<ArrayList<Integer>> = {{amount of enemies, enemy type}, {amount of enemies, enemy type}, ...}
    private final ArrayList<ArrayList<Integer>> waves = new ArrayList<>();

    private ArrayList<Enemy> enemyWaves = new ArrayList<>();

    /* Planet classes:
    1 - Asteroid 15%
    2 - Moon 15% (if there is a planet)
    3 - Planet 50%
    4 - Gas Giant 10%
    5 - Ice Giant 10%
    */
    private int planetClass = 0;
    private int whichPlanetTexture;

    private static final long serialVersionUID = 6529685098267757690L;

    //Boolean for if it can have moons and which planet this moon orbits
    private final ArrayList<Planet> moonList = new ArrayList<>();

    //Position in Solar System
    private float posX;
    private float posY;
    private float angle;

    //Orbit
    private int orbit = 100;
    private boolean orbitClockWise = true;
    private final float rotationSpeed;
    private Planet orbitPlanet;

    //Is this a moon?
    private boolean isMoon = false;

    //Color and texture of the Planet
    private transient Texture currentPlanetTexture = new Texture("Asteroid.png");

    //Radius of planet
    private int radius = 0;

    //Textures
    transient static Texture asteroidTexture = new Texture("Asteroid.png");
    transient static Texture gasGiantTexture = new Texture("GasGiant.png");
    transient static Texture iceGiantTexture = new Texture("IceGiant.png");
    transient static Texture moonTexture = new Texture("Moon.png");
    transient static Texture planet1Texture = new Texture(Gdx.files.internal("Planet1.png"));
    transient static Texture planet2Texture = new Texture(Gdx.files.internal("Planet2.png"));
    transient static Texture planet3Texture = new Texture(Gdx.files.internal("Planet3.png"));

    //All defenses
    static ArrayList<Defense> defenses = new ArrayList<>();

    public Planet(int orbit, SolarSystem solarSystem){
        int random = MathUtils.random(85);
        int randomDirection = MathUtils.random(0,100);

        if (isBetween(random, 0, 50)){
            int randomColor = MathUtils.random(1,5);
            currentPlanetTexture = (randomColor == 1)? planet1Texture: (randomColor == 2)? planet3Texture:(randomColor == 3)? planet2Texture:(randomColor == 4)? new Texture(Gdx.files.internal("Planet2.png")): new Texture(Gdx.files.internal("Planet2.png"));
            whichPlanetTexture = (randomColor == 1)? 1: (randomColor == 2)? 3:(randomColor == 3)? 2:(randomColor == 4)? 2: 2;
            this.planetClass = 3;
            System.out.println("Added a Planet");
            this.generateMoons(this.planetClass, solarSystem);
            this.radius = 30;
        }
        else if (isBetween(random, 51, 60)){
            this.currentPlanetTexture = gasGiantTexture;
            this.planetClass = 4;
            System.out.println("Added a Gas Giant");
            this.generateMoons(this.planetClass, solarSystem);
            this.radius = 50;
        }
        else if (isBetween(random, 61, 70)){
            this.currentPlanetTexture = iceGiantTexture;
            this.planetClass = 5;
            System.out.println("Added a Ice Giant");
            this.generateMoons(this.planetClass, solarSystem);
            this.radius = 50;
        }
        else if (isBetween(random, 71, 85)) {
            this.currentPlanetTexture = asteroidTexture;
            this.planetClass = 1;
            System.out.println("Added an asteroid");
            this.radius = 10;
        }
        this.difficulty = solarSystem.getGlobalDifficulty();

        solarSystem.setGlobalDifficulty(solarSystem.getGlobalDifficulty()+1);
        orbitClockWise = randomDirection <= 50;
        angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
        posX = (float)Math.cos(angle)*orbit;
        posY = (float)Math.sin(angle)*orbit;
        this.orbit = orbit;
        this.rotationSpeed = MathUtils.random(0.5f,2);

        this.generateWaves();
        this.generateEnemies(1);
        solarSystem.getPlanetListOfDifficulty().offer(this);
    }

    public Planet(boolean isMoon, Planet orbitPlanet, SolarSystem solarSystem){
        //Moon properties
        this.isMoon = isMoon;
        this.orbitPlanet = orbitPlanet;
        this.currentPlanetTexture = moonTexture;
        this.planetClass = 2;
        this.radius = 15;
        angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
        posX = (float)Math.cos(angle)*orbit;
        posY = (float)Math.sin(angle)*orbit;
        this.rotationSpeed = MathUtils.random(1.5f,3);
        this.difficulty = solarSystem.getGlobalDifficulty();
        solarSystem.setGlobalDifficulty(solarSystem.getGlobalDifficulty()+1);
        this.generateWaves();
        this.generateEnemies(1);
        solarSystem.getPlanetListOfDifficulty().offer(this);
    }

    public void generateMoons(int planetType, SolarSystem solarSystem){
        int amountOfMoons = MathUtils.random(-3,3);
        amountOfMoons = Math.max(amountOfMoons, 0);
        for (int i = amountOfMoons; i > 0; i--){
            if (50 > ((i < amountOfMoons && planetType == 3)? MathUtils.random(0,100):100) || (10 > ((planetType == 4 || planetType == 5)? MathUtils.random(0,100):100))){
                //System.out.println("break");
                break;
            }
            addMoon(new Planet(true, this, solarSystem));
            //System.out.println("Added a Moon");
        }
    }

    public void addMoon(Planet moon){
        this.moonList.add(moon);
    }

    public void orbit(){
        this.setCurrentOrbit(getOrbitDirection(this.rotationSpeed));
        this.posX = (float)Math.cos(angle)*orbit;
        this.posY = (float)Math.sin(angle)*orbit;
    }

    public void setCurrentOrbit(float angle){
        if ((this.angle + angle > 2*Math.PI && orbitClockWise)||(this.angle + angle > 2*Math.PI && !orbitClockWise)){
            this.angle = 0;
        }
        else if ((this.angle + angle < 0 && !orbitClockWise)){
            this.angle = (float)(2*Math.PI);
        }
        else{
            this.angle += angle;
        }
    }

    public float getOrbitDirection(float speed){
        if (this.orbitClockWise){
            return 0.001f * speed;
        }
        else{
            return -0.001f * speed;
        }
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public void generateWaves(){
        int amountOfWaves = 4 + Math.round(difficulty/2f);
        for (int wavesOrder = 0; wavesOrder < amountOfWaves; wavesOrder++){
            ArrayList<Integer> wave = new ArrayList<>();
            int amountOfTypesOfEnemies = Math.min(4,Math.max(2,Math.round(MathUtils.random(1,2*(difficulty/3f*Math.max(MathUtils.random(-5,2),1))))));
            for (int enemyType = 1; enemyType <= (Math.min(amountOfTypesOfEnemies, 2)); enemyType++){
                int randomAmount = MathUtils.random(4,12);
                wave.add(randomAmount);
                wave.add(enemyType);
                this.waves.add(wave);
            }
        }
    }

    public void generateEnemies(int wave) {
        int amountOfEnemiesOnLine = 6;
        int lineCount = 0;

        for (int amount = 0; amount < waves.get(wave - 1).size(); amount += 2) {
            int totalAmountOfEnemies = waves.get(wave - 1).get(amount);
            int count = 0;
            for (int i = 0; i < totalAmountOfEnemies; i++) {
                if (waves.get(wave - 1).get(amount) > 6) {
                    amountOfEnemiesOnLine = 6;
                } else if (waves.get(wave - 1).get(amount) <= 6 || waves.get(wave - 1).get(amount) != 0) {
                    amountOfEnemiesOnLine = waves.get(wave - 1).get(amount);
                }
                if (waves.get(wave - 1).get(amount) == 0){
                    break;
                }

                if (count / 6 == 1) {
                    lineCount++;
                    count = 0;
                    waves.get(wave - 1).set(0, waves.get(wave - 1).get(amount) - 6);
                }

                int padding = (1920 - amountOfEnemiesOnLine * 140 - (amountOfEnemiesOnLine - 1) * 80) / 2;

                int posXEnemy = (amountOfEnemiesOnLine * 140 + (amountOfEnemiesOnLine - 1) * 80) / amountOfEnemiesOnLine;
                enemyWaves.add(new Enemy(waves.get(wave - 1).get(amount+1), count * posXEnemy + padding, lineCount * 220 + 1000, count * posXEnemy + padding - 50, count * posXEnemy + padding + 50,this));
                count++;
            }
            lineCount++;

        }
    }

    public void checkList(SolarSystem solarSystem){
        for (Planet planet : solarSystem.getPlanetListOfDifficulty()){
            System.out.println(planet.difficulty);
        }
    }

    public static void regenerateDefenses(){
        defenses.add(new Defense(233, 300));
        defenses.add(new Defense(570, 300));
        defenses.add(new Defense(904, 300));
        defenses.add(new Defense(1241, 300));
        defenses.add(new Defense(1578, 300));
    }

    public void setCurrentPlanetTexture() {
        if (planetClass == 1) {
            this.currentPlanetTexture = asteroidTexture;
        }
        else if (planetClass == 2) {
            this.currentPlanetTexture = moonTexture;
        }
        else if (planetClass == 3) {
            if (whichPlanetTexture == 1) {
                this.currentPlanetTexture = planet1Texture;
            }
            else if (whichPlanetTexture == 2) {
                this.currentPlanetTexture = planet2Texture;
            }
            else if (whichPlanetTexture == 3) {
                this.currentPlanetTexture = planet3Texture;
            }
        }
        else if (planetClass == 4) {
            this.currentPlanetTexture = gasGiantTexture;
        }else if (planetClass == 5) {
            this.currentPlanetTexture = iceGiantTexture;
        }
        else{
            this.currentPlanetTexture = planet1Texture;
        }
    }

    public static ArrayList<Defense> getDefenses() {
        return defenses;
    }

    public ArrayList<Enemy> getEnemyWaves() {
        return enemyWaves;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public Texture getPlanetTexture() {
        return currentPlanetTexture;
    }

    public int getRadius() {
        return radius;
    }

    public int getOrbit() {
        return orbit;
    }

    public void setOrbit(int orbit) {
        this.orbit = orbit;
    }

    public ArrayList<Planet> getMoonList() {
        return moonList;
    }

    public ArrayList<ArrayList<Integer>> getWaves() {
        return waves;
    }

    public void setEnemyWaves(ArrayList<Enemy> enemyWaves) {
        this.enemyWaves = enemyWaves;
    }

    public boolean isMoon() {
        return isMoon;
    }

    public Planet getOrbitPlanet() {
        return orbitPlanet;
    }
}
