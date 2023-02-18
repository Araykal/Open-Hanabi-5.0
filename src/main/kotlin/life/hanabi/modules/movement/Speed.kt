package life.hanabi.modules.movement

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ModeValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventMove
import life.hanabi.modules.combat.KillAura
import life.hanabi.modules.combat.TargetStrafe
import life.hanabi.utils.MathUtils
import life.hanabi.utils.MovementUtils
import life.hanabi.utils.math.TimerUtil
import net.minecraft.potion.Potion
import org.lwjgl.input.Keyboard

@IIiIIiiiIiii
class Speed(name: String) : Module(name, ModuleCategory.Movement) {
    private val modeValue = ModeValue("Speed", "Watchdog", "Watchdog", "Vanilla")
    private val damage = BooleanValue("Damage", "Damage", false)
    private val boostdelay = NumberValue("BoostDelay", "BoostDelay", 2000.0, 0.0, 10000.0, 100.0)
    var speed = 0f

    init {
        addValues(modeValue, damage, boostdelay)
    }

    override fun onDisable() {
        super.onDisable()
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onMove(event: EventMotion) {
        if (event.isPre) {
            when (modeValue.current) {
                "Watchdog" -> if (MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) mc.thePlayer.jump()
                    if (KillAura.target == null) {
                        if (mc.thePlayer.motionY > 0 && mc.thePlayer.motionY < 0.3 || mc.thePlayer.motionY < 0 && mc.thePlayer.motionY > -0.24 && KillAura.target == null) {
                            if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) mc.timer.timerSpeed = 3.8f else mc.timer.timerSpeed = 1.381f
                        } else {
                            mc.timer.timerSpeed = 1f
                        }
                    }

                    speed = if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) 1.68f else 1.48f
                    if (mc.thePlayer.hurtTime in 1..5 && damage.value) speed *= 1.1f

                    speed -= if (mc.thePlayer.moveStrafing != 0f || mc.thePlayer.moveForward < 0) 0.20f else MathUtils.getRandomInRange(0.00411, 0.0465123).toFloat()
                    if (mc.thePlayer.hurtTime > 0 || !damage.value || mc.thePlayer.onGround) MovementUtils.setSpeed((MovementUtils.getBaseMoveSpeed() * speed))
                }

                "Vanilla" -> {}
            }
        }
    }

    @EventTarget
    fun onMove(event: EventMove) {
        when (modeValue.current) {
            "Watchdog" -> {
                val sp = MovementUtils.getBaseMoveSpeed()
                (ModuleManager.modules["TargetStrafe"] as TargetStrafe).isStrafing(event, KillAura.target, sp)
            }
        }
    }
}