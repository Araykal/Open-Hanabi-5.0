package life.hanabi.core.command

import life.hanabi.core.Command
import life.hanabi.irc.ClientHandler
import life.hanabi.irc.packets.impl.PacketMessage
import life.hanabi.irc.packets.impl.clientside.PacketCommand
import life.hanabi.irc.packets.impl.clientside.PacketGet
import life.hanabi.irc.packets.impl.clientside.PacketLogin
import life.hanabi.irc.utils.PacketUtil
import life.hanabi.utils.PlayerUtil
import net.minecraft.client.Minecraft


class IRC : Command("irc") {
    override fun execute(args: Array<out String>?) {
        if (args != null) {
            if (args[0].equals("get")) {
                ClientHandler.context.writeAndFlush(
                    PacketUtil.pack(
                        PacketGet("2", args[1])
                    )
                )
            } else if (args[0].equals("cmd")) {
                val strs = args.drop(1).toTypedArray()
                args.forEach { println(it) }
                ClientHandler.context.writeAndFlush(
                    PacketUtil.pack(
                        PacketCommand(strs)
                    )
                )
            } else {
                val sb: StringBuilder = StringBuilder()
                args.forEach { sb.append("$it ") }
                ClientHandler.context.writeAndFlush(
                    PacketUtil.pack(
                        PacketMessage(sb.toString())
                    )
                )
            }
        } else {
            PlayerUtil.tellPlayerWithPrefix("Usage: .irc <message>")
        }
    }
}