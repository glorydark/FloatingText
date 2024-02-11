package glorydark.floatingtext.forms;

import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.utils.Config;
import glorydark.floatingtext.FloatingTextMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author glorydark
 * @date {2023/8/30} {16:53}
 */
public class FormFactory {

    public static HashMap<Player, Integer> editPlayerCaches = new HashMap<>();

    public static void showAdminMain(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("浮空字管理面板", "请选择需要的功能");
        simple.addButton(new ElementButton("创建浮空字"));
        simple.addButton(new ElementButton("编辑/删除浮空字"));
        simple.addButton(new ElementButton("重载数据"));
        simple.onClicked((elementButton, player1) -> {
            switch (simple.getResponse().getClickedButtonId()) {
                case 0:
                    showCreateInfo(player);
                    break;
                case 1:
                    showEditList(player);
                    break;
                case 2:
                    FloatingTextMain.getInstance().loadAll(true);
                    player.sendMessage("重载完成！");
                    break;
            }
        });
        simple.showToPlayer(player);
    }

    public static void showCreateInfo(Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("创建浮空字");
        custom.addElement(new ElementInput("名称"));
        custom.addElement(new ElementInput("x", "", String.valueOf(player.getX())));
        custom.addElement(new ElementInput("y", "", String.valueOf(player.getY())));
        custom.addElement(new ElementInput("z", "", String.valueOf(player.getZ())));
        custom.addElement(new ElementInput("世界", "", player.getLevel().getName()));
        custom.addElement(new ElementToggle("Tips变量支持", false));
        custom.onResponded((formResponseCustom, customResponsePlayer) -> {
            Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
            List<Map<String, Object>> maps = new ArrayList<>(config.get("texts", new ArrayList<>()));
            Map<String, Object> map = new HashMap<>();
            map.put("name", custom.getResponse().getInputResponse(0));
            map.put("x", Double.parseDouble(custom.getResponse().getInputResponse(1)));
            map.put("y", Double.parseDouble(custom.getResponse().getInputResponse(2)));
            map.put("z", Double.parseDouble(custom.getResponse().getInputResponse(3)));
            map.put("level", custom.getResponse().getInputResponse(4));
            map.put("enable_tips_variable", custom.getResponse().getToggleResponse(5));
            maps.add(map);
            config.set("texts", maps);
            config.save();
            FloatingTextMain.getInstance().loadAll(false);
        });
        custom.onClosed(FormFactory::showAdminMain);
        custom.showToPlayer(player);
    }

    // Edit
    public static void showEditList(Player player) {
        Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
        List<Map<String, Object>> keys = new ArrayList<>(config.get("texts", new ArrayList<>()));
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("编辑浮空字 - 编辑列表", "请选择你要编辑的浮空字");
        if (keys.size() == 0) {
            simple.setContent("暂无浮空字，请先创建！");
            simple.onClosed(FormFactory::showAdminMain);
            simple.showToPlayer(player);
            return;
        }
        for (Map<String, Object> map : keys) {
            if (map.containsKey("name")) {
                simple.addButton(new ElementButton((String) map.get("name")));
            } else {
                simple.addButton(new ElementButton(map.get("x") + ":" + map.get("y") + ":" + map.get("z")));
            }
        }
        simple.onClicked((elementButton, simpleResponsePlayer) -> {
            int editId = simple.getResponse().getClickedButtonId();
            if (!editPlayerCaches.containsValue(editId)) {
                if (!editPlayerCaches.containsKey(player)) {
                    showEditChoice(simpleResponsePlayer, simple.getResponse().getClickedButtonId());
                    editPlayerCaches.put(simpleResponsePlayer, editId);
                } else {
                    showReturnMenu(simpleResponsePlayer, "§c错误：您仍在编辑状态，请尝试重进游戏！", FormFactory::showEditList);
                }
            } else {
                showReturnMenu(simpleResponsePlayer, "§c已经有管理员在编辑该项了！", FormFactory::showEditList);
            }
        });
        simple.onClosed(FormFactory::showAdminMain);
        simple.showToPlayer(player);
    }

    public static void showEditChoice(Player player, int id) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("编辑浮空字 - 编辑类型", "请选择你要编辑的类型");
        simple.addButton(new ElementButton("基础配置"));
        simple.addButton(new ElementButton("编辑浮空字文本"));
        simple.addButton(new ElementButton("添加浮空字文本"));
        simple.onClicked((elementButton, simpleResponsePlayer) -> {
            switch (simple.getResponse().getClickedButtonId()) {
                case 0:
                    showEditBasicData(player, id);
                    break;
                case 1:
                    showEditTextSelect(player, id);
                    break;
                case 2:
                    showAddText(player, id);
                    break;
            }
        });
        simple.onClosed(responsePlayer -> {
            editPlayerCaches.remove(player);
            showEditList(responsePlayer);
        });
        simple.showToPlayer(player);
    }

    public static void showAddText(Player player, int id) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("编辑浮空字 - 添加行文本");
        custom.addElement(new ElementInput("内容"));
        custom.onResponded((formResponseCustom, responsePlayer) -> {
            Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
            List<Map<String, Object>> maps = new ArrayList<>(config.get("texts", new ArrayList<>()));
            Map<String, Object> map = maps.get(id);
            if (map != null) {
                List<String> strings = (List<String>) map.getOrDefault("lines", new ArrayList<>());
                strings.add(formResponseCustom.getInputResponse(0));
                map.put("lines", strings);
                maps.set(id, map);
                config.set("texts", maps);
                config.save();
                FloatingTextMain.getInstance().loadAll(false);
                showReturnMenu(player, "§a创建成功!", subResponsePlayer -> showEditChoice(subResponsePlayer, id));
            } else {
                editPlayerCaches.remove(player);
                showReturnMenu(player, "§cError in serializing config data!", FormFactory::showEditList);
            }
        });
        custom.onClosed(responsePlayer -> showEditChoice(responsePlayer, id));
        custom.showToPlayer(player);
    }

    public static void showEditTextSelect(Player player, int id) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("编辑浮空字 - 选择编辑文本", "请选择你要编辑的文本");
        Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
        List<Map<String, Object>> maps = new ArrayList<>(config.get("texts", new ArrayList<>()));
        if (id >= maps.size()) {
            showReturnMenu(player, "§c该文本已被删除!", responsePlayer -> showEditTextSelect(responsePlayer, id));
            return;
        }
        Map<String, Object> map = maps.get(id);
        if (map != null) {
            List<String> strings = (List<String>) map.getOrDefault("lines", new ArrayList<>());
            if (strings.size() == 0) {
                simple.setContent("暂无文本，请先添加文本！");
                simple.onClosed(player1 -> showEditChoice(player, id));
                simple.showToPlayer(player);
                return;
            }
            for (String string : strings) {
                simple.addButton(new ElementButton(string));
            }
            simple.onClicked((elementButton, simpleResponsePlayer) -> showEditText(simpleResponsePlayer, id, simple.getResponse().getClickedButtonId()));
            simple.onClosed(simpleResponsePlayer -> showEditChoice(simpleResponsePlayer, id));
            simple.showToPlayer(player);
        } else {
            editPlayerCaches.remove(player);
            showReturnMenu(player, "§cError in serializing config data!", FormFactory::showEditList);
        }
    }

    public static void showEditText(Player player, int id, int textId) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("编辑浮空字 - 文本编辑");
        Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
        List<Map<String, Object>> maps = new ArrayList<>(config.get("texts", new ArrayList<>()));
        Map<String, Object> map = maps.get(id);
        List<String> strings = (List<String>) map.getOrDefault("lines", new ArrayList<>());
        if (textId < strings.size()) {
            custom.addElement(new ElementToggle("是否删除", false));
            custom.addElement(new ElementInput("文本", "", strings.get(textId)));
            custom.onResponded((formResponseCustom, customResponsePlayer) -> {
                if (formResponseCustom.getToggleResponse(0)) {
                    strings.remove(textId);
                } else {
                    strings.set(textId, formResponseCustom.getInputResponse(1));
                }
                map.put("lines", strings);
                maps.set(id, map);
                config.set("texts", maps);
                config.save();
                FloatingTextMain.getInstance().loadAll(false);
                showReturnMenu(player, "§a保存成功!", responsePlayer -> showEditTextSelect(responsePlayer, id));
            });
            custom.onClosed(responsePlayer -> showEditTextSelect(responsePlayer, id));
            custom.showToPlayer(player);
        } else {
            editPlayerCaches.remove(player);
            showReturnMenu(player, "§cError in serializing config data!", FormFactory::showEditList);
        }
    }

    public static void showEditBasicData(Player player, int id) {
        Config config = new Config(FloatingTextMain.getPath() + "/config.yml", Config.YAML);
        List<Map<String, Object>> maps = new ArrayList<>(config.get("texts", new ArrayList<>()));
        Map<String, Object> map = maps.get(id);
        if (map != null) {
            AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("编辑浮空字 - 基础配置编辑");
            custom.addElement(new ElementLabel(
                    "您当前的坐标: " + player.getX() + ":" + player.getY() + ":" + player.getZ()
                            + "\n您当前所在世界" + player.getLevel().getName()
            ));
            custom.addElement(new ElementToggle("是否删除", false));
            custom.addElement(new ElementInput("名称", "", (String) map.getOrDefault("name", "")));
            custom.addElement(new ElementInput("x", "", String.valueOf(map.get("x"))));
            custom.addElement(new ElementInput("y", "", String.valueOf(map.get("y"))));
            custom.addElement(new ElementInput("z", "", String.valueOf(map.get("z"))));
            custom.addElement(new ElementInput("世界", "", (String) map.get("level")));
            custom.addElement(new ElementToggle("Tips变量支持", (Boolean) map.get("enable_tips_variable")));
            custom.onResponded((formResponseCustom, i) -> {
                if (custom.getResponse().getToggleResponse(1)) {
                    maps.remove(id);
                    editPlayerCaches.remove(player);
                    showReturnMenu(player, "§a删除成功！", modalResponsePlayer -> showEditChoice(player, id));
                    List<Map.Entry<Player, Integer>> entries = new ArrayList<>(editPlayerCaches.entrySet());
                    editPlayerCaches.clear();
                    for (Map.Entry<Player, Integer> integerPlayerEntry : entries) {
                        int newId = integerPlayerEntry.getValue() - 1;
                        if (newId >= 0) {
                            editPlayerCaches.put(integerPlayerEntry.getKey(), newId);
                        }
                    }
                } else {
                    if (editPlayerCaches.get(player) == id) {
                        map.put("name", custom.getResponse().getInputResponse(2));
                        map.put("x", Double.parseDouble(custom.getResponse().getInputResponse(3)));
                        map.put("y", Double.parseDouble(custom.getResponse().getInputResponse(4)));
                        map.put("z", Double.parseDouble(custom.getResponse().getInputResponse(5)));
                        map.put("level", custom.getResponse().getInputResponse(6));
                        map.put("enable_tips_variable", custom.getResponse().getToggleResponse(7));
                        maps.set(id, map);
                        showReturnMenu(player, "§a保存成功！", modalResponsePlayer -> showEditChoice(player, id));
                    } else {
                        showReturnMenu(player, "§c该文本已被其他管理员删除！", FormFactory::showEditList);
                    }
                }
                config.set("texts", maps);
                config.save();
                FloatingTextMain.getInstance().loadAll(false);
            });
            custom.onClosed(customResponsePlayer -> {
                editPlayerCaches.remove(player);
                showEditChoice(player, id);
            });
            custom.showToPlayer(player);
        } else {
            editPlayerCaches.remove(player);
            showReturnMenu(player, "§cError in serializing config data!", FormFactory::showEditList);
        }
    }

    public static void showReturnMenu(Player player, String content, Consumer<Player> consumer) {
        AdvancedFormWindowModal modal = new AdvancedFormWindowModal("提示", content, "返回", "退出");
        modal.onClickedTrue(consumer);
        modal.showToPlayer(player);
    }
}
