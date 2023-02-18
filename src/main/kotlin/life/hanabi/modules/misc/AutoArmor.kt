package life.hanabi.modules.misc

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.misc.EventTick
import life.hanabi.utils.MovementUtils
import life.hanabi.utils.math.TimerUtil
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import java.util.*

internal enum class ArmorType {
    BOOTS, LEGGINGS, CHEST_PLATE, HELMET
}

class AutoArmor(name: String) : Module(name, ModuleCategory.Misc) {
    private val timer = TimerUtil()
    private val glitchFixer = TimerUtil()

    // idk why this not same name as InvCleaner
    private val openInv = BooleanValue("Sort In Inv", "Sort In Inv", false)
    private val noMove = BooleanValue("No Move", "No Move", false)
    private val delay = NumberValue("Delay", "Delay", 150.0, 0.0, 1000.0, 50.0)

    init {
        addValues(openInv, noMove, delay)
    }

    @EventTarget
    fun onTick(event: EventTick) {
        if (noMove.value && MovementUtils.isMoving()) return
        if (openInv.value) {
            if (mc.currentScreen !is GuiInventory) return
        } else {
            // Glitch Fix
            if (mc.currentScreen != null) glitchFixer.reset()
        }
        if (mc.thePlayer.capabilities.isCreativeMode || mc.currentScreen != null && !openInv.value) {
            timer.reset()
            return
        }
        var slot: Int
        for (armorType in ArmorType.values()) {
            if (findArmor(armorType, getArmorScore(mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal))).also { slot = it } != -1) {
                // set
                isDone = false
                if (mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal) != null) {
                    dropArmor(armorType.ordinal)
                    timer.reset()
                    return
                }
                warmArmor(slot)
                timer.reset()
                return
            } else {
                isDone = true
            }
        }
    }

    private fun findArmor(armorType: ArmorType, minimum: Float): Int {
        var best = 0f
        var result = -1
        for (i in mc.thePlayer.inventory.mainInventory.indices) {
            val itemStack = mc.thePlayer.inventory.mainInventory[i]
            if (getArmorScore(itemStack) < 0 || getArmorScore(itemStack) <= minimum || getArmorScore(itemStack) < best || !isValid(armorType, itemStack)) continue

            best = getArmorScore(itemStack)
            result = i
        }
        return result
    }

    private fun isValid(type: ArmorType, itemStack: ItemStack): Boolean {
        if (itemStack.item !is ItemArmor) return false
        val armor = itemStack.item as ItemArmor
        if (type == ArmorType.HELMET && armor.armorType == 0) return true
        if (type == ArmorType.CHEST_PLATE && armor.armorType == 1) return true
        return if (type == ArmorType.LEGGINGS && armor.armorType == 2) true else type == ArmorType.BOOTS && armor.armorType == 3
    }

    private fun warmArmor(slot_In: Int) {
        if (slot_In in 0..8) { // 0-8 is hotbar
            clickSlot(slot_In + 36, 0, true)
        } else {
            clickSlot(slot_In, 0, true)
        }
    }

    private fun clickSlot(slot: Int, mouseButton: Int, shiftClick: Boolean) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, if (shiftClick) 1 else 0, mc.thePlayer)
    }

    private fun dropArmor(armorSlot: Int) {
        val slot = armorSlotToNormalSlot(armorSlot)
        if (!isFull) {
            clickSlot(slot, 0, true)
        } else {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer)
        }
    }

    companion object {
        var isDone = false
        fun getArmorScore(itemStack: ItemStack): Float {
            if (itemStack.item !is ItemArmor) return -1f
            val itemArmor = itemStack.item as ItemArmor
            var score = 0f

            //basic reduce amount
            score += itemArmor.damageReduceAmount

            if (EnchantmentHelper.getEnchantments(itemStack).isEmpty()) score -= 0.1f

            val protection = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack)
            score += protection * 0.2f
            return score
        }

        fun armorSlotToNormalSlot(armorSlot: Int): Int {
            return 8 - armorSlot
        }

        val isFull: Boolean
            get() = !listOf(*mc.thePlayer.inventory.mainInventory).contains(null)
    }
}