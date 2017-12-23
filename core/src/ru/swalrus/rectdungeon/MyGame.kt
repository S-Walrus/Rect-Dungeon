package ru.swalrus.rectdungeon

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MyGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var chunk : Chunk

    override fun create() {
        Const.importImages()
        batch = SpriteBatch()

        chunk = Chunk()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
    }
}
