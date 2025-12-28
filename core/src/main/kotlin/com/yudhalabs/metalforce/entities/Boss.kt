package com.yudhalabs.metalforce.entities

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.yudhalabs.metalforce.utils.Assets
import kotlin.random.Random

enum class BossPhase {
    IDLE,
    SHOOTING,
    MISSILE_BARRAGE,
    CHARGE,
    VULNERABLE
}

class Boss(
    x: Float,
    private val assets: Assets
) {
    val position = Vector2(x, GROUND_Y)
    val bounds = Rectangle()
    var health = MAX_HEALTH
    var isAlive = true
    var isDying = false
    var isDefeated = false
    var facingRight = false

    private var phase = BossPhase.IDLE
    private var phaseTimer = 0f
    private var attackCooldown = 0f
    private var animFrame = 0
    private var animTimer = 0f
    private var deathTimer = 0f
    private var chargeSpeed = 0f
    private var flashTimer = 0f
    private var isFlashing = false

    companion object {
        const val WIDTH = 128f
        const val HEIGHT = 128f
        const val GROUND_Y = 80f
        const val MAX_HEALTH = 500
    }

    fun update(delta: Float, playerX: Float): List<Any> {
        if (!isAlive) return emptyList()

        val attacks = mutableListOf<Any>()

        if (isDying) {
            deathTimer += delta
            flashTimer += delta
            isFlashing = (flashTimer * 10).toInt() % 2 == 0

            if (deathTimer >= 3f) {
                isAlive = false
                isDefeated = true
            }
            return emptyList()
        }

        // Face player
        facingRight = playerX < position.x

        // Update bounds
        bounds.set(position.x + 20, position.y, WIDTH - 40, HEIGHT - 20)

        // Animation
        animTimer += delta
        if (animTimer >= 0.15f) {
            animTimer = 0f
            animFrame = (animFrame + 1) % 2
        }

        // Phase management
        phaseTimer += delta
        attackCooldown -= delta

        when (phase) {
            BossPhase.IDLE -> {
                if (phaseTimer >= 2f) {
                    phaseTimer = 0f
                    phase = selectNextPhase()
                }
            }

            BossPhase.SHOOTING -> {
                if (attackCooldown <= 0) {
                    attackCooldown = 0.3f
                    // Triple shot
                    for (angle in listOf(-10f, 0f, 10f)) {
                        attacks.add(Bullet(
                            x = if (facingRight) position.x else position.x + WIDTH,
                            y = position.y + HEIGHT / 2,
                            movingRight = facingRight,
                            isEnemyBullet = true,
                            assets = assets,
                            damage = 15,
                            speed = 400f,
                            angle = angle
                        ))
                    }
                }
                if (phaseTimer >= 4f) {
                    phaseTimer = 0f
                    phase = BossPhase.IDLE
                }
            }

            BossPhase.MISSILE_BARRAGE -> {
                if (attackCooldown <= 0) {
                    attackCooldown = 0.5f
                    // Launch homing missile (simplified as grenade)
                    attacks.add(Grenade(
                        x = position.x + WIDTH / 2,
                        y = position.y + HEIGHT,
                        movingRight = facingRight,
                        targetX = playerX,
                        assets = assets
                    ))
                }
                if (phaseTimer >= 3f) {
                    phaseTimer = 0f
                    phase = BossPhase.IDLE
                }
            }

            BossPhase.CHARGE -> {
                if (phaseTimer < 1f) {
                    // Wind up
                    chargeSpeed = 0f
                } else {
                    // Charge!
                    chargeSpeed = 300f
                    val direction = if (facingRight) -1 else 1
                    position.x += chargeSpeed * direction * delta

                    // Create melee hitbox while charging
                    if (attackCooldown <= 0) {
                        attackCooldown = 0.2f
                        attacks.add(MeleeAttack(position.x, position.y, !facingRight))
                    }
                }
                if (phaseTimer >= 3f) {
                    phaseTimer = 0f
                    phase = BossPhase.VULNERABLE
                }
            }

            BossPhase.VULNERABLE -> {
                // Boss is tired, takes extra damage in this phase
                if (phaseTimer >= 2f) {
                    phaseTimer = 0f
                    phase = BossPhase.IDLE
                }
            }
        }

        // Keep boss in bounds
        if (position.x < 200) position.x = 200f
        if (position.x > 1500) position.x = 1500f

        return attacks
    }

    private fun selectNextPhase(): BossPhase {
        val healthPercent = health.toFloat() / MAX_HEALTH
        return when {
            healthPercent < 0.3f -> {
                // Low health - more aggressive
                when (Random.nextInt(3)) {
                    0 -> BossPhase.CHARGE
                    1 -> BossPhase.MISSILE_BARRAGE
                    else -> BossPhase.SHOOTING
                }
            }
            healthPercent < 0.6f -> {
                when (Random.nextInt(4)) {
                    0 -> BossPhase.CHARGE
                    1 -> BossPhase.MISSILE_BARRAGE
                    else -> BossPhase.SHOOTING
                }
            }
            else -> {
                if (Random.nextBoolean()) BossPhase.SHOOTING else BossPhase.MISSILE_BARRAGE
            }
        }
    }

    fun takeDamage(amount: Int) {
        val actualDamage = if (phase == BossPhase.VULNERABLE) amount * 2 else amount
        health -= actualDamage

        if (health <= 0 && !isDying) {
            health = 0
            isDying = true
            deathTimer = 0f
        }
    }

    fun render(batch: SpriteBatch) {
        if (!isAlive) return

        // Skip rendering during flash
        if (isDying && !isFlashing) return

        val texture = when {
            isDying -> assets.bossSprites[0]
            phase == BossPhase.CHARGE -> assets.bossCharge[animFrame % assets.bossCharge.size]
            phase == BossPhase.VULNERABLE -> assets.bossWeak[0]
            else -> assets.bossSprites[animFrame % assets.bossSprites.size]
        }

        if (facingRight) {
            batch.draw(texture, position.x + WIDTH, position.y, -WIDTH, HEIGHT)
        } else {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT)
        }

        // Health bar
        val healthBarWidth = 100f
        val healthPercent = health.toFloat() / MAX_HEALTH
        batch.draw(assets.healthBarBg, position.x + 14, position.y + HEIGHT + 10, healthBarWidth, 10f)
        batch.draw(assets.healthBarFg, position.x + 14, position.y + HEIGHT + 10, healthBarWidth * healthPercent, 10f)
    }

    fun isVulnerable(): Boolean = phase == BossPhase.VULNERABLE
}
