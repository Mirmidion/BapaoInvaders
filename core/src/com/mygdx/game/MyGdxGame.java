package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.w3c.dom.Text;

import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	//Scene control
	boolean mainMenu = true;
	boolean game = false;

	//Object visuals
	Skin buttonSkin;
	TextureAtlas atlas;

	//Fonts
	BitmapFont normalFont;
	BitmapFont titleFont;

	//Main Menu Layout and Objects
	Table mainMenuTable;
	TextButton start;
	TextButton settings;
	TextButton exit;
	Text title;

	//Settings Menu
	Texture settingsMenu;
	boolean settingsMenuSwitch;

	//Camera
	private OrthographicCamera camera;

	//Screen width & height
	int width = 1920;
	int height = 1080;

	//SpriteBatches
	SpriteBatch batch;
	Stage stage;

	//Drawer of shapes
	ShapeRenderer shapeRenderer;

	//Background
	Texture background;

	//Player
	Texture player;

	//Solar Systems
	SolarSystem solarSystem = new SolarSystem();
	
	@Override
	public void create () {
		//Inititializing SpriteBatches
		batch = new SpriteBatch();
		stage = new Stage();
		shapeRenderer = new ShapeRenderer();

		//Initializing Textures
		background = new Texture("background.png");

		//Initializing Fonts
		normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
		titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
		titleFont.getData().setScale(2);

		Gdx.input.setInputProcessor(stage);
		mainMenuTable = new Table();
		mainMenuTable.setPosition(250,600);
		mainMenuTable.left();

		buttonSkin = new Skin(Gdx.files.internal("Skin1.json"));

		start = new TextButton("Start", buttonSkin);
		settings = new TextButton("Settings",buttonSkin);
		settingsMenu = new Texture(Gdx.files.internal("button.png"));

		settingsMenuSwitch = false;

		exit = new TextButton("Exit",buttonSkin);

		start.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mainMenu = false;
				game = true;
			}
		});
		settings.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				settingsMenuSwitch = !settingsMenuSwitch;
			}

		});
		exit.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dispose();
			}

		});

		start.setTransform(true);
		settings.setTransform(true);
		exit.setTransform(true);




		mainMenuTable.row();
		mainMenuTable.add(start).padTop(225);
		mainMenuTable.row();
		mainMenuTable.add(settings).padTop(50);
		mainMenuTable.row();
		mainMenuTable.add(exit).padTop(50);


		stage.addActor(mainMenuTable);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);


	}

	@Override
	public void render () {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (mainMenu) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();

			batch.draw(background, 0, 0, width, height);
			titleFont.draw(batch, "Bapao Invaders", 200, 800);

			batch.end();
			stage.draw();

			if (settingsMenuSwitch){
				batch.begin();
				batch.draw(settingsMenu, 200,200, 1520, 680);
				batch.end();
				if (Gdx.input.isButtonPressed(Input.Keys.SPACE)){
					settingsMenuSwitch = false;
				}
			}

		}
		else if (game) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.begin();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
			for (int i : solarSystem.orbitRings){
				shapeRenderer.circle(solarSystem.posXStar, solarSystem.posYStar, i);
			}
			shapeRenderer.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(1f,0.95686f,0.2627f,1));
			shapeRenderer.circle(solarSystem.posXStar, solarSystem.posYStar, solarSystem.radiusInPixels );
			for (Planet planet : solarSystem.planets){
				if (planet.planetTexture == null) {
					shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) planet.planetColor.getRed() / 255), ((float) planet.planetColor.getGreen() / 255), ((float) planet.planetColor.getBlue() / 255), ((float) planet.planetColor.getAlpha() / 255)));

					System.out.println((solarSystem.posXStar + planet.posX) + (solarSystem.posYStar + planet.posY) + "");
					shapeRenderer.circle((solarSystem.posXStar + planet.posX), (solarSystem.posYStar + planet.posY), planet.radius);
				}
				else{

				}

			}
			shapeRenderer.end();
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	class SolarSystem{

		// Properties of the star
		int posXStar = width/2;
		int posYStar = height/2;
		int radiusInPixels = 100;

		Color starColor = new Color(255, 224, 67);

		// Planets inside the Solar System
		ArrayList<Planet> planets = new ArrayList<Planet>();

		// Amount of orbits around the star
		int[] orbitRings = {200, 275, 375, 500};

		public SolarSystem(){
			int randomAmountOfPlanets = MathUtils.random(4,8);
			for (int i = randomAmountOfPlanets; i > 0; i--){
				int randomOrbit = MathUtils.random(0,3);
				this.planets.add(new Planet(this.orbitRings[randomOrbit]));
			}
			System.out.println(randomAmountOfPlanets);
		}

	}

	class Planet {

		// Difficulty of the waves
		int difficulty;

		// Waves consist of int[] that contain amount and types of enemies
		int[][] waves = {
				{	},
				{	},
				{	}
		};

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

		public Planet(int orbit){
			int random = MathUtils.random(85);
			if (isBetween(random, 0, 50)){
				int randomColor = MathUtils.random(0,5);
				this.planetColor = possiblePlanetColors[randomColor];
				this.planetClass = 3;
				System.out.println("Added a Planet");
				this.GenerateMoons(this.planetClass);
				this.radius = 30;
			}
			else if (isBetween(random, 51, 60)){
				this.planetColor = gasGiantColor;
				this.planetClass = 4;
				System.out.println("Added a Gas Giant");
				this.GenerateMoons(this.planetClass);
				this.radius = 50;
			}
			else if (isBetween(random, 61, 70)){
				this.planetColor = iceGiantColor;
				this.planetClass = 5;
				System.out.println("Added a Ice Giant");
				this.GenerateMoons(this.planetClass);
				this.radius = 50;
			}
			else if (isBetween(random, 71, 85)) {
				this.planetColor = asteroidColor;
				this.planetClass = 1;
				System.out.println("Added an asteroid");
				this.radius = 10;
			}
			float angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
			posX = (float)Math.cos(angle)*orbit;
			posY = (float)Math.sin(angle)*orbit;
		}

		public Planet(boolean isMoon, Planet orbitPlanet){
			this.planetColor = moonColor;
			this.planetClass = 2;
			this.radius = 15;
			orbitPlanet.addMoon(this);
		}

		public void GenerateMoons(int planetType){
			int amountOfMoons = MathUtils.random(-3,3);
			amountOfMoons = Math.max(amountOfMoons, 0);
			for (int i = amountOfMoons; i > 0; i--){
				if (50 > ((i <= amountOfMoons && planetType == 3)? MathUtils.random(0,100):100) || (10 > ((planetType == 4 || planetType == 5)? MathUtils.random(0,100):100))){
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
	}

	public static boolean isBetween(int x, int lower, int upper) {
		return lower <= x && x <= upper;
	}

}

