package life.hanabi.core.managers

import life.hanabi.core.Command
import life.hanabi.core.command.Bind
import life.hanabi.core.command.IRC
import life.hanabi.core.command.Music
import life.hanabi.event.EventManager
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.misc.EventChat
import life.hanabi.utils.PlayerUtil

class CommandManager {
    val commands = mutableListOf<Command>()

    fun init() {
        commands.add(Bind())
        commands.add(IRC())
        commands.add(Music())
        EventManager.register(this)
    }

    @EventTarget
    fun onCmd(e: EventChat) {
        if(e.chatMessage.unformattedText.startsWith(".")){
            e.isCancelled = true
            val split = e.chatMessage.unformattedText.split(" ")
            val cmd = split[0].substring(1)
            val args = split.drop(1)
            var has = false;
            commands.forEach {
                if(it.name.equals(cmd, ignoreCase = true)){
                    it.execute(args.toTypedArray())
                    has = true
                    return@forEach
                }
            }
            if (!has) {
                PlayerUtil.tellPlayerWithPrefix("Unknown command")
            }
        }
    }
}