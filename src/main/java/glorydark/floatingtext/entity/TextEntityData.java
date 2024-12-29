package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;

import java.util.List;

public class TextEntityData {

    protected String name;
    protected Location location;
    protected List<String> lines;
    protected String replacedText;
    protected boolean enableTipsVariable;
    public static String NO_STRING_TEXT = "";

    public TextEntityData(String name, Location location, List<String> lines, boolean enableTipsVariable) {
        this.name = name;
        this.location = location;
        this.enableTipsVariable = enableTipsVariable;
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
        StringBuilder text = new StringBuilder(NO_STRING_TEXT);
        if (!lines.isEmpty()) {
            for (int i = 0; i < lines.size(); i++) {
                text.append(lines.get(i));
                if (i != lines.size() - 1) {
                    text.append("\n");
                }
            }
        }
        this.replacedText = text.toString();
    }

    public String getText() {
        return this.replacedText;
    }

    public void spawnTipsVariableFloatingTextTo(Player player) {
        if (enableTipsVariable) {
            if (!this.location.isValid() || location.getLevel().getPlayers().isEmpty()) {
                return;
            }
            FullChunk chunk = this.location.getChunk();
            if (chunk == null || !chunk.isLoaded() || chunk.getProvider() == null) {
                return;
            }
            if (this.location.getLevel().getPlayers().isEmpty()) {
                return;
            }
            TextEntityWithTipsVariable entity = new TextEntityWithTipsVariable(this.location.getChunk(), Entity.getDefaultNBT(this.location), player, this);
            entity.spawnTo(player);
            entity.scheduleUpdate();
        }
    }

    public void spawnSimpleFloatingText() {
        if (!location.isValid() || location.getLevel().getPlayers().isEmpty()) {
            return;
        }
        FullChunk chunk = location.getChunk();
        if (chunk == null || !chunk.isLoaded() || chunk.getProvider() == null) {
            return;
        }
        if (this.location.getLevel().getPlayers().isEmpty()) {
            return;
        }
        TextEntity entity = new  TextEntity(location.getChunk(), Entity.getDefaultNBT(this.location), null, this);
        entity.spawnToAll();
        entity.scheduleUpdate();
    }

    public boolean isEnableTipsVariable() {
        return enableTipsVariable;
    }

    public String getName() {
        return name;
    }
}
