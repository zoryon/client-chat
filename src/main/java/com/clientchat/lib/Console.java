package com.clientchat.lib;

public class Console {
    public static void clear() {
        try {
            // check the operating system
            String os = System.getProperty("os.name").toLowerCase();

            // windows
            if (os.contains("windows")) {
                // Run the 'cls' command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            // UNIX-based systems (Linux, MacOS)
            else if (os.contains("linux") || os.contains("mac")) {
                // run the 'clear' command
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
            // for IDEs or unsupported systems, use ANSI escape codes or fallback
            else {
                // attempt ANSI escape codes for screen clearing
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // fallback method: print multiple new lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
