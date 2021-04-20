package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

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
    Texture planetTexture;

    // Radius of planet
    int radius = 0;

    // List of all possible planet colours
    Color[] possiblePlanetColors = {
            new Color(200,10,10),
            new Color(125,0,0),
            new Color(113, 31, 127),
            new Color(34, 90, 12),
            new Color(86, 97, 30),
            new Color(37, 92, 79),
    };

    // Moon colour
    Color moonColor = new Color(73, 72, 72);

    // Asteroid colour
    Color asteroidColor = new Color(87, 75, 75);

    // Ice Giant colour
    Color iceGiantColor = new Color(37, 103, 203);

    // Gas Giant colour
    Color gasGiantColor = new Color(163, 128, 83);

    // All defenses
    Defense[] defenses = {new Defense(233,300),new Defense(570,300),new Defense(904,300),new Defense(1241,300),new Defense(1578,300)};

    public Planet(int orbit, Texture gasGiantTexture, Texture iceGiantTexture, Texture asteroidTexture){
        int random = MathUtils.random(85);
        int randomDirection = MathUtils.random(0,100);
        this.difficulty = globalDifficulty;
        planetListOfDifficulty.offer(this);
        System.out.println(planetListOfDifficulty.size());
        globalDifficulty++;
        if (isBetween(random, 0, 50)){
            int randomColor = MathUtils.random(1,5);
            planetTexture = (randomColor == 1)? new Texture(Gdx.files.internal("Planet1.png")): (randomColor == 2)? new Texture(Gdx.files.internal("Planet3.png")):(randomColor == 3)? new Texture(Gdx.files.internal("Planet2.png")):(randomColor == 4)? new Texture(Gdx.files.internal("Planet2.png")): new Texture(Gdx.files.internal("Planet2.png"));
            this.planetColor = possiblePlanetColors[randomColor];
            this.planetClass = 3;
            System.out.println("Added a Planet");
            this.GenerateMoons(this.planetClass);
            this.radius = 30;
        }
        else if (isBetween(random, 51, 60)){
            this.planetColor = gasGiantColor;
            this.planetTexture = gasGiantTexture;
            this.planetClass = 4;
            System.out.println("Added a Gas Giant");
            this.GenerateMoons(this.planetClass);
            this.radius = 50;
        }
        else if (isBetween(random, 61, 70)){
            this.planetColor = iceGiantColor;
            this.planetTexture = iceGiantTexture;
            this.planetClass = 5;
            System.out.println("Added a Ice Giant");
            this.GenerateMoons(this.planetClass);
            this.radius = 50;
        }
        else if (isBetween(random, 71, 85)) {
            this.planetTexture = asteroidTexture;
            this.planetColor = asteroidColor;
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
        int amountOfWaves = MathUtils.random(4,7);
        for (int wavesOrder = 0; wavesOrder < amountOfWaves; wavesOrder++){
            ArrayList<Integer> wave = new ArrayList<Integer>();
            int amountOfTypesOfEnemies = Math.round(MathUtils.random(1,2*(difficulty/3f*Math.max(MathUtils.random(-5,2),1))));
            for (int enemyType = 1; enemyType <= amountOfTypesOfEnemies+1; enemyType++){
                int randomAmount = MathUtils.random(4,12);
                wave.add(12);
                wave.add(enemyType);
                waves.add(wave);
            }
        }
    }

    public void generateEnemies(int wave){
        int count = 0;
        int lineCount = 0;
        for (int i = 0; i < waves.get(wave-1).get(0); i++){
            count++;
            int padding = 180;

            if (0<count && count <=6) {
                padding = ((1920 - (count - (lineCount * 6)) * 140 - (count - (lineCount * 6 + 1)) * 80) / 10);
            }
            if (count%6==0){
                lineCount++;
                count-=6;
            }




            enemyWaves.add(new Enemy(waves.get(wave-1).get(1), i%6*220+padding ,lineCount*220+900, i%6*220+padding -50, i%6*220+padding +50));
            System.out.println(i%6);
        }

    }
}
