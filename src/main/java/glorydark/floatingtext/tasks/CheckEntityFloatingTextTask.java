package glorydark.floatingtext.tasks;

import cn.nukkit.scheduler.PluginTask;
import glorydark.floatingtext.FloatingTextMain;
import glorydark.floatingtext.entity.TextEntityData;

public class CheckEntityFloatingTextTask extends PluginTask<FloatingTextMain> {

    public CheckEntityFloatingTextTask(FloatingTextMain owner) {
        super(owner);
    }
    @Override
    public void onRun(int i) {
        for (TextEntityData textEntityData : this.owner.textEntitiesDataList) {
            textEntityData.checkEntity();
        }
    }
}
