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
import java.util.Iterator;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {

	//Scene control
	enum scene  {mainMenu, map, level}
	scene currentScene =  scene.mainMenu;
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
				currentScene = scene.map;
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

		player = new Player(width);

		solarSystem = new SolarSystem(width, height, gasGiantTexture, iceGiantTexture);
	}

	@Override
	public void render () {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (currentScene == scene.mainMenu) {
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
		// If on the map, draw the solar system
		else if (currentScene == scene.map) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			shapeRenderer.setAutoShapeType(true);

			// Background being drawn
			batch.begin();
			batch.draw(gameBackground, 0, 0, width, height);
			batch.end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));

			// Orbits around the sun being drawn, limited to the amount of planets present
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

			// Star texture being drawn
			batch.begin();
			batch.draw(starTexture, solarSystem.posXStar-starTexture.getWidth()/2f/mapScale, (solarSystem.posYStar-starTexture.getHeight()/2f/mapScale+9), starTexture.getWidth()/mapScale, starTexture.getHeight()/mapScale);
			batch.end();
			shapeRenderer.begin();

			// Loop for going through every planet in the system
			for (Planet planet : solarSystem.planets){

				// Calculating the planets position relative to the scale and star position
				float planetPositionY = solarSystem.posYStar + planet.posY / mapScale;
				float planetPositionX = solarSystem.posXStar + planet.posX / mapScale;

				// If there is no texture, draw an ellipse
				if (planet.planetTexture == null ) {

					// If there are moons around the planet, do a for-loop
					if (planet.moonList.size() != 0){

						// Calculating the moon orbit in a way it doesnt interfere with the planets size
						int moonOrbit = planet.radius+25;

						// Loop for going through every moon
						for (Planet moon : planet.moonList){
							moon.orbit = moonOrbit/2;
							shapeRenderer.set(ShapeRenderer.ShapeType.Line);

							// Draw the orbit with a grey-ish colour
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
							shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);

							// Calculate the position of the moon on the orbit
							moon.setMoonOrbit(moon.orbit);

							//Draw the moon with its own colour
							shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) moon.planetColor.getRed() / 255), ((float) moon.planetColor.getGreen() / 255), ((float) moon.planetColor.getBlue() / 255), ((float) moon.planetColor.getAlpha() / 255)));
							shapeRenderer.ellipse((moon.posX + planetPositionX - 6), (moon.posY +planetPositionY -6), moon.radius ,moon.radius );

							// If the moon is the next level you are going to play, outline this moon
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

					// Calculate the planets position in orbit
					planet.orbit();

					// Draw the planet with its own colour
					shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) planet.planetColor.getRed() / 255), ((float) planet.planetColor.getGreen() / 255), ((float) planet.planetColor.getBlue() / 255), ((float) planet.planetColor.getAlpha() / 255)));
					shapeRenderer.circle(planetPositionX, planetPositionY, planet.radius/2f);

					// If the planet is the next level, outline the planet
					if (planet.difficulty == level){
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((planetPositionX - planet.radius/2f), (planetPositionY - planet.radius/2f) , planet.radius, planet.radius);
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					}
				}

				// Else just draw the texture
				else{
					// If there are moons around the planet, do a for-loop
					if (planet.moonList.size() != 0){

						// Calculating the moon orbit in a way it doesnt interfere with the planets size
						int moonOrbit = planet.radius+25;

						// Loop for going through every moon
						for (Planet moon : planet.moonList){
							moon.orbit = moonOrbit/2;
							shapeRenderer.set(ShapeRenderer.ShapeType.Line);

							// Draw the orbit with a grey-ish colour
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(0.8f,0.8f,0.8f,1));
							shapeRenderer.ellipse(planetPositionX - moonOrbit/2f,(planetPositionY - moonOrbit/2f), moonOrbit, moonOrbit);

							// Calculate the position of the moon on the orbit
							moon.setMoonOrbit(moon.orbit);

							//Draw the moon with its own colour
							shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
							shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(((float) moon.planetColor.getRed() / 255), ((float) moon.planetColor.getGreen() / 255), ((float) moon.planetColor.getBlue() / 255), ((float) moon.planetColor.getAlpha() / 255)));
							shapeRenderer.ellipse((moon.posX + planetPositionX - 6), (moon.posY +planetPositionY -6), moon.radius ,moon.radius );

							// If the moon is the next level you are going to play, outline this moon
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

					// Calculate the planets position in the orbit
					planet.orbit();
					batch.begin();
					batch.draw(planet.planetTexture, (planetPositionX - planet.planetTexture.getWidth()/2f) ,solarSystem.posYStar*0.017f + planet.posY*0.017f + (planetPositionY - planet.planetTexture.getHeight()/2f ));
					batch.end();

					// If the planet is the next level, outline the planet
					if (planet.difficulty == level){
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((solarSystem.posXStar + planet.posX - planet.radius/2f), (solarSystem.posYStar + planet.posY - planet.radius/2f) , planet.radius, planet.radius);
						shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
					}
				}
			}
			shapeRenderer.end();

			// If ENTER is pressed, start the next level
			if (Gdx.input.isKeyPressed(Input.Keys.ENTER)){
				currentScene = scene.level;
			}

			// If the left or right arrow is pressed, zoom in/out
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

		// If in a level, draw everything of that level
		else if (currentScene == scene.level){
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();

			// Move the background down
			backgroundPosY -= (!paused)? 2:0;
			if (backgroundPosY % height == 0){
				backgroundPosY = 0;
			}

			// Draw a background texture on the posY and posX, and one above that
			batch.draw(gameBackground, 0, backgroundPosY + height, width, height);
			batch.draw(gameBackground, 0, backgroundPosY, width, height);

			// Draw the playe sprite with the correct position
			batch.draw(player.getPlayerSprite(), player.getPosX(),player.getPosY());

			// Draw every bullet and move them up
			for (Bullet bullet : player.allBullets){
				batch.draw(bullet.laser, bullet.getPosX(), bullet.getPosY());
				if (!paused) {
					bullet.setPosY(5, player, height);
				}
			}
			batch.end();

			// Change the position of the player depending on the keys pressed
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !paused){
				player.setPosX(-2, width);
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !paused){
				player.setPosX(2, width);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !paused){
				player.shoot();
			}
		}

		// Enable using the ESCAPE KEY to pause the scene. Supported scenes for pausing are: level
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && TimeUtils.millis() - pauseDelay > 500){
			if (!paused){
				paused = true;
			}
			else {
				paused = false;
			}
			pauseDelay = TimeUtils.millis();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

}

