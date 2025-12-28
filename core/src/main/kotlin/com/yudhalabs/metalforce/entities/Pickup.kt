package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets

enum class PickupType {
    HEALTH,         // Restore health
    HEAVY_MG,       // Heavy machine gun
    SHOTGUN,        // Shotgun pickup
    ROCKET,         // Rocket launcher
    FLAME,          // Flamethrower
    AMMO,           // Restore ammo for current weapon
    BOMB            // Screen-clearing bomb
}

class Pickup(
    x: Float,
    y: Float,
    val type: PickupType,
    private val assets: Assets
) {
    val position = Vector2(x, y)
    val bounds = Rectangle(x, y, WIDTH, HEIGHT)
    var isActive = true
    private var animTimer = 0f
    private var floatOffset = 0f

    companion object {
        const val WIDTH = 32f
        const val HEIGHT = 32f
    }

    fun update(delta: Float) {
        // Floating animation
        animTimer += delta
        floatOffset = kotlin.math.sin(animTimer * 4f).toFloat() * 4f
    }

    fun render(batch: SpriteBatch) {
        if (!isActive) return

        val texture = when (type) {
            PickupType.HEALTH -> assets.healthPickup
            PickupType.HEAVY_MG -> assets.heavyMGPickup
            PickupType.SHOTGUN -> assets.shotgunPickup
            PickupType.ROCKET -> assets.rocketPickup
            PickupType.FLAME -> assets.flamePickup
            PickupType.AMMO -> assets.ammoPickup
            PickupType.BOMB -> assets.bombPickup
        }

        batch.draw(texture, position.x, position.y + floatOffset, WIDTH, HEIGHT)
    }

    fun collect(): PickupEffect {
        isActive = false
        return when (type) {
            PickupType.HEALTH -> PickupEffect.Health(50)
            PickupType.HEAVY_MG -> PickupEffect.Weapon(WeaponType.HEAVY_MG)
            PickupType.SHOTGUN -> PickupEffect.Weapon(WeaponType.SHOTGUN)
            PickupType.ROCKET -> PickupEffect.Weapon(WeaponType.ROCKET)
            PickupType.FLAME -> PickupEffect.Weapon(WeaponType.FLAME)
            PickupType.AMMO -> PickupEffect.Ammo(50)
            PickupType.BOMB -> PickupEffect.Bomb
        }
    }
}

sealed class PickupEffect {
    data class Health(val amount: Int) : PickupEffect()
    data class Weapon(val type: WeaponType) : PickupEffect()
    data class Ammo(val amount: Int) : PickupEffect()
    object Bomb : PickupEffect()
}
