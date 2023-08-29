package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;

import java.util.List;

public class TextEntityData {
    protected Location location;
    protected List<String> lines;
    protected String replacedText;
    protected boolean enableTipsVariable;

    public TextEntityData(Location location, List<String> lines, boolean enableTipsVariable) {
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
        StringBuilder text = new StringBuilder();
        if (lines.size() > 0) {
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
            TextEntityWithTipsVariable entity = new TextEntityWithTipsVariable(this.location.getChunk(), Entity.getDefaultNBT(this.location), player, this);
            entity.spawnTo(player);
            entity.scheduleUpdate();
        }
    }

    public void spawnSimpleFloatingText() {
        TextEntity entity = new TextEntity(this.location.getChunk(), Entity.getDefaultNBT(this.location), null, this);
        entity.spawnToAll();
        entity.scheduleUpdate();
    }

    public boolean isEnableTipsVariable() {
        return enableTipsVariable;
    }
}
