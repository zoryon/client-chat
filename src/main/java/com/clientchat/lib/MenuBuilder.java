package com.clientchat.lib;

import java.util.HashMap;
import java.util.Map;

public class MenuBuilder {
    /*
     * map is an interface.
     * we are using it to get the HasMap structure.
     * HashMap are similar to ArrayLists,
     * BUT, instead of an index, it
     * binds a KEY (of generic type) to an object (of generic type).
     * in this case we bind the menu's number (0, 1, 2, 3..) of type STRING
     * to it's related MenuOption
     * (which contains the function to use and its own description)
     */
    private Map<String, MenuOption> options;

    public MenuBuilder() {
        this.options = new HashMap<>();
    }

    public MenuBuilder addOption(String key, String description, Runnable action) {
        options.put(key, new MenuOption(description, action));

        /*
         * return "this".
         * Like so, it is possible to concatenate
         * many times this function, or other MenuBuilder's functions.
         * this is done so that it is possible to add
         * different options in a single time.
         * example:
         * new MenuBuilder()
         * .addOption(placeholder, placeholder, placeholder)
         * .addOption(placeholder, placeholder, placeholder)
         * .etc
         * .build()
         */
        return this;
    }

    public Map<String, MenuOption> build() {
        /*
         * at the end of the added options,
         * we can directly return the whole structure
         */
        return options;
    }
}
