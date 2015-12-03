package net.caspervg.llm;

import org.apache.commons.lang3.StringUtils;

public class LlmUtils {
    public static <T> String orElse(T t, String def) {
        if (t == null) return def;
        else {
            return StringUtils.defaultIfBlank(t.toString(), def);
        }
    }
}
