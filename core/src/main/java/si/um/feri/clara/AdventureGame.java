package si.um.feri.clara;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AdventureGame extends ApplicationAdapter {
    private SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    private Texture characterImg;
    private Texture villainImg;
    private Texture carrotImg;
    private Texture goldenCarrotImg;
    private Texture backgroundImg;

    private Sound rewardSound;
    private Sound damageSound;
    private Sound gameOverSound;

    private String scoreBoard;
    private int score = 0;
    private BitmapFont font;

    private Rectangle player;
    private Array<Rectangle> villains;
    private Array<Rectangle> carrots;
    private Array<Rectangle> goldenCarrots;

    private float currentHealth = 100f;
    private boolean isGameOver = false;

    private float villainSpawnTimer = 0f;
    private float carrotSpawnTimer = 0f;

    private static final float PLAYER_SPEED = 200f;
    private static final float VILLAIN_SPAWN_INTERVAL = 3f;
    private static final float CARROT_SPAWN_INTERVAL = 2f;
    private static final float VILLAIN_SPEED = 150f;

    private static final float BAR_WIDTH = 200f;
    private static final float BAR_HEIGHT = 20f;
    private static final float BAR_PADDING = 10f;
    private static final float PADDING = 20f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Creating the 'character' and 'objects' images
        characterImg = new Texture("./assets/big_down_player.png");
        villainImg = new Texture("./assets/big_villain.png");
        carrotImg = new Texture("./assets/big_carrot.png");
        goldenCarrotImg = new Texture("./assets/golden_carrot.png");
        backgroundImg = new Texture("./assets/background.jpg");

        rewardSound = Gdx.audio.newSound(Gdx.files.internal("./assets/reward.wav"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("./assets/damage.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("./assets/game-over.wav"));

        font = new BitmapFont();
        scoreBoard = "Score: " + score;

        villains = new Array<>();
        carrots = new Array<>();
        goldenCarrots = new Array<>();
        createCharacter();
    }

    public void createCharacter() {
        characterImg = new Texture("./assets/big_down_player.png");
        player = new Rectangle(Gdx.graphics.getWidth() / 2f - characterImg.getWidth() / 2f,
            PADDING, characterImg.getWidth(), characterImg.getHeight());

    }

    public void spawnCarrot() {
        Rectangle carrot = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - carrotImg.getWidth() - PADDING),
            MathUtils.random(PADDING, Gdx.graphics.getHeight() - carrotImg.getHeight() - PADDING), carrotImg.getWidth(), carrotImg.getHeight());
        carrots.add(carrot);
    }

    public void spawnGoldenCarrot() {
        Rectangle goldenCarrot = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - carrotImg.getWidth() - PADDING),
            MathUtils.random(PADDING, Gdx.graphics.getHeight() - carrotImg.getHeight() - PADDING), carrotImg.getWidth(), carrotImg.getHeight());
        goldenCarrots.add(goldenCarrot);
    }

    public void spawnVillain() {
        Rectangle villain = new Rectangle(MathUtils.random(PADDING, Gdx.graphics.getWidth() - villainImg.getWidth() - PADDING),
            Gdx.graphics.getHeight(), villainImg.getWidth(), villainImg.getHeight());
        villains.add(villain);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        handleInput(Gdx.graphics.getDeltaTime());

        if (!isGameOver) {
            update(Gdx.graphics.getDeltaTime());
        }

        batch.begin();
        drawBackground();
        drawPlayer();
        drawVillains();
        drawCarrots();
        drawGoldenCarrots();
        drawScoreBoard();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawHealthBar();
        shapeRenderer.end();

        if (isGameOver) {
            batch.begin();
            drawGameOver();
            batch.end();
        }
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && isGameOver) {
            createCharacter();
            villains.clear();
            carrots.clear();
            goldenCarrots.clear();
            currentHealth = 100f;
            score = 0;
            scoreBoard = "Score: " + score;
            isGameOver = false;
        }

        if (currentHealth > 0f) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                characterImg = new Texture("./assets/big_right_player.png");
                player.x += PLAYER_SPEED * delta;
                if(player.x > Gdx.graphics.getWidth() - player.getWidth() - PADDING) {
                    player.x = Gdx.graphics.getWidth() - player.getWidth() - PADDING;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                characterImg = new Texture("./assets/big_left_player.png");
                player.x -= PLAYER_SPEED * delta;
                if(player.x < player.getWidth()) {
                    player.x = player.getWidth();
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                characterImg = new Texture("./assets/big_up_player.png");
                player.y += PLAYER_SPEED * delta;
                if(player.y > Gdx.graphics.getHeight() - player.getHeight() - PADDING) {
                    player.y = Gdx.graphics.getHeight() - player.getHeight() - PADDING;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                characterImg = new Texture("./assets/big_down_player.png");
                player.y -= PLAYER_SPEED * delta;
                if(player.y < player.getHeight()) {
                    player.y = player.getHeight();
                }
            }
        }
    }

    private void update(float delta) {
        Random rand = new Random();
        int roll = rand.nextInt(10);
        if (currentHealth > 0f) {
            villainSpawnTimer += delta;
            carrotSpawnTimer += delta;
            if (villainSpawnTimer > VILLAIN_SPAWN_INTERVAL) {
                spawnVillain();
                villainSpawnTimer = 0f;
            }
            if (carrotSpawnTimer > CARROT_SPAWN_INTERVAL) {
                if (roll == 1){
                    spawnGoldenCarrot();
                } else {
                    spawnCarrot();
                }
                carrotSpawnTimer = 0f;
            }
        }

        for(Iterator<Rectangle> villainIterator = villains.iterator(); villainIterator.hasNext();) {
            Rectangle villain = villainIterator.next();
            villain.y -= VILLAIN_SPEED * delta;
            if (villain.overlaps(player)) {
                villainIterator.remove();
                currentHealth -= 10f;
                scoreBoard = "Score: " + score;
                if (currentHealth <= 0f) {
                    gameOver();
                }
                damageSound.play();
            }

            if (villain.y + villain.height < 0f) {
                villainIterator.remove();
            }
        }

        for (Iterator<Rectangle> carrotIterator = carrots.iterator(); carrotIterator.hasNext(); ) {
            Rectangle carrot = carrotIterator.next();
            if (carrot.overlaps(player)) {
                rewardSound.play();
                score++;
                scoreBoard = "Score: " + score;
                carrotIterator.remove();
            }
        }

        for (Iterator<Rectangle> goldenCarrotIterator = goldenCarrots.iterator(); goldenCarrotIterator.hasNext(); ) {
            Rectangle goldenCarrot = goldenCarrotIterator.next();
            if (goldenCarrot.overlaps(player)) {
                rewardSound.play();
                score++;
                scoreBoard = "Score: " + score;
                currentHealth += 10f;
                goldenCarrotIterator.remove();
            }
        }
    }


    private void drawBackground(){
        batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    private void drawCarrots() {
        for (Rectangle carrot : carrots) {
            batch.draw(carrotImg, carrot.x, carrot.y, carrot.width, carrot.height);
        }
    }

    private void drawGoldenCarrots() {
        for (Rectangle goldenCarrot : goldenCarrots) {
            batch.draw(goldenCarrotImg, goldenCarrot.x, goldenCarrot.y, goldenCarrot.width, goldenCarrot.height);
        }
    }

    private void drawVillains() {
        for (Rectangle villain : villains) {
            batch.draw(villainImg, villain.x, villain.y, villain.width, villain.height);
        }
    }

    private void drawPlayer() {
        batch.draw(characterImg, player.x, player.y, player.width, player.height);
    }


    public void drawScoreBoard() {
        font.getData().setScale(2);
        font.setColor(0, 0, 0, 1);
        font.draw(batch, scoreBoard, 10, Gdx.graphics.getHeight() - 10);
    }

    private void drawHealthBar() {
        float healthPercentage = currentHealth / 100f;
        shapeRenderer.setColor(1, 0, 0, 1); // Red
        shapeRenderer.rect(BAR_PADDING, Gdx.graphics.getHeight() - BAR_PADDING - BAR_HEIGHT - font.getLineHeight(), BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.setColor(0, 1, 0, 1); // Green
        shapeRenderer.rect(BAR_PADDING, Gdx.graphics.getHeight() - BAR_PADDING - BAR_HEIGHT - font.getLineHeight(), BAR_WIDTH * healthPercentage, BAR_HEIGHT);
    }

    private void drawGameOver() {
        font.getData().setScale(2);
        font.setColor(1, 0, 0, 1);
        font.draw(batch, "Game Over",
            Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
    }

    public void gameOver() {
        isGameOver = true;
        gameOverSound.play();
    }

    @Override
    public void dispose() {
        batch.dispose();
        characterImg.dispose();
        villainImg.dispose();
        carrotImg.dispose();
        goldenCarrotImg.dispose();
        backgroundImg.dispose();
        rewardSound.dispose();
        damageSound.dispose();
        font.dispose();
    }
}
