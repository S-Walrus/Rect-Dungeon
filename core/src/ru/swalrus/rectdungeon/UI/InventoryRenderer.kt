package ru.swalrus.rectdungeon.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.Gdx.app
import com.badlogic.gdx.Graphics
import ru.swalrus.rectdungeon.Const
import ru.swalrus.rectdungeon.Game.Player
import ru.swalrus.rectdungeon.Items.Armor
import ru.swalrus.rectdungeon.Items.Weapon
import ru.swalrus.rectdungeon.Items.equippable
import ru.swalrus.rectdungeon.Utils

class InventoryRenderer (val player: Player, val card: ItemCard) {

    val MARGIN_LEFT = Const.INV_MARGIN_LEFT
    val MARGIN_BOTTOM = Const.INV_MARGIN_BOTTOM
    val PADDING = Const.INV_PADDING * Const.INV_SCALE
    val CELL_SIZE = Const.INV_CELL_SIZE * Const.INV_SCALE
    val STEP = (Const.INV_CELL_SIZE - 1) * Const.INV_SCALE
    val background = Utils.getImg("inventory")
    val SCALE = Const.INV_SCALE
    val WIDTH = SCALE * Const.INV_BACKGROUND_WIDTH
    val HEIGHT = SCALE * Const.INV_BACKGROUND_HEIGHT
    val SCREEN_HEIGHT = Const.SCREEN_HEIGHT
    val ROW = Const.INVENTORY_ROW_SIZE
    val new = Utils.getImg("item_new")
    val equipped = Utils.getImg("item_equipped")

    private var time: Float = 0f                                // Just a timer
    var opened = false


    fun draw(batch: SpriteBatch) {
        if (opened) {
            time += Gdx.graphics.deltaTime

            batch.draw(background, MARGIN_LEFT, MARGIN_BOTTOM, WIDTH, HEIGHT, 0f, 1f, 1f, 0f)

            // Draw the extra slot
            val extra = player.extraSlot
            if ((extra != null) && ((time / Const.EXTRA_SLOT_BLINK_TIME).toInt() % 2 == 0)) {
                batch.draw(extra.img, MARGIN_LEFT + PADDING + (ROW - 1) * STEP,
                        SCREEN_HEIGHT - MARGIN_BOTTOM - PADDING - (Const.INVENTORY_SIZE / ROW + 1) * STEP - 1 * SCALE,
                        CELL_SIZE, CELL_SIZE, 0f, 1f, 1f, 0f)
            }

            var i = 0
            for (item in player.inventory) {
                if (item != null) {
                    batch.draw(item.img, MARGIN_LEFT + PADDING + (i % ROW) * STEP,
                            SCREEN_HEIGHT - MARGIN_BOTTOM - PADDING - (i / ROW + 1) * STEP - 1 * SCALE,
                            CELL_SIZE, CELL_SIZE, 0f, 1f, 1f, 0f)
                    if (item is equippable && item.equipped) {
                        batch.draw(equipped, MARGIN_LEFT + PADDING + (i % ROW) * STEP,
                                SCREEN_HEIGHT - MARGIN_BOTTOM - PADDING - (i / ROW + 1) * STEP - 1 * SCALE,
                                CELL_SIZE, CELL_SIZE, 0f, 1f, 1f, 0f)
                    }
                    if (item.new) {
                        batch.draw(new, MARGIN_LEFT + PADDING + (i % ROW) * STEP,
                                SCREEN_HEIGHT - MARGIN_BOTTOM - PADDING - (i / ROW + 1) * STEP - 1 * SCALE,
                                CELL_SIZE, CELL_SIZE, 0f, 1f, 1f, 0f)
                    }
                    // TODO: Y клетки
                    i++
                }
            }

            time %= (10 * Const.EXTRA_SLOT_BLINK_TIME)
        }
    }

    // Open/close
    fun switch() {
        if (opened) {
            close()
        } else {
            open()
        }
    }

    fun open() {
        opened = true
    }

    fun close() {
        opened = false
        for (item in player.inventory) {
            item?.new = false
        }
    }

    // Called when a slot at (x, y) is pressed
    // Opens an item card
    fun press(x: Int, y: Int) {
        press(y * ROW + x)
    }

    // Called when the slot number 'i' is pressed
    // Opens an item card
    fun press(i: Int) {
        if (i < Const.INVENTORY_SIZE) {
            var c = 0
            var q = 0
            while (c != i && q < Const.INVENTORY_SIZE || player.inventory[q] == null) {
                if (player.inventory[q] != null) {
                    c++
                }
                q++
            }
            if (q < Const.INVENTORY_SIZE) {
                val item = player.inventory[q]
                if (item != null) {
                    card.open(item)
                }
            }
        }
    }
}