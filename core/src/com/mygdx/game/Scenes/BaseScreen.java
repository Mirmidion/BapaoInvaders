package com.mygdx.game.Scenes;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.GameScreen;

public abstract class BaseScreen implements Screen {
    GameScreen mainRenderScreen;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
}
