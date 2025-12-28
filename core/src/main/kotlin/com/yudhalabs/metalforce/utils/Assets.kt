package com.yudhalabs.metalforce.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable

class Assets : Disposable {
    lateinit var playerIdle: Array<TextureRegion>
    lateinit var playerRun: Array<TextureRegion>
    lateinit var playerJump: TextureRegion
    lateinit var playerShoot: TextureRegion

    lateinit var enemySoldier: Array<TextureRegion>
    lateinit var enemyDeath: Array<TextureRegion>

    lateinit var bullet: TextureRegion
    lateinit var enemyBullet: TextureRegion

    lateinit var ground: TextureRegion
    lateinit var background: Texture

    private val textures = mutableListOf<Texture>()

    fun load() {
        createPlayerSprites()
        createEnemySprites()
        createBulletSprites()
        createEnvironment()
    }

    private fun createPlayerSprites() {
        // Player soldier sprite - 32x32 pixels
        val playerPixels = arrayOf(
            // Frame 1 - Idle
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
            // Frame 2 - Idle (slight movement)
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
            // Run frame 1
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
            // Run frame 2
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
            // Run frame 3
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
        val enemyPixels = arrayOf(
            // Frame 1
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
            // Frame 2
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

        enemySoldier = enemyPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
        enemyDeath = deathPixels.map { createTextureFromAscii(it, 32, 32) }.toTypedArray()
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
    }

    private fun createEnvironment() {
        // Ground texture
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

        // Create gradient background
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
                    'R' -> Color(0.7f, 0.2f, 0.2f, 1f)     // Red (enemy uniform)
                    'F' -> Color(0.9f, 0.75f, 0.6f, 1f)    // Flesh/skin
                    'B' -> Color(0.3f, 0.3f, 0.3f, 1f)     // Black/dark (boots)
                    'D' -> Color(0.4f, 0.35f, 0.3f, 1f)    // Dark brown
                    'Y' -> Color(1f, 0.9f, 0.2f, 1f)       // Yellow (bullet)
                    'M' -> Color(0.5f, 0.5f, 0.5f, 1f)     // Metal (gun)
                    'C' -> Color(0.55f, 0.35f, 0.2f, 1f)   // Brown (dirt)
                    'W' -> Color.WHITE
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
