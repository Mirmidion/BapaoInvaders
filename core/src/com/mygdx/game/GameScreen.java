package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.Entities.*;
import com.mygdx.game.Scenes.Level;
import com.mygdx.game.Scenes.MainMenu;
import com.mygdx.game.Scenes.Map;
import com.mygdx.game.Scenes.loadingScreen;

public class GameScreen implements Screen {

	//Scene control
	public enum scene  {mainMenu, map, level, loadingScreen}
	private scene currentScene =  scene.mainMenu;
	private int level = 0;


	//Fonts
	private BitmapFont normalFont;
	private BitmapFont titleFont;



	private Music music;
	private Music music2;
	private Music music3;

	//Settings Menu
	private Texture settingsMenu;
	private boolean settingsMenuSwitch;

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


	//Player


	//Solar Systems
	private SolarSystem solarSystem;





	private static boolean paused = false;

	private long pauseDelay = 0;

	private Planet currentPlanet;

	private static int score;

	MainMenu mainMenuScene;
	Level levelScene;
	Map mapScene;
	loadingScreen loadingScreenScene;

	public GameScreen () {

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

		solarSystem = new SolarSystem(width, height);

		music.setLooping(true);
		music2.setLooping(true);
		music3.setLooping(true);

		mainMenuScene = new MainMenu(this);
		levelScene = new Level(this);
		mapScene = new Map(this);
		loadingScreenScene = new loadingScreen(this);
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

		else if (currentScene == scene.loadingScreen){
			loadingScreenScene.render(delta);
		}
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

	public SolarSystem getSolarSystem() {
		return solarSystem;
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

	public void setSolarSystem(SolarSystem solarSystem) {
		this.solarSystem = solarSystem;
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

	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		GameScreen.score += score;
	}

	public void addLevel(){
		this.level++;
	}

	public scene getCurrentScene() {
		return currentScene;
	}
}

