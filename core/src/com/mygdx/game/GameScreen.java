package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Entities.*;
import com.mygdx.game.SaveSystem.SerializeManager;
import com.mygdx.game.Scenes.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class GameScreen implements Screen {

	//----- These are all the variables used in more than 1 scene ----//

	//Scene control
	public enum scene  {mainMenu, map, level, gameOver, win}
	private scene currentScene =  scene.win;
	private int level = 0; //TODO -- to save


	//Fonts
	private BitmapFont normalFont;
	private BitmapFont titleFont;

	// Textures
	static Texture asteroidTexture = new Texture("Asteroid.png");
	static Texture gasGiantTexture = new Texture("GasGiant.png");
	static Texture iceGiantTexture = new Texture("IceGiant.png");
	static Texture moonTexture = new Texture("Moon.png");

	//Music
	private Music music;
	private Music music2;
	private Music music3;

	//Settings Menu
	private Texture settingsMenu;
	private boolean settingsMenuSwitch;
	private boolean saveGameMenuSwitch;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;

	//Screen width & height
	private int width = 1920;
	private int height = 1080;

	//SpriteBatches
	private SpriteBatch batch;

	//Background
	private Texture gameBackground;

	//Solar Systems

	private Planet currentPlanet; //TODO -- to save

	//Paused variables
	private static boolean paused = false;
	private long pauseDelay = 0;



	SerializeManager serializeManager = new SerializeManager();
	enum saveGames{
		saveGame1(1),
		saveGame2(2),
		saveGame3(3);
		int score;
		SolarSystem solarSystem;

		saveGames(int saveGame){

			SerializeManager serializeManager = new SerializeManager();
			solarSystem = new SolarSystem();
			try {
				if (saveGame == 1) {
					solarSystem.setPlanets((ArrayList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame1_", "Planets"));
					solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame1_", "PlanetDifficulty"));
					setScore((Integer) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame1_", "Score"));
					solarSystem.setPlayed((Boolean) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame1_", "Played"));
					solarSystem.resetList();
					solarSystem.setFresh(false);
					for (Planet planet : solarSystem.getPlanets()){
						planet.setCurrentPlanetTexture();
						for (Planet moon : planet.getMoonList()){
							moon.setCurrentPlanetTexture();
						}
					}
				} else if (saveGame == 2) {
					solarSystem.setPlanets((ArrayList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame2_", "Planets"));
					solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame2_", "PlanetDifficulty"));
					setScore((Integer) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame2_", "Score"));
					solarSystem.setPlayed((Boolean) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame2_", "Played"));
					solarSystem.resetList();
					solarSystem.setFresh(false);
					for (Planet planet : solarSystem.getPlanets()){
						planet.setCurrentPlanetTexture();
						for (Planet moon : planet.getMoonList()){
							moon.setCurrentPlanetTexture();
						}
					}
				} else if (saveGame == 3) {
					solarSystem.setPlanets((ArrayList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame3_", "Planets"));
					solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame3_", "PlanetDifficulty"));
					setScore((Integer) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame3_", "Score"));
					solarSystem.setPlayed((Boolean) serializeManager.readByteStreamFromFileAndDeSerializeToObject("SaveGame3_", "Played"));
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
				System.out.println(e);
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
	private saveGames saveGame1 = saveGames.saveGame1;
	private saveGames saveGame2 = saveGames.saveGame2;
	private saveGames saveGame3 = saveGames.saveGame3;
	private saveGames currentSaveGame;

	//Different scenes used throughout the game
	MainMenu mainMenuScene;
	Level levelScene;
	Map mapScene;
	GameOverMenu gameOverScene;
	WinMenu winScene;

	//FPS counter
	int framesPerSecond;
	long lastChecked;

	public GameScreen (){


		settingsMenu = new Texture(Gdx.files.internal("button.png"));
		//Inititializing SpriteBatches
		batch = new SpriteBatch();

		//Initializing Textures
		gameBackground = new Texture("gameBackground.png");

		music = Gdx.audio.newMusic(Gdx.files.internal("Theme.mp3"));
		music2 = Gdx.audio.newMusic(Gdx.files.internal("Theme2.mp3"));
		music3 = Gdx.audio.newMusic(Gdx.files.internal("Theme3.mp3"));

		//Initializing Fonts
		normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
		titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
		titleFont.getData().setScale(2);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);

		viewport = new StretchViewport(1920, 1080, camera);


//		try {
//			solarSystem.setPlanets((ArrayList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("ByteStream_ArrayList"));
//			solarSystem.setPlanetListOfDifficulty((LinkedList<Planet>) serializeManager.readByteStreamFromFileAndDeSerializeToObject("ByteStream_LinkedList"));
//			solarSystem.resetList();
//			solarSystem.setFresh(false);
//		}
//		catch (Exception e) {
//			System.out.println(e);
//
//		}




		music.setLooping(true);
		music2.setLooping(true);
		music3.setLooping(true);

		mainMenuScene = new MainMenu(this);
		levelScene = new Level(this);
		mapScene = new Map(this);
		winScene = new WinMenu(this);
		gameOverScene = new GameOverMenu(this);

	}



	@Override
	public void render (float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (currentScene == scene.mainMenu) {
			mainMenuScene.render(delta);
		}

		// If on the map, draw the solar system
		else if (currentScene == scene.map) {
			mapScene.render(delta);
		}

		// If in a level, draw everything of that level
		else if (currentScene == scene.level) {
			levelScene.render(delta);
		}

		else if (currentScene == scene.gameOver) {
			gameOverScene.render(delta);
		}

		else if (currentScene == scene.win) {
			winScene.render(delta);
		}

		if (TimeUtils.millis() - lastChecked >= 1000){
			framesPerSecond = Gdx.graphics.getFramesPerSecond();
			lastChecked = TimeUtils.millis();
		}


		batch.begin();
		normalFont.getData().setScale(0.2f);
		normalFont.draw(batch,framesPerSecond + "", 10, 1070);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);

	}

	@Override
	public void show() {

	}


	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		batch.dispose();
		music.dispose();
		music2.dispose();
		music3.dispose();
	}

	public void saveSaveGame(int saveGame){
		try {
			if (saveGame == 1) {
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame1_", "Planets");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame1_", "PlanetDifficulty");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getScore(), "SaveGame1_", "Score");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame1_", "Played");
			} else if (saveGame == 2) {
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame2_", "Planets");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame2_", "PlanetDifficulty");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getScore(), "SaveGame2_", "Score");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame2_", "Played");
			} else if (saveGame == 3) {
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanets(), "SaveGame3_", "Planets");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame3_", "PlanetDifficulty");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getScore(), "SaveGame3_", "Score");
				serializeManager.serializeObjectAndSaveToFile(currentSaveGame.getSolarSystem().isPlayed(), "SaveGame3_", "Played");
			}
		}
		catch(Exception e){

		}
	}

	public SolarSystem getSolarSystem(int saveGame){
		switch (saveGame){
			case 1:{
				return saveGame1.getSolarSystem();
			}
			case 2:{
				return saveGame2.getSolarSystem();
			}
			case 3:{
				return saveGame3.getSolarSystem();
			}
		}
		return saveGame1.getSolarSystem();
	}

	public Music getMusic() {
		return music;
	}

	public Music getMusic2() {
		return music2;
	}

	public Music getMusic3() {
		return music3;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Texture getGameBackground() {
		return gameBackground;
	}

	public Texture getSettingsMenu() {
		return settingsMenu;
	}

	public boolean isSettingsMenuSwitch() {
		return settingsMenuSwitch;
	}

	public void setSettingsMenuSwitch(boolean settingsMenuSwitch) {
		this.settingsMenuSwitch = settingsMenuSwitch;
	}

	public BitmapFont getTitleFont() {
		return titleFont;
	}

	public void setCurrentScene(scene currentScene) {
		this.currentScene = currentScene;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		this.currentPlanet = currentPlanet;
	}

	public int getLevel() {
		return level;
	}

	public static boolean isPaused() {
		return paused;
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public static void setPaused(boolean paused) {
		GameScreen.paused = paused;
	}

	public long getPauseDelay() {
		return pauseDelay;
	}

	public void setPauseDelay(long pauseDelay) {
		this.pauseDelay = pauseDelay;
	}

	public void addLevel(){
		this.level++;
	}

	public scene getCurrentScene() {
		return currentScene;
	}

	public BitmapFont getNormalFont() {
		return normalFont;
	}

	public static Texture getAsteroidTexture() {
		return asteroidTexture;
	}

	public static Texture getMoonTexture() {
		return moonTexture;
	}

	public static Texture getGasGiantTexture() {
		return gasGiantTexture;
	}

	public static Texture getIceGiantTexture() {
		return iceGiantTexture;
	}

	public boolean isSaveGameMenuSwitch() {
		return saveGameMenuSwitch;
	}

	public void setSaveGameMenuSwitch(boolean saveGameMenuSwitch) {
		this.saveGameMenuSwitch = saveGameMenuSwitch;
	}

	public saveGames getCurrentSaveGame() {
		return currentSaveGame;
	}

	public SolarSystem getSolarSystem() {
		return currentSaveGame.getSolarSystem();
	}

	public int getScore(){
		return currentSaveGame.getScore();
	}

	public void setScore(int score){
		this.currentSaveGame.setScore(score);
	}

	public void setSolarSystem(SolarSystem solarSystem) {
		this.currentSaveGame.setSolarSystem(solarSystem);
	}

	public void setCurrentSaveGame(int selected) {
		if (selected == 1){
			currentSaveGame = saveGame1;
		}
		else if (selected == 2){
			currentSaveGame = saveGame2;
		}
		else if (selected == 3){
			currentSaveGame = saveGame3;
		}
		currentSaveGame.solarSystem.setPlayed(true);
		saveSaveGame(selected);
	}

	public SolarSystem getSaveGame1SolarSystem() {
		return saveGame1.getSolarSystem();
	}

	public SolarSystem getSaveGame2SolarSystem() {
		return saveGame2.getSolarSystem();
	}

	public SolarSystem getSaveGame3SolarSystem() {
		return saveGame3.getSolarSystem();
	}

	public int getSaveGame1Score() {
		return saveGame1.getScore();
	}
	public int getSaveGame2Score() {
		return saveGame2.getScore();
	}
	public int getSaveGame3Score() {
		return saveGame3.getScore();
	}

	//	public void saveSaveGame(){
//		try {
//			if (currentSaveGame == saveGame1) {
//				serializeManager.serializeObjectAndSaveToFile(saveGame1.getSolarSystem().getPlanets(), "SaveGame1_", "Planets");
//				serializeManager.serializeObjectAndSaveToFile(saveGame1.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame1_", "PlanetDifficulty");
//			} else if (currentSaveGame == saveGame2) {
//				serializeManager.serializeObjectAndSaveToFile(saveGame2.getSolarSystem().getPlanets(), "SaveGame2_", "Planets");
//				serializeManager.serializeObjectAndSaveToFile(saveGame2.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame2_", "PlanetDifficulty");
//			} else if (currentSaveGame == saveGame3) {
//				serializeManager.serializeObjectAndSaveToFile(saveGame3.getSolarSystem().getPlanets(), "SaveGame3_", "Planets");
//				serializeManager.serializeObjectAndSaveToFile(saveGame3.getSolarSystem().getPlanetListOfDifficulty(), "SaveGame3_", "PlanetDifficulty");
//			}
//		}
//		catch (Exception e){
//			System.out.println(e);
//		}
//	}




}

