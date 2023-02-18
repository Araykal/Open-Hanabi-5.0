package life.hanabi.config;

import life.hanabi.Hanabi;
import life.hanabi.core.values.Value;
import life.hanabi.core.Module;
import life.hanabi.core.ModuleCategory;

import java.util.HashMap;
import java.util.Map;

public class Language {
    public Map<String, String> texts = new HashMap<>();

    public Language() {

        texts.put("language.done", "Done");
        texts.put("language.import", "Import");
        texts.put("clickgui.custom", "Custom");
        texts.put("gui.login.done", "Done");
        texts.put("gui.login.cancel", "Cancel");
        texts.put("mainmenu.info", "Made by Hanabi Team");

        for (Map.Entry<String, Module> m : Hanabi.INSTANCE.moduleManager.modules.entrySet()) {
            texts.put("mod." + m.getValue().name, m.getValue().name);
            texts.put("mod." + m.getValue().name + ".desc", m.getValue().desc);
            for (Value v : m.getValue().values) {
                texts.put("mod." + m.getValue().name + "." + v.name, v.name);
                texts.put("mod." + m.getValue().name + "." + v.name + ".desc", v.desc);
            }
        }
        for (ModuleCategory m : ModuleCategory.values()) {
            texts.put("type." + m.name(), m.name());
        }
    }


}
