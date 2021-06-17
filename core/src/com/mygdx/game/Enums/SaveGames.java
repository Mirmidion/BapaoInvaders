package com.mygdx.game.Enums;

import com.mygdx.game.Entities.Planet;
import com.mygdx.game.Entities.SolarSystem;
import com.mygdx.game.SaveSystem.SerializeManager;

import java.util.ArrayList;
import java.util.LinkedList;

public enum SaveGames{
    saveGame1(1),
    saveGame2(2),
    saveGame3(3);

    private int score;
    private SolarSystem solarSystem;

    SaveGames(int saveGame){

        SerializeManager serializeManager = new SerializeManager();
        solarSystem = new SolarSystem();

        try {
            if (saveGame == 1) {
                solarSystem.setPlanets((ArrayList<Planet>) serializeManager.load("SaveGame1_", "Planets"));
                solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.load("SaveGame1_", "PlanetDifficulty"));
                setScore((Integer) serializeManager.load("SaveGame1_", "Score"));
                solarSystem.setPlayed((Boolean) serializeManager.load("SaveGame1_", "Played"));
                solarSystem.resetList();
                solarSystem.setFresh(false);
                for (Planet planet : solarSystem.getPlanets()){
                    planet.setCurrentPlanetTexture();
                    for (Planet moon : planet.getMoonList()){
                        moon.setCurrentPlanetTexture();
                    }
                }
            } else if (saveGame == 2) {
                solarSystem.setPlanets((ArrayList<Planet>) serializeManager.load("SaveGame2_", "Planets"));
                solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.load("SaveGame2_", "PlanetDifficulty"));
                setScore((Integer) serializeManager.load("SaveGame2_", "Score"));
                solarSystem.setPlayed((Boolean) serializeManager.load("SaveGame2_", "Played"));
                solarSystem.resetList();
                solarSystem.setFresh(false);
                for (Planet planet : solarSystem.getPlanets()){
                    planet.setCurrentPlanetTexture();
                    for (Planet moon : planet.getMoonList()){
                        moon.setCurrentPlanetTexture();
                    }
                }
            } else if (saveGame == 3) {
                solarSystem.setPlanets((ArrayList<Planet>) serializeManager.load("SaveGame3_", "Planets"));
                solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.load("SaveGame3_", "PlanetDifficulty"));
                setScore((Integer) serializeManager.load("SaveGame3_", "Score"));
                solarSystem.setPlayed((Boolean) serializeManager.load("SaveGame3_", "Played"));
                solarSystem.resetList();
                solarSystem.setFresh(false);
                for (Planet planet : solarSystem.getPlanets()){
                    planet.setCurrentPlanetTexture();
                    for (Planet moon : planet.getMoonList()){
                        moon.setCurrentPlanetTexture();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            solarSystem = new SolarSystem(1920, 1080);
        }
    }



    public int getScore() {
        return this.score;
    }

    public SolarSystem getSolarSystem() {
        return solarSystem;
    }

    public void setScore(int score) {
        this.score += score;

    }

    public void setSolarSystem(SolarSystem solarSystem) {
        this.solarSystem = solarSystem;
    }

}
