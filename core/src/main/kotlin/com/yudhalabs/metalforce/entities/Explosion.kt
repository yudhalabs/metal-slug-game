package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets

class Explosion(
    x: Float,
    y: Float,
    private val assets: Assets,
    private val isBig: Boolean = false
) {
    private val position = Vector2(x, y)
    private var timer = 0f
    private var frame = 0
    var isActive = true

    fun update(delta: Float) {
        timer += delta
        frame = (timer / 0.1f).toInt()
        if (frame >= assets.explosion.size) {
            isActive = false
        }
    }

    fun render(batch: SpriteBatch) {
        if (!isActive) return
        val texture = assets.explosion[frame.coerceAtMost(assets.explosion.size - 1)]
        val size = if (isBig) 200f else 64f
        batch.draw(texture, position.x - size / 2, position.y - size / 2, size, size)
    }
}
