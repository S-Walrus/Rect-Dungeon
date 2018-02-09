package ru.swalrus.rectdungeon

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ru.swalrus.rectdungeon.Game.*
import kotlin.math.abs

object Utils {

    fun reverseDirection(direction : Char) : Char {

        return when (direction) {
            Const.TOP -> Const.BOTTOM
            Const.BOTTOM -> Const.TOP
            Const.RIGHT -> Const.LEFT
            Const.LEFT -> Const.RIGHT
            Const.CENTER -> Const.CENTER
            else -> Const.CENTER
        }
    }

    fun dir2vec (direction: Char) : Vector2 {
        return when (direction) {
            Const.TOP -> Vector2(0f, 1f)
            Const.BOTTOM -> Vector2(0f, -1f)
            Const.RIGHT -> Vector2(1f, 0f)
            Const.LEFT -> Vector2(-1f, 0f)
            else -> Vector2(0f, 0f)
        }
    }

    fun vec2dir (vector: Vector2) : Char {
        return when {
            vector.x > 0 -> Const.RIGHT
            vector.x < 0 -> Const.LEFT
            vector.y > 0 -> Const.TOP
            vector.y < 0 -> Const.BOTTOM
            else -> Const.CENTER
        }
    }

    fun randomDirection() : Char {
        return arrayOf(Const.TOP, Const.BOTTOM, Const.LEFT, Const.RIGHT)[MathUtils.random(4)]
    }

    fun getDirection(x: Float, y: Float) : Char {
        return if (abs(x) > abs(y)) {
            if (x > 0) {
                Const.RIGHT
            } else {
                Const.LEFT
            }
        } else {
            if (y > 0) {
                Const.TOP
            } else {
                Const.BOTTOM
            }
        }
    }

    fun getDirection(x: Int, y: Int) : Char {
        return getDirection(x.toFloat(), y.toFloat())
    }

    fun getTile(id: Int) : Tile {
        return when (id) {
            0 -> Floor()
            1 -> Lava()
            else -> Floor()
        }
    }

    fun getTileID(name: String) : Int {
        return when (name) {
            "floor" -> 0
            "lava" -> 1
            else -> 0
        }
    }

    fun isTarget(creature: Creature?, target: Char) : Boolean {
        return when (target) {
            'a' -> true
            'c' -> (creature != null)
            'p' -> (creature != null) and (creature is Player)
            'e' -> (creature != null) and (creature !is Player)
            else -> false
        }
    }

    fun getTileDirection(map: Array<Array<Int>>, x: Int, y: Int, connect: (Int) -> Boolean) : Char {
        val top = (y == map[0].size - 1) or (connect(map[x][y+1]))
        val right = (x == map.size - 1) or (connect(map[x+1][y]))
        val bottom = (y == 0) or (connect(map[x][y-1]))
        val left = (x == 0) or (connect(map[x-1][y]))
        // TODO: add return
    }

    fun getImg(name: String, direction: Char = Const.CENTER) : Texture {
        when (name) {
            "wall" -> {
                when (direction) {
                    Const.TOP -> return Const.images["wall_top"]!!
                    Const.BOTTOM -> return Const.images["wall_bottom"]!!
                    Const.RIGHT -> return Const.images["wall_right"]!!
                    Const.LEFT -> return Const.images["wall_left"]!!
                }
            }
            "door" -> {
                when (direction) {
                    Const.TOP -> return Const.images["door_top"]!!
                    Const.BOTTOM -> return Const.images["door_bottom"]!!
                    Const.RIGHT -> return Const.images["door_right"]!!
                    Const.LEFT -> return Const.images["door_left"]!!
                }
            }
            "lava" -> {
                when (direction) {
                    't' -> return Const.images["lava_top"]!!
                    'r' -> return Const.images["lava_right"]!!
                    'b' -> return Const.images["lava_bottom"]!!
                    'l' -> return Const.images["lava_left"]!!
                    'h' -> return Const.images["lava_horizontal"]!!
                    'v' -> return Const.images["lava_vertical"]!!
                    else -> return Const.images["lava"]!!
                }
            }
            else -> return Const.images[name]!!
        }
        return Const.images["empty"]!!
    }
}