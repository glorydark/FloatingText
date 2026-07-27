package glorydark.floatingtext.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.LangCode;
import cn.nukkit.utils.TextFormat;
import glorydark.floatingtext.FloatingTextMain;
import glorydark.floatingtext.entity.TextEntityData;
import glorydark.floatingtext.forms.FormFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static glorydark.floatingtext.FloatingTextMain.getI18n;
import static glorydark.floatingtext.FloatingTextMain.serverLangCode;

public class FloatingTextCommand extends Command {
    public FloatingTextCommand(String command) {
        super(command);

        this.setDescription("FloatingText manger.");
        this.getCommandParameters().clear();

        this.getCommandParameters().put("admin", new CommandParameter[]{
                CommandParameter.newEnum("admin", false, new String[]{"admin"})
        });

        this.getCommandParameters().put("reload", new CommandParameter[]{
                CommandParameter.newEnum("reload", false, new String[]{"reload"})
        });

        this.getCommandParameters().put("add", new CommandParameter[]{
                CommandParameter.newEnum("add", false, new String[]{"add"}),
                CommandParameter.newType("flag", false, CommandParamType.STRING),
                CommandParameter.newType("text", false, CommandParamType.STRING),
                CommandParameter.newEnum("tipsVariable", true, new CommandEnum("TipsVariable", "true", "false")),
                CommandParameter.newEnum("type", true, new CommandEnum("FloatingTextType", "armor_stand", "debug_text")),
                CommandParameter.newEnum("backgroundColor", true, new CommandEnum("BackgroundColor", "transparent"))
        });
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
                case "add": // ctc add name text [tipsVariable] [type] [backgroundColor] [color]
                    if (commandSender.isPlayer() && strings.length >= 3) {
                        String name = strings[1];
                        String text = strings[2];
                        boolean enableTipsVariable = false;
                        String type = "armor_stand";
                        String backgroundColor = "";
                        String color = "";
                        if (strings.length >= 4) {
                            enableTipsVariable = Boolean.parseBoolean(strings[3]);
                        }
                        if (strings.length >= 5) {
                            type = strings[4];
                        }
                        if (strings.length >= 6) {
                            backgroundColor = strings[5];
                        }
                        if (strings.length >= 7) {
                            color = strings[6];
                        }
                        if (!"armor_stand".equals(type) && !"debug_text".equals(type)) {
                            commandSender.sendMessage(TextFormat.RED + "Invalid type! Valid types: armor_stand, debug_text");
                            return true;
                        }
                        if ("debug_text".equals(type) && !"mot".equals(FloatingTextMain.serverPlat)) {
                            commandSender.sendMessage(TextFormat.RED + "Debug text type requires MOT server platform!");
                            return true;
                        }
                        Player player = (Player) commandSender;
                        TextEntityData data = new TextEntityData(name, player, new ArrayList<>(Arrays.asList(text.split("\\n"))), enableTipsVariable, type, 1.0f, false, 0f, backgroundColor, color, true);
                        FloatingTextMain.getInstance().addFloatingText(data);
                        commandSender.sendMessage(TextFormat.GREEN + getI18n().tr(commandSender, "add_floating_text_successfully", player.getX(), player.getY(), player.getZ(), player.getLevel().getName()));
                        data.checkEntity();
                        return true;
                    }
                    break;
                case "admin":
                    if (commandSender.isPlayer() && commandSender.isOp() && Server.getInstance().getPluginManager().getPlugin("MemoriesOfTime-GameCore") != null) {
                        FormFactory.showAdminMain((Player) commandSender);
                    } else {
                        commandSender.sendMessage(TextFormat.RED + getI18n().tr(commandSender, "softdepend.notfound", "MemoriesOfTime-GameCore"));
                    }
                    break;
            }
        }
        return false;
    }
}
