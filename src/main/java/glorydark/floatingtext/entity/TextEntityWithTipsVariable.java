package glorydark.floatingtext.entity;

import cn.lanink.gamecore.utils.EntityUtils;
import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.DataPacket;
import tip.utils.Api;

import java.util.Map;

public class TextEntityWithTipsVariable extends TextEntity {

    public TextEntityWithTipsVariable(FullChunk chunk, CompoundTag nbt, Player owner, TextEntityData data) {
        super(chunk, nbt, owner, data);
    }

    public void replaceTipVariable(Player player) {
//        String replaceText = this.getData().getText();
//        if (replaceText.isEmpty()) {
//            return;
//        }
//        this.setNameTag(Api.strReplace(this.data.getText(), this.owner));
        EntityMetadata metadata = new EntityMetadata();
        metadata.put(new StringEntityData(DATA_NAMETAG, Api.strReplace(this.data.getText(), player)));
        this.sendData(player, metadata);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (currentTick % 10 != 0) {
            return super.onUpdate(currentTick);
        }

        Map<Long, Player> players = this.getLevel().getPlayers();
        if (players.isEmpty()) {
            return super.onUpdate(currentTick);
        }
        players.values().forEach(this::replaceTipVariable);
        return super.onUpdate(currentTick);
    }

    @Override
    public void spawnTo(Player player) {
        player.dataPacket(createAddEntityPacket(player));
        this.replaceTipVariable(player);
    }

    public DataPacket createAddEntityPacket(Player player) {
        AddEntityPacket pk = (AddEntityPacket) this.createAddEntityPacket();
        pk.metadata.putString(
                EntityUtils.getEntityField("DATA_NAMETAG", DATA_NAMETAG),
                Api.strReplace(this.data.getText(), player)
        );
        return pk;
    }
}
