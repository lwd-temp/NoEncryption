package me.doclic.noencryption.config;

import me.doclic.noencryption.NoEncryption;
import me.doclic.noencryption.utils.CommentedConfiguration;
import me.doclic.noencryption.utils.FileMgmt;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class ConfigurationHandler {
    private static NoEncryption main;
    private static CommentedConfiguration config, newConfig;
    private static HashMap<String, Object> newOptions;
    private static HashMap<String, String> messages;

    public static void initialize(NoEncryption main) {
        ConfigurationHandler.main = main;
        newOptions = new HashMap<>();
        messages = new HashMap<>();
    }

    public static boolean loadSettings() {
        FileMgmt.checkFolders(new String[]{
                main.getRootFolder(),
                main.getRootFolder() + FileMgmt.fileSeparator() + "settings"});

        return ConfigurationHandler.loadConfig();
    }

    private static boolean loadConfig() {
        String filepath = main.getRootFolder() + FileMgmt.fileSeparator() + "settings" + FileMgmt.fileSeparator() + "config.yml";

        File file = FileMgmt.CheckYMLExists(new File(filepath));
        if (file != null) {

            // read the config.yml into memory
            config = new CommentedConfiguration(file);
            if (!config.load()) {
                main.getLogger().log(Level.SEVERE, "Failed to load config.yml");
                return false;
            }

            setDefaults(file);

            config.save();
        }
        return true;
    }

    /**
     * Builds a new config reading old config data.
     */
    private static void setDefaults(File file) {

        newConfig = new CommentedConfiguration(file);
        newConfig.load();

        for (ConfigNodes root : ConfigNodes.values()) {
            if (root.getComments().length > 0) {
                addComment(root.getRoot(), root.getComments());
            }

            setNewProperty(root.getRoot(), (config.get(root.getRoot().toLowerCase()) != null) ? config.get(root.getRoot().toLowerCase()) : root.getDefault());

            if (config != null && !config.getKeys(true).contains(root.getRoot()) && !root.getDefault().equals("")) {
                newOptions.put(root.getRoot(), root.getDefault());
            }

            if (root.getNotice() != null) {
                messages.put(root.getRoot(), root.getNotice());
            }

        }

        config = newConfig;
        newConfig = null;
    }

    /**
     * Prints any new config options to the config
     */
    public static void printChanges() {

        main.getLogger().log(Level.INFO, "Checking for new config option...");
        if (!newOptions.isEmpty()) {

            newOptions.forEach((root, def) -> main.getLogger().log(Level.INFO, "  " + root + ": " + def));
        } else {
            main.getLogger().log(Level.INFO, "No new config options detected");
        }

        main.getLogger().log(Level.INFO, "Checking for important messages...");
        if (!messages.isEmpty()) {

            messages.forEach((root, msg) -> main.getLogger().log(Level.WARNING, "  " + root + ": \"" + msg + "\""));
        } else {
            main.getLogger().log(Level.INFO, "No important messages detected");
        }
    }

    private static void addComment(String root, String... comments) {

        newConfig.addComment(root.toLowerCase(), comments);
    }

    private static void setProperty(String root, Object value) {

        config.set(root.toLowerCase(), value.toString());
    }

    private static void setNewProperty(String root, Object value) {

        if (value == null) {
            // System.out.print("value is null for " + root.toLowerCase());
            value = "";
        }
        newConfig.set(root.toLowerCase(), value.toString());
    }


    /**
     * Get's a value for a ConfigNode
     *
     * @param node - ConfigNode
     * @return - Value for node
     */
    private static String getString(ConfigNodes node) {

        return config.getString(node.getRoot().toLowerCase(), String.valueOf(node.getDefault()));

    }

    /**
     * Get's a value for a ConfigNode
     *
     * @param node - ConfigNode
     * @return - Value for node (specifically boolean)
     */
    private static boolean getBoolean(ConfigNodes node) {

        return config.getBoolean(node.getRoot().toLowerCase(), Boolean.parseBoolean(String.valueOf(node.getDefault())));
    }

    /**
     * Get's a value for a ConfigNode
     *
     * @param node - ConfigNode
     * @return - Value for node (specifically double)
     */
    private static double getDouble(ConfigNodes node) {

        try {
            return config.getDouble(node.getRoot().toLowerCase(), Double.parseDouble(String.valueOf(node.getDefault())));
        } catch (NumberFormatException e) {
            main.getLogger().log(Level.SEVERE, "Could not get/read double for value: " + node.getRoot().toLowerCase());
            return 0.0;
        }
    }

    /**
     * Get's a value for a ConfigNode
     *
     * @param node - ConfigNode
     * @return - Value for node (specifically int)
     */
    private static int getInt(ConfigNodes node) {

        try {
            return config.getInt(node.getRoot().toLowerCase(), Integer.parseInt(String.valueOf(node.getDefault())));
        } catch (NumberFormatException e) {
            main.getLogger().log(Level.SEVERE, "Could not get/read int for value: " + node.getRoot().toLowerCase());
            return 0;
        }
    }

   /* public static String getDBTablePrefix() {
        return getString(ConfigNodes.DATABASE_TABLE_PREFIX);
    }

    public static String getLoadDBType() {
        return getString(ConfigNodes.DATABASE_LOAD_TYPE).toLowerCase();
    }

    public static String getLoadDBHostname() {
        return getString(ConfigNodes.DATABASE_LOAD_HOSTNAME);
    }

    public static String getLoadDBPort() {
        return getString(ConfigNodes.DATABASE_LOAD_PORT);
    }

    public static String getLoadDBSchemaName() {
        return getString(ConfigNodes.DATABASE_LOAD_SCHEMA_NAME);
    }

    public static String getLoadDBUsername() {
        return getString(ConfigNodes.DATABASE_LOAD_USERNAME);
    }

    public static String getLoadDBPassword() {
        return getString(ConfigNodes.DATABASE_LOAD_PASSWORD);
    }

    public static String getSaveDBType() {
        return getString(ConfigNodes.DATABASE_SAVE_TYPE).toLowerCase();
    }

    public static String getSaveDBHostname() {
        return getString(ConfigNodes.DATABASE_SAVE_HOSTNAME);
    }

    public static String getSaveDBPort() {
        return getString(ConfigNodes.DATABASE_SAVE_PORT);
    }

    public static String getSaveDBSchemaName() {
        return getString(ConfigNodes.DATABASE_SAVE_SCHEMA_NAME);
    }

    public static String getSaveDBUsername() {
        return getString(ConfigNodes.DATABASE_SAVE_USERNAME);
    }

    public static String getSaveDBPassword() {
        return getString(ConfigNodes.DATABASE_SAVE_PASSWORD);
    }*/
}