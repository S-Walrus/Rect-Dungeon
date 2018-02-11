package ru.swalrus.rectdungeon.Game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import ru.swalrus.rectdungeon.Const
import ru.swalrus.rectdungeon.Effects.Buff
import ru.swalrus.rectdungeon.Items.Weapon
import ru.swalrus.rectdungeon.Utils
import kotlin.math.abs
import kotlin.math.exp

abstract class Creature (var x: Int, var y: Int, var HP: Int, var img: Texture, var room: Room) {

    open var ready : Boolean = true             // Is ready to make next turn
    var buffs: Array<Buff> = emptyArray()

    private var active : Boolean = true         // Is not sleeping
    private var sprite: Sprite = Sprite(img)
    private var action: Char = 'n'              // {Nothing, Move, Attack, Push}
    private var moveDir: Vector2 = Vector2()
    private var dTime: Float = 0f
    private var animTime: Float = 0f
    private var target: Creature? = null
    private var afterAttack: (attacker: Creature, defender: Creature) -> Unit = {_, _ -> }
    private var dPos: Vector2 = Vector2()
    private var rotated: Boolean = false        // If creature has been rotated during the current movement
    private var attacked: Boolean = false       // If creature has attacked during the current attack
    private var lookRight: Boolean = true


    init {
        // Add creature to the queue
        room.addCreature(this)

        // Set position of sprite
        align()
        sprite.setSize(Const.TILE_SIZE, Const.TILE_SIZE)
        sprite.setOriginCenter()

        room.getTile(x, y).onStand(this)
    }

    abstract fun act()

    open fun onDeath() {

    }


    private fun makeTurn() {
        for (buff in buffs) {
            buff.onTurn(this)
        }

        act()
    }

    fun render(batch: SpriteBatch) {
        update()
        sprite.draw(batch)
    }

    fun isActive() : Boolean {
        return active
    }

    // Returns true if the creature will move
    open fun move(direction : Int, force: Boolean = false) : Boolean {
        moveDir = Vector2(Utils.dir2vec(direction))
        val newX = x + moveDir.x.toInt()
        val nexY = y + moveDir.y.toInt()
        val tile = room.getTile(newX, nexY)

        if (force or tile.passable or ((this is Player) and (tile is Door))) {
            val creature = room.getCreatureAt(newX, nexY)
            // If there are no creature
            if (force or (creature == null)) {
                startAnim()
                dTime = 0f
                animTime = Const.MOVE_TIME
                action = 'm'
                return true
            } else {
                moveDir = Vector2.Zero
                endAnim()
                return false
            }
        } else {
            moveDir = Vector2.Zero
            endAnim()
            return false
        }
    }

    open fun attack(direction: Int, target: Creature,
                    afterAttack: (attacker: Creature, defender: Creature) -> Unit,
                    requiredAP: Int, resetAP: Boolean) {
        // TODO: Make animation (change sprite in attack)
        if (direction != Const.CENTER) {
            moveDir = Vector2(Utils.dir2vec(direction))
            startAnim()
            animTime = Const.ATTACK_TIME
            action = 'a'
            this.target = target
            this.afterAttack = afterAttack
        } else {
            afterAttack(this, target)
            endAttack()
        }
    }

    fun dealDamage(damage: Float, direction: Int = Const.CENTER) {
        // TODO: Calculate damage
        if (direction != Const.CENTER) {
            moveDir = Vector2(Utils.dir2vec(direction))
            startAnim()
            animTime = Const.PUSH_TIME
            action = 'p'
        }
        // TODO: Show damage (number moving up)
        HP -= damage.toInt()
        if (HP <= 0) {
            onDeath()
            die()
        }
    }


    fun inAnim() : Boolean {
        return action != 'n'
    }

    private fun die() {
        // TODO: Play effect
        room.removeCreatureAt(x, y)
    }


    private fun update() {
        // Move creature
        if (inAnim()) {
            // Check if creature has reached the destination
            if (dTime >= animTime) {
                when (action) {
                    'm' -> endMove()
                    'a' -> endAttack()
                    'p' -> endAnim()
                }
            } else {
                align()
                dPos = Vector2(moveDir)
                dPos.scl(Const.TILE_SIZE * when (action) {
                    'm' -> moveFun(dTime / animTime)
                    'a' -> attackFun(dTime / animTime)
                    'p' -> pushFun(dTime / animTime)
                    else -> 0f
                })
                sprite.translate(dPos.x, dPos.y)
                dTime += graphics.deltaTime
                // Change look direction if necessary
                if (!rotated and (action != 'p') and (dTime >= Const.ROTATE_TIME * animTime)) {
                    changeSpriteDirection(Utils.vec2dir(moveDir))
                }
                // Attack creature if necessary
                if ((action == 'a') and (!attacked) and (dTime >= 0.5f * Const.ATTACK_TIME)) {
                    afterAttack(this, target!!)
                    attacked = true
                }
            }
        }
    }

    // Set sprite position to the center of the tile
    private fun align() {
        val xPos = x * Const.TILE_SIZE + Const.MAP_MARGIN_LEFT
        val yPos = y * Const.TILE_SIZE + Const.MAP_MARGIN_BOTTOM
        sprite.setPosition(xPos, yPos)
    }

    // Move function (x, y ~ (0; 1))
    private fun moveFun(x: Float) : Float {
        return 1 / (1 + exp(5 - 10 * x))
    }

    // Attack function (x, y ~ (0; 1))
    private fun attackFun(x: Float) : Float {
        return Const.ATTACK_D_S * (abs(4 * x - 2) - 2) * (abs(4 * x - 2) - 2) / 4
    }

    // Push function (x, y ~ (0; 1))
    private fun pushFun(x: Float) : Float {
        return -Const.PUSH_D_S / 2 * (abs(x - 0.5f) - 0.5f)
    }

    private fun startAnim() {
        ready = false
        dTime = 0f
        rotated = false
        attacked = false
    }

    open fun endMove() {
        x += moveDir.x.toInt()
        y += moveDir.y.toInt()
        align()
        endAnim()
        room.getTile(x, y).onStand(this)
    }

    open fun endAttack() {
        endAnim()
    }

    fun endAnim() {
        ready = true
        action = 'n'
        moveDir = Vector2.Zero
        align()
    }

    private fun changeSpriteDirection(dir: Int) {
        if (lookRight and (dir == Const.LEFT)) {
            sprite.setScale(-1f, 1f)
            lookRight = false
        } else if (!lookRight and (dir == Const.RIGHT)) {
            sprite.setScale(1f, 1f)
            lookRight = true
        }
    }
}