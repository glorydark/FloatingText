package glorydark.floatingtext;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.lang.LangCode;
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
import glorydark.floatingtext.utils.Language;
import glorydark.floatingtext.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloatingTextMain extends PluginBase implements Listener {
    private static String path;
    private static FloatingTextMain instance;

    public static LangCode serverLangCode;

    public List<TextEntityData> textEntitiesDataList = new ArrayList<>();
    public String command;

    private static Language language;

    public static boolean hasTips = false;
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
        this.saveDefaultConfig();
        this.saveResource("language/zh_CN.properties");
        this.saveResource("language/en_US.properties");
        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        language = new Language(config.getString("default_lang", "en_US"));
        language.addLanguage(new File(path + File.separator + "language" + File.separator + "zh_CN.properties"));
        language.addLanguage(new File(path + File.separator + "language" + File.separator + "en_US.properties"));
    }

    public void onEnable() {
        instance = this;
        path = getDataFolder().getPath();
        hasTips = Server.getInstance().getPluginManager().getPlugin("Tips") != null;
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
        for (TextEntityData data : this.textEntitiesDataList) {
            data.removeDebugText();
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
        map.put("enable_tips_variable", data.isEnableTipsVariable());
        map.put("type", data.getType());
        if ("debug_text".equals(data.getType())) {
            map.put("scale", (double) data.getScale());
            map.put("use_rotation", data.isUseRotation());
            map.put("duration", (double) data.getDuration());
            map.put("background_color", data.getBackgroundColor());
            map.put("color", data.getColor());
            map.put("depth_test", data.isDepthTest());
            map.put("rotation_yaw", (double) data.getRotationYaw());
            map.put("rotation_pitch", (double) data.getRotationPitch());
            map.put("show_backface", data.isShowBackface());
            map.put("show_text_backface", data.isShowTextBackface());
        }
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
        for (TextEntityData oldData : this.textEntitiesDataList) {
            oldData.removeDebugText();
        }
        this.textEntitiesDataList = new ArrayList<>();
        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        this.command = config.getString("command", "ctc");
        List<Map<String, Object>> list = config.get("texts", new ArrayList<>());
        for (Map<String, Object> map : list) {
            Location location = new Location((Double) map.get("x"), (Double) map.get("y"), (Double) map.get("z"), Server.getInstance().getLevelByName((String) map.get("level")));
            if (!location.isValid()) {
                if (bool) {
                    this.getLogger().info(getI18n().tr("floatingtext.load.failed.invalid", location));
                }
                continue;
            }
            String type = (String) map.getOrDefault("type", "armor_stand");
            float scale = ((Number) map.getOrDefault("scale", 1.0d)).floatValue();
            boolean useRotation = (Boolean) map.getOrDefault("use_rotation", false);
            float duration = ((Number) map.getOrDefault("duration", 0.0d)).floatValue();
            String backgroundColor = (String) map.getOrDefault("background_color", "");
            String color = (String) map.getOrDefault("color", "");
            boolean depthTest = (Boolean) map.getOrDefault("depth_test", true);
            float rotationYaw = ((Number) map.getOrDefault("rotation_yaw", (double) location.yaw)).floatValue();
            float rotationPitch = ((Number) map.getOrDefault("rotation_pitch", (double) location.pitch)).floatValue();
            boolean showBackface = (Boolean) map.getOrDefault("show_backface", true);
            boolean showTextBackface = (Boolean) map.getOrDefault("show_text_backface", true);
            boolean enableTips = (Boolean) map.getOrDefault("enable_tips_variable", false);
            TextEntityData data = new TextEntityData((String) map.getOrDefault("name", ""), location, Tools.castList(map.getOrDefault("lines", new ArrayList<>()), String.class), enableTips, type, scale, useRotation, duration, backgroundColor, color, depthTest, rotationYaw, rotationPitch, showBackface, showTextBackface);
            if ("debug_text".equals(type) && !"mot".equals(serverPlat)) {
                if (bool) {
                    this.getLogger().warning("Debug text type requires MOT server! FloatingText at " + location + " will be skipped.");
                }
                continue;
            }
            if (!hasTips && data.isEnableTipsVariable()) {
                if (bool) {
                    this.getLogger().info(language.tr("floatingtext.load.failed.tips", location));
                }
            } else {
                this.textEntitiesDataList.add(data);
                if (bool) {
                    this.getLogger().info(getI18n().tr("floatingtext.load.success", location));
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

    public static Language getI18n() {
        return language;
    }
}
