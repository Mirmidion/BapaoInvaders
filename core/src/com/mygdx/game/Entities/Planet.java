package com.mygdx.game.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Planet {

    // Difficulty of the waves
    int difficulty;
    int currentWave;
    static LinkedList<Planet> planetListOfDifficulty = new LinkedList<Planet>();


    // Waves consist of arraylist<int> that contain amount and types of enemies
    // The ArrayList<ArrayList<Integer>> = {{amount of enemies, enemy type}, {amount of enemies, enemy type}, ...}
    ArrayList<ArrayList<Integer>> waves = new ArrayList<ArrayList<Integer>>();
    int lastWave;

    ArrayList<Enemy> enemyWaves = new ArrayList<Enemy>();

    /* Planet classes:
    1 - Asteroid 15%
    2 - Moon 15% (if there is a planet)
    3 - Planet 50%
    4 - Gas Giant 10%
    5 - Ice Giant 10%
    */
    int planetClass = 0;

    // Boolean for if it can have moons and which planet this moon orbits
    ArrayList<Planet> moonList = new ArrayList<Planet>();

    // Position in Solar System
    float posX;
    float posY;
    private float angle;
    int orbit = 100;
    private boolean orbitClockWise = true;
    private float rotationSpeed = 1;

    static int globalDifficulty = 0;


    // Color and texture of the Planet
    Color planetColor = new Color(100,100,100);
    Texture currentPlanetTexture;

    // Radius of planet
    int radius = 0;


    Texture asteroidTexture = new Texture("Asteroid.png");
    Texture gasGiantTexture = new Texture("GasGiant.png");
    Texture iceGiantTexture = new Texture("IceGiant.png");
    Texture moonTexture = new Texture("Moon.png");

    // Moon colour
    Color moonColor = new Color(73, 72, 72);



    // All defenses
    static ArrayList<Defense> defenses = new ArrayList<Defense>();


    public Planet(int orbit){
        int random = MathUtils.random(85);
        int randomDirection = MathUtils.random(0,100);
        this.difficulty = globalDifficulty;
        planetListOfDifficulty.offer(this);
        System.out.println(planetListOfDifficulty.size());
        globalDifficulty++;
        if (isBetween(random, 0, 50)){
            int randomColor = MathUtils.random(1,5);
            currentPlanetTexture = (randomColor == 1)? new Texture(Gdx.files.internal("Planet1.png")): (randomColor == 2)? new Texture(Gdx.files.internal("Planet3.png")):(randomColor == 3)? new Texture(Gdx.files.internal("Planet2.png")):(randomColor == 4)? new Texture(Gdx.files.internal("Planet2.png")): new Texture(Gdx.files.internal("Planet2.png"));
            this.planetClass = 3;
            System.out.println("Added a Planet");
            this.GenerateMoons(this.planetClass);
            this.radius = 30;
        }
        else if (isBetween(random, 51, 60)){
            this.currentPlanetTexture = gasGiantTexture;
            this.planetClass = 4;
            System.out.println("Added a Gas Giant");
            this.GenerateMoons(this.planetClass);
            this.radius = 50;
        }
        else if (isBetween(random, 61, 70)){
            this.currentPlanetTexture = iceGiantTexture;
            this.planetClass = 5;
            System.out.println("Added a Ice Giant");
            this.GenerateMoons(this.planetClass);
            this.radius = 50;
        }
        else if (isBetween(random, 71, 85)) {
            this.currentPlanetTexture = asteroidTexture;
            this.planetClass = 1;
            System.out.println("Added an asteroid");
            this.radius = 10;
        }
        orbitClockWise = randomDirection <= 50;
        angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
        posX = (float)Math.cos(angle)*orbit;
        posY = (float)Math.sin(angle)*orbit;
        this.orbit = orbit;
        this.rotationSpeed = MathUtils.random(0.5f,2);

        this.generateWaves();
        this.generateEnemies(1);
        System.out.println(enemyWaves.size());
        this.lastWave = this.enemyWaves.size()-1;

    }

    public Planet(boolean isMoon, Planet orbitPlanet){
        this.planetColor = moonColor;
        this.currentPlanetTexture = moonTexture;
        this.planetClass = 2;
        this.radius = 15;
        angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
        posX = (float)Math.cos(angle)*orbit;
        posY = (float)Math.sin(angle)*orbit;
        this.rotationSpeed = MathUtils.random(1.5f,3);
        this.difficulty = globalDifficulty;
        globalDifficulty++;
        this.generateWaves();
        this.generateEnemies(1);
        planetListOfDifficulty.offer(this);
    }

    public void GenerateMoons(int planetType){
        int amountOfMoons = MathUtils.random(-3,3);
        amountOfMoons = Math.max(amountOfMoons, 0);
        for (int i = amountOfMoons; i > 0; i--){
            if (50 > ((i < amountOfMoons && planetType == 3)? MathUtils.random(0,100):100) || (10 > ((planetType == 4 || planetType == 5)? MathUtils.random(0,100):100))){
                System.out.println("break");
                break;
            }
            addMoon(new Planet(true, this));
            System.out.println("Added a Moon");
        }
    }

    public void addMoon(Planet moon){
        this.moonList.add(moon);
    }

    public void orbit(){
        this.setOrbit(getOrbitDirection(this.rotationSpeed));
        this.posX = (float)Math.cos(angle)*orbit;
        this.posY = (float)Math.sin(angle)*orbit;
    }

    public void setOrbit(float angle){
        if ((this.angle + angle > 2*Math.PI && orbitClockWise)||(this.angle + angle > 2*Math.PI && !orbitClockWise)){
            this.angle = 0;
        }
        else if ((this.angle + angle < 0&&!orbitClockWise)){
            this.angle = (float)(2*Math.PI);
        }
        else{
            this.angle += angle;
        }
    }

    public void setMoonOrbit(int orbit){
        this.orbit = orbit;
        this.orbit();
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
            ArrayList<Integer> wave = new ArrayList<Integer>();
            int amountOfTypesOfEnemies = Math.min(4,Math.max(2,Math.round(MathUtils.random(1,2*(difficulty/3f*Math.max(MathUtils.random(-5,2),1))))));
            System.out.println(amountOfWaves);
            for (int enemyType = 1; enemyType <= amountOfTypesOfEnemies; enemyType++){
                int randomAmount = MathUtils.random(4,12);
                wave.add(randomAmount);
                wave.add(enemyType);
                this.waves.add(wave);
            }
        }
        //System.out.println(this.waves);
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
                enemyWaves.add(new Enemy(waves.get(wave - 1).get(amount+1), count * posXEnemy + padding, lineCount * 220 + 900, count * posXEnemy + padding - 50, count * posXEnemy + padding + 50));
                count++;
            }
            lineCount++;

        }
    }

    public static ArrayList<Defense> getDefenses() {
        return defenses;
    }

    public static void setDefenses(ArrayList<Defense> defenses) {
        Planet.defenses = defenses;
    }

    public ArrayList<Enemy> getEnemyWaves() {
        return enemyWaves;
    }

    public static int getGlobalDifficulty() {
        return globalDifficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public static void setGlobalDifficulty(int globalDifficulty) {
        Planet.globalDifficulty = globalDifficulty;
    }

    public static void setPlanetListOfDifficulty(LinkedList<Planet> planetListOfDifficulty) {
        Planet.planetListOfDifficulty = planetListOfDifficulty;
    }

    public static LinkedList<Planet> getPlanetListOfDifficulty() {
        return planetListOfDifficulty;
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

    public Color getPlanetColor() {
        return planetColor;
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

    public static void regenerateDefenses(){
        defenses.add(new Defense(233, 300));
        defenses.add(new Defense(570, 300));
        defenses.add(new Defense(904, 300));
        defenses.add(new Defense(1241, 300));
        defenses.add(new Defense(1578, 300));
    }

    public ArrayList<ArrayList<Integer>> getWaves() {
        return waves;
    }
}
