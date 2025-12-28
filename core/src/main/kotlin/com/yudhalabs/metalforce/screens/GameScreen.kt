package com.yudhalabs.metalforce.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.yudhalabs.metalforce.MetalForceGame
import com.yudhalabs.metalforce.entities.*
import kotlin.random.Random

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
    private val pickups = mutableListOf<Pickup>()
    private val grenades = mutableListOf<Grenade>()
    private val explosions = mutableListOf<Explosion>()

    private var boss: Boss? = null
    private var bossSpawned = false

    private var score = 0
    private var cameraX = 0f
    private var gameOver = false
    private var victory = false
    private var paused = false
    private var spawnTimer = 0f
    private var pickupTimer = 0f

    // Level system
    private var currentStage = 1
    private var enemiesKilled = 0
    private var stageProgress = 0f
    private val stageLength = 2000f // Distance to complete stage
    private var showingStageIntro = true
    private var stageIntroTimer = 0f

    // Touch controls
    private val leftButton = TouchButton(20f, 20f, 100f, 100f)
    private val rightButton = TouchButton(140f, 20f, 100f, 100f)
    private val jumpButton = TouchButton(WORLD_WIDTH - 140f, 20f, 100f, 100f)
    private val shootButton = TouchButton(WORLD_WIDTH - 260f, 20f, 100f, 100f)
    private val pauseButton = TouchButton(WORLD_WIDTH - 80f, WORLD_HEIGHT - 60f, 60f, 40f)
    private val bombButton = TouchButton(WORLD_WIDTH - 380f, 20f, 80f, 80f)

    companion object {
        const val WORLD_WIDTH = 800f
        const val WORLD_HEIGHT = 480f
    }

    init {
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0f)
        uiCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0f)
        font.data.setScale(2f)
        font.color = Color.WHITE
    }

    private fun startStage() {
        enemies.clear()
        bullets.clear()
        pickups.clear()
        grenades.clear()
        explosions.clear()
        boss = null
        bossSpawned = false
        stageProgress = 0f
        enemiesKilled = 0
        showingStageIntro = true
        stageIntroTimer = 0f

        // Spawn initial enemies based on stage
        spawnEnemies(2 + currentStage)
    }

    private fun spawnEnemies(count: Int) {
        repeat(count) { i ->
            val x = cameraX + WORLD_WIDTH + 100 + i * 200
            val type = getRandomEnemyType()
            enemies.add(Enemy(x, Player.GROUND_Y, game.assets, type))
        }
    }

    private fun getRandomEnemyType(): EnemyType {
        val roll = Random.nextFloat()
        return when {
            currentStage == 1 -> {
                when {
                    roll < 0.7f -> EnemyType.SOLDIER
                    roll < 0.9f -> EnemyType.RUNNER
                    else -> EnemyType.GRENADIER
                }
            }
            currentStage == 2 -> {
                when {
                    roll < 0.4f -> EnemyType.SOLDIER
                    roll < 0.6f -> EnemyType.HEAVY
                    roll < 0.8f -> EnemyType.GRENADIER
                    else -> EnemyType.SHIELD
                }
            }
            else -> {
                when {
                    roll < 0.25f -> EnemyType.SOLDIER
                    roll < 0.45f -> EnemyType.HEAVY
                    roll < 0.65f -> EnemyType.GRENADIER
                    roll < 0.85f -> EnemyType.SHIELD
                    else -> EnemyType.RUNNER
                }
            }
        }
    }

    private fun spawnPickup() {
        val x = cameraX + WORLD_WIDTH + Random.nextFloat() * 200
        val type = getRandomPickupType()
        pickups.add(Pickup(x, Player.GROUND_Y + 20, type, game.assets))
    }

    private fun getRandomPickupType(): PickupType {
        val roll = Random.nextFloat()
        return when {
            roll < 0.25f -> PickupType.HEALTH
            roll < 0.40f -> PickupType.HEAVY_MG
            roll < 0.55f -> PickupType.SHOTGUN
            roll < 0.70f -> PickupType.ROCKET
            roll < 0.80f -> PickupType.FLAME
            roll < 0.90f -> PickupType.AMMO
            else -> PickupType.BOMB
        }
    }

    private fun spawnBoss() {
        boss = Boss(cameraX + WORLD_WIDTH + 100, game.assets)
        bossSpawned = true
    }

    override fun render(delta: Float) {
        if (showingStageIntro) {
            renderStageIntro(delta)
            return
        }

        if (!gameOver && !victory && !paused) {
            update(delta)
        }

        Gdx.gl.glClearColor(0.4f, 0.6f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera to follow player
        cameraX = player.position.x - WORLD_WIDTH / 3
        if (cameraX < 0) cameraX = 0f

        // Boss fight - lock camera
        if (bossSpawned && boss != null && boss!!.isAlive) {
            val bossLockX = boss!!.position.x - WORLD_WIDTH / 2
            if (cameraX > bossLockX - 100) {
                cameraX = bossLockX - 100
            }
        }

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
        pickups.forEach { it.render(game.batch) }
        player.render(game.batch)
        enemies.forEach { it.render(game.batch) }
        boss?.render(game.batch)
        bullets.forEach { it.render(game.batch) }
        grenades.forEach { it.render(game.batch) }
        explosions.forEach { it.render(game.batch) }

        game.batch.end()

        // Draw UI
        drawUI()
    }

    private fun renderStageIntro(delta: Float) {
        stageIntroTimer += delta

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.batch.projectionMatrix = uiCamera.combined
        game.batch.begin()

        font.data.setScale(4f)
        font.color = Color.YELLOW
        val stageText = "STAGE $currentStage"
        font.draw(game.batch, stageText, WORLD_WIDTH / 2 - 100, WORLD_HEIGHT / 2 + 40)

        font.data.setScale(2f)
        font.color = Color.WHITE
        val subtitle = when (currentStage) {
            1 -> "JUNGLE ASSAULT"
            2 -> "ENEMY BASE"
            3 -> "FINAL SHOWDOWN"
            else -> "BONUS STAGE"
        }
        font.draw(game.batch, subtitle, WORLD_WIDTH / 2 - 80, WORLD_HEIGHT / 2 - 20)

        game.batch.end()

        if (stageIntroTimer >= 2f) {
            showingStageIntro = false
            startStage()
        }

        // Touch to skip
        if (Gdx.input.justTouched() && stageIntroTimer > 0.5f) {
            showingStageIntro = false
            startStage()
        }
    }

    private fun update(delta: Float) {
        handleInput()

        // Update player
        player.update(delta)

        // Update stage progress
        stageProgress = player.position.x

        // Keep player in bounds
        if (player.position.x < cameraX) {
            player.position.x = cameraX
        }

        // Update enemies
        enemies.forEach { enemy ->
            val attack = enemy.update(delta, player.position.x, player.position.y)
            when (attack) {
                is Bullet -> bullets.add(attack)
                is Grenade -> grenades.add(attack)
                is MeleeAttack -> {
                    if (attack.bounds.overlaps(player.bounds)) {
                        player.takeDamage(attack.damage)
                    }
                }
            }
        }
        enemies.removeAll { !it.isAlive }

        // Update boss
        boss?.let { b ->
            val attacks = b.update(delta, player.position.x)
            attacks.forEach { attack ->
                when (attack) {
                    is Bullet -> bullets.add(attack)
                    is Grenade -> grenades.add(attack)
                    is MeleeAttack -> {
                        if (attack.bounds.overlaps(player.bounds)) {
                            player.takeDamage(attack.damage)
                        }
                    }
                }
            }

            if (b.isDefeated) {
                victory = true
                score += 5000
            }
        }

        // Update bullets
        bullets.forEach { it.update(delta) }
        bullets.removeAll { !it.isActive }

        // Update grenades
        grenades.forEach { grenade ->
            if (grenade.update(delta)) {
                // Explode
                explosions.add(Explosion(grenade.position.x, grenade.position.y, game.assets))
                val explosionBounds = grenade.getExplosionBounds()
                if (explosionBounds.overlaps(player.bounds)) {
                    player.takeDamage(30)
                }
            }
        }
        grenades.removeAll { !it.isActive }

        // Update pickups
        pickups.forEach { it.update(delta) }

        // Update explosions
        explosions.forEach { it.update(delta) }
        explosions.removeAll { !it.isActive }

        // Check collisions
        checkCollisions()

        // Spawn more enemies
        spawnTimer += delta
        if (spawnTimer >= 3f && enemies.size < 5 + currentStage && !bossSpawned) {
            spawnTimer = 0f
            spawnEnemies(1 + currentStage / 2)
        }

        // Spawn pickups
        pickupTimer += delta
        if (pickupTimer >= 8f) {
            pickupTimer = 0f
            spawnPickup()
        }

        // Check for boss spawn
        if (!bossSpawned && stageProgress >= stageLength) {
            spawnBoss()
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
                    pauseButton.contains(touchPos) && Gdx.input.justTouched() -> {
                        paused = !paused
                    }
                    !paused -> {
                        when {
                            leftButton.contains(touchPos) -> player.moveLeft()
                            rightButton.contains(touchPos) -> player.moveRight()
                            jumpButton.contains(touchPos) -> player.jump()
                            shootButton.contains(touchPos) -> {
                                val newBullets = player.shoot()
                                bullets.addAll(newBullets)
                            }
                            bombButton.contains(touchPos) && Gdx.input.justTouched() -> {
                                if (player.useBomb()) {
                                    useBomb()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun useBomb() {
        // Clear all enemies on screen
        enemies.filter { it.position.x >= cameraX && it.position.x <= cameraX + WORLD_WIDTH }
            .forEach {
                it.takeDamage(999, true)
                score += it.getScoreValue()
            }

        // Damage boss
        boss?.takeDamage(50)

        // Clear enemy bullets
        bullets.filter { it.isEnemyBullet }.forEach { it.isActive = false }

        // Big explosion effect
        explosions.add(Explosion(cameraX + WORLD_WIDTH / 2, WORLD_HEIGHT / 2, game.assets, true))
    }

    private fun checkCollisions() {
        // Player bullets vs enemies
        bullets.filter { !it.isEnemyBullet && it.isActive }.forEach { bullet ->
            // Check regular enemies
            enemies.filter { it.isAlive && !it.isDying }.forEach { enemy ->
                if (bullet.bounds.overlaps(enemy.bounds)) {
                    bullet.isActive = false

                    // Check if shot from behind (for shield enemies)
                    val fromBehind = (bullet.position.x > enemy.position.x) != enemy.facingRight
                    enemy.takeDamage(bullet.damage, fromBehind)

                    if (enemy.isDying) {
                        score += enemy.getScoreValue()
                        enemiesKilled++

                        // Chance to drop pickup
                        if (Random.nextFloat() < 0.15f) {
                            val pickupType = getRandomPickupType()
                            pickups.add(Pickup(enemy.position.x, Player.GROUND_Y + 20, pickupType, game.assets))
                        }
                    }

                    // Explosion for rocket
                    if (bullet.explosionRadius > 0) {
                        explosions.add(Explosion(bullet.position.x, bullet.position.y, game.assets))
                        // Splash damage
                        val splash = Rectangle(
                            bullet.position.x - bullet.explosionRadius / 2,
                            bullet.position.y - bullet.explosionRadius / 2,
                            bullet.explosionRadius,
                            bullet.explosionRadius
                        )
                        enemies.filter { it.isAlive && !it.isDying && it != enemy }.forEach { other ->
                            if (splash.overlaps(other.bounds)) {
                                other.takeDamage(bullet.damage / 2, true)
                                if (other.isDying) {
                                    score += other.getScoreValue()
                                    enemiesKilled++
                                }
                            }
                        }
                    }
                }
            }

            // Check boss
            boss?.let { b ->
                if (b.isAlive && !b.isDying && bullet.bounds.overlaps(b.bounds)) {
                    bullet.isActive = false
                    b.takeDamage(bullet.damage)

                    if (bullet.explosionRadius > 0) {
                        explosions.add(Explosion(bullet.position.x, bullet.position.y, game.assets))
                    }
                }
            }
        }

        // Enemy bullets vs player
        bullets.filter { it.isEnemyBullet && it.isActive }.forEach { bullet ->
            if (bullet.bounds.overlaps(player.bounds)) {
                bullet.isActive = false
                player.takeDamage(bullet.damage)
            }
        }

        // Player vs enemies (contact damage)
        enemies.filter { it.isAlive && !it.isDying }.forEach { enemy ->
            if (player.bounds.overlaps(enemy.bounds)) {
                player.takeDamage(5)
            }
        }

        // Player vs boss
        boss?.let { b ->
            if (b.isAlive && !b.isDying && player.bounds.overlaps(b.bounds)) {
                player.takeDamage(10)
            }
        }

        // Player vs pickups
        pickups.filter { it.isActive }.forEach { pickup ->
            if (player.bounds.overlaps(pickup.bounds)) {
                val effect = pickup.collect()
                when (effect) {
                    is PickupEffect.Health -> player.heal(effect.amount)
                    is PickupEffect.Weapon -> player.pickupWeapon(effect.type)
                    is PickupEffect.Ammo -> player.addAmmo(effect.amount)
                    is PickupEffect.Bomb -> player.addBomb()
                }
                score += 50
            }
        }
        pickups.removeAll { !it.isActive }
    }

    private fun drawUI() {
        // Draw touch buttons
        shapeRenderer.projectionMatrix = uiCamera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // Movement buttons
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.6f)
        drawButton(leftButton)
        drawButton(rightButton)

        // Jump button
        shapeRenderer.setColor(0.2f, 0.4f, 0.2f, 0.6f)
        drawButton(jumpButton)

        // Shoot button
        shapeRenderer.setColor(0.4f, 0.2f, 0.2f, 0.6f)
        drawButton(shootButton)

        // Bomb button
        shapeRenderer.setColor(0.4f, 0.4f, 0.2f, 0.6f)
        drawButton(bombButton)

        // Pause button
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.7f)
        drawButton(pauseButton)

        // Health bar background
        shapeRenderer.setColor(0.3f, 0.0f, 0.0f, 0.8f)
        shapeRenderer.rect(20f, WORLD_HEIGHT - 35f, 150f, 20f)

        // Health bar foreground
        val healthPercent = player.health.toFloat() / player.maxHealth
        shapeRenderer.setColor(0.0f, 0.8f, 0.0f, 1f)
        shapeRenderer.rect(20f, WORLD_HEIGHT - 35f, 150f * healthPercent, 20f)

        // Stage progress bar
        if (!bossSpawned) {
            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f)
            shapeRenderer.rect(200f, WORLD_HEIGHT - 25f, 400f, 10f)
            val progress = (stageProgress / stageLength).coerceAtMost(1f)
            shapeRenderer.setColor(0.8f, 0.6f, 0.0f, 1f)
            shapeRenderer.rect(200f, WORLD_HEIGHT - 25f, 400f * progress, 10f)
        }

        shapeRenderer.end()

        // Draw button outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        drawButtonOutline(leftButton)
        drawButtonOutline(rightButton)
        drawButtonOutline(jumpButton)
        drawButtonOutline(shootButton)
        drawButtonOutline(bombButton)
        drawButtonOutline(pauseButton)
        shapeRenderer.end()

        // Draw HUD text
        game.batch.projectionMatrix = uiCamera.combined
        game.batch.begin()

        // Score
        font.data.setScale(1.5f)
        font.color = Color.WHITE
        font.draw(game.batch, "SCORE: $score", WORLD_WIDTH - 200f, WORLD_HEIGHT - 50f)

        // Stage indicator
        font.draw(game.batch, "STAGE $currentStage", 620f, WORLD_HEIGHT - 25f)

        // HP label
        font.data.setScale(1.2f)
        font.draw(game.batch, "HP", 25f, WORLD_HEIGHT - 40f)

        // Weapon info
        font.color = Color.YELLOW
        font.draw(game.batch, player.getWeaponName(), 20f, WORLD_HEIGHT - 60f)
        font.color = Color.WHITE
        font.draw(game.batch, "AMMO: ${player.getAmmoDisplay()}", 20f, WORLD_HEIGHT - 80f)

        // Bombs
        font.color = Color.ORANGE
        font.draw(game.batch, "BOMB x${player.bombs}", 20f, WORLD_HEIGHT - 100f)

        // Button labels
        font.data.setScale(1.5f)
        font.color = Color.WHITE
        font.draw(game.batch, "<", leftButton.x + 40f, leftButton.y + 60f)
        font.draw(game.batch, ">", rightButton.x + 40f, rightButton.y + 60f)
        font.draw(game.batch, "JUMP", jumpButton.x + 15f, jumpButton.y + 55f)
        font.draw(game.batch, "FIRE", shootButton.x + 20f, shootButton.y + 55f)
        font.data.setScale(1.2f)
        font.draw(game.batch, "BOMB", bombButton.x + 10f, bombButton.y + 50f)
        font.draw(game.batch, "||", pauseButton.x + 20f, pauseButton.y + 28f)

        // Boss health
        if (bossSpawned && boss != null && boss!!.isAlive) {
            font.data.setScale(2f)
            font.color = Color.RED
            font.draw(game.batch, "BOSS", WORLD_WIDTH / 2 - 40f, WORLD_HEIGHT - 10f)
        }

        font.data.setScale(2f)

        // Paused overlay
        if (paused) {
            font.color = Color.YELLOW
            font.draw(game.batch, "PAUSED", WORLD_WIDTH / 2 - 60f, WORLD_HEIGHT / 2 + 20f)
            font.data.setScale(1.5f)
            font.color = Color.WHITE
            font.draw(game.batch, "Tap pause to continue", WORLD_WIDTH / 2 - 100f, WORLD_HEIGHT / 2 - 30f)
            font.data.setScale(2f)
        }

        // Game over
        if (gameOver) {
            font.color = Color.RED
            font.draw(game.batch, "GAME OVER", WORLD_WIDTH / 2 - 80f, WORLD_HEIGHT / 2 + 40f)
            font.data.setScale(1.5f)
            font.color = Color.WHITE
            font.draw(game.batch, "Final Score: $score", WORLD_WIDTH / 2 - 80f, WORLD_HEIGHT / 2)
            font.draw(game.batch, "TAP TO RESTART", WORLD_WIDTH / 2 - 90f, WORLD_HEIGHT / 2 - 40f)
            font.data.setScale(2f)
            if (Gdx.input.justTouched()) {
                restartGame()
            }
        }

        // Victory
        if (victory) {
            font.color = Color.YELLOW
            font.draw(game.batch, "VICTORY!", WORLD_WIDTH / 2 - 70f, WORLD_HEIGHT / 2 + 40f)
            font.data.setScale(1.5f)
            font.color = Color.WHITE
            font.draw(game.batch, "Final Score: $score", WORLD_WIDTH / 2 - 80f, WORLD_HEIGHT / 2)

            if (currentStage < 3) {
                font.draw(game.batch, "TAP FOR NEXT STAGE", WORLD_WIDTH / 2 - 100f, WORLD_HEIGHT / 2 - 40f)
                if (Gdx.input.justTouched()) {
                    nextStage()
                }
            } else {
                font.color = Color.GREEN
                font.draw(game.batch, "YOU BEAT THE GAME!", WORLD_WIDTH / 2 - 100f, WORLD_HEIGHT / 2 - 40f)
                font.color = Color.WHITE
                font.draw(game.batch, "TAP FOR MAIN MENU", WORLD_WIDTH / 2 - 90f, WORLD_HEIGHT / 2 - 80f)
                if (Gdx.input.justTouched()) {
                    game.screen = MainMenuScreen(game)
                }
            }
            font.data.setScale(2f)
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
        player.reset()
        enemies.clear()
        bullets.clear()
        pickups.clear()
        grenades.clear()
        explosions.clear()
        boss = null
        bossSpawned = false
        score = 0
        cameraX = 0f
        gameOver = false
        victory = false
        currentStage = 1
        stageProgress = 0f
        enemiesKilled = 0
        showingStageIntro = true
        stageIntroTimer = 0f
    }

    private fun nextStage() {
        currentStage++
        player.position.set(100f, Player.GROUND_Y)
        player.velocity.set(0f, 0f)
        enemies.clear()
        bullets.clear()
        pickups.clear()
        grenades.clear()
        explosions.clear()
        boss = null
        bossSpawned = false
        cameraX = 0f
        victory = false
        stageProgress = 0f
        showingStageIntro = true
        stageIntroTimer = 0f
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
