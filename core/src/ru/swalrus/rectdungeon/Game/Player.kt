package ru.swalrus.rectdungeon.Game

import ru.swalrus.rectdungeon.Const
import ru.swalrus.rectdungeon.Utils
import ru.swalrus.rectdungeon.Items.*

class Player (x: Int, y: Int, HP: Int, room: Room) : Creature(x, y, 6, Utils.getImg("human"), room) {

    var AP: Int = Const.MAX_AP
    var maxHP = HP
    var inventory: Array<Item?> = arrayOfNulls(Const.INVENTORY_SIZE)

    var weapon: Weapon? = null
    var armor: Armor? = null
    var stuff: Array<Artefact?> = arrayOfNulls(Const.ARTEFACT_SLOTS)
    var rightHand: Weapon? = ShortSword()
    var leftHand: Weapon? = Rapier()


    override fun act() {
        ready = false
        AP = Const.MAX_AP
    }

    override fun endMove() {
        super.endMove()
        endAction()
    }


    fun endTurn() {
        ready = true
    }


    fun swipe (dir: Int) {
        if (!ready and !super.inAnim()) {
            move(dir)
        }
    }

    fun onButtonTouch(id: Int) {
        when (id) {
            1 -> if (leftHand != null) {
                room.setYellowArea(x-1, y-1, leftHand!!.area, leftHand!!.target, leftHand!!.range)
            }
            2 -> if (rightHand != null) {
                room.setYellowArea(x-1, y-1, rightHand!!.area, rightHand!!.target, rightHand!!.range)
            }
        }
    }

    fun cast(id: Int, x: Int, y: Int) {
        when (id) {
            1 -> if (leftHand != null) {
                leftHand!!.cast(x, y, this, room.getCreatureAt(x, y))
            }
            2 -> if (rightHand != null) {
                rightHand!!.cast(x, y, this, room.getCreatureAt(x, y))
            }
        }
    }

    private fun endAction() {
        if (AP > 0) {
            ready = false
        } else {
            endTurn()
        }
    }


    fun addItem(item: Item) {
        if (null in inventory) {
            var i = 0
            while (inventory[i] != null) {
                i++
            }
            inventory[i] = item
        } else {
            // TODO: Написать в лог, что инвентарь полон, анимировать предмет
        }
    }

    ///////////////////////////////////////
    ////////        ACTIONS        ////////
    ///////////////////////////////////////

    fun makeAction(requiredAP: Int) : Boolean {
        if (AP >= requiredAP) {
            AP -= requiredAP
            return true
        } else {
            // TODO: Помигать ОД на панели
            return false
        }
    }

    override fun move(direction: Int, force: Boolean) {
        if (force or makeAction(1)) {
            super.move(direction, force)
        }
    }
}