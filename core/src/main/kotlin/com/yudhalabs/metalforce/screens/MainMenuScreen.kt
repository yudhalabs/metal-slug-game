package com.yudhalabs.metalforce.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.yudhalabs.metalforce.MetalForceGame

class MainMenuScreen(private val game: MetalForceGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(800f, 480f, camera)
    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()
    private val titleFont = BitmapFont()
    private val layout = GlyphLayout()

    private val startButton = Rectangle(300f, 220f, 200f, 60f)
    private val howToPlayButton = Rectangle(300f, 140f, 200f, 60f)

    private var animTimer = 0f
    private var showInstructions = false

    init {
        camera.position.set(400f, 240f, 0f)
        titleFont.data.setScale(4f)
        titleFont.color = Color.YELLOW
        font.data.setScale(2f)
        font.color = Color.WHITE
    }

    override fun render(delta: Float) {
        animTimer += delta

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        // Draw background effects
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Animated background lines
        shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 1f)
        for (i in 0..10) {
            val y = (i * 50f + animTimer * 30f) % 600f - 60f
            shapeRenderer.rect(0f, y, 800f, 20f)
        }

        // Draw buttons
        if (!showInstructions) {
            // Start button
            shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1f)
            shapeRenderer.rect(startButton.x, startButton.y, startButton.width, startButton.height)

            // How to play button
            shapeRenderer.setColor(0.2f, 0.4f, 0.6f, 1f)
            shapeRenderer.rect(howToPlayButton.x, howToPlayButton.y, howToPlayButton.width, howToPlayButton.height)
        } else {
            // Instructions panel
            shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.9f)
            shapeRenderer.rect(100f, 80f, 600f, 320f)
        }

        shapeRenderer.end()

        // Draw button outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        if (!showInstructions) {
            shapeRenderer.rect(startButton.x, startButton.y, startButton.width, startButton.height)
            shapeRenderer.rect(howToPlayButton.x, howToPlayButton.y, howToPlayButton.width, howToPlayButton.height)
        } else {
            shapeRenderer.rect(100f, 80f, 600f, 320f)
        }
        shapeRenderer.end()

        // Draw text
        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        // Title with animation
        val titleScale = 4f + kotlin.math.sin(animTimer * 2f).toFloat() * 0.2f
        titleFont.data.setScale(titleScale)
        titleFont.color = Color.YELLOW
        layout.setText(titleFont, "METAL FORCE")
        titleFont.draw(game.batch, "METAL FORCE", 400f - layout.width / 2, 420f)

        // Subtitle
        font.data.setScale(1.5f)
        font.color = Color.ORANGE
        layout.setText(font, "Run and Gun Action!")
        font.draw(game.batch, "Run and Gun Action!", 400f - layout.width / 2, 360f)

        font.data.setScale(2f)
        font.color = Color.WHITE

        if (!showInstructions) {
            // Button text
            layout.setText(font, "START")
            font.draw(game.batch, "START", startButton.x + startButton.width / 2 - layout.width / 2, startButton.y + 40f)

            layout.setText(font, "HOW TO PLAY")
            font.data.setScale(1.5f)
            font.draw(game.batch, "HOW TO PLAY", howToPlayButton.x + howToPlayButton.width / 2 - 70f, howToPlayButton.y + 40f)
            font.data.setScale(2f)

            // Version
            font.data.setScale(1f)
            font.color = Color.GRAY
            font.draw(game.batch, "v1.0 - YudhaLabs", 20f, 30f)
        } else {
            // Instructions
            font.data.setScale(2f)
            font.color = Color.YELLOW
            font.draw(game.batch, "HOW TO PLAY", 320f, 380f)

            font.data.setScale(1.5f)
            font.color = Color.WHITE
            font.draw(game.batch, "< > - Move Left/Right", 150f, 310f)
            font.draw(game.batch, "JUMP - Jump over enemies", 150f, 270f)
            font.draw(game.batch, "FIRE - Shoot your weapon", 150f, 230f)
            font.draw(game.batch, "Collect weapons & power-ups!", 150f, 180f)
            font.draw(game.batch, "Defeat the boss to win!", 150f, 140f)

            font.color = Color.GRAY
            font.draw(game.batch, "Tap anywhere to go back", 280f, 100f)
        }

        game.batch.end()

        // Handle input
        if (Gdx.input.justTouched()) {
            val touchPos = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport.unproject(touchPos)

            if (showInstructions) {
                showInstructions = false
            } else {
                when {
                    startButton.contains(touchPos) -> {
                        game.screen = GameScreen(game)
                    }
                    howToPlayButton.contains(touchPos) -> {
                        showInstructions = true
                    }
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun show() {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
        titleFont.dispose()
    }
}
