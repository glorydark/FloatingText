package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

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
    public boolean onUpdate(int currentTick) {
        if (this.getNameTag().equals("待编辑...") && this.getNameTag().replace(" ", "").equals("")) {
            this.setNameTag("待编辑...");
        } else {
            if (!this.getNameTag().equals(data.getText())) {
                this.setNameTag(data.getText());
            }
        }
        return super.onUpdate(currentTick);
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
}
