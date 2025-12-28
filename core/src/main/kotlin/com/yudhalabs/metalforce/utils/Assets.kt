package com.yudhalabs.metalforce.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class Assets : Disposable {
    // Player sprites
    lateinit var playerIdle: Array<TextureRegion>
    lateinit var playerRun: Array<TextureRegion>
    lateinit var playerJump: TextureRegion
    lateinit var playerShoot: TextureRegion

    // Enemy sprites
    lateinit var enemySoldier: Array<TextureRegion>
    lateinit var enemyHeavy: Array<TextureRegion>
    lateinit var enemyGrenadier: Array<TextureRegion>
    lateinit var enemyShield: Array<TextureRegion>
    lateinit var enemyRunner: Array<TextureRegion>
    lateinit var enemyDeath: Array<TextureRegion>
    lateinit var shieldSprite: TextureRegion
    lateinit var grenadeSprite: TextureRegion

    // Boss sprites
    lateinit var bossSprites: Array<TextureRegion>
    lateinit var bossCharge: Array<TextureRegion>
    lateinit var bossWeak: Array<TextureRegion>
    lateinit var healthBarBg: TextureRegion
    lateinit var healthBarFg: TextureRegion

    // Bullet sprites
    lateinit var bullet: TextureRegion
    lateinit var enemyBullet: TextureRegion
    lateinit var heavyBullet: TextureRegion
    lateinit var shotgunPellet: TextureRegion
    lateinit var rocketBullet: TextureRegion
    lateinit var flameBullet: TextureRegion

    // Pickup sprites
    lateinit var healthPickup: TextureRegion
    lateinit var heavyMGPickup: TextureRegion
    lateinit var shotgunPickup: TextureRegion
    lateinit var rocketPickup: TextureRegion
    lateinit var flamePickup: TextureRegion
    lateinit var ammoPickup: TextureRegion
    lateinit var bombPickup: TextureRegion

    // Environment
    lateinit var ground: TextureRegion
    lateinit var background: Texture

    // Effects
    lateinit var explosion: Array<TextureRegion>

    private val textures = mutableListOf<Texture>()

    fun load() {
        createPlayerSprites()
        createEnemySprites()
        createBossSprites()
        createBulletSprites()
        createPickupSprites()
        createEnvironment()
        createEffects()
    }

    private fun createPlayerSprites() {
        val playerPixels = arrayOf(
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..GGBBBBGG..
            ...BB..BB...
            ...BB..BB...
            ..BBB..BBB..
            """,
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..GGBBBBGG..
            ...BB..BB...
            ..BB....BB..
            ..BBB..BBB..
            """
        )

        val runPixels = arrayOf(
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..BB....BB..
            .BB......BB.
            .BB......BB.
            """,
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..GGBBBBGG..
            ...BB..BB...
            ....BB..BB..
            ...BB..BB...
            """,
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..GGBBBBGG..
            ..BB....BB..
            BB........BB
            BB........BB
            """
        )

        playerIdle = playerPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        playerRun = runPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        playerJump = createTextureFromAscii(
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGG...
            .GGGGBBBBGG.
            ..GGBBBBGG..
            ..GGBBBBGG..
            .BB......BB.
            BB........BB
            BB........BB
            """, 32, 32
        )
        playerShoot = createTextureFromAscii(
            """
            ....GGGG....
            ...GGGGGG...
            ...FFFFFG...
            ...FFFFFF...
            ....GGGG....
            ...GGGGGGGGG
            ..GGBBBBGGMM
            ..GGBBBBGG..
            ..GGBBBBGG..
            ...BB..BB...
            ...BB..BB...
            ..BBB..BBB..
            """, 32, 32
        )
    }

    private fun createEnemySprites() {
        // Basic soldier (red)
        val soldierPixels = arrayOf(
            """
            ....RRRR....
            ...RRRRRR...
            ...FFFFFR...
            ...FFFFFF...
            ....RRRR....
            ...RRRRRR...
            ..RRDDDDDR..
            ..RRDDDDDR..
            ..RRDDDDDR..
            ...DD..DD...
            ...DD..DD...
            ..DDD..DDD..
            """,
            """
            ....RRRR....
            ...RRRRRR...
            ...FFFFFR...
            ...FFFFFF...
            ....RRRR....
            ...RRRRRR...
            ..RRDDDDRR..
            ..RRDDDDRR..
            ..RRDDDDRR..
            ..DD....DD..
            .DD......DD.
            .DD......DD.
            """
        )

        // Heavy soldier (dark red, bulkier)
        val heavyPixels = arrayOf(
            """
            ...DDDDDD...
            ..DDDDDDDD..
            ..FFFFFFFD..
            ..FFFFFFFF..
            ...DDDDDD...
            ..DDDDDDDD..
            .DDDRRRRRDD.
            .DDDRRRRRDD.
            .DDDRRRRRDD.
            ..DD....DD..
            ..DD....DD..
            .DDD....DDD.
            """,
            """
            ...DDDDDD...
            ..DDDDDDDD..
            ..FFFFFFFD..
            ..FFFFFFFF..
            ...DDDDDD...
            ..DDDDDDDD..
            .DDDRRRRRDD.
            .DDDRRRRRDD.
            .DDDRRRRRDD.
            .DD......DD.
            DD........DD
            DD........DD
            """
        )

        // Grenadier (olive green)
        val grenadierPixels = arrayOf(
            """
            ....OOOO....
            ...OOOOOO...
            ...FFFFFO...
            ...FFFFFF...
            ....OOOO....
            ...OOOOOO...
            ..OOBBBBOO..
            ..OOBBBBOO..
            ..OOBBBBOO..
            ...BB..BB...
            ...BB..BB...
            ..BBB..BBB..
            """,
            """
            ....OOOO....
            ...OOOOOO...
            ...FFFFFO...
            ...FFFFFF...
            ....OOOO....
            ..OOOOOOOO..
            ..OOBBBBOO..
            ..OOBBBBOO..
            ..OOBBBBOO..
            ..BB....BB..
            .BB......BB.
            .BB......BB.
            """
        )

        // Shield soldier (blue)
        val shieldPixels = arrayOf(
            """
            ....LLLL....
            ...LLLLLL...
            ...FFFFFL...
            ...FFFFFF...
            ....LLLL....
            ...LLLLLL...
            ..LLBBBBLL..
            ..LLBBBBLL..
            ..LLBBBBLL..
            ...BB..BB...
            ...BB..BB...
            ..BBB..BBB..
            """,
            """
            ....LLLL....
            ...LLLLLL...
            ...FFFFFL...
            ...FFFFFF...
            ....LLLL....
            ...LLLLLL...
            ..LLBBBBLL..
            ..LLBBBBLL..
            ..LLBBBBLL..
            ..BB....BB..
            .BB......BB.
            .BB......BB.
            """
        )

        // Runner (orange, lean)
        val runnerPixels = arrayOf(
            """
            ....PPPP....
            ...PPPPPP...
            ...FFFFFP...
            ...FFFFFF...
            ....PPPP....
            ...PPPPPP...
            ..PPBBBBPP..
            ..PPBBBBPP..
            ..PPBBBBPP..
            .BB......BB.
            BB........BB
            BB........BB
            """,
            """
            ....PPPP....
            ...PPPPPP...
            ...FFFFFP...
            ...FFFFFF...
            ....PPPP....
            ...PPPPPP...
            ..PPBBBBPP..
            ..PPBBBBPP..
            ..PPBBBBPP..
            BB........BB
            .BB......BB.
            ..BB....BB..
            """
        )

        val deathPixels = arrayOf(
            """
            ............
            ....RRRR....
            ...RRRRRR...
            ...FFFFFF...
            ....RRRR....
            ..RRRRRRRR..
            ..RRDDDDRR..
            ...DDDDDD...
            ....DDDD....
            ............
            ............
            ............
            """,
            """
            ............
            ............
            ....RRRR....
            ...FFFFFF...
            ..RRRRRRRR..
            ...DDDDDD...
            ....DDDD....
            ............
            ............
            ............
            ............
            ............
            """,
            """
            ............
            ............
            ............
            ....FFFF....
            ...RRRRRR...
            ....DDDD....
            ............
            ............
            ............
            ............
            ............
            ............
            """
        )

        enemySoldier = soldierPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyHeavy = heavyPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyGrenadier = grenadierPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyShield = shieldPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyRunner = runnerPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyDeath = deathPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()

        // Shield sprite
        shieldSprite = createTextureFromAscii(
            """
            MMMM
            MLLM
            MLLM
            MLLM
            MLLM
            MLLM
            MLLM
            MMMM
            """, 16, 32
        )

        // Grenade sprite
        grenadeSprite = createTextureFromAscii(
            """
            .OO.
            OOOO
            OOOO
            .OO.
            """, 16, 16
        )
    }

    private fun createBossSprites() {
        // Boss tank - large mechanical enemy
        val bossPixels = arrayOf(
            """
            ....MMMMMMMM....
            ...MMMMMMMMMM...
            ..MMMMMMMMMMMM..
            ..MMRRRRRRRRMM..
            .MMRRRRRRRRRRM.
            .MMRRRRRRRRRRM.
            MMMRRRRRRRRRRMMM
            MMMRRRRRRRRRRMMM
            MMMMMMMMMMMMMMMM
            .BBBBBB..BBBBBB.
            .BBBBBB..BBBBBB.
            BBBBBBBBBBBBBBBB
            """,
            """
            ....MMMMMMMM....
            ...MMMMMMMMMM...
            ..MMMMMMMMMMMM..
            ..MMRRRRRRRRMM..
            .MMRRRRRRRRRRM.
            .MMRRRRRRRRRRM.
            MMMRRRRRRRRRRMMM
            MMMRRRRRRRRRRMMM
            MMMMMMMMMMMMMMMM
            BBBBBB....BBBBBB
            .BBBBBB..BBBBBB.
            BBBBBBBBBBBBBBBB
            """
        )

        val bossChargePixels = arrayOf(
            """
            ....MMMMMMMM....
            ...MMMMMMMMMM...
            ..MMMMMMMMMMMM..
            ..MMYYYYYYYYYYMM
            .MMYYYYYYYYYYY.
            .MMYYYYYYYYYYY.
            MMMRRRRRRRRRRMMM
            MMMRRRRRRRRRRMMM
            MMMMMMMMMMMMMMMM
            BBBBBB....BBBBBB
            BBBBBB....BBBBBB
            BBBBBBBBBBBBBBBB
            """,
            """
            ....MMMMMMMM....
            ...MMMMMMMMMM...
            ..MMMMMMMMMMMM..
            ..MMYYYYYYYYYYMM
            .MMYYYYYYYYYYY.
            .MMYYYYYYYYYYY.
            MMMRRRRRRRRRRMMM
            MMMRRRRRRRRRRMMM
            MMMMMMMMMMMMMMMM
            .BBBBBB..BBBBBB.
            .BBBBBB..BBBBBB.
            BBBBBBBBBBBBBBBB
            """
        )

        val bossWeakPixels = arrayOf(
            """
            ....MMMMMMMM....
            ...MMMMMMMMMM...
            ..MMMMMMMMMMMM..
            ..MMDDDDDDDDDM..
            .MMDDDDDDDDDDD.
            .MMDDDDDDDDDDD.
            MMMDDDDDDDDDMMMM
            MMMDDDDDDDDDMMMM
            MMMMMMMMMMMMMMMM
            .BBBBBB..BBBBBB.
            .BBBBBB..BBBBBB.
            BBBBBBBBBBBBBBBB
            """
        )

        bossSprites = bossPixels.map { createTextureFromAscii(it, 64, 64) }.toTypedArray()
        bossCharge = bossChargePixels.map { createTextureFromAscii(it, 64, 64) }.toTypedArray()
        bossWeak = bossWeakPixels.map { createTextureFromAscii(it, 64, 64) }.toTypedArray()

        // Health bars
        healthBarBg = createSolidColor(Color.DARK_GRAY, 4, 4)
        healthBarFg = createSolidColor(Color.RED, 4, 4)
    }

    private fun createBulletSprites() {
        bullet = createTextureFromAscii(
            """
            .YYY
            YYYY
            YYYY
            .YYY
            """, 8, 8
        )

        enemyBullet = createTextureFromAscii(
            """
            .RRR
            RRRR
            RRRR
            .RRR
            """, 8, 8
        )

        heavyBullet = createTextureFromAscii(
            """
            .YYYY.
            YYYYYY
            YYYYYY
            .YYYY.
            """, 12, 8
        )

        shotgunPellet = createTextureFromAscii(
            """
            YY
            YY
            """, 6, 6
        )

        rocketBullet = createTextureFromAscii(
            """
            ..MMMM
            RRMMMM
            RRMMMM
            ..MMMM
            """, 16, 8
        )

        flameBullet = createTextureFromAscii(
            """
            .RYY.
            RYYYR
            RYYYR
            .RYY.
            """, 12, 8
        )
    }

    private fun createPickupSprites() {
        // Health pickup (green cross)
        healthPickup = createTextureFromAscii(
            """
            ..WWWW..
            ..GGGG..
            WWGGGGWW
            GGGGGGGG
            GGGGGGGG
            WWGGGGWW
            ..GGGG..
            ..WWWW..
            """, 32, 32
        )

        // Heavy MG pickup
        heavyMGPickup = createTextureFromAscii(
            """
            ........
            ..MMMMMM
            .MMMMMMM
            MMMMMMMM
            MMMMMMMM
            .MMMMMMM
            ..MMMMMM
            ........
            """, 32, 32
        )

        // Shotgun pickup
        shotgunPickup = createTextureFromAscii(
            """
            ........
            CCMMMMMM
            CCMMMMMM
            ..MMMMMM
            ..MMMMMM
            CCMMMMMM
            CCMMMMMM
            ........
            """, 32, 32
        )

        // Rocket pickup
        rocketPickup = createTextureFromAscii(
            """
            ....GGGG
            ...GGGGG
            RRMMMMMM
            RRMMMMMM
            RRMMMMMM
            RRMMMMMM
            ...GGGGG
            ....GGGG
            """, 32, 32
        )

        // Flame pickup
        flamePickup = createTextureFromAscii(
            """
            .....RYY
            ....RYYY
            MMMRYYYY
            MMMRYYYY
            MMMRYYYY
            MMMRYYYY
            ....RYYY
            .....RYY
            """, 32, 32
        )

        // Ammo pickup
        ammoPickup = createTextureFromAscii(
            """
            .YYYYYY.
            YYYYYYYY
            YY....YY
            YY.YY.YY
            YY.YY.YY
            YY....YY
            YYYYYYYY
            .YYYYYY.
            """, 32, 32
        )

        // Bomb pickup
        bombPickup = createTextureFromAscii(
            """
            ....BB..
            ...BBB..
            ..BBBB..
            .BBBBBB.
            BBBBBBBB
            BBBBBBBB
            .BBBBBB.
            ..BBBB..
            """, 32, 32
        )
    }

    private fun createEnvironment() {
        ground = createTextureFromAscii(
            """
            BBBBBBBBBBBBBBBB
            BBBBBBBBBBBBBBBB
            CCCCCCCCCCCCCCCC
            CCCCCCCCCCCCCCCC
            CCCCCCCCCCCCCCCC
            CCCCCCCCCCCCCCCC
            CCCCCCCCCCCCCCCC
            CCCCCCCCCCCCCCCC
            """, 64, 32
        )

        val bgPixmap = Pixmap(1, 256, Pixmap.Format.RGBA8888)
        for (y in 0 until 256) {
            val ratio = y / 256f
            val r = (0.2f + ratio * 0.3f)
            val g = (0.3f + ratio * 0.3f)
            val b = (0.5f + ratio * 0.3f)
            bgPixmap.setColor(r, g, b, 1f)
            bgPixmap.drawPixel(0, y)
        }
        background = Texture(bgPixmap)
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge)
        textures.add(background)
        bgPixmap.dispose()
    }

    private fun createEffects() {
        val explosionPixels = arrayOf(
            """
            ....YYYY....
            ..YYYYYYYY..
            .YYRRRRRRYY.
            YYRRRRRRRRYY
            YRRRRRRRRRYY
            YRRRRRRRRRYY
            YYRRRRRRRRYY
            .YYRRRRRRYY.
            ..YYYYYYYY..
            ....YYYY....
            """,
            """
            ..YYYYYYYY..
            .YYRRRRRRYY.
            YRROOOOOORRY
            YROOOOOOORY
            ROOOOOOOOOR
            ROOOOOOOOOR
            YROOOOOOORY
            YRROOOOOORRY
            .YYRRRRRRYY.
            ..YYYYYYYY..
            """,
            """
            .YYRRRRRRYY.
            YRROOOOOORRY
            ROOWWWWWWOOR
            ROWWWWWWWWOR
            OWWWWWWWWWWO
            OWWWWWWWWWWO
            ROWWWWWWWWOR
            ROOWWWWWWOOR
            YRROOOOOORRY
            .YYRRRRRRYY.
            """
        )

        explosion = explosionPixels.map { createTextureFromAscii(it, 48, 48) }.toTypedArray()
    }

    private fun createSolidColor(color: Color, width: Int, height: Int): TextureRegion {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fill()
        val texture = Texture(pixmap)
        textures.add(texture)
        pixmap.dispose()
        return TextureRegion(texture)
    }

    private fun createTextureFromAscii(ascii: String, width: Int, height: Int): TextureRegion {
        val lines = ascii.trimIndent().lines().filter { it.isNotBlank() }
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(0f, 0f, 0f, 0f)
        pixmap.fill()

        val charWidth = width / (lines.maxOfOrNull { it.length } ?: 1)
        val charHeight = height / lines.size

        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                val color = when (char) {
                    'G' -> Color(0.2f, 0.5f, 0.2f, 1f)     // Green (helmet/uniform)
                    'R' -> Color(0.8f, 0.2f, 0.2f, 1f)     // Red
                    'F' -> Color(0.9f, 0.75f, 0.6f, 1f)    // Flesh/skin
                    'B' -> Color(0.3f, 0.3f, 0.3f, 1f)     // Black/dark
                    'D' -> Color(0.4f, 0.35f, 0.3f, 1f)    // Dark brown
                    'Y' -> Color(1f, 0.9f, 0.2f, 1f)       // Yellow
                    'M' -> Color(0.5f, 0.5f, 0.5f, 1f)     // Metal gray
                    'C' -> Color(0.55f, 0.35f, 0.2f, 1f)   // Brown (dirt)
                    'W' -> Color.WHITE                     // White
                    'O' -> Color(0.6f, 0.5f, 0.2f, 1f)     // Olive
                    'L' -> Color(0.2f, 0.3f, 0.6f, 1f)     // Blue
                    'P' -> Color(0.9f, 0.5f, 0.2f, 1f)     // Orange
                    else -> null
                }
                color?.let {
                    pixmap.setColor(it)
                    for (px in 0 until charWidth) {
                        for (py in 0 until charHeight) {
                            pixmap.drawPixel(x * charWidth + px, y * charHeight + py)
                        }
                    }
                }
            }
        }

        val texture = Texture(pixmap)
        textures.add(texture)
        pixmap.dispose()
        return TextureRegion(texture)
    }

    override fun dispose() {
        textures.forEach { it.dispose() }
    }
}
