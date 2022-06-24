/*
 * PlayCrates
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.playcrates;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.darkhelmet.playcrates.commands.AboutCommand;
import network.darkhelmet.playcrates.commands.CrateCommand;
import network.darkhelmet.playcrates.commands.ImportCommand;
import network.darkhelmet.playcrates.commands.ReloadCommand;
import network.darkhelmet.playcrates.injection.PlayCratesModule;
import network.darkhelmet.playcrates.listeners.PlayerInteractListener;
import network.darkhelmet.playcrates.services.configuration.ConfigurationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayCrates extends JavaPlugin {
    /**
     * Cache static instance.
     */
    private static PlayCrates instance;

    /**
     * The logger.
     */
    private static final Logger logger = LogManager.getLogger("PlayCrates");

    /**
     * The injector.
     */
    private Injector injector;

    /**
     * Sets a numeric version we can use to handle differences between serialization formats.
     */
    protected short serializerVersion;

    /**
     * The configuration service.
     */
    private ConfigurationService configurationService;

    /**
     * Get this instance.
     *
     * @return The plugin instance
     */
    public static PlayCrates getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    public PlayCrates() {
        instance = this;
    }

    @Override
    public void onLoad() {
        this.injector = Guice.createInjector(new PlayCratesModule(this, logger));
    }

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        String pluginName = this.getDescription().getName();
        String pluginVersion = this.getDescription().getVersion();
        logger.info("Initializing {} {} by viveleroi", pluginName, pluginVersion);

        serializerVersion = mcVersion();
        logger.info("Serializer version: {}", serializerVersion);

        // Load the configuration service (and files)
        configurationService = injector.getInstance(ConfigurationService.class);

        if (isEnabled()) {
            // Register listeners
            getServer().getPluginManager().registerEvents(injector.getInstance(PlayerInteractListener.class), this);

            // Register commands
            BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

            // Register online player auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> {
                List<String> players = new ArrayList<>();
                for (Player player : getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }

                return players;
            });

            commandManager.registerCommand(injector.getInstance(AboutCommand.class));
            commandManager.registerCommand(injector.getInstance(CrateCommand.class));
            commandManager.registerCommand(injector.getInstance(ImportCommand.class));
            commandManager.registerCommand(injector.getInstance(ReloadCommand.class));
        }
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(PlayCrates.getInstance());

        logger.error("PlayCrates has to disable due to a fatal error.");
    }

    /**
     * Parses the mc version as a short. Fed to nbt serializers.
     *
     * @return The mc version as a number
     */
    protected Short mcVersion() {
        Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());
        if (matcher.find()) {
            return Short.parseShort(matcher.group(1).replace(".", ""));
        }

        return null;
    }

    /**
     * Get the serializer version.
     *
     * @return The version
     */
    public short serializerVersion() {
        return serializerVersion;
    }

    /**
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (configurationService.playCratesConfig().debug()) {
            logger.info(message);
        }
    }

    /**
     * Handle exceptions.
     *
     * @param e The exception
     */
    public void handleException(Exception e) {
        e.printStackTrace();
    }
}
