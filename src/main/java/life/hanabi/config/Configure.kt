package life.hanabi.config

import com.google.gson.JsonObject
import life.hanabi.Hanabi
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.*

class Configure {
    fun getConfig(): String {
        val jsonObject = JsonObject();
        for (module in ModuleManager.modules!!.values) {
            val je = JsonObject()
            je.add("stage", Hanabi.INSTANCE.gson.toJsonTree(module.stage))
            je.add("key", Hanabi.INSTANCE.gson.toJsonTree(module.key))
            je.add("x", Hanabi.INSTANCE.gson.toJsonTree(module.x))
            je.add("y", Hanabi.INSTANCE.gson.toJsonTree(module.y))
            je.add("scale", Hanabi.INSTANCE.gson.toJsonTree(module.scale))

            for (value in module.values) {
                when (value) {
                    is BooleanValue -> {
                        je.add(value.name, Hanabi.INSTANCE.gson.toJsonTree(value.value))
                    }
                    is NumberValue -> {
                        je.add(value.name, Hanabi.INSTANCE.gson.toJsonTree(value.value))
                    }
                    is ModeValue -> {
                        je.add(value.name, Hanabi.INSTANCE.gson.toJsonTree(value.value))
                    }
                    is ColorValue -> {
                        val color = JsonObject()
                        color.addProperty("red", value.value.red)
                        color.addProperty("green", value.value.green)
                        color.addProperty("blue", value.value.blue)
                        color.addProperty("alpha", value.value.alpha)
                        je.add(value.name, color)
                    }
                    is TextValue -> {
                        je.add(value.name, Hanabi.INSTANCE.gson.toJsonTree(value.value))
                    }
                }
            }
            jsonObject.add(module.name, je)
        }
        return Hanabi.INSTANCE.gson.toJson(jsonObject)
    }

    fun setConfig(config: String) {
        val jsonObject = Hanabi.INSTANCE.gson.fromJson(config, JsonObject::class.java)
        for (module in ModuleManager.modules!!.values) {
            val je = jsonObject.getAsJsonObject(module.name)
            ModuleManager.modules?.get(module.name)?.fromJson(je)
        }
    }
}