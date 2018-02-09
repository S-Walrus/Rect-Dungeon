package ru.swalrus.rectdungeon.Game

import com.badlogic.gdx.Gdx.app
import ru.swalrus.rectdungeon.Const
import ru.swalrus.rectdungeon.Utils

class Wall(direction: Int) : Tile(Utils.getImg("wall", direction), passable = false)

class Floor : Tile(Utils.getImg("floor"))

class EmptyTile : Tile(Utils.getImg("empty"), passable = false)


class Door(val direction: Int, val room: Room) : Tile(Utils.getImg("door", direction), passable = false) {

    override fun onStand(creature: Creature) {
        if (creature is Player) {
            room.chunk.move(direction)
            room.removeCreatureAt(creature.x, creature.y)
            creature.room = room.chunk.Center
            room.chunk.Center.addCreature(creature)
            room.chunk.Center.setFocusToPlayer()
            creature.act()
            when (direction) {
                Const.TOP -> {
                    creature.x = Const.ROOM_SIZE / 2 + 1
                    creature.y = 0
                }
                Const.BOTTOM -> {
                    creature.x = Const.ROOM_SIZE / 2 + 1
                    creature.y = Const.ROOM_SIZE + 1
                }
                Const.LEFT -> {
                    creature.x = Const.ROOM_SIZE + 1
                    creature.y = Const.ROOM_SIZE / 2 + 1
                }
                Const.RIGHT -> {
                    creature.x = 0
                    creature.y = Const.ROOM_SIZE / 2 + 1
                }
            }
            creature.move(direction, true)
            // TODO: Закрываем двери
        }
    }
}


class Lava : Tile(Utils.getImg("lava")) {

    override fun onStand(creature: Creature) {
        creature.dealDamage(2f)
    }
}