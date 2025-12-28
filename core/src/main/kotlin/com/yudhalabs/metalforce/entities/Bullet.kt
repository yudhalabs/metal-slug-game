package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets

class Bullet(
    x: Float,
    y: Float,
    private val movingRight: Boolean,
    val isEnemyBullet: Boolean,
    private val assets: Assets
) {
    val position = Vector2(x, y)
    val bounds = Rectangle(x, y, WIDTH, HEIGHT)
    var isActive = true

    companion object {
        const val WIDTH = 16f
        const val HEIGHT = 8f
        const val SPEED = 500f
    }

    fun update(delta: Float) {
        val direction = if (movingRight) 1 else -1
        position.x += SPEED * direction * delta
        bounds.setPosition(position.x, position.y)

        // Deactivate if off screen
        if (position.x < -50 || position.x > 2000) {
            isActive = false
        }
    }

    fun render(batch: SpriteBatch) {
        val texture = if (isEnemyBullet) assets.enemyBullet else assets.bullet
        if (movingRight) {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT)
        } else {
            batch.draw(texture, position.x + WIDTH, position.y, -WIDTH, HEIGHT)
        }
    }
}
