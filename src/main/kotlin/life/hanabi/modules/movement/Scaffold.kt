package life.hanabi.modules.movement

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventMove
import life.hanabi.utils.MathUtils
import life.hanabi.utils.MovementUtils
import life.hanabi.utils.RotationUtil
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.*
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList
import net.minecraft.util.*
import net.minecraft.util.MathHelper.atan2
import net.minecraft.util.MathHelper.sqrt_double
import org.apache.commons.lang3.RandomUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

@IIiIIiiiIiii
class Scaffold(name: String) : Module(name, ModuleCategory.World) {
    //BUILD
    private val delay = NumberValue("Place Delay", "Place Delay", 0.0, 0.0, 500.0, 10.0)
    private val diagonal = BooleanValue("Diagonal", "Diagonal", true)
    private val silent = BooleanValue("Slient", "Slient", true)
    private val noSwing = BooleanValue("No Swing", "No Swing", true)

    //MOVEMENT
    private val safeWalk = BooleanValue("Safe Walk", "Safe Walk", true)
    private val onlyGround = BooleanValue("Only Ground", "Only Ground", true)
    private val sprint = BooleanValue("Sprint", "Sprint", true)
    private val sneak = BooleanValue("Sneak", "Sneak", true)
    private val jump = BooleanValue("AutoJump", "AutoJump", true)
    private val speedlimit = NumberValue("Move Motify", "Move Motify", 1.0, 0.6, 1.2, 0.01)

    //RAYCAST
    private val rayCast = BooleanValue("Ray Cast", "Ray Cast", true)

    //RENDER
    private val render = BooleanValue("ESP", "ESP", true)

    //OTHER
    private val sneakAfter = NumberValue("Sneak Tick", "Sneak Tick", 1, 1, 10, 1)
    private val packetSneak = BooleanValue("packetSneak", "packetSneak", false)
    private val packetSpirnt = BooleanValue("packetSpirnt", "packetSpirnt", false)
    private val moveTower = BooleanValue("Move Tower", "Move Tower", true)
    private val timer = NumberValue("Timer Speed", "Timer Speed", 1f, 0.1f, 5f, 0.01f)

    //ROTATE
    private val turnspeed = NumberValue("Rotation Speed", "Rotation Speed", 6f, 1f, 6f, 0.5f)
    var curYaw = 0f
    var curPitch = 0f
    var rotate: Vec3i? = null
    var angles: FloatArray? = null
    private var startPosY = 0.0

    //Sneak
    var sneakCount = 0

    //Slot
    var slot = 0

    //Other
    private val lastPlacement: BlockData? = null
    private var data: BlockData? = null
    private var slowTicks = 0

    //Facing
    var enumFacing: EnumFacing? = null

    //Timer
    private var timeHelper = TimerUtil()

    //Tower
    var istower = false
    var jumpGround = 0.0

    //BlackList
    var blackList: List<Block> = listOf(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.gravel, Blocks.ender_chest, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence, Blocks.redstone_torch)

    init {
        addValues(delay, diagonal, silent, noSwing, safeWalk, onlyGround, sprint, sneak, jump, speedlimit, rayCast, render, sneakAfter, packetSneak, packetSpirnt, moveTower, timer, turnspeed)
    }

    fun faceBlock(pos: BlockPos, yTranslation: Float, currentYaw: Float, currentPitch: Float, speed: Float): FloatArray {
        val x = pos.x + 0.5f - mc.thePlayer.posX - mc.thePlayer.motionX
        val y = pos.y - yTranslation - (mc.thePlayer.posY + mc.thePlayer.eyeHeight)
        val z = pos.z + 0.5f - mc.thePlayer.posZ - mc.thePlayer.motionZ
        val calculate = sqrt_double(x * x + z * z).toDouble()
        val calcYaw = (atan2(z, x) * 180f / Math.PI).toFloat() - 90.0f
        val calcPitch = -(atan2(y, calculate) * 180f / Math.PI).toFloat()
        var yaw = updateRotation(currentYaw, calcYaw, speed)
        var pitch = updateRotation(currentPitch, calcPitch, speed)
        val sense = mc.gameSettings.mouseSensitivity * 0.8f + 0.2f
        val fix = sense.pow(3f) * 1.5f
        yaw -= yaw % fix
        pitch -= pitch % fix
        return floatArrayOf(yaw, if (pitch >= 90f) 90f else if (pitch <= -90f) -90f else pitch)
    }

    fun updateRotation(curRot: Float, destination: Float, speed: Float): Float {
        var f = MathHelper.wrapAngleTo180_float(destination - curRot)
        if (f > speed) {
            f = speed
        }
        if (f < -speed) {
            f = -speed
        }
        return curRot + f
    }

    //todo: safewalk
    //    @EventTarget
    //    private void onSafe(EventSafeWalk e) {
    //        if (safeWalk.getValue())
    //            e.setSafe(mc.thePlayer.onGround || !onlyGround.getValue());
    //    }
    @EventTarget
    private fun onUpdateR(event: EventMotion) {
        if (!event.isPre) return
        if (!mc.gameSettings.keyBindJump.isKeyDown) {
            var rot = 0f
            if (mc.thePlayer.movementInput.moveForward > 0f) {
                rot = 180f
                if (mc.thePlayer.movementInput.moveStrafe > 0f) {
                    rot = -120f
                } else if (mc.thePlayer.movementInput.moveStrafe < 0f) {
                    rot = 120f
                }
            } else if (mc.thePlayer.movementInput.moveForward == 0f) {
                rot = 180f
                if (mc.thePlayer.movementInput.moveStrafe > 0f) {
                    rot = -90f
                } else if (mc.thePlayer.movementInput.moveStrafe < 0f) {
                    rot = 90f
                }
            } else if (mc.thePlayer.movementInput.moveForward < 0f) {
                if (mc.thePlayer.movementInput.moveStrafe > 0f) {
                    rot = -45f
                } else if (mc.thePlayer.movementInput.moveStrafe < 0f) {
                    rot = 45f
                }
            }
            event.setYaw(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - rot.also { mc.thePlayer.renderYawOffset = it }.also { mc.thePlayer.rotationYawHead = it })
            event.setPitch(87.5f)
        } else {
            event.setYaw(curYaw.also { mc.thePlayer.renderYawOffset = it }.also { mc.thePlayer.rotationYawHead = it })
            event.setPitch(88f)
        }
    }

    private fun getRotByFaceFlick(rotationYaw: Float): Float {
        var rotationYaw = rotationYaw
        if (data == null) return 0f

        // TODO: THESE ARE NONE POSSIBLE ROTATIONS / THEY BAN ON ANTICHEATS -- FIX THEM
        if (data!!.face.getName().equals("north", ignoreCase = true)) rotationYaw = -402.21146f
        if (data!!.face.getName().equals("south", ignoreCase = true)) rotationYaw = 151.19199f
        if (data!!.face.getName().equals("west", ignoreCase = true)) rotationYaw = -152.41011f
        if (data!!.face.getName().equals("east", ignoreCase = true)) rotationYaw = 151.1411f
        return rotationYaw
    }

    @EventTarget
    private fun onPre(event: EventMotion) {
        if (!event.isPre) return
        val blockUnder = blockUnder
        data = blockData

        if (data == null) return
        if (data == null) data = getBlockData(blockUnder.offset(EnumFacing.DOWN))
        if (data != null) {
            // If ray trace fails hit vec will be null
            if (validateReplaceable(data!!) && data!!.hitVec != null) {
                // Calculate rotations to hit vec
                angles = RotationUtil.getRotations(floatArrayOf(mc.thePlayer.lastReportedYaw, mc.thePlayer.lastReportedPitch), 15.5f, RotationUtil.getHitOrigin(mc.thePlayer), data!!.hitVec)
                curYaw = getRotByFaceFlick(0f)
            }
        }
        if (rotate != null) {
            if (angles == null || lastPlacement == null) {
                // Get the last rotations (EntityPlayerSP#rotationYaw/rotationPitch)
                val lastAngles = if (angles != null) angles else floatArrayOf(event.getYaw(), event.getPitch())
                // Get the opposite direct that you are moving
                val moveDir = movementDirection
                // Desired rotations
                val dstRotations =
                    floatArrayOf(moveDir + MathUtils.getRandomInRange(178, 180), 87.5f + randomHypixelValuesFloat)
                // Smooth to opposite
                RotationUtil.applySmoothing(lastAngles, 15.5f, dstRotations)
                // Apply GCD fix (just for fun)
                // RotationUtil.applyGCD(dstRotations, lastAngles);
                angles = dstRotations
            }
        }

        // Set rotations to persistent rotations
    }

    private fun validateReplaceable(data: BlockData): Boolean {
        val pos = data.pos.offset(data.face)

        return mc.theWorld.getBlockState(pos).block.isReplaceable(mc.theWorld, pos)
    }

    @EventTarget
    private fun onPacket(e: EventPacketReceived) {
        if (e.packet is C09PacketHeldItemChange) {
            val c09 = e.packet as C09PacketHeldItemChange
            if (slot != c09.slotId) slot = c09.slotId
        }
    }

    fun isAirBlock(block: Block): Boolean {
        return if (block.material.isReplaceable) { block !is BlockSnow || block.getBlockBoundsMaxY() <= 0.125 } else false
    }

    private val blockUnder: BlockPos
        get() {
            return if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                val air = isAirBlock(mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, Math.min(startPosY, mc.thePlayer.posY) - 1, mc.thePlayer.posZ)).block)

                BlockPos(mc.thePlayer.posX, Math.min(startPosY, mc.thePlayer.posY) - 1, if (air) mc.thePlayer.posZ else mc.thePlayer.posZ)
            } else {
                startPosY = mc.thePlayer.posY

                BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
            }
        }

    @EventTarget
    fun onMove(event: EventMove) {
        if (mc.thePlayer.onGround) {
            event.x = speedlimit.value.let { mc.thePlayer.motionX *= it; mc.thePlayer.motionX }
            event.z = speedlimit.value.let { mc.thePlayer.motionZ *= it; mc.thePlayer.motionZ }
        }
    }

    @EventTarget
    private fun onUpdate(e: EventMotion) {
        if (!e.isPost) return
        val blockPos = getBlockPosToPlaceOn(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ))
        if (blockPos != null) rotate = translate(blockPos, enumFacing)
        mc.timer.timerSpeed = (timer.value + Math.random() / 100f).toFloat()
        if (silent.value) {
            slot = blockSlot
            if (mc.thePlayer.inventory.currentItem != blockSlot) {
                if (blockSlot == -1) return
                mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(blockSlot))
            }
        }
        val itemStack = mc.thePlayer.inventory.getStackInSlot(slot)
        if (blockPos != null && itemStack != null && itemStack.item is ItemBlock) {
            items = itemStack
            mc.thePlayer.isSprinting = sprint.value
            mc.gameSettings.keyBindSprint.pressed = sprint.value
            if (sprint.value) mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))

            if (sprint.value && sneakCount >= sneakAfter.value) if (packetSpirnt.value) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
            } else {
                mc.gameSettings.keyBindSprint.pressed = true
            } else if (sneakCount < sneakAfter.value) if (packetSpirnt.value) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
            } else {
                mc.gameSettings.keyBindSprint.pressed = false
            }
            if (sneak.value && sneakCount >= sneakAfter.value) if (packetSneak.value) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
            } else {
                mc.gameSettings.keyBindSneak.pressed = true
            } else if (sneakCount < sneakAfter.value) if (packetSneak.value) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
            } else {
                mc.gameSettings.keyBindSneak.pressed = false
            }

            val rotation = faceBlock(blockPos, (mc.theWorld.getBlockState(blockPos).block.blockBoundsMaxY - mc.theWorld.getBlockState(blockPos).block.blockBoundsMinY).toFloat() + 0.5f, curYaw, curPitch, turnspeed.value * 30f)
            curYaw = rotation[0]
            curPitch = rotation[1]
            curYaw = getRotByFaceFlick(0f)
            val ray = rayCastedBlock(curYaw, curPitch)
            if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                if (jump.value) {
                    mc.thePlayer.motionY = 0.3544999999
                }
            }
            val hitVec = if (ray != null) ray.hitVec else Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
            
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, blockPos, enumFacing, hitVec)) {
                sneakCount++
                slowTicks = 3
                if (sneakCount > sneakAfter.value.toInt()) sneakCount = 0
                if (!noSwing.value) mc.thePlayer.swingItem() else mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
               
                timeHelper.reset()
            }
            // tower
            if (jumpEffect == 0) {
                if (mc.thePlayer.movementInput.jump) { // if Scaffolded to UP
                    if (isOnGround(0.15) && (moveTower.value || !MovementInput())) {
                        if (mc.gameSettings.keyBindJump.isKeyDown) {
                            mc.timer.timerSpeed = 1f
                            // different tower mode
                            istower = true
                            mc.thePlayer.motionX *= 0.6
                            mc.thePlayer.motionZ *= 0.6
                            mc.thePlayer.motionY = 0.41999976
                        }
                    }
                }
            }
        }
    }

    override fun onEnable() {
        sneakCount = 0
        curYaw = mc.thePlayer.rotationYaw
        curPitch = mc.thePlayer.rotationPitch
        slot = mc.thePlayer.inventory.currentItem
    }

    override fun onDisable() {
        if (silent.value && slot != mc.thePlayer.inventory.currentItem) mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem.also { slot = it }))
        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.gameSettings.keyBindSneak.pressed = false
        }
        if (packetSneak.value) mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
       
        if (mc.timer.timerSpeed != 1f) mc.timer.timerSpeed = 1.0f
        
        angles = null
    }

    private fun getBlockPosToPlaceOn(pos: BlockPos): BlockPos? {
        val blockPos1 = pos.add(-1, 0, 0)
        val blockPos2 = pos.add(1, 0, 0)
        val blockPos3 = pos.add(0, 0, -1)
        val blockPos4 = pos.add(0, 0, 1)
        val down = 0f
        if (mc.theWorld.getBlockState(pos.add(0.0, -1.0 - down, 0.0)).block != Blocks.air) {
            enumFacing = EnumFacing.UP
            return (pos.add(0, -1, 0))
        } else if (mc.theWorld.getBlockState(pos.add(-1.0, 0.0 - down, 0.0)).block != Blocks.air) {
            enumFacing = EnumFacing.EAST
            return (pos.add(-1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(pos.add(1.0, 0.0 - down, 0.0)).block != Blocks.air) {
            enumFacing = EnumFacing.WEST
            return (pos.add(1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(pos.add(0.0, 0.0 - down, -1.0)).block != Blocks.air) {
            enumFacing = EnumFacing.SOUTH
            return (pos.add(0.0, 0.0 - down, -1.0))
        } else if (mc.theWorld.getBlockState(pos.add(0.0, 0.0 - down, 1.0)).block != Blocks.air) {
            enumFacing = EnumFacing.NORTH
            return (pos.add(0.0, 0.0 - down, 1.0))
        } else if (mc.theWorld.getBlockState(blockPos1.add(0.0, -1.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.UP
            return (blockPos1.add(0.0, -1.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos1.add(-1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.EAST
            return (blockPos1.add(-1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos1.add(1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.WEST
            return (blockPos1.add(1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos1.add(0.0, 0.0 - down, -1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.SOUTH
            return (blockPos1.add(0.0, 0.0 - down, -1.0))
        } else if (mc.theWorld.getBlockState(blockPos1.add(0.0, 0.0 - down, 1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.NORTH
            return (blockPos1.add(0.0, 0.0 - down, 1.0))
        } else if (mc.theWorld.getBlockState(blockPos2.add(0.0, -1.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.UP
            return (blockPos2.add(0.0, -1.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos2.add(-1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.EAST
            return (blockPos2.add(-1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos2.add(1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.WEST
            return (blockPos2.add(1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos2.add(0.0, 0.0 - down, -1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.SOUTH
            return (blockPos2.add(0.0, 0.0 - down, -1.0))
        } else if (mc.theWorld.getBlockState(blockPos2.add(0.0, 0.0 - down, 1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.NORTH
            return (blockPos2.add(0.0, 0.0 - down, 1.0))
        } else if (mc.theWorld.getBlockState(blockPos3.add(0.0, -1.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.UP
            return (blockPos3.add(0.0, -1.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos3.add(-1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.EAST
            return (blockPos3.add(-1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos3.add(1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.WEST
            return (blockPos3.add(1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos3.add(0.0, 0.0 - down, -1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.SOUTH
            return (blockPos3.add(0.0, 0.0 - down, -1.0))
        } else if (mc.theWorld.getBlockState(blockPos3.add(0.0, 0.0 - down, 1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.NORTH
            return (blockPos3.add(0.0, 0.0 - down, 1.0))
        } else if (mc.theWorld.getBlockState(blockPos4.add(0.0, -1.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.UP
            return (blockPos4.add(0.0, -1.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos4.add(-1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.EAST
            return (blockPos4.add(-1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos4.add(1.0, 0.0 - down, 0.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.WEST
            return (blockPos4.add(1.0, 0.0 - down, 0.0))
        } else if (mc.theWorld.getBlockState(blockPos4.add(0.0, 0.0 - down, -1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.SOUTH
            return (blockPos4.add(0.0, 0.0 - down, -1.0))
        } else if (mc.theWorld.getBlockState(blockPos4.add(0.0, 0.0 - down, 1.0)).block != Blocks.air && diagonal.value) {
            enumFacing = EnumFacing.NORTH
            return (blockPos4.add(0.0, 0.0 - down, 1.0))
        }
        return null
    }

    private fun fakeJump() {
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
    }

    private fun getBlockData(pos: BlockPos): BlockData? {
        val facings = FACINGS

        // 1 of the 4 directions around player
        for (facing: EnumFacing in facings) {
            val blockPos = pos.add(facing.opposite.directionVec)
            if (validateBlock(mc.theWorld.getBlockState(blockPos).block, BlockAction.PLACE_ON)) {
                val data = BlockData(blockPos, facing)
                if (validateBlockRange(data)) return data
            }
        }

        // 2 Blocks Under e.g. When jumping
        val posBelow = pos.add(0, -1, 0)
        if (validateBlock(mc.theWorld.getBlockState(posBelow).block, BlockAction.PLACE_ON)) {
            val data = BlockData(posBelow, EnumFacing.UP)
            if (validateBlockRange(data)) return data
        }

        // 2 Block extension & diagonal
        for (facing: EnumFacing in facings) {
            val blockPos = pos.add(facing.opposite.directionVec)
            for (facing1: EnumFacing in facings) {
                val blockPos1 = blockPos.add(facing1.opposite.directionVec)
                if (validateBlock(mc.theWorld.getBlockState(blockPos1).block, BlockAction.PLACE_ON)) {
                    val data = BlockData(blockPos1, facing1)
                    if (validateBlockRange(data)) return data
                }
            }
        }
        return null
    }

    //todo:sameY
//        if (sameY && ModuleManager.modules.get("Speed").stage || sprintValue.getCurrent().equalsIgnoreCase("Hypixel") && !mc.gameSettings.keyBindJump.isKeyDown()) {
//            playerpos = new BlockPos(new Vec3(mc.thePlayer.getPositionVector().xCoord, this.startY, mc.thePlayer.getPositionVector().zCoord)).offset(EnumFacing.DOWN);
//        } else {
//            this.startY = mc.thePlayer.posY;
//        }
    val blockData: BlockData?
        get() {
            val invert = arrayOf(EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST)
            var yValue = 0.0
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode) && !mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, false)
                yValue -= 1.0
            }
            val playerpos = BlockPos(mc.thePlayer.positionVector).offset(EnumFacing.DOWN).add(0.0, yValue, 0.0)

            //todo:sameY
            //        if (sameY && ModuleManager.modules.get("Speed").stage || sprintValue.getCurrent().equalsIgnoreCase("Hypixel") && !mc.gameSettings.keyBindJump.isKeyDown()) {
            //            playerpos = new BlockPos(new Vec3(mc.thePlayer.getPositionVector().xCoord, this.startY, mc.thePlayer.getPositionVector().zCoord)).offset(EnumFacing.DOWN);
            //        } else {
            //            this.startY = mc.thePlayer.posY;
            //        }
            val facingVals = EnumFacing.values()
            for (facingVal: EnumFacing in facingVals) {
                if (mc.theWorld.getBlockState(playerpos.offset(facingVal)).block.material != Material.air) {
                    return BlockData(playerpos.offset(facingVal), invert[facingVal.ordinal])
                }
            }
            val addons = arrayOf(BlockPos(-1, 0, 0), BlockPos(1, 0, 0), BlockPos(0, 0, -1), BlockPos(0, 0, 1))
            val length2 = addons.size
            var j = 0
            while (j < length2) {
                val offsetPos = playerpos.add(addons[j].x, 0, addons[j].z)
                if (mc.theWorld.getBlockState(offsetPos).block !is BlockAir) {
                    ++j
                    continue
                }
                for (k in EnumFacing.values().indices) {
                    if (mc.theWorld.getBlockState(offsetPos.offset(EnumFacing.values()[k])).block.material == Material.air) continue
                    return BlockData(offsetPos.offset(EnumFacing.values()[k]), invert[EnumFacing.values()[k].ordinal])
                }
                ++j
            }
            return null
        }

    enum class BlockAction {
        PLACE, REPLACE, PLACE_ON
    }

    private fun validateBlockRange(data: BlockData): Boolean {
        val pos = data.hitVec ?: return false
        val player = mc.thePlayer
        val x = (pos.xCoord - player.posX)
        val y = (pos.yCoord - (player.posY + player.eyeHeight))
        val z = (pos.zCoord - player.posZ)
        val reach = mc.playerController.blockReachDistance
        return Math.sqrt((x * x) + (y * y) + (z * z)) <= reach
    }

    private val blockSlot: Int
        get() {
            var slot = -1
            for (i in 0..8) {
                val itemStack = mc.thePlayer.inventory.mainInventory[i]
                if ((itemStack == null) || itemStack.item !is ItemBlock || (itemStack.stackSize < 1)) continue
                val block = itemStack.item as ItemBlock
                if (blackList.contains(block.block)) continue
                slot = i
                break
            }
            return slot
        }

    class BlockData(val pos: BlockPos, val face: EnumFacing) {
        val hitVec: Vec3?

        init {
            hitVec = calculateBlockData()
        }

        private fun calculateBlockData(): Vec3? {
            val directionVec = face.directionVec
            var x: Double
            var z: Double
            when (face.axis) {
                EnumFacing.Axis.Z -> {
                    val absX = abs(mc.thePlayer.posX)

                    //TODO: ????
                    var xOffset = absX - absX.toInt()

                    if (mc.thePlayer.posX < 0) {
                        xOffset = 1.0f - xOffset
                    }
                    x = directionVec.x * xOffset
                    z = directionVec.z * xOffset
                }

                EnumFacing.Axis.X -> {
                    val absZ = abs(mc.thePlayer.posZ)

                    //TODO: ????
                    var zOffset = absZ - absZ.toInt()

                    if (mc.thePlayer.posZ < 0) {
                        zOffset = 1.0f - zOffset
                    }
                    x = directionVec.x * zOffset
                    z = directionVec.z * zOffset
                }

                else -> {
                    x = 0.25
                    z = 0.25
                }
            }
            if (face.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x
                z = -z
            }
            val hitVec = Vec3(pos).addVector(x + z, directionVec.y * 0.5, x + z)
            val src = mc.thePlayer.getPositionEyes(1.0f)
            val obj = mc.theWorld.rayTraceBlocks(src, hitVec, false, false, true)

            if ((obj == null) || (obj.hitVec == null) || (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)) return null
            when (face.axis) {
                EnumFacing.Axis.Z -> obj.hitVec = Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, obj.hitVec.zCoord.roundToInt().toDouble())

                EnumFacing.Axis.X -> obj.hitVec = Vec3(obj.hitVec.xCoord.roundToInt().toDouble(), obj.hitVec.yCoord, obj.hitVec.zCoord)

                else -> {}
            }
            if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
                val blockState = mc.theWorld.getBlockState(obj.blockPos)
                val blockAtPos = blockState.block
                var blockFaceOffset = RandomUtils.nextDouble(0.1, 0.3)
                if (blockAtPos is BlockSlab && !blockAtPos.isDouble) {
                    val half = blockState.getValue(BlockSlab.HALF)
                    if (half != BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5
                    }
                }
                obj.hitVec = obj.hitVec.addVector(0.0, -blockFaceOffset, 0.0)
            }
            return obj.hitVec
        }
    }

    companion object {
        private val FACINGS = arrayOf(
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.NORTH
        )

        //OUT
        var items: ItemStack? = null
        fun translate(blockPos: BlockPos, enumFacing: EnumFacing?): Vec3i {
            var x = blockPos.x.toDouble()
            var y = blockPos.y.toDouble()
            var z = blockPos.z.toDouble()
            val r1 = ThreadLocalRandom.current().nextDouble(0.3, 0.5)
            val r2 = ThreadLocalRandom.current().nextDouble(0.9, 1.0)
            if ((enumFacing == EnumFacing.UP)) {
                x += r1
                z += r1
                y += 1.0
            } else if ((enumFacing == EnumFacing.DOWN)) {
                x += r1
                z += r1
            } else if ((enumFacing == EnumFacing.WEST)) {
                y += r2
                z += r1
            } else if ((enumFacing == EnumFacing.EAST)) {
                y += r2
                z += r1
                x += 1.0
            } else if ((enumFacing == EnumFacing.SOUTH)) {
                y += r2
                x += r1
                z += 1.0
            } else if ((enumFacing == EnumFacing.NORTH)) {
                y += r2
                x += r1
            }
            return Vec3i(x, y, z)
        }

        fun rayCastedBlock(yaw: Float, pitch: Float): MovingObjectPosition? {
            val range = mc.playerController.blockReachDistance
            val vec31 = getVectorForRotation(pitch, yaw)
            val vec3 = mc.thePlayer.getPositionEyes(1.0f)
            val vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range)
            val ray = mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false)
            return if (ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) ray else null
        }

        protected fun getVectorForRotation(pitch: Float, yaw: Float): Vec3 {
            val f = MathHelper.cos(-yaw * 0.017453292f - Math.PI.toFloat())
            val f1 = MathHelper.sin(-yaw * 0.017453292f - Math.PI.toFloat())
            val f2 = -MathHelper.cos(-pitch * 0.017453292f)
            val f3 = MathHelper.sin(-pitch * 0.017453292f)
            return Vec3((f1 * f2).toDouble(), f3.toDouble(), (f * f2).toDouble())
        }

        val randomHypixelValuesFloat: Float
            get() {
                val secureRandom = SecureRandom()
                var value = secureRandom.nextFloat() * (1f / System.currentTimeMillis())
                for (i in 0 until MathUtils.getRandomInRange(MathUtils.getRandomInRange(4, 6), MathUtils.getRandomInRange(8, 20))) value *= (1.0f / System.currentTimeMillis())

                return value
            }
        val movementDirection: Float
            get() = getMovementDirection(mc.thePlayer.rotationYaw)

        private fun getMovementDirection(yaw: Float): Float {
            val forward = mc.thePlayer.moveForward
            val strafe = mc.thePlayer.moveStrafing
            val forwards = forward > 0
            val backwards = forward < 0
            val right = strafe > 0
            val left = strafe < 0
            var direction = 0f
            if (backwards) direction += 180f
            direction += (if (forwards) (if (right) -45 else if (left) 45 else 0) else if (backwards) (if (right) 45 else if (left) -45 else 0) else (if (right) -90 else if (left) 90 else 0)).toFloat()
            direction += yaw
            return MathHelper.wrapAngleTo180_float(direction)
        }

        fun MovementInput(): Boolean {
            return (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindBack.pressed)
        }

        fun isOnGround(height: Double): Boolean {
            return mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.entityBoundingBox.offset(0.0, -height, 0.0)).isNotEmpty()
        }

        val jumpEffect: Int
            get() {
                return if (mc.thePlayer.isPotionActive(Potion.jump)) mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1 else 0
            }

        // get the count of blocks
        val blockCount: Int
            get() {
                var count = 0
                for (i in 9..44) {
                    val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                    if (stack == null || stack.item == null) continue
                    val item = stack.item
                    if (item is ItemBlock) {
                        val block = item.block
                        if (block is BlockAir) continue
                        count += stack.stackSize
                    }
                }
                return count
            }

        fun validateBlock(block: Block, action: BlockAction?): Boolean {
            if (block is BlockContainer) return false
            val material = block.material
            when (action) {
                BlockAction.PLACE -> return block !is BlockFalling && block.isFullBlock && block.isFullCube
                BlockAction.REPLACE -> return material.isReplaceable
                BlockAction.PLACE_ON -> return block !is BlockAir
                else -> {}
            }
            return true
        }
    }
}