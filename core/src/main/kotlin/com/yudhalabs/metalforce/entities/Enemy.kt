package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets
import kotlin.random.Random

enum class EnemyType {
    SOLDIER,        // Basic soldier - shoots straight
    HEAVY,          // Slow, more HP, heavy gun
    GRENADIER,      // Throws grenades (arc trajectory)
    SHIELD,         // Has shield, must attack from behind
    RUNNER          // Fast, melee attack
}

open class Enemy(
    x: Float,
    y: Float,
    protected val assets: Assets,
    val type: EnemyType = EnemyType.SOLDIER
) {
    val position = Vector2(x, y)
    val velocity = Vector2()
    val bounds = Rectangle()

    var health = getMaxHealth()
    var isAlive = true
    var isDying = false
    var facingRight = false
    protected var hasShield = type == EnemyType.SHIELD

    protected var animFrame = 0
    protected var animTimer = 0f
    protected var deathTimer = 0f
    protected var shootTimer = Random.nextFloat() * 2f
    protected var moveTimer = 0f
    protected var isMoving = false
    protected var attackCooldown = 0f

    companion object {
        const val WIDTH = 64f
        const val HEIGHT = 64f
        const val GROUND_Y = 80f
    }

    private fun getMaxHealth(): Int = when (type) {
        EnemyType.SOLDIER -> 30
        EnemyType.HEAVY -> 80
        EnemyType.GRENADIER -> 40
        EnemyType.SHIELD -> 60
        EnemyType.RUNNER -> 20
    }

    private fun getSpeed(): Float = when (type) {
        EnemyType.SOLDIER -> 80f
        EnemyType.HEAVY -> 40f
        EnemyType.GRENADIER -> 60f
        EnemyType.SHIELD -> 50f
        EnemyType.RUNNER -> 150f
    }

    private fun getShootInterval(): Float = when (type) {
        EnemyType.SOLDIER -> 2f
        EnemyType.HEAVY -> 0.5f
        EnemyType.GRENADIER -> 3f
        EnemyType.SHIELD -> 2.5f
        EnemyType.RUNNER -> 999f // Doesn't shoot
    }

    open fun update(delta: Float, playerX: Float, playerY: Float = GROUND_Y): Any? {
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
            isMoving = when (type) {
                EnemyType.RUNNER -> true
                EnemyType.SHIELD -> kotlin.math.abs(playerX - position.x) > 100
                else -> Random.nextBoolean()
            }
        }

        if (isMoving) {
            val direction = if (facingRight) 1 else -1
            val moveMultiplier = if (type == EnemyType.RUNNER) 1f else 0.5f
            velocity.x = getSpeed() * direction * moveMultiplier
        } else {
            velocity.x = 0f
        }

        position.add(velocity.x * delta, 0f)

        // Keep on ground
        position.y = GROUND_Y

        // Update bounds - shield blocks frontal attacks
        if (hasShield) {
            val shieldOffset = if (facingRight) 0f else 20f
            bounds.set(position.x + shieldOffset, position.y, WIDTH - 20, HEIGHT - 10)
        } else {
            bounds.set(position.x + 10, position.y, WIDTH - 20, HEIGHT - 10)
        }

        // Animation
        animTimer += delta
        val animSpeed = if (type == EnemyType.RUNNER) 0.1f else 0.2f
        if (animTimer >= animSpeed) {
            animTimer = 0f
            animFrame = (animFrame + 1) % 2
        }

        // Attack
        attackCooldown -= delta
        shootTimer += delta
        if (shootTimer >= getShootInterval() && attackCooldown <= 0) {
            shootTimer = 0f
            attackCooldown = 0.3f

            return when (type) {
                EnemyType.GRENADIER -> {
                    // Return grenade
                    Grenade(
                        position.x + if (facingRight) WIDTH else 0f,
                        position.y + HEIGHT / 2,
                        facingRight,
                        playerX,
                        assets
                    )
                }
                EnemyType.RUNNER -> {
                    // Melee attack - return melee hit area
                    if (kotlin.math.abs(playerX - position.x) < 60) {
                        MeleeAttack(position.x, position.y, facingRight)
                    } else null
                }
                else -> {
                    val bulletX = if (facingRight) position.x + WIDTH else position.x - 8
                    val bulletY = position.y + HEIGHT / 2
                    Bullet(bulletX, bulletY, facingRight, true, assets)
                }
            }
        }

        return null
    }

    fun takeDamage(amount: Int, fromBehind: Boolean = false) {
        // Shield blocks frontal damage
        if (hasShield && !fromBehind) {
            // Shield takes damage instead
            return
        }

        health -= amount
        if (health <= 0 && !isDying) {
            isDying = true
            deathTimer = 0f
            animFrame = 0
        }
    }

    fun destroyShield() {
        hasShield = false
    }

    open fun render(batch: SpriteBatch) {
        if (!isAlive) return

        val texture: TextureRegion = when {
            isDying -> assets.enemyDeath[animFrame.coerceAtMost(assets.enemyDeath.size - 1)]
            type == EnemyType.HEAVY -> assets.enemyHeavy[animFrame % assets.enemyHeavy.size]
            type == EnemyType.GRENADIER -> assets.enemyGrenadier[animFrame % assets.enemyGrenadier.size]
            type == EnemyType.SHIELD -> assets.enemyShield[animFrame % assets.enemyShield.size]
            type == EnemyType.RUNNER -> assets.enemyRunner[animFrame % assets.enemyRunner.size]
            else -> assets.enemySoldier[animFrame % assets.enemySoldier.size]
        }

        if (facingRight) {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT)
        } else {
            batch.draw(texture, position.x + WIDTH, position.y, -WIDTH, HEIGHT)
        }

        // Draw shield indicator
        if (hasShield && !isDying) {
            val shieldX = if (facingRight) position.x - 5 else position.x + WIDTH - 15
            batch.draw(assets.shieldSprite, shieldX, position.y + 10, 20f, 50f)
        }
    }

    fun getScoreValue(): Int = when (type) {
        EnemyType.SOLDIER -> 100
        EnemyType.HEAVY -> 300
        EnemyType.GRENADIER -> 200
        EnemyType.SHIELD -> 250
        EnemyType.RUNNER -> 150
    }
}

// Grenade projectile for Grenadier enemy
class Grenade(
    x: Float,
    y: Float,
    movingRight: Boolean,
    targetX: Float,
    private val assets: Assets
) {
    val position = Vector2(x, y)
    val velocity = Vector2()
    val bounds = Rectangle(x, y, 16f, 16f)
    var isActive = true
    var hasExploded = false
    private var timer = 0f

    init {
        val distance = targetX - x
        velocity.x = distance / 1.5f
        velocity.y = 300f
    }

    fun update(delta: Float): Boolean {
        if (!isActive) return false

        timer += delta
        velocity.y -= 400f * delta
        position.add(velocity.x * delta, velocity.y * delta)
        bounds.setPosition(position.x, position.y)

        // Explode on ground or after 2 seconds
        if (position.y <= Player.GROUND_Y || timer > 2f) {
            isActive = false
            hasExploded = true
            return true
        }

        return false
    }

    fun render(batch: SpriteBatch) {
        if (isActive) {
            batch.draw(assets.grenadeSprite, position.x, position.y, 16f, 16f)
        }
    }

    fun getExplosionBounds(): Rectangle {
        return Rectangle(position.x - 40, position.y - 40, 80f, 80f)
    }
}

// Melee attack for Runner enemy
data class MeleeAttack(
    val x: Float,
    val y: Float,
    val facingRight: Boolean
) {
    val bounds: Rectangle
        get() {
            val attackX = if (facingRight) x + 32 else x - 32
            return Rectangle(attackX, y, 40f, 60f)
        }
    val damage = 25
}
