package life.hanabi.core.managers;

import life.hanabi.event.events.impl.misc.EventKey;
import life.hanabi.modules.combat.*;
import life.hanabi.modules.misc.*;
import life.hanabi.modules.movement.Scaffold;
import life.hanabi.modules.world.*;
import life.hanabi.modules.movement.*;
import life.hanabi.modules.pvp.MemoryManager;
import life.hanabi.modules.pvp.*;
import life.hanabi.modules.render.*;
import life.hanabi.modules.render.ClickGui;
import life.hanabi.modules.settings.ClientSettings;
import life.hanabi.core.Module;
import life.hanabi.event.EventManager;
import life.hanabi.event.EventTarget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager extends Manager {
    public static Map<String, Module> modules = new HashMap<>();

    public void init() {
        List<Module> modules = Arrays.asList(
                new Sprint("Sprint"),
                new ClickGui("ClickGui"),
                new OldAnimation("OldAnimation"),
                new MoreParticles("MoreParticles"),
                new CustomFov("CustomFov"),
                new PotionDisplay("PotionDisplay"),
                new MemoryManager("MemoryManager"),
                new AutoGG("AutoGG"),
                new KeyStrokes("KeyStrokes"),
                new FullBright("FullBright"),
                new Nametags("NameTags"),
                new Blink("Blink"),
                new Scoreboard("Scoreboard"),
                new TimeChanger("TimeChanger"),
                new DragonWings("DragonWings"),
                new BlockOverlay("BlockOverlay"),
                new NotificationModule("Notification"),
                new ItemPhysics("ItemPhysics"),
                new Crosshair("Crosshair"),
                new TNTTimer("TNTTimer"),
                new MotionBlur("MotionBlur"),
                new Coordinates("Coordinates"),
                new SnapLook("SnapLook"),
                new ArmorStatus("ArmorStatus"),
                new ClientSettings("ClientSettings"),
                new KillAura("KillAura"),
                new AimAssistant("AimAssistant"),
                new Reach("Reach"),
                new Velocity("Velocity"),
                new HideAndSeek("HideAndSeek"),
                new MurdererFinder("MurdererFinder"),
                new Fly("Fly"),
                new KeepSprint("KeepSprint"),
                new NoSlow("NoSlow"),
                new Speed("Speed"),
                new ArrowESP("ArrowESP"),
                new AntiBot("AntiBot"),
                new Chams("Chams"),
                new ESP("ESP"),
                new Xray("Xray"),
                new Scaffold("Scaffold"),
                new SpeedMine("SpeedMine"),
                new FastPlace("FastPlace"),
                new Eagle("Eagle"),
                new Tracers("Tracers"),
                new Nuker("Nuker"),
                new InventoryManager("InventoryManager"),
                new ChestStealer("ChestStealer"),
                new AutoArmor("AutoArmor"),
                new AntiFall("AntiFall"),
                new Timer("Timer"),
                new Disabler("Disabler"),
                new LagBackChecker("LagBackChecker"),
                new HUD("HUD"),
                new Teams("Teams"),
                new InvMove("InvMove"),
                new TargetStrafe("TargetStrafe")
        );
        modules.forEach(m -> ModuleManager.modules.put(m.name, m));
        EventManager.register(this);
    }


    @EventTarget
    public void onKey(EventKey e) {
        for (Map.Entry<String, Module> m : modules.entrySet()) {
            if (m.getValue().key == e.key) {
                m.getValue().toggle();
            }
        }
    }
}

