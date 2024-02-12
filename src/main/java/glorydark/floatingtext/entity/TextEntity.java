package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;

public class TextEntity extends Entity {
    protected Player owner;
    protected TextEntityData data;

    protected String WaitingForEditString = "待编辑...";

    public TextEntity(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt);
        this.data = data;
        this.owner = owner;
    }

    public int getNetworkId() {
        return 64;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        String replaceText = data.getText();
        boolean noString = replaceText.equals(TextEntityData.NO_STRING_TEXT);
        if (!this.getNameTag().equals(WaitingForEditString) && noString) {
            this.setNameTag(WaitingForEditString);
        } else {
            if (noString) {
                return false;
            }
            replaceText(replaceText);
        }
        return super.onUpdate(currentTick);
    }

    public void replaceText(String replaceText){
        if (!this.getNameTag().equals(replaceText)) {
            this.setNameTag(replaceText);
        }
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

    @Override
    public void spawnTo(Player player) {
        if (this.getNetworkId() == 64) {
            super.spawnTo(player);
        }
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
            player.dataPacket(this.createAddEntityPacket());
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.getNetworkId() == 64) {
            super.despawnFrom(player);
        }
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }
}
