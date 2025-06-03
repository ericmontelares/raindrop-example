package com.raindrop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final RaindropGame game;

    Texture raindropImage;
    Texture bucketImage;
    Sound raindropSound;
    Music rainMusic;


    OrthographicCamera camera;
    Vector3 touchPos;


    Rectangle bucket;
    Array<Rectangle> raindrops;


    long lastDropTime;
    int dropsGathered;

    public GameScreen(final RaindropGame game) {
        this.game = game;

        try {

            raindropImage = new Texture(Gdx.files.internal("raindrop.png"));
            bucketImage = new Texture(Gdx.files.internal("bucket.png"));


            raindropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error loading assets: " + e);
        }

        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 400);

        touchPos = new Vector3();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 470;
        raindrop.width = 20;
        raindrop.height = 20;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();

        Gdx.app.log("GameScreen", "Rendering with " + dropsGathered + " drops collected");

        if (game.batch != null && bucketImage != null && raindropImage != null) {
            game.batch.setProjectionMatrix(camera.combined);

            game.batch.begin();

            game.font.setColor(1, 1, 0, 1);

            game.font.draw(game.batch, "Raindrops Collected: " + dropsGathered, 10, 450);
            game.font.setColor(1, 1, 1, 1);


            game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);

            for (Rectangle raindrop : raindrops) {
                game.batch.draw(raindropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
            }

            game.batch.end();
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }

        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 100 * Gdx.graphics.getDeltaTime();

            if (raindrop.y + 64 < 0) iter.remove();

            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                raindropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        raindropImage.dispose();
        bucketImage.dispose();
        raindropSound.dispose();
        rainMusic.dispose();
    }
}

