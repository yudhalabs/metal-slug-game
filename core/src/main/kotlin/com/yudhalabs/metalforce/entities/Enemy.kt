package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets
import kotlin.random.Random

class Enemy(
    x: Float,
    y: Float,
    private val assets: Assets
) {
    val position = Vector2(x, y)
    val velocity = Vector2()
    val bounds = Rectangle()

    var health = 30
    var isAlive = true
    var isDying = false
    var facingRight = false

    private var animFrame = 0
    private var animTimer = 0f
    private var deathTimer = 0f
    private var shootTimer = Random.nextFloat() * 2f
    private var moveTimer = 0f
    private var isMoving = false

    companion object {
        const val WIDTH = 64f
        const val HEIGHT = 64f
        const val GROUND_Y = 80f
        const val SPEED = 80f
        const val SHOOT_INTERVAL = 2f
    }

    fun update(delta: Float, playerX: Float): Bullet? {
        if (!isAlive) return null

        if (isDying) {
            deathTimer += delta
            animFrame = (deathTimer / 0.15f).toInt().coerceAtMost(assets.enemyDeath.size - 1)
            if (deathTimer >= 0.45f) {
                isAlive = false
            }
            return null
        }

        // Face player
        facingRight = playerX > position.x

        // Movement AI
        moveTimer += delta
        if (moveTimer >= 1.5f) {
            moveTimer = 0f
            isMoving = Random.nextBoolean()
        }

        if (isMoving) {
            val direction = if (facingRight) 1 else -1
            velocity.x = SPEED * direction * 0.5f
        } else {
            velocity.x = 0f
        }

        position.add(velocity.x * delta, 0f)

        // Keep on ground
        position.y = GROUND_Y

        // Update bounds
        bounds.set(position.x + 10, position.y, WIDTH - 20, HEIGHT - 10)

        // Animation
        animTimer += delta
        if (animTimer >= 0.2f) {
            animTimer = 0f
            animFrame = (animFrame + 1) % assets.enemySoldier.size
        }

        // Shooting
        shootTimer += delta
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer = 0f
            val bulletX = if (facingRight) position.x + WIDTH else position.x - 8
            val bulletY = position.y + HEIGHT / 2
            return Bullet(bulletX, bulletY, facingRight, true, assets)
        }

        return null
    }

    fun takeDamage(amount: Int) {
        health -= amount
        if (health <= 0 && !isDying) {
            isDying = true
            deathTimer = 0f
            animFrame = 0
        }
    }

    fun render(batch: SpriteBatch) {
        if (!isAlive) return

        val texture = if (isDying) {
            assets.enemyDeath[animFrame.coerceAtMost(assets.enemyDeath.size - 1)]
        } else {
            assets.enemySoldier[animFrame % assets.enemySoldier.size]
        }

        if (facingRight) {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT)
        } else {
            batch.draw(texture, position.x + WIDTH, position.y, -WIDTH, HEIGHT)
        }
    }
}
