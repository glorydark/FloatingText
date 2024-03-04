package glorydark.floatingtext.entity;

import cn.nukkit.Player;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import tip.utils.Api;

public class TextEntityWithTipsVariable extends TextEntity {

    public TextEntityWithTipsVariable(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt, owner, data);
    }

    public void spawnTo(Player player) {
        if (getOwner() == player) {
            super.spawnTo(player);
        }
    }

    public Player getOwner() {
        return this.owner;
    }

    public void replaceTipVariable() {
        String replaceText = this.getData().getText();
        if (replaceText.equals("")) {
            return;
        }
        this.setNameTag(Api.strReplace(this.data.getText(), this.owner));
    }
}
