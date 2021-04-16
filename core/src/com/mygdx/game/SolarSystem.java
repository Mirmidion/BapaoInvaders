package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.awt.*;
import java.util.ArrayList;

public class SolarSystem{

    // Properties of the star
    int posXStar = 0;
    int posYStar = 0;
    int radiusInPixels = 100;
    float yScaleOfOrbits = 0.9f;

    Color starColor = new Color(255, 224, 67);

    // Planets inside the Solar System
    ArrayList<Planet> planets = new ArrayList<Planet>();

    // Amount of orbits around the star
    int[] orbitRings = {200, 275, 375, 500, 575, 650};


    public SolarSystem(int width, int height, Texture gasGiantTexture, Texture iceGiantTexture, Texture asteroidTexture){
        posYStar = height/2;
        posXStar = width/2;
        int randomAmountOfPlanets = MathUtils.random(4,6);
        int randomOrbit = 0;
        for (int i = randomAmountOfPlanets; i > 0; i--){
            this.planets.add(new Planet(this.orbitRings[randomOrbit], gasGiantTexture, iceGiantTexture, asteroidTexture));
            randomOrbit++;
        }
        System.out.println(randomAmountOfPlanets);
    }

}
