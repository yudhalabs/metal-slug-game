package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets

class Player(private val assets: Assets) {
    val position = Vector2(100f, GROUND_Y)
    val velocity = Vector2()
    val bounds = Rectangle()

    var facingRight = true
    var isJumping = false
    var isShooting = false
    var health = 100
    var maxHealth = 100
    var isAlive = true
    var bombs = 3

    // Weapon system
    val weapon = Weapon(WeaponType.PISTOL, assets)

    private var stateTime = 0f
    private var animFrame = 0
    private var animTimer = 0f
    private var invincibleTimer = 0f
    private var flashTimer = 0f

    companion object {
        const val WIDTH = 64f
        const val HEIGHT = 64f
        const val GROUND_Y = 80f
        const val SPEED = 200f
        const val JUMP_VELOCITY = 450f
        const val GRAVITY = -800f
        const val INVINCIBLE_TIME = 1.5f
    }

    fun update(delta: Float) {
        stateTime += delta
        weapon.update(delta)

        // Invincibility after taking damage
        if (invincibleTimer > 0) {
            invincibleTimer -= delta
            flashTimer += delta
        }

        // Apply gravity
        velocity.y += GRAVITY * delta
        position.add(velocity.x * delta, velocity.y * delta)

        // Ground collision
        if (position.y <= GROUND_Y) {
            position.y = GROUND_Y
            velocity.y = 0f
            isJumping = false
        }

        // Update bounds
        bounds.set(position.x + 10, position.y, WIDTH - 20, HEIGHT - 10)

        // Animation
        animTimer += delta
        if (animTimer >= 0.1f) {
            animTimer = 0f
            animFrame = (animFrame + 1) % 3
        }

        // Friction
        velocity.x *= 0.85f
    }

    fun moveLeft() {
        velocity.x = -SPEED
        facingRight = false
    }

    fun moveRight() {
        velocity.x = SPEED
        facingRight = true
    }

    fun jump() {
        if (!isJumping) {
            velocity.y = JUMP_VELOCITY
            isJumping = true
        }
    }

    fun shoot(): List<Bullet> {
        if (weapon.canShoot()) {
            isShooting = true
            val bulletX = if (facingRight) position.x + WIDTH else position.x
            val bulletY = position.y + HEIGHT / 2
            return weapon.shoot(bulletX, bulletY, facingRight)
        }
        return emptyList()
    }

    fun takeDamage(amount: Int) {
        if (invincibleTimer > 0) return

        health -= amount
        invincibleTimer = INVINCIBLE_TIME

        if (health <= 0) {
            health = 0
            isAlive = false
        }
    }

    fun heal(amount: Int) {
        health = (health + amount).coerceAtMost(maxHealth)
    }

    fun pickupWeapon(type: WeaponType) {
        weapon.pickupWeapon(type)
    }

    fun addAmmo(amount: Int) {
        if (weapon.type != WeaponType.PISTOL) {
            weapon.ammo += amount
        }
    }

    fun useBomb(): Boolean {
        if (bombs > 0) {
            bombs--
            return true
        }
        return false
    }

    fun addBomb() {
        bombs++
    }

    fun isInvincible(): Boolean = invincibleTimer > 0

    fun render(batch: SpriteBatch) {
        // Flash when invincible
        if (invincibleTimer > 0 && (flashTimer * 10).toInt() % 2 == 0) {
            return
        }

        val texture: TextureRegion = when {
            isJumping -> assets.playerJump
            isShooting && weapon.cooldown > weapon.getStats().fireRate - 0.05f -> assets.playerShoot
            kotlin.math.abs(velocity.x) > 10f -> assets.playerRun[animFrame % assets.playerRun.size]
            else -> assets.playerIdle[animFrame % assets.playerIdle.size]
        }

        isShooting = false

        if (facingRight) {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT)
        } else {
            batch.draw(texture, position.x + WIDTH, position.y, -WIDTH, HEIGHT)
        }
    }

    fun reset() {
        position.set(100f, GROUND_Y)
        velocity.set(0f, 0f)
        health = maxHealth
        isAlive = true
        isJumping = false
        isShooting = false
        facingRight = true
        bombs = 3
        weapon.type = WeaponType.PISTOL
        weapon.ammo = -1
        invincibleTimer = 0f
    }

    fun getWeaponName(): String = when (weapon.type) {
        WeaponType.PISTOL -> "PISTOL"
        WeaponType.HEAVY_MG -> "HEAVY MG"
        WeaponType.SHOTGUN -> "SHOTGUN"
        WeaponType.ROCKET -> "ROCKET"
        WeaponType.FLAME -> "FLAME"
    }

    fun getAmmoDisplay(): String {
        return if (weapon.type == WeaponType.PISTOL) "∞" else "${weapon.ammo}"
    }
}
