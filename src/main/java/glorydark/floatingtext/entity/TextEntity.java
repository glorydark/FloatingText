package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.entity.EntityDamageEvent;
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
        return EntityArmorStand.NETWORK_ID;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile(true);
        this.getDataProperties().putLong(0, 65536L);
        this.setScale(0.0f);

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
    public boolean attack(EntityDamageEvent source) {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) return false;
        return super.onUpdate(currentTick);
    }
}
