package life.hanabi.core.command

import life.hanabi.Hanabi
import life.hanabi.core.Command
import life.hanabi.core.managers.ModuleManager
import life.hanabi.utils.PlayerUtil
import org.lwjgl.input.Keyboard

class Bind() : Command("bind") {
    override fun execute(args: Array<out String>?) {
        if (args != null) {
            if (args.size >= 2) {
                val mod = args[0]
                ModuleManager.modules.entries.forEach { e->
                    if(e.key.lowercase() == mod.lowercase()){
                        e.value.key = Keyboard.getKeyIndex(args[1].uppercase())
                        PlayerUtil.tellPlayerWithPrefix("bound ${args[1]} to ${args[0]}")
                    }
                }
            } else {
                PlayerUtil.tellPlayerWithPrefix("Usage: .bind [mod] [key]")
            }
        }

    }
}