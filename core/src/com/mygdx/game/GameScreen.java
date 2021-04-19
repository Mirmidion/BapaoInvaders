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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.ArrayList;

public class GameScreen implements Screen {

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

	Music music;
	Music music2;
	Music music3;

	//Settings Menu
	Texture settingsMenu;
	boolean settingsMenuSwitch;

	//Camera
	private OrthographicCamera camera;
	private Viewport viewport;

	//Screen width & height
	int width = 1920;
	int height = 1080;

	//SpriteBatches
	SpriteBatch batch;
	Stage stage;

	//Sprites
	Sprite[] bapaoSprites;
	Sprite bapaoSprite;

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
	Texture asteroidTexture;
	float mapScale = 1;

	//Other
	float[] bapaoY = new float[]{500, 14, 129, 1049, 280, 809, 102, 758, 640, 20, 70, 780, 420, 920, 320};
	float[] bapaoX = new float[]{294, 0, 498, 928, 1359, 200, 800, 500, 1060, 12, 1500, 1400, 1600, 1800, 1900};
	int[] bapaoSpeed = new int[]{300, 250, 200, 150, 100, 250, 450, 100, 230, 400, 500, 210, 500, 320, 340};

	boolean paused = false;
	String previousScene = "";

	long pauseDelay = 0;

	Planet currentPlanet;


	public GameScreen () {
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
		asteroidTexture = new Texture(Gdx.files.internal("Asteroid.png"), true);

		//Initializing Sprites
		bapaoSprite = new Sprite(new Texture("Bapao1.png"));
		bapaoSprites = new Sprite[15];
		for(int i = 0; i<bapaoSprites.length; i++)
		{
			bapaoSprites[i] = new Sprite(new Texture("Bapao1.png"));
		}

		music = Gdx.audio.newMusic(Gdx.files.internal("Theme.mp3"));
		music2 = Gdx.audio.newMusic(Gdx.files.internal("Theme2.mp3"));
		music3 = Gdx.audio.newMusic(Gdx.files.internal("Theme3.mp3"));


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

		viewport = new StretchViewport(1920, 1080, camera);

		player = new Player(width);

		solarSystem = new SolarSystem(width, height, gasGiantTexture, iceGiantTexture, asteroidTexture);

		music.setLooping(true);
		music2.setLooping(true);
		music3.setLooping(true);
	}



	@Override
	public void render (float delta) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glDisable(GL20.GL_BLEND);

		if (currentScene == scene.mainMenu) {
			music.play();
			music2.dispose();
			music3.dispose();
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.begin();
			batch.draw(gameBackground, 0, 0, width, height);
			batch.end();

			renderBapaos(delta);  //todo

			batch.begin();
			titleFont.draw(batch, "Bapao Invaders", 200, 800);
			batch.end();


			stage.draw();
			stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
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
			music.dispose();
			music2.play();
			music3.dispose();
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
						currentPlanet = planet;
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((planetPositionX*mapScale - planet.radius/2f*mapScale), (planetPositionY*mapScale - planet.radius/2f*mapScale), planet.radius, planet.radius);
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
								shapeRenderer.ellipse((planetPositionX - planet.radius/2f*mapScale) , (planetPositionY - planet.radius/2f*mapScale) , planet.radius, planet.radius);
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
					batch.draw(planet.planetTexture, (planetPositionX - planet.planetTexture.getWidth()/2f) ,solarSystem.posYStar*0.017f + planet.posY*0.018f/mapScale + (planetPositionY - planet.planetTexture.getHeight()/2f ));
					batch.end();

					// If the planet is the next level, outline the planet
					if (planet.difficulty == level){
						currentPlanet = planet;
						shapeRenderer.set(ShapeRenderer.ShapeType.Line);
						shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(255,255,255,255));
						shapeRenderer.ellipse((solarSystem.posXStar + planet.posX/mapScale - planet.radius/2f), (solarSystem.posYStar + planet.posY/mapScale - planet.radius/2f) , planet.radius, planet.radius);
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
			music.dispose();
			music2.dispose();
			music3.play();
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

			// Draw the player sprite with the correct position
			if (player.getHealth() != 0) {
				batch.draw(player.getPlayerSprite(), player.getPosX(), player.getPosY());
			}
			else{
				solarSystem = new SolarSystem(width, height, gasGiantTexture, iceGiantTexture, asteroidTexture);
				currentScene = scene.mainMenu;
				player = new Player(width);
				Planet.globalDifficulty = 0;
				currentPlanet = null;
			}
			if (currentPlanet != null) {
				batch.draw(new Texture("healthBar.png"), 0, 0, player.getHealth() * 19.2f, 30);

				for (Enemy enemy : currentPlanet.enemyWaves.get(0)) {
					if (enemy.getHealth() != 0) {
						enemy.moveEnemy();
						batch.draw(enemy.getEnemySprite(), enemy.getPosX(), enemy.getPosY());
						for (Bullet bullet : Enemy.allEnemyBullets) {
							Rectangle playerRectangle = new Rectangle(player.getPosX(), player.getPosY(), 140, player.getPlayerSprite().getHeight());
							Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
							if (player.getHealth() != 0 && bullet.exists && overlaps(playerRectangle, bulletRectangle) && bulletRectangle.x > playerRectangle.x && bulletRectangle.y > playerRectangle.y/*&& bulletRectangle.x > player.getPosX() && bulletRectangle.x < player.getPosX()+player.getPlayerSprite().getWidth()*/) {
								bullet.exists = false;
								player.setHealth(-25);
							}
						}
						for (Bullet bullet : player.allBullets) {
							Rectangle enemyRectangle = new Rectangle((int) enemy.getPosX(), (int) enemy.getPosY(), 140, enemy.getEnemySprite().getHeight());
							Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
							if (enemy.getHealth() != 0 && bullet.exists && overlaps(bulletRectangle, enemyRectangle) && bulletRectangle.x > enemy.getPosX() && bulletRectangle.x < enemy.getPosX() + enemy.getEnemySprite().getWidth()) {
								bullet.exists = false;
								enemy.setHealth(-50);
								System.out.println("sheeeesh");
							}
						}
					}
				}
				// Draw every bullet and move them up
				for (Bullet bullet : player.allBullets) {

					if (bullet.exists) {
						batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
					}
					if (!paused) {
						bullet.setPosY(5, height);
					}
				}

				for (Bullet bullet : Enemy.allEnemyBullets) {
					if (bullet.exists) {
						batch.draw(bullet.getLaser(), bullet.getPosX(), bullet.getPosY());
					}
					if (!paused) {
						bullet.setPosY(-5f, height);
					}
				}

				for (Planet planet : solarSystem.planets) {
					if (planet.difficulty == Planet.globalDifficulty) {
						currentPlanet = planet;
					}
				}


				// draw all defenses present

				int defenseCount = 0;
				for (Defense defense : currentPlanet.defenses) {
					Rectangle defenseRectangle = new Rectangle(defense.getPosX(), defense.getPosY(), defense.getTexture().getWidth(), defense.getTexture().getHeight());
					for (Bullet bullet : player.allBullets) {
						Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
						if (defense.getHealth() != 0 && bullet.exists && overlaps(bulletRectangle, defenseRectangle) && bulletRectangle.x > defense.getPosX() && bulletRectangle.x < defense.getPosX() + defense.getTexture().getWidth() && defense.getPosY() + defense.getTexture().getHeight() > bullet.getPosY()) {
							bullet.exists = false;
							defense.setHealth(-50);
						}
					}
					for (Bullet bullet : Enemy.allEnemyBullets) {
						Rectangle bulletRectangle = new Rectangle((int) bullet.getPosX(), (int) bullet.getPosY(), bullet.getLaser().getWidth(), bullet.getLaser().getHeight());
						if (defense.getHealth() != 0 && bullet.exists && overlaps(bulletRectangle, defenseRectangle) && bulletRectangle.x > defense.getPosX() && bulletRectangle.x < defense.getPosX() + defense.getTexture().getWidth() && defense.getPosY() + defense.getTexture().getHeight() > bullet.getPosY()) {
							bullet.exists = false;
							defense.setHealth(-50);
						}
					}
					if (defense.getHealth() > 0) {
						batch.draw(defense.getTexture(), defense.getPosX(), defense.getPosY());
					}
					defenseCount++;

				}


				// Change the position of the player depending on the keys pressed
				if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !paused) {
					player.setPosX(-2, width);
				} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !paused) {
					player.setPosX(2, width);
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !paused) {
					player.shoot();
				}
			}
			batch.end();
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
		background.dispose();
		shapeRenderer.dispose();
		music.dispose();
		music2.dispose();
	}

	public void renderBapaos(float delta)
	{
		for(int i = 0; i<bapaoSprites.length; i++)
		{
			batch.begin();
			bapaoSprites[i].draw(batch);
			batch.end();
		}

		for(int i = 0; i<bapaoSprites.length; i++)
		{
			bapaoSprites[i].setPosition(bapaoX[i], bapaoY[i]);
			bapaoSprites[i].setRotation(bapaoY[i]);
			if(bapaoY[i] < -50)
			{
				bapaoY[i] = 1210;
				bapaoX[i] = (float) (Math.random()) * (1920);

			}
			bapaoY[i] -= (bapaoSpeed[i] * delta);
		}
	}

	public boolean overlaps (Rectangle r, Rectangle r2) {
		return r2.x < r.x + r.width && r2.x + width > r.x && r2.y < r.y + r.height && r2.y + height > r.y;
	}

}

