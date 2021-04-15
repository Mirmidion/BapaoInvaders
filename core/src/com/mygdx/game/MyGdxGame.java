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
import com.badlogic.gdx.utils.TimeUtils;
import org.w3c.dom.Text;

import javax.swing.plaf.synth.Region;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	//Scene control
	boolean mainMenu = true;
	boolean game = false;
	boolean inLevel = false;

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
	Texture gameBackground;
	int backgroundPosY;

	//Player
	Player player;

	//Solar Systems
	SolarSystem solarSystem;
	int globalDifficulty = 0;
	int level = 0;
	Texture starTexture;
	Texture iceGiantTexture;
	Texture gasGiantTexture;
	float mapScale = 1;

	boolean paused = false;
	String previousScene = "";

	long pauseDelay = 0;


	@Override
	public void create () {
		//Inititializing SpriteBatches
		batch = new SpriteBatch();
		stage = new Stage();
		shapeRenderer = new ShapeRenderer();

		//Initializing Textures
		background = new Texture("background.png");
		gameBackground = new Texture("gameBackground.png");
		starTexture = new Texture(Gdx.files.internal("sun.png"), true);
		iceGiantTexture = new Texture(Gdx.files.internal("IceGiant.png"), true);
		gasGiantTexture = new Texture(Gdx.files.internal("GasGiant.png"), true);

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

		player = new Player();

		solarSystem = new SolarSystem();
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
			shapeRenderer.setAutoShapeType(true);
			batch.begin();
			batch.draw(gameBackground, 0, 0, width, height);
			batch.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
			int localOrbitCounter = 0;
			for (int i : solarSystem.orbitRings){
				localOrbitCounter++;
				if (localOrbitCounter > solarSystem.planets.size()){
					break;
				}
				shapeRenderer.ellipse(solarSystem.posXStar - i/mapScale, solarSystem.posYStar -i/mapScale, i*2/mapScale, i*2/mapScale);
			}
			shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.end();
			batch.begin();
			batch.draw(starTexture, solarSystem.posXStar-starTexture.getWidth()/2f/mapScale, (solarSystem.posYStar-starTexture.getHeight()/2f/mapScale), starTexture.getWidth()/mapScale, starTexture.getHeight()/mapScale);
			batch.end();
			shapeRenderer.begin();
			for (Planet planet : solarSystem.planets){
				float planetPositionY = solarSystem.posYStar + planet.posY / mapScale;
				float planetPositionX = solarSystem.posXStar + planet.posX / mapScale;
				if (planet.planetTexture == null || true) {
					if (planet.moonList.size() != 0){
						int moonOrbit = planet.radius+25;
						for (Planet moon : planet.moonList){
							moon.orbit = moonOrbit/2;
							shapeRenderer.set(ShapeRenderer.ShapeType.Line);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
							shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);
							shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) moon.planetColor.getRed() / 255), ((float) moon.planetColor.getGreen() / 255), ((float) moon.planetColor.getBlue() / 255), ((float) moon.planetColor.getAlpha() / 255)));
							moon.setMoonOrbit(moon.orbit);
							shapeRenderer.ellipse((moon.posX + planetPositionX - 6), (moon.posY +planetPositionY -6), moon.radius ,moon.radius );
							if (moon.difficulty == level){
								shapeRenderer.set(ShapeRenderer.ShapeType.Line);
								shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
								shapeRenderer.ellipse((planetPositionX - planet.radius/2f), (planetPositionY - planet.radius/2f) , planet.radius, planet.radius);
								shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							}
							moonOrbit += 25;
						}
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(1f,0.95686f,0.2627f,1));
					}
					shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) planet.planetColor.getRed() / 255), ((float) planet.planetColor.getGreen() / 255), ((float) planet.planetColor.getBlue() / 255), ((float) planet.planetColor.getAlpha() / 255)));
					planet.orbit();
//					if (planet.planetClass == 4 || planet.planetClass == 5){
//						shapeRenderer.end();
//						batch.begin();
//						batch.draw(planet.planetTexture, (planetPositionX - planet.planetTexture.getWidth()/2f) ,(planetPositionY - planet.planetTexture.getHeight()/2f ));
//
//						batch.end();
//						shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//					}

					shapeRenderer.circle(planetPositionX, planetPositionY, planet.radius/2f);
					if (planet.difficulty == level){
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((planetPositionX - planet.radius/2f), (planetPositionY - planet.radius/2f) , planet.radius, planet.radius);
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					}

				}
				else{
					if (planet.moonList.size() != 0){
						int moonOrbit = planet.planetTexture.getWidth()+25;
						for (Planet moon : planet.moonList){
							moon.orbit = moonOrbit/2;
							shapeRenderer.set(ShapeRenderer.ShapeType.Line);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
							shapeRenderer.ellipse(solarSystem.posXStar + planet.posX - moonOrbit/2f,solarSystem.posYStar + planet.posY - moonOrbit/2f, moonOrbit, moonOrbit);
							shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) moon.planetColor.getRed() / 255), ((float) moon.planetColor.getGreen() / 255), ((float) moon.planetColor.getBlue() / 255), ((float) moon.planetColor.getAlpha() / 255)));
							moon.setMoonOrbit(moon.orbit);
							shapeRenderer.ellipse((moon.posX + planet.posX + solarSystem.posXStar), (moon.posY +planet.posY + solarSystem.posYStar), moon.radius ,moon.radius );
							System.out.println(Math.sqrt((moon.posX * moon.posX)+(moon.posY*moon.posY)));
							if (moon.difficulty == level){
								shapeRenderer.set(ShapeRenderer.ShapeType.Line);
								shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
								shapeRenderer.ellipse((solarSystem.posXStar + planet.posX - planet.planetTexture.getWidth()/2f), (solarSystem.posYStar + 3 + planet.posY - planet.planetTexture.getWidth()/2f) , planet.radius, planet.radius);
								shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							}
							moonOrbit += 25;
						}
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(1f,0.95686f,0.2627f,1));
					}
					planet.orbit();
					batch.begin();
					batch.draw(planet.planetTexture, solarSystem.posXStar + planet.posX - planet.planetTexture.getWidth()/2f ,solarSystem.posYStar + planet.posY - planet.planetTexture.getHeight()/2f);

					batch.end();
					if (planet.difficulty == level){
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((solarSystem.posXStar + planet.posX - planet.radius/2f), (solarSystem.posYStar + planet.posY - planet.radius/2f) , planet.radius, planet.radius);
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					}
				}

			}
			shapeRenderer.end();


			if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
				inLevel = true;
				game = false;
				mainMenu = false;

			}

			if (Gdx.input.isKeyPressed(Input.Keys.UP)){
				if (mapScale == 2 || mapScale + 0.01f >= 2){
					mapScale = 2;
				}
				else{
					mapScale += 0.01f;
				}
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
				if (mapScale == 1 || mapScale + 0.01f <= 1){
					mapScale = 1;
				}
				else{
					mapScale -= 0.01f;
				}
			}


		}
		else if (inLevel){
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			backgroundPosY-= 2;
			if (backgroundPosY % height == 0){
				backgroundPosY = 0;
			}
			batch.draw(gameBackground, 0, backgroundPosY + height, width, height);
			batch.draw(gameBackground, 0, backgroundPosY, width, height);

			batch.draw(player.playerSprite, player.posX,player.posY);
			for (Bullet bullet : player.allBullets){
				batch.draw(bullet.laser, bullet.posX, bullet.posY);
				bullet.setPosY(5);
			}
			batch.end();

			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				player.setPosX(-2);
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				player.setPosX(2);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
				player.shoot();
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && TimeUtils.millis() - pauseDelay > 500){
			if (!paused){
				previousScene = (inLevel)? "inLevel" : (mainMenu)? "mainMenu" : (game)? "map": "";
				inLevel = mainMenu = game = false;
				paused = true;
				pauseDelay = TimeUtils.millis();
			}
			else {
				inLevel = previousScene.equals("inLevel");
				mainMenu = previousScene.equals("mainMenu");
				game = previousScene.equals("map");
				paused = false;
				pauseDelay = TimeUtils.millis();
			}

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
		float yScaleOfOrbits = 0.9f;

		Color starColor = new Color(255, 224, 67);

		// Planets inside the Solar System
		ArrayList<Planet> planets = new ArrayList<Planet>();

		// Amount of orbits around the star
		int[] orbitRings = {200, 275, 375, 500, 575, 650};


		public SolarSystem(){
			int randomAmountOfPlanets = MathUtils.random(4,6);
			int randomOrbit = 0;
			for (int i = randomAmountOfPlanets; i > 0; i--){
				this.planets.add(new Planet(this.orbitRings[randomOrbit]));
				randomOrbit++;
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
		private float angle;
		int orbit = 100;
		private boolean orbitClockWise = true;
		private float rotationSpeed = 1;


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
				this.planetColor = asteroidColor;
				this.planetClass = 1;
				System.out.println("Added an asteroid");
				this.radius = 10;
			}
			angle = (float) ((Math.random()*(360)+0)/180*Math.PI);
			posX = (float)Math.cos(angle)*orbit;
			posY = (float)Math.sin(angle)*orbit;
			this.orbit = orbit;
			this.rotationSpeed = MathUtils.random(0.5f,2);
			this.difficulty = globalDifficulty;
			globalDifficulty++;
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
			if (this.angle + angle > 2*Math.PI){
				this.angle = 0;
			}
			else if (this.angle + angle < 0){
				this.angle = 1;
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
	}

	public static boolean isBetween(int x, int lower, int upper) {
		return lower <= x && x <= upper;
	}

	class Player {
		private int posX = width/2;
		private int posY = 100;
		private Texture playerSprite = new Texture(Gdx.files.internal("Playership.png"));
		private int gun = 1;
		private long time = 0;
		ArrayList<Bullet> allBullets = new ArrayList<Bullet>();

		public Player (){
			this.posX = width/2-playerSprite.getWidth()/2;
		}

		public void setPosX(int x){
			if (posX + x > width - playerSprite.getWidth()){
				posX = width - playerSprite.getWidth();
			}
			else if (posX + x < 0){
				posX = 0;
			}
			else {
				posX += x;
			}
		}

		public void shoot(){
			if (TimeUtils.millis() - time > 500){
				allBullets.add(new Bullet(playerSprite.getWidth()-((gun == 1)?40:104),playerSprite.getHeight()-32));
				time = TimeUtils.millis();
				gun *= -1;
			}

		}

		public void destroyBullet(Bullet bulletToRemove){
			//allBullets.remove(bulletToRemove);
		}


	}

	class Bullet{
		private int posX;
		private int posY;
		Texture laser = new Texture(Gdx.files.internal("laser.png"));


		public Bullet (int x, int y){
			this.posX = x + player.posX;
			this.posY = y + player.posY;
		}



		public void setPosX(int posX) {
			if (this.posX+posX > width || this.posX+posX < 0){
				player.destroyBullet(this);
			}
			else{
				this.posX += posX;
			}
		}

		public void setPosY(int posY) {
			if (this.posY+posY > height || this.posY+posY < 0){
				player.destroyBullet(this);
			}
			else{
				this.posY += posY;
			}
		}
	}


}

