package com.yudhalabs.metalforce

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.yudhalabs.metalforce.screens.GameScreen
import com.yudhalabs.metalforce.utils.Assets

class MetalForceGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var assets: Assets

    override fun create() {
        batch = SpriteBatch()
        assets = Assets()
        assets.load()
        setScreen(GameScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        assets.dispose()
        super.dispose()
    }
}
