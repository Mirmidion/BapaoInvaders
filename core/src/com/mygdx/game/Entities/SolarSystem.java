package com.mygdx.game.Entities;

import com.badlogic.gdx.math.MathUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class SolarSystem implements Serializable {

    //Properties of the star
    private int posXStar = 960;
    private int posYStar = 540;

    //Is this a new savegame?
    private boolean fresh = true;

    //Difficulty of hardest planet in the solar system
    private int globalDifficulty = 0;

    //The current level
    private boolean isPlayed = false;

    //Planets ranked in order of difficulty
    private LinkedList<Planet> planetListOfDifficulty = new LinkedList<>();

    // Planets inside the Solar System
    private ArrayList<Planet> planets = new ArrayList<>();

    // Amount of orbits around the star
    private final int[] orbitRings = {200, 275, 375, 500, 575, 650};

    public SolarSystem(int width, int height){
        posYStar = height/2;
        posXStar = width/2;
        int randomAmountOfPlanets = MathUtils.random(4,6);
        int randomOrbit = 0;
        for (int i = randomAmountOfPlanets; i > 0; i--){
            Planet planet = new Planet(this.orbitRings[randomOrbit],this);
            this.planets.add(planet);
            randomOrbit++;
        }
        //System.out.println(planetListOfDifficulty);
    }

    public void resetList(){
        LinkedList<Planet> temp = new LinkedList<>();
        for (Planet planet : planetListOfDifficulty){
            temp.offer(planet);
            System.out.println(temp.indexOf(planet));
        }
        planetListOfDifficulty = new LinkedList<>();
        planetListOfDifficulty = temp;
    }

    public SolarSystem(){
        planetListOfDifficulty = new LinkedList<>();
    }

    public int getPosXStar() {
        return posXStar;
    }

    public int getPosYStar() {
        return posYStar;
    }

    public int[] getOrbitRings() {
        return orbitRings;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(ArrayList<Planet> planets) {
        this.planets = planets;
    }

    public LinkedList<Planet> getPlanetListOfDifficulty() {
        return planetListOfDifficulty;
    }

    public void setPlanetListOfDifficulty(LinkedList<Planet> planetListOfDifficulty) {
        this.planetListOfDifficulty = planetListOfDifficulty;
    }

    public void setFresh(boolean fresh) {
        this.fresh = fresh;
    }

    public boolean isFresh() {
        return fresh;
    }

    public void setGlobalDifficulty(int globalDifficulty) {
        this.globalDifficulty = globalDifficulty;
    }

    public int getGlobalDifficulty() {
        return globalDifficulty;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
