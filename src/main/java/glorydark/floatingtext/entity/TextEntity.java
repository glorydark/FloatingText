package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import glorydark.floatingtext.FloatingTextMain;

public class TextEntity extends Entity {
    protected Player owner;
    protected TextEntityData data;

    public TextEntity(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt);
        this.data = data;
        this.owner = owner;
    }

    public int getNetworkId() {
        return 64;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);

        if (FloatingTextMain.serverPlat.equals("mot")) {
            this.setCanBeSavedWithChunk(false);
        }
    }

    public Player getOwner() {
        return this.owner;
    }

    public TextEntityData getData() {
        return data;
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.setNameTag(getData().getText());
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) return false;
        // After 5 minutes, Minecraft client-side entities may become invisible.
        // To ensure the player remains visible, we force respawn.
        if (currentTick % (20 * 300) == 0) {
            this.respawnToAll();
        }
        return super.onUpdate(currentTick);
    }
}
