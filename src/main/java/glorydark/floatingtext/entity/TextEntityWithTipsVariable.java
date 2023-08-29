package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import tip.utils.Api;

public class TextEntityWithTipsVariable extends TextEntity {
    protected Player owner;

    protected TextEntityData data;

    public TextEntityWithTipsVariable(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt, owner, data);
    }

    public void spawnTo(Player player) {
        if (getOwner() == player) {
            super.spawnTo(player);
        }
    }

    public boolean onUpdate(int currentTick) {
        this.setNameTag(Api.strReplace(this.data.getText(), getOwner()));
        return super.onUpdate(currentTick);
    }

    public Player getOwner() {
        return this.owner;
    }
}
