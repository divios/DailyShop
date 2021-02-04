package io.github.divios.dailyrandomshop.hooks;

import io.github.divios.dailyrandomshop.main;
import io.github.divios.dailyrandomshop.tasks.taskManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

class placeholderApiHook extends PlaceholderExpansion {

    private static final main plugin = main.getInstance();
    private static placeholderApiHook instance = null;

    private placeholderApiHook() {};

    public static placeholderApiHook getInstance() {
        if (instance == null ) {
            instance = new placeholderApiHook();
            instance.register();
            plugin.getLogger().info("Hooked to PlaceholderAPI");
        }
        return instance;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "DailyRandomShop";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %DailyRandomShop_
        if(identifier.equals("time")){

            int timeInSeconds = taskManager.getInstance().getTimer();
            int secondsLeft = timeInSeconds % 3600 % 60;
            int minutes = (int) Math.floor(timeInSeconds % 3600 / 60F);
            int hours = (int) Math.floor(timeInSeconds / 3600F);

            String HH = ((hours       < 10) ? "0" : "") + hours;
            String MM = ((minutes     < 10) ? "0" : "") + minutes;
            String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;

            return HH + ":" + MM + ":" + SS;
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }

}
