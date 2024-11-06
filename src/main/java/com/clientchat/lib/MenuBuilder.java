package com.clientchat.lib;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuBuilder {
    private Map<String, MenuOption> options;

    public MenuBuilder() {
        this.options = new LinkedHashMap<>();
    }

    public MenuBuilder addOption(String key, String description, Runnable action) {
        options.put(key, new MenuOption(description, action));
        return this;
    }

    public Map<String, MenuOption> build() {
        return options;
    }
}
