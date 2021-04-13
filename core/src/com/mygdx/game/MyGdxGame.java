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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.w3c.dom.Text;

import java.awt.*;

public class MyGdxGame extends ApplicationAdapter {

	boolean mainMenu = true;
	boolean game = false;
	Skin buttonSkin;
	TextureAtlas atlas;
	BitmapFont normalFont;
	BitmapFont titleFont;
	Table mainMenuTable;
	TextButton start;
	TextButton settings;
	TextButton exit;
	Text title;
	Stage stage;
	Texture settingsMenu;
	boolean settingsMenuSwitch;

	private OrthographicCamera camera;

	int width;
	int height;

	SpriteBatch batch;

	Texture background;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		width = 1920;
		height = 1080;

		background = new Texture("background.png");

		//normalFont = new BitmapFont(Gdx.files.internal("normalFont.fnt"));
		titleFont = new BitmapFont(Gdx.files.internal("titleFontV2.fnt"));
		titleFont.getData().setScale(2);
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		mainMenuTable = new Table();
		mainMenuTable.setPosition(250,600);
		mainMenuTable.left();

		buttonSkin = new Skin(Gdx.files.internal("Skin.json"));

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
		start.setScale(2);
		settings.setTransform(true);
		settings.setScale(2);
		exit.setTransform(true);
		exit.setScale(2);




		mainMenuTable.row();
		mainMenuTable.add(start).padTop(50);
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

			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
