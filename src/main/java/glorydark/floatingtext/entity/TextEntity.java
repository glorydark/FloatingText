package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.Map;

public class TextEntity extends Entity {
    protected Player owner;
    protected TextEntityData data;

    public final String WaitingForEditString = "待编辑...";

    public TextEntity(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt);
        this.data = data;
        this.owner = owner;
    }

    public int getNetworkId() {
        return 64;
    }

    protected void initEntity() {
        super.initEntity();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
    }

    public Player getOwner() {
        return this.owner;
    }

    public TextEntityData getData() {
        return data;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        for (Map.Entry<Integer, Player> entry : new ArrayList<>(this.hasSpawned.entrySet())) {
            Player player = entry.getValue();
            if (!player.isOnline() || player.getLevel() != this.getLevel()) {
                this.despawnFrom(player);
                this.hasSpawned.remove(entry.getKey());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean canBeSavedWithChunk() {
        return false;
    }
}
