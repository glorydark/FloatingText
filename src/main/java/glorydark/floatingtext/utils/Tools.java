package glorydark.floatingtext.utils;

import glorydark.floatingtext.FloatingTextMain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 * @date {2023/8/29} {22:15}
 */
public class Tools {
    public static <T> List<T> castList(Object objects, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (objects instanceof List<?>) {
            for (Object object : (List<?>) objects) {
                result.add(clazz.cast(object));
            }
            return result;
        }
        FloatingTextMain.getInstance().getLogger().warning("Error in casting list, please check your configuration!");
        return new ArrayList<>();
    }
}
