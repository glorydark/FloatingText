package glorydark.floatingtext.tasks;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import glorydark.floatingtext.FloatingTextMain;
import glorydark.floatingtext.entity.TextEntityData;

public class DebugTextCleanupTask extends PluginTask<FloatingTextMain> {

    public DebugTextCleanupTask(FloatingTextMain owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (TextEntityData data : this.owner.textEntitiesDataList) {
            if (!"debug_text".equalsIgnoreCase(data.getType())) continue;
            if (data.getDebugShapeId() == -1) continue;

            String textLevel = data.getLocation().getLevelName();
            for (Player player : this.owner.getServer().getOnlinePlayers().values()) {
                if (!player.getLevel().getName().equals(textLevel)) {
                    data.removeDebugTextFrom(player);
                }
            }
        }
    }
}
