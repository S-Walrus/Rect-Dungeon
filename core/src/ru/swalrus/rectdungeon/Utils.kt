package ru.swalrus.rectdungeon

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ru.swalrus.rectdungeon.Creatures.*
import ru.swalrus.rectdungeon.Game.*
import kotlin.math.abs

object Utils {

    // Return the opposite direction of 'direction'
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
            else -> Vector2.Zero
        }
    }

    fun vec2dir(x: Int, y: Int) : Char {
        return vec2dir(Vector2(x.toFloat(), y.toFloat()))
    }

    fun vec2dir (vector: Vector2) : Char {
        return if (vector.isZero) {
            Const.CENTER
        } else if (vector.y > vector.x) {
            if (vector.x + vector.y > 0) {
                Const.TOP
            } else {
                Const.LEFT
            }
        } else {
            if (vector.x + vector.y > 0) {
                Const.RIGHT
            } else {
                Const.BOTTOM
            }
        }
    }

    // Returns one of horizontal or vertical directions
    fun randomDirection() : Char {
        return arrayOf(Const.TOP, Const.BOTTOM, Const.LEFT, Const.RIGHT)[MathUtils.random(3)]
    }

    // Return the nearest direction of given vector
    // If there isn't one nearest direction, return one of them
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

    // The same as the previous
    fun getDirection(x: Int, y: Int) : Char {
        return getDirection(x.toFloat(), y.toFloat())
    }

    // Place creature to a room
    // id - Type of the creature by its role in battle (Killer, Range(r), Mob, Stealer)
    // biome - Type of the creature by the place where it can be found
    // x, y - Where to place the creature
    // room - The room where the creature must be placed to
    fun newCreature(id: Char, biome: Char, x: Int, y: Int, room: Room) : Creature {
        return when (biome) {
            'n' -> when (id) {
                'm' -> Zombie(x, y, room)
                'k' -> Skeleton(x, y, room)
                's' -> Goblin(x, y, room)
                'r' -> Archer(x, y, room)
                else -> Dummy(x, y, room)
            }
            else -> Dummy(x, y, room)
        }
    }

    // Returns tile by id
    fun getTile(id: Int) : Tile {
        return when (id) {
            0 -> Floor()
            1 -> Lava(getImg("lava"))
            else -> Floor()
        }
    }

    // Returns id of tile with the given name
    fun getTileID(name: String) : Int {
        return when (name) {
            "floor" -> 0
            "lava" -> 1
            else -> 0
        }
    }

    // Returns 'true' if the creature can be a target of an attack with the given mark
    fun isTarget(creature: Creature?, target: Char) : Boolean {
        return when (target) {
            'a' -> true
            'c' -> (creature != null)
            'p' -> (creature != null) and (creature is Player)
            'e' -> (creature != null) and (creature !is Player)
            else -> false
        }
    }

    // Returns an image of a lava tile with the given coordinates in the given room
    fun getLavaImg(map: Array<Array<Int>>, x: Int, y: Int, connect: (Int) -> Boolean) : Texture {
        // Directions are reversed
        val bottom = (y == map[0].size - 1) or (connect(map[x][y+1]))
        val left = (x == map.size - 1) or (connect(map[x+1][y]))
        val top = (y == 0) or (connect(map[x][y-1]))
        val right = (x == 0) or (connect(map[x-1][y]))

        return when {
            top and right -> Const.images["lava_top_right"]!!
            top and bottom -> Const.images["lava_vertical"]!!
            top and left -> Const.images["lava_top_left"]!!
            right and bottom -> Const.images["lava_bottom_right"]!!
            right and left -> Const.images["lava_horizontal"]!!
            bottom and left -> Const.images["lava_bottom_left"]!!
            top -> Const.images["lava_top"]!!
            right -> Const.images["lava_right"]!!
            bottom -> Const.images["lava_bottom"]!!
            left -> Const.images["lava_left"]!!
            else -> Const.images["lava"]!!
        }
    }

    // Returns image by name (and direction)
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
            "door_closed" -> {
                when (direction) {
                    Const.TOP -> return Const.images["door_closed_top"]!!
                    Const.BOTTOM -> return Const.images["door_closed_bottom"]!!
                    Const.RIGHT -> return Const.images["door_closed_right"]!!
                    Const.LEFT -> return Const.images["door_closed_left"]!!
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
            "arrow" -> {
                return Const.images["arrow_$direction"]!!
            }
            else -> return Const.images[name]!!
        }
        return Const.images["empty"]!!
    }
}