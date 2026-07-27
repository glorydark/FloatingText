package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.DebugDrawerPacket;
import cn.nukkit.network.protocol.types.debugshape.DebugShape;
import cn.nukkit.network.protocol.types.debugshape.DebugText;
import glorydark.floatingtext.FloatingTextMain;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static glorydark.floatingtext.FloatingTextMain.*;

public class TextEntityData {

    protected String name;
    protected Location location;
    protected List<String> lines;
    protected String replacedText;
    protected boolean enableTipsVariable;

    protected String type = "armor_stand";
    protected float scale = 1.0f;
    protected boolean useRotation = false;
    protected float duration = 0f;
    protected String backgroundColor = "";
    protected String color = "";
    protected boolean depthTest = true;
    protected float rotationYaw = 0f;
    protected float rotationPitch = 0f;
    protected boolean showBackface = true;
    protected boolean showTextBackface = true;

    private TextEntity textEntity;

    private long debugShapeId = -1;
    private static final AtomicLong debugShapeIdCounter = new AtomicLong(1000000L);

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable) {
        this(name, location, lines, enableTipsVariable, "armor_stand", 1.0f, false, 0f, "", "", true, 0f, 0f, true, true);
    }

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable, String type, float scale, boolean useRotation, float duration) {
        this(name, location, lines, enableTipsVariable, type, scale, useRotation, duration, "", "", true, 0f, 0f, true, true);
    }

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable, String type, float scale, boolean useRotation, float duration, String backgroundColor) {
        this(name, location, lines, enableTipsVariable, type, scale, useRotation, duration, backgroundColor, "", true, 0f, 0f, true, true);
    }

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable, String type, float scale, boolean useRotation, float duration, String backgroundColor, String color, boolean depthTest) {
        this(name, location, lines, enableTipsVariable, type, scale, useRotation, duration, backgroundColor, color, depthTest, (float) location.yaw, (float) location.pitch, true, true);
    }

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable, String type, float scale, boolean useRotation, float duration, String backgroundColor, String color, boolean depthTest, float rotationYaw, float rotationPitch, boolean showBackface, boolean showTextBackface) {
        this.name = name;
        this.location = location;
        this.enableTipsVariable = enableTipsVariable;
        this.type = type;
        this.scale = scale;
        this.useRotation = useRotation;
        this.duration = duration;
        this.backgroundColor = backgroundColor;
        this.color = color;
        this.depthTest = depthTest;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.showBackface = showBackface;
        this.showTextBackface = showTextBackface;
        setLines(lines);
    }

    public Location getLocation() {
        return this.location;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
        this.replacedText = String.join("\n", lines);
    }

    public String getText() {
        return this.replacedText;
    }

    public String getType() {
        return type;
    }

    public float getScale() {
        return scale;
    }

    public boolean isUseRotation() {
        return useRotation;
    }

    public float getDuration() {
        return duration;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getColor() {
        return color;
    }

    public boolean isDepthTest() {
        return depthTest;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public boolean isShowBackface() {
        return showBackface;
    }

    public boolean isShowTextBackface() {
        return showTextBackface;
    }

    public long getDebugShapeId() {
        return debugShapeId;
    }

    public void removeDebugText() {
        if (debugShapeId == -1) return;
        if (!"mot".equals(FloatingTextMain.serverPlat)) return;
        if (this.location.getLevel() == null) return;

        Map<Long, Player> players = this.location.getLevel().getPlayers();
        if (players.isEmpty()) return;

        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(new DebugShape(debugShapeId, 0));
        for (Player player : players.values()) {
            player.dataPacket(packet);
        }
    }

    public void removeDebugTextFrom(Player player) {
        if (debugShapeId == -1) return;
        if (!"mot".equals(FloatingTextMain.serverPlat)) return;

        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(new DebugShape(debugShapeId, 0));
        player.dataPacket(packet);
    }

    public void spawnDebugTextTo(Player player) {
        if (!"debug_text".equalsIgnoreCase(type)) return;
        if (debugShapeId == -1) return;
        if (!"mot".equals(FloatingTextMain.serverPlat)) return;

        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(createDebugText());
        player.dataPacket(packet);
    }

    private void spawnDebugText() {
        if (!"mot".equals(FloatingTextMain.serverPlat)) {
            FloatingTextMain.getInstance().getLogger().warning("Debug text type requires MOT server platform for \"" + this.getName() + "\"! Falling back to armor_stand.");
            this.type = "armor_stand";
            return;
        }
        if (this.location.getLevel() == null || this.location.getLevel().getProvider() == null) return;

        if (debugShapeId == -1) {
            debugShapeId = debugShapeIdCounter.getAndIncrement();
        }

        Map<Long, Player> players = this.location.getLevel().getPlayers();
        if (players.isEmpty()) return;

        if (!this.location.getLevel().isChunkLoaded(this.location.getChunkX(), this.location.getChunkZ())) return;

        // FloatingTextMain.getInstance().getLogger().info("Debug text \"" + this.getName() + "\" displayed, requires client protocol >= 1.26.20 (975)!");

        DebugDrawerPacket packet = new DebugDrawerPacket();
        packet.shapes.add(createDebugText());
        for (Player player : players.values()) {
            player.dataPacket(packet);
        }
    }

    private DebugText createDebugText() {
        Vector3f rotation = useRotation ? new Vector3f(
                rotationPitch,
                rotationYaw,
                0f
        ) : null;
        return new DebugText(
                debugShapeId,
                0,
                new Vector3f((float) location.x, (float) location.y, (float) location.z),
                scale,
                rotation,
                null,
                null,
                parseColor(color),
                getText(),
                useRotation,
                parseBackgroundColor(backgroundColor),
                depthTest,
                showBackface,
                showTextBackface
        );
    }

    private static Color parseColor(String hex) {
        if (hex == null || hex.isEmpty()) return null;
        try {
            String clean = hex.startsWith("#") ? hex.substring(1) : hex;
            if (clean.length() == 6) {
                return new Color(Integer.parseInt(clean, 16));
            } else if (clean.length() == 8) {
                int argb = (int) Long.parseLong(clean, 16);
                return new Color(argb, true);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private static Color parseBackgroundColor(String hex) {
        Color c = parseColor(hex);
        return c != null ? c : new Color(0, true);
    }

    @Deprecated
    public void spawnTipsVariableFloatingTextTo(Player player) {
        if (!enableTipsVariable) return;
        TextEntityWithTipsVariable entity = new TextEntityWithTipsVariable(this.location.getChunk(), Entity.getDefaultNBT(this.location), player, this);
        entity.spawnTo(player);
        entity.scheduleUpdate();
        this.textEntity = entity;
    }

    @Deprecated
    public void spawnSimpleFloatingText() {
        TextEntity entity = new TextEntity(this.location.getChunk(), Entity.getDefaultNBT(this.location), null, this);
        entity.spawnToAll();
        entity.scheduleUpdate();
        if (FloatingTextMain.serverPlat.equals("mot")) {
            entity.setCanBeSavedWithChunk(false);
        }
        this.textEntity = entity;
    }

    public boolean isEnableTipsVariable() {
        return enableTipsVariable;
    }

    public String getName() {
        return name;
    }

    public void respawnTo(Player player) {
        if (!this.location.getLevelName().equals(player.getLevelName())) return;

        if ("debug_text".equalsIgnoreCase(type)) {
            spawnDebugTextTo(player);
            return;
        }

        if (this.textEntity == null) return;

        textEntity.spawnTo(player);
    }

    public void checkEntity() {
        if ((this.location.getLevel() == null && !Server.getInstance().loadLevel(this.location.getLevelName())) || this.location.getLevel().getProvider() == null) {
            FloatingTextMain.getInstance().getLogger().error(getI18n().tr("world.notfound", this.location.getLevelName(), this.getName()));
            return;
        }

        if ("debug_text".equalsIgnoreCase(type)) {
            if ("mot".equals(FloatingTextMain.serverPlat)) {
                spawnDebugText();
            } else {
                FloatingTextMain.getInstance().getLogger().warning("Debug text type requires MOT server platform for \"" + this.getName() + "\"! Skipping.");
            }
            return;
        }

        if (this.textEntity != null && !this.textEntity.isClosed()) {
            return;
        }

        Map<Long, Player> players = this.location.getLevel().getPlayers();
        if (players.isEmpty()) {
            return;
        }

        if (!this.location.getLevel().isChunkLoaded(this.location.getChunkX(), this.location.getChunkZ())) {
            return;
        }

        if (enableTipsVariable) {
            if (!hasTips) {
                FloatingTextMain.getInstance().getLogger().warning(getI18n().tr("softdepend.notfound.tips", this.getName()));
                spawnSimpleFloatingText();
                return;
            }
            TextEntityWithTipsVariable entity = new TextEntityWithTipsVariable(this.location.getChunk(), Entity.getDefaultNBT(this.location), null, this);
            entity.scheduleUpdate();
            if (FloatingTextMain.serverPlat.equals("mot")) {
                entity.setCanBeSavedWithChunk(false);
            }
            players.values().forEach(entity::spawnTo);
            this.textEntity = entity;
        } else {
            spawnSimpleFloatingText();
        }
    }
}
