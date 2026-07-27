package glorydark.floatingtext.utils;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Language {

    protected HashMap<String, Map<String, Object>> lang;

    protected String defaultLanguage;

    public Language(String defaultLanguage) {
        lang = new HashMap<>();
        this.defaultLanguage = defaultLanguage;
    }

    public void addLanguage(File file) {
        if (file.getName().endsWith(".properties")) {
            String locale = file.getName().substring(0, file.getName().lastIndexOf("."));
            lang.put(locale, new Config(file, Config.PROPERTIES).getAll());
        }
    }

    public String getTranslationWithDefaultValue(String language, String key, String defaultValue, Object... param) {
        String processedText = (String) lang.getOrDefault(language, new HashMap<>()).getOrDefault(key, defaultValue == null ? key : defaultValue);
        if (param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                processedText = processedText.replace("{%" + i + "}", String.valueOf(param[i]));
            }
        }
        processedText = processedText.replace("\\n", "\n");
        return processedText;
    }

    public String tr(String key, Object... param) {
        return getTranslationWithDefaultValue(defaultLanguage, key, key, param);
    }

    public String tr(CommandSender sender, String key, Object... param) {
        if (sender.isPlayer()) {
            return tr((Player) sender, key, param);
        } else {
            return tr(key, param);
        }
    }

    public String tr(Player player, String key, Object... param) {
        return tr(getLang(player), key, key, param);
    }

    public String tr(String lang, String key, Object... param) {
        return getTranslationWithDefaultValue(lang, key, key, param);
    }

    private String getLang(Player player) {
        String languageCode = player.getLoginChainData().getLanguageCode();
        return lang.containsKey(languageCode) ? languageCode : defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
