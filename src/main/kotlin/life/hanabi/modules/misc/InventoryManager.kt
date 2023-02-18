package life.hanabi.modules.misc

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*
import net.minecraft.potion.Potion
import net.minecraft.util.EnumChatFormatting
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class InventoryManager(name: String) : Module(name, ModuleCategory.Misc) {
    private val toolsValue = BooleanValue("Keep Tools", true)
    private val archeryValue = BooleanValue("Archery", true)
    private val invValue = BooleanValue("Inventory Only", false)
    private val delayValue = NumberValue("Delay", 80.0, 10.0, 1000.0, 100.0)
    var timer = TimerUtil()

    init {
        addValues(toolsValue, archeryValue, invValue, delayValue)
    }

    override fun onEnable() {
        timer.reset()
        super.onEnable()
    }

    override fun onDisable() {
        timer.reset()
        super.onDisable()
    }

    @EventTarget
    fun onUpdate(event: EventMotion) {
        setSuffix(delayValue.value.toString() + "")
        if (mc.currentScreen is GuiChest || invValue.value && mc.currentScreen !is GuiContainer) return
        val realdelay = delayValue.value.toDouble()
        val delay = Math.max(20.0, realdelay + ThreadLocalRandom.current().nextDouble(-40.0, 40.0))

        if (timer.hasReached(delay.toLong().toDouble())) {
            invManager(delay)
            timer.reset()
        }
    }

    private fun invManager(delay: Double) {
        var bestSword = -1
        var bestDamage = 1f
        for (k in mc.thePlayer.inventory.mainInventory.indices) {
            val item = mc.thePlayer.inventory.mainInventory[k] ?: continue
            if (item.item !is ItemSword) continue
            val `is` = item.item as ItemSword
            var damage = `is`.damageVsEntity

            damage += (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, item) * 1.26f + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, item) * 0.01f)

            if (damage > bestDamage) {
                bestDamage = damage
                bestSword = k
            }
        }

        if (bestSword != -1 && bestSword != 0) {
            for (i in mc.thePlayer.inventoryContainer.inventorySlots.indices) {
                val s = mc.thePlayer.inventoryContainer.inventorySlots[i]
                if (!(s.hasStack && s.stack == mc.thePlayer.inventory.mainInventory[bestSword])) continue
                val slot = 0

                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, s.slotNumber, slot, 2, mc.thePlayer)
                timer.reset()
                return
            }
        }
        moveGapToHotBar()
        moveBlocks()

        for (i in 9..44) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) continue
            val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
            if (shouldDrop(`is`, i) && timer.hasReached(delay.toLong().toDouble())) {
                this.drop(i)
                timer.reset()
                break
            }
        }
    }

    fun drop(slot: Int) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer)
    }

    fun shouldDrop(`is`: ItemStack, k: Int): Boolean {
        val bestSword = swordSlot
        if (`is`.item is ItemSword) {
            if (bestSword != -1 && bestSword != k) {
                return true
            }
        }
        val bestPick = pickaxeSlot
        if (`is`.item is ItemPickaxe) {
            if (!toolsValue.value) return true
            if (bestPick != -1 && bestPick != k) return true
        }
        val bestAxe = axeSlot
        if (`is`.item is ItemAxe) {
            if (!toolsValue.value) return true
            if (bestAxe != -1 && bestAxe != k) return true
        }
        val bestShovel = shovelSlot
        if (isShovel(`is`.item)) {
            if (!toolsValue.value) return true
            if (bestShovel != -1 && bestShovel != k) return true
        }
        return isBad(`is`)
    }

    fun isBad(item: ItemStack): Boolean {
        return (!(item.item is ItemArmor || item.item is ItemTool || item.item is ItemBlock || item.item is ItemSword || item.item is ItemEnderPearl || item.item is ItemFood || item.item is ItemPotion && !isBadPotion(item)) && !item.displayName.lowercase(Locale.getDefault()).contains(EnumChatFormatting.GRAY.toString() + "(right click)"))
    }

    fun isBadPotion(stack: ItemStack): Boolean {
        if (stack.item !is ItemPotion) return false
        val potion = stack.item as ItemPotion
        if (!ItemPotion.isSplash(stack.itemDamage)) return false
        for (o in potion.getEffects(stack)) {
            if (o.potionID == Potion.poison.getId() || o.potionID == Potion.harm.getId() || o.potionID == Potion.moveSlowdown.getId() || o.potionID == Potion.weakness.getId()) {
                return true
            }
        }
        return false
    }

    fun moveGapToHotBar() {
        var added = false
        if (emptySlot != -1) {
            for (k in mc.thePlayer.inventory.mainInventory.indices) {
                if (k > 8 && !added) {
                    val itemStack = mc.thePlayer.inventory.mainInventory[k]
                    if (itemStack != null && !(mc.thePlayer.inventory.getStackInSlot(1) != null && mc.thePlayer.inventory.getStackInSlot(1).item is ItemAppleGold)) {
                        if (itemStack.item is ItemAppleGold) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, k, 1, 2, mc.thePlayer)
                            added = true
                        }
                    }
                }
            }
        }
    }

    fun moveBlocks() {
        var added = false
        if (emptySlot == -1) return
        for (k in mc.thePlayer.inventory.mainInventory.indices) {
            if (!(k > 8 && !added)) continue
            val itemStack = mc.thePlayer.inventory.mainInventory[k] ?: continue
            if (itemStack.item !is ItemBlock) continue
            shiftClickSlot(k)
            added = true
        }
    }

    val emptySlot: Int
        get() {
            for (k in 0..8) {
                if (mc.thePlayer.inventory.mainInventory[k] == null) {
                    return k
                }
            }
            return -1
        }

    val swordSlot: Int
        get() {
            if (mc.thePlayer == null) {
                return -1
            }
            var bestSword = -1
            var bestDamage = 1f
            for (i in 9..44) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) continue
                val item = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
                if (item.item !is ItemSword) continue
                val `is` = item.item as ItemSword
                var damage = `is`.damageVsEntity
                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, item) * 1.26f
                damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, item) * 0.01f
                if (damage > bestDamage) {
                    bestDamage = damage
                    bestSword = i
                }
            }
            return bestSword
        }
    val pickaxeSlot: Int
        get() {
            if (mc.thePlayer == null) {
                return -1
            }
            var bestSword = -1
            var bestDamage = 1f
            for (i in 9..44) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) continue
                val item = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
                if (item.item !is ItemPickaxe) continue
                val `is` = item.item as ItemPickaxe
                val damage = `is`.getStrVsBlock(item, Block.getBlockById(4))
                if (damage > bestDamage) {
                    bestDamage = damage
                    bestSword = i
                }
            }
            return bestSword
        }
    val axeSlot: Int
        get() {
            if (mc.thePlayer == null) {
                return -1
            }
            var bestSword = -1
            var bestDamage = 1f
            for (i in 9..44) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) continue
                val item = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
                if (item.item !is ItemAxe) continue
                val `is` = item.item as ItemAxe
                val damage = `is`.getStrVsBlock(item, Block.getBlockById(17))
                if (damage > bestDamage) {
                    bestDamage = damage
                    bestSword = i
                }
            }
            return bestSword
        }
    val shovelSlot: Int
        get() {
            if (mc.thePlayer == null) {
                return -1
            }
            var bestSword = -1
            var bestDamage = 1f
            for (i in 9..44) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) continue
                val item = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
                if (item.item !is ItemTool) continue
                val itemTool = item.item as ItemTool
                if (!isShovel(itemTool)) continue
                val damage = itemTool.getStrVsBlock(item, Block.getBlockById(3))
                if (damage > bestDamage) {
                    bestDamage = damage
                    bestSword = i
                }
            }
            return bestSword
        }

    fun shiftClickSlot(slotId: Int) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, 0, 1, mc.thePlayer)
    }

    companion object {
        fun isShovel(item: Item): Boolean {
            val ids = intArrayOf(256, 269, 273, 277, 284)
            for (id in ids) {
                if (Item.getItemById(id) != item) continue
                return true
            }
            return false
        }
    }
}