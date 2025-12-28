package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets
import kotlin.math.cos
import kotlin.math.sin

class Bullet(
    x: Float,
    y: Float,
    private val movingRight: Boolean,
    val isEnemyBullet: Boolean,
    private val assets: Assets,
    val damage: Int = 20,
    private val speed: Float = 500f,
    private val angle: Float = 0f,
    val weaponType: WeaponType = WeaponType.PISTOL
) {
    val position = Vector2(x, y)
    val bounds = Rectangle(x, y, WIDTH, HEIGHT)
    var isActive = true
    val explosionRadius: Float = if (weaponType == WeaponType.ROCKET) 80f else 0f

    companion object {
        const val WIDTH = 16f
        const val HEIGHT = 8f
    }

    fun update(delta: Float) {
        val direction = if (movingRight) 1 else -1
        val angleRad = Math.toRadians(angle.toDouble()).toFloat()

        position.x += speed * direction * cos(angleRad) * delta
        position.y += speed * sin(angleRad) * delta

        bounds.setPosition(position.x, position.y)

        // Deactivate if off screen
        if (position.x < -50 || position.x > 3000 || position.y < 0 || position.y > 600) {
            isActive = false
        }
    }

    fun render(batch: SpriteBatch) {
        val texture = when {
            isEnemyBullet -> assets.enemyBullet
            weaponType == WeaponType.ROCKET -> assets.rocketBullet
            weaponType == WeaponType.FLAME -> assets.flameBullet
            weaponType == WeaponType.HEAVY_MG -> assets.heavyBullet
            weaponType == WeaponType.SHOTGUN -> assets.shotgunPellet
            else -> assets.bullet
        }

        val width = when (weaponType) {
            WeaponType.ROCKET -> 24f
            WeaponType.FLAME -> 20f
            else -> WIDTH
        }
        val height = when (weaponType) {
            WeaponType.ROCKET -> 12f
            WeaponType.FLAME -> 12f
            else -> HEIGHT
        }

        if (movingRight) {
            batch.draw(texture, position.x, position.y, width, height)
        } else {
            batch.draw(texture, position.x + width, position.y, -width, height)
        }
    }
}
