package life.hanabi.core.command

import life.hanabi.core.Command
import life.hanabi.core.cloudmusic.MusicManager
import life.hanabi.core.cloudmusic.api.CloudMusicAPI
import life.hanabi.core.cloudmusic.impl.Track
import life.hanabi.utils.PlayerUtil

class Music : Command("music") {
    override fun execute(args: Array<out String>?) {
        MusicManager.INSTANCE.playlist =
            CloudMusicAPI.INSTANCE.getPlaylistDetail(args?.get(0) ?: "")[1] as ArrayList<Track>
        MusicManager.INSTANCE.play(MusicManager.INSTANCE.playlist[0])
        PlayerUtil.tellPlayerWithPrefix("Now playing ${MusicManager.INSTANCE.playlist[0].name}")

    }
}