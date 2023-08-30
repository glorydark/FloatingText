package glorydark.floatingtext.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.floatingtext.FloatingTextMain;
import glorydark.floatingtext.entity.TextEntityData;
import glorydark.floatingtext.forms.FormFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class FloatingTextCommand extends Command {
    public FloatingTextCommand(String command) {
        super(command);
    }

    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isPlayer() || commandSender.isOp()) {
            if (strings.length == 0) {
                return false;
            }
            String subCmdName = strings[0];
            switch (subCmdName) {
                case "reload":
                    FloatingTextMain.getInstance().loadAll();
                    commandSender.sendMessage("§aReload successfully!");
                    break;
                case "add":
                    if (commandSender.isPlayer() && strings.length >= 2 && commandSender.isPlayer()) {
                        boolean enableTipsVariable = true;
                        if (strings.length == 3) {
                            enableTipsVariable = Boolean.parseBoolean(strings[2]);
                        }
                        Player player = (Player) commandSender;
                        TextEntityData data = new TextEntityData(player, new ArrayList<>(Arrays.asList(strings[1].split("\\n"))), enableTipsVariable);
                        FloatingTextMain.getInstance().addFloatingText(data);
                        commandSender.sendMessage("§a成功添加浮空字至 " + player.getX() + "," + player.getY() + "," + player.getZ() + "," + player.getLevel().getName());
                        for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                            if (enableTipsVariable) {
                                data.spawnTipsVariableFloatingTextTo(value);
                            }
                        }
                        return true;
                    }
                    break;
                case "admin":
                    if (commandSender.isPlayer() && commandSender.isOp() && Server.getInstance().getPluginManager().getPlugin("MemoriesOfTime-GameCore") != null) {
                        FormFactory.showAdminMain((Player) commandSender);
                    } else {
                        commandSender.sendMessage("请先启用MemoriesOfTime-GameCore！");
                    }
                    break;
            }
        }
        return false;
    }
}
