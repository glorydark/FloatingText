package glorydark.floatingtext;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.floatingtext.command.FloatingTextCommand;
import glorydark.floatingtext.entity.TextEntity;
import glorydark.floatingtext.entity.TextEntityData;
import glorydark.floatingtext.entity.TextEntityWithTipsVariable;
import glorydark.floatingtext.forms.FormFactory;
import glorydark.floatingtext.tasks.CheckEntityFloatingTextTask;
import glorydark.floatingtext.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloatingTextMain extends PluginBase implements Listener {
    private static String path;
    private static FloatingTextMain instance;
    public List<TextEntityData> textEntitiesDataList = new ArrayList<>();
    public String command;
    public boolean tipsLoaded;
    public static String serverPlat = "nukkit";

    public static FloatingTextMain getInstance() {
        return instance;
    }

    public static String getPath() {
        return path;
    }

    public void onLoad() {
        getLogger().info("FloatingText onLoad");
        if (Server.getInstance().getCodename().equalsIgnoreCase("mot")) {
            serverPlat = "mot";
        }
    }

    public void onEnable() {
        instance = this;
        path = getDataFolder().getPath();
        tipsLoaded = (this.getServer().getPluginManager().getPlugin("Tips") != null);
        if (!tipsLoaded) {
            this.getLogger().alert("Tips is not loaded. Floating texts with tips variable features will not be loaded!");
        }
        this.saveDefaultConfig();
        this.loadAll();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getCommandMap().register("", new FloatingTextCommand(this.command));
        this.getServer().getScheduler().scheduleRepeatingTask(this, new CheckEntityFloatingTextTask(this), 60);
        this.getLogger().info("FloatingText onEnable");
    }

    public void onDisable() {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity e : level.getEntities()) {
                if (e instanceof TextEntity) {
                    e.despawnFromAll();
                    e.close();
                }
            }
        }
        this.getLogger().info("FloatingText onDisable");
    }

    public void addFloatingText(TextEntityData data) {
        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        List<Map<String, Object>> list = config.get("texts", new ArrayList<>());
        Map<String, Object> map = new HashMap<>();
        Location location = data.getLocation();
        map.put("name", data.getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());
        map.put("level", location.getLevel().getName());
        map.put("lines", data.getLines());
        list.add(map);
        config.set("texts", list);
        config.save();
        this.textEntitiesDataList.add(data);
    }

    public void loadAll() {
        this.loadAll(true);
    }

    public void loadAll(boolean bool) {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity e : level.getEntities()) {
                if (e instanceof TextEntity) {
                    e.despawnFromAll();
                    e.close();
                }
            }
        }
        this.textEntitiesDataList = new ArrayList<>();
        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        this.command = config.getString("command", "ctc");
        List<Map<String, Object>> list = config.get("texts", new ArrayList<>());
        for (Map<String, Object> map : list) {
            Location location = new Location((Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), Server.getInstance().getLevelByName((String) map.get("level")));
            if (location.isValid()) {
                TextEntityData data = new TextEntityData((String) map.getOrDefault("name", ""), location, Tools.castList(map.getOrDefault("lines", new ArrayList<>()), String.class), (Boolean) map.getOrDefault("enable_tips_variable", true));
                if (!tipsLoaded && data.isEnableTipsVariable()) {
                    if (bool) {
                        this.getLogger().info("§cFailed to load floating text at §e" + location + "§c. Caused by: Tips is not loaded when enabling tips variables!");
                    }
                } else {
                    this.textEntitiesDataList.add(data);
                    if (bool) {
                        this.getLogger().info("§aLoad floating text at §e" + location);
                    }
                }
            } else {
                if (bool) {
                    this.getLogger().info("§cFailed to load floating text at §e" + location);
                }
            }
        }
        for (TextEntityData textEntityData : this.textEntitiesDataList) {
            textEntityData.checkEntity();
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof TextEntity) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerLocallyInitializedEvent event) {
        Player player = event.getPlayer();
        for (TextEntityData textEntityData : this.textEntitiesDataList) {
            textEntityData.respawnTo(player);
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Level value : Server.getInstance().getLevels().values()) {
            for (Entity entity : value.getEntities()) {
                if ((entity instanceof TextEntityWithTipsVariable) && ((TextEntity) entity).getOwner() == player) {
                    entity.kill();
                    entity.close();
                }
            }
        }
        FormFactory.editPlayerCaches.remove(player);
    }
}
