package com.yudhalabs.metalforce.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.yudhalabs.metalforce.MetalForceGame
import com.yudhalabs.metalforce.entities.Bullet
import com.yudhalabs.metalforce.entities.Enemy
import com.yudhalabs.metalforce.entities.Player

class GameScreen(private val game: MetalForceGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val uiCamera = OrthographicCamera()
    private val uiViewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera)

    private val shapeRenderer = ShapeRenderer()
    private val font = BitmapFont()

    private val player = Player(game.assets)
    private val enemies = mutableListOf<Enemy>()
    private val bullets = mutableListOf<Bullet>()

    private var score = 0
    private var cameraX = 0f
    private var gameOver = false
    private var spawnTimer = 0f

    // Touch controls
    private val leftButton = TouchButton(20f, 20f, 100f, 100f)
    private val rightButton = TouchButton(140f, 20f, 100f, 100f)
    private val jumpButton = TouchButton(WORLD_WIDTH - 140f, 20f, 100f, 100f)
    private val shootButton = TouchButton(WORLD_WIDTH - 260f, 20f, 100f, 100f)

    companion object {
        const val WORLD_WIDTH = 800f
        const val WORLD_HEIGHT = 480f
    }

    init {
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0f)
        uiCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0f)
        font.data.setScale(2f)
        font.color = Color.WHITE

        // Spawn initial enemies
        spawnEnemies(3)
    }

    private fun spawnEnemies(count: Int) {
        repeat(count) { i ->
            val x = cameraX + WORLD_WIDTH + 100 + i * 200
            enemies.add(Enemy(x, Player.GROUND_Y, game.assets))
        }
    }

    override fun render(delta: Float) {
        if (!gameOver) {
            update(delta)
        }

        Gdx.gl.glClearColor(0.4f, 0.6f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera to follow player
        cameraX = player.position.x - WORLD_WIDTH / 3
        if (cameraX < 0) cameraX = 0f
        camera.position.x = cameraX + WORLD_WIDTH / 2
        camera.update()

        // Draw background
        game.batch.projectionMatrix = camera.combined
        game.batch.begin()

        // Draw sky gradient
        game.batch.draw(
            game.assets.background,
            cameraX, 0f,
            WORLD_WIDTH, WORLD_HEIGHT
        )

        // Draw ground
        var groundX = cameraX - (cameraX % 64)
        while (groundX < cameraX + WORLD_WIDTH + 64) {
            game.batch.draw(game.assets.ground, groundX, 48f, 64f, 32f)
            groundX += 64
        }

        // Draw entities
        player.render(game.batch)
        enemies.forEach { it.render(game.batch) }
        bullets.forEach { it.render(game.batch) }

        game.batch.end()

        // Draw UI
        drawUI()
    }

    private fun update(delta: Float) {
        handleInput()

        // Update player
        player.update(delta)

        // Keep player in bounds
        if (player.position.x < cameraX) {
            player.position.x = cameraX
        }

        // Update enemies
        val newBullets = mutableListOf<Bullet>()
        enemies.forEach { enemy ->
            enemy.update(delta, player.position.x)?.let { newBullets.add(it) }
        }
        bullets.addAll(newBullets)
        enemies.removeAll { !it.isAlive }

        // Update bullets
        bullets.forEach { it.update(delta) }
        bullets.removeAll { !it.isActive }

        // Check collisions
        checkCollisions()

        // Spawn more enemies
        spawnTimer += delta
        if (spawnTimer >= 3f && enemies.size < 5) {
            spawnTimer = 0f
            spawnEnemies(2)
        }

        // Check game over
        if (!player.isAlive) {
            gameOver = true
        }
    }

    private fun handleInput() {
        // Multi-touch support
        for (i in 0 until 10) {
            if (Gdx.input.isTouched(i)) {
                val touchPos = Vector2(Gdx.input.getX(i).toFloat(), Gdx.input.getY(i).toFloat())
                uiViewport.unproject(touchPos)

                when {
                    leftButton.contains(touchPos) -> player.moveLeft()
                    rightButton.contains(touchPos) -> player.moveRight()
                    jumpButton.contains(touchPos) -> player.jump()
                    shootButton.contains(touchPos) -> {
                        player.shoot()?.let { bullets.add(it) }
                    }
                }
            }
        }
    }

    private fun checkCollisions() {
        // Player bullets vs enemies
        bullets.filter { !it.isEnemyBullet && it.isActive }.forEach { bullet ->
            enemies.filter { it.isAlive && !it.isDying }.forEach { enemy ->
                if (bullet.bounds.overlaps(enemy.bounds)) {
                    bullet.isActive = false
                    enemy.takeDamage(20)
                    if (enemy.isDying) {
                        score += 100
                    }
                }
            }
        }

        // Enemy bullets vs player
        bullets.filter { it.isEnemyBullet && it.isActive }.forEach { bullet ->
            if (bullet.bounds.overlaps(player.bounds)) {
                bullet.isActive = false
                player.takeDamage(10)
            }
        }

        // Player vs enemies (contact damage)
        enemies.filter { it.isAlive && !it.isDying }.forEach { enemy ->
            if (player.bounds.overlaps(enemy.bounds)) {
                player.takeDamage(1)
            }
        }
    }

    private fun drawUI() {
        // Draw touch buttons
        shapeRenderer.projectionMatrix = uiCamera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.6f)
        drawButton(leftButton)
        drawButton(rightButton)
        shapeRenderer.setColor(0.2f, 0.4f, 0.2f, 0.6f)
        drawButton(jumpButton)
        shapeRenderer.setColor(0.4f, 0.2f, 0.2f, 0.6f)
        drawButton(shootButton)

        shapeRenderer.end()

        // Draw button outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        drawButtonOutline(leftButton)
        drawButtonOutline(rightButton)
        drawButtonOutline(jumpButton)
        drawButtonOutline(shootButton)
        shapeRenderer.end()

        // Draw HUD text
        game.batch.projectionMatrix = uiCamera.combined
        game.batch.begin()

        font.draw(game.batch, "SCORE: $score", 20f, WORLD_HEIGHT - 20f)
        font.draw(game.batch, "HP: ${player.health}", 20f, WORLD_HEIGHT - 50f)

        // Button labels
        font.data.setScale(1.5f)
        font.draw(game.batch, "<", leftButton.x + 40f, leftButton.y + 60f)
        font.draw(game.batch, ">", rightButton.x + 40f, rightButton.y + 60f)
        font.draw(game.batch, "JUMP", jumpButton.x + 15f, jumpButton.y + 55f)
        font.draw(game.batch, "FIRE", shootButton.x + 20f, shootButton.y + 55f)
        font.data.setScale(2f)

        if (gameOver) {
            font.draw(game.batch, "GAME OVER", WORLD_WIDTH / 2 - 80f, WORLD_HEIGHT / 2 + 20f)
            font.draw(game.batch, "TAP TO RESTART", WORLD_WIDTH / 2 - 110f, WORLD_HEIGHT / 2 - 30f)
            if (Gdx.input.justTouched()) {
                restartGame()
            }
        }

        game.batch.end()
    }

    private fun drawButton(button: TouchButton) {
        shapeRenderer.rect(button.x, button.y, button.width, button.height)
    }

    private fun drawButtonOutline(button: TouchButton) {
        shapeRenderer.rect(button.x, button.y, button.width, button.height)
    }

    private fun restartGame() {
        player.position.set(100f, Player.GROUND_Y)
        player.health = 100
        player.isAlive = true
        enemies.clear()
        bullets.clear()
        score = 0
        cameraX = 0f
        gameOver = false
        spawnEnemies(3)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        uiViewport.update(width, height)
    }

    override fun show() {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        shapeRenderer.dispose()
        font.dispose()
    }

    private data class TouchButton(val x: Float, val y: Float, val width: Float, val height: Float) {
        fun contains(point: Vector2): Boolean {
            return point.x >= x && point.x <= x + width &&
                   point.y >= y && point.y <= y + height
        }
    }
}
