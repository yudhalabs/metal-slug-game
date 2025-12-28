package com.yudhalabs.metalforce.entities

import com.yudhalabs.metalforce.utils.Assets

enum class WeaponType {
    PISTOL,      // Default - single shot
    HEAVY_MG,    // Fast fire rate
    SHOTGUN,     // Multiple bullets spread
    ROCKET,      // Slow but powerful, splash damage
    FLAME        // Continuous short range
}

data class WeaponStats(
    val damage: Int,
    val fireRate: Float,      // Seconds between shots
    val bulletSpeed: Float,
    val ammo: Int,            // -1 = infinite
    val bulletCount: Int = 1, // For shotgun spread
    val spread: Float = 0f,   // Bullet spread angle
    val explosionRadius: Float = 0f
)

class Weapon(var type: WeaponType, private val assets: Assets) {
    var ammo: Int = getStats().ammo
    var cooldown: Float = 0f

    companion object {
        fun getStatsFor(type: WeaponType): WeaponStats = when (type) {
            WeaponType.PISTOL -> WeaponStats(
                damage = 20,
                fireRate = 0.2f,
                bulletSpeed = 500f,
                ammo = -1
            )
            WeaponType.HEAVY_MG -> WeaponStats(
                damage = 15,
                fireRate = 0.08f,
                bulletSpeed = 600f,
                ammo = 100
            )
            WeaponType.SHOTGUN -> WeaponStats(
                damage = 25,
                fireRate = 0.5f,
                bulletSpeed = 450f,
                ammo = 30,
                bulletCount = 5,
                spread = 15f
            )
            WeaponType.ROCKET -> WeaponStats(
                damage = 100,
                fireRate = 1.0f,
                bulletSpeed = 300f,
                ammo = 10,
                explosionRadius = 80f
            )
            WeaponType.FLAME -> WeaponStats(
                damage = 8,
                fireRate = 0.05f,
                bulletSpeed = 250f,
                ammo = 150
            )
        }
    }

    fun getStats(): WeaponStats = getStatsFor(type)

    fun update(delta: Float) {
        cooldown -= delta
        if (cooldown < 0) cooldown = 0f
    }

    fun canShoot(): Boolean {
        val stats = getStats()
        return cooldown <= 0 && (stats.ammo == -1 || ammo > 0)
    }

    fun shoot(x: Float, y: Float, facingRight: Boolean): List<Bullet> {
        if (!canShoot()) return emptyList()

        val stats = getStats()
        cooldown = stats.fireRate

        if (stats.ammo != -1) {
            ammo--
            if (ammo <= 0) {
                // Switch back to pistol when out of ammo
                type = WeaponType.PISTOL
                ammo = -1
            }
        }

        val bullets = mutableListOf<Bullet>()
        val bulletX = if (facingRight) x else x - 8

        if (stats.bulletCount > 1) {
            // Shotgun spread
            val startAngle = -stats.spread / 2
            val angleStep = stats.spread / (stats.bulletCount - 1)

            for (i in 0 until stats.bulletCount) {
                val angle = startAngle + angleStep * i
                bullets.add(Bullet(
                    x = bulletX,
                    y = y,
                    movingRight = facingRight,
                    isEnemyBullet = false,
                    assets = assets,
                    damage = stats.damage,
                    speed = stats.bulletSpeed,
                    angle = angle,
                    weaponType = type
                ))
            }
        } else {
            bullets.add(Bullet(
                x = bulletX,
                y = y,
                movingRight = facingRight,
                isEnemyBullet = false,
                assets = assets,
                damage = stats.damage,
                speed = stats.bulletSpeed,
                weaponType = type
            ))
        }

        return bullets
    }

    fun pickupWeapon(newType: WeaponType) {
        type = newType
        ammo = getStatsFor(newType).ammo
    }
}
