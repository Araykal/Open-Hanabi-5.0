package life.hanabi.config

import life.hanabi.Hanabi
import life.hanabi.utils.FileUtils
import net.minecraft.client.Minecraft
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

class ConfigManager {
    @JvmField
    var settings: MutableMap<String, Boolean> = HashMap()
    var configure: Configure = Configure()

    init {
        supported.add("English")
        supported.add("简体中文")
        supported.add("日本語")
        var f: File
        if (!File("$dir/languages").absoluteFile.also { f = it }.exists()) {
            f.mkdirs()
        }
        if (!File("$dir/configs").absoluteFile.also { f = it }.exists()) {
            f.mkdirs()
        }
        if (!File("$dir/themes").absoluteFile.also { f = it }.exists()) {
            f.mkdirs()
        }

        // if (!File("$dir/themes/default.json").absoluteFile.also { f = it }.exists()) {
            // FileUtils.saveFile("$dir/themes/default.json", GsonBuilder().create().toJson(Hanabi.INSTANCE.theme))
        // } else {
            // Hanabi.INSTANCE.theme =
                // Hanabi.INSTANCE.gson.fromJson(FileUtils.readFile("$dir/themes/default.json"), Theme::class.java)
        // }

        if (!File("$dir/javascripts").absoluteFile.also { f = it }.exists()) {
            f.mkdirs()
        }

        for (s in supported) {
            f = File("$dir/languages/$s.json").absoluteFile
            try {
                f.createNewFile()
                val resourceAsStream = this.javaClass.getResourceAsStream("/assets/minecraft/client/language/$s.json")
                if (resourceAsStream == null) {
                    continue
                }
                val `in` = InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)
                val reader = BufferedReader(`in`)
                val sb = StringBuilder()
                var s1: String?
                while (reader.readLine().also { s1 = it } != null) {
                    sb.append(s1)
                }
                FileUtils.saveFile(f.absolutePath, sb.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (!File("$dir/configs/current.json").absoluteFile.exists()) {
            saveConfig("current")
        } else {
            loadConfig("current")
        }
    }


    fun loadConfig(name: String){
        val readFile = FileUtils.readFile(File("$dir/configs/current.json").absoluteFile)
        configure.setConfig(readFile)
    }

    fun saveConfig(name: String) {
        val file = File("$dir/configs/$name.json").absoluteFile
        FileUtils.saveFile(file.absolutePath, configure.getConfig())
    }

    fun toggle(name: String) {
        settings.putIfAbsent(name, false)
        settings.replace(name, !settings[name]!!)
    }

    fun getSettings(name: String): Boolean {
        settings.putIfAbsent(name, false)
        return settings[name]!!
    }

    fun reloadLanguages() {

        supported.clear()
        for (f in Objects.requireNonNull(File("$dir/languages/").absoluteFile.listFiles())) {
            if (f.absolutePath.endsWith(".json")) {
                supported.add(f.name.replace(".json", ""))
            }
        }
    }

    companion object {
        var supported: MutableList<String> = ArrayList()

        private var dirFile = File(Minecraft.getMinecraft().mcDataDir, Hanabi.CLIENT_NAME)
        var language = "English"
        @JvmField
        var dir: String = dirFile.absolutePath
    }
}