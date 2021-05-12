package com.mygdx.game.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.Entities.Planet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class SolarSystem implements Serializable {

    // Properties of the star
    int posXStar = 960;
    int posYStar = 540;
    int radiusInPixels = 100;
    float yScaleOfOrbits = 0.9f;
    boolean fresh = true;
    int globalDifficulty = 0;
    int currentlevel = 1;
    boolean isPlayed = false;
    LinkedList<Planet> planetListOfDifficulty = new LinkedList<Planet>(); //TODO -- to save

    // Planets inside the Solar System
    ArrayList<Planet> planets = new ArrayList<Planet>();

    // Amount of orbits around the star
    int[] orbitRings = {200, 275, 375, 500, 575, 650};


    public SolarSystem(int width, int height){
        posYStar = height/2;
        posXStar = width/2;
        int randomAmountOfPlanets = MathUtils.random(4,6);
        int randomOrbit = 0;
        for (int i = randomAmountOfPlanets; i > 0; i--){
            Planet planet = new Planet(this.orbitRings[randomOrbit],this);
            this.planets.add(planet);
            planetListOfDifficulty.offer(planet);
            randomOrbit++;
        }
        currentlevel = 1;
        //System.out.println(planetListOfDifficulty);
    }

    public SolarSystem(){
        planetListOfDifficulty = new LinkedList<>();
    }

    public int getPosXStar() {
        return posXStar;
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    public int getPosYStar() {
        return posYStar;
    }

    public float getyScaleOfOrbits() {
        return yScaleOfOrbits;
    }

    public int getRadiusInPixels() {
        return radiusInPixels;
    }

    public int[] getOrbitRings() {
        return orbitRings;
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

    public void resetList(){
        LinkedList<Planet> temp = new LinkedList<>();
        for (Planet planet : planetListOfDifficulty){
            temp.offer(planet);
            System.out.println(temp.indexOf(planet));
        }
        planetListOfDifficulty = new LinkedList<>();
        planetListOfDifficulty = temp;
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

    public void setCurrentlevel(int currentlevel) {
        this.currentlevel += currentlevel;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
