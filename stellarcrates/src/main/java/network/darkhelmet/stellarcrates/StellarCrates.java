/*
 * StellarCrates
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

package network.darkhelmet.stellarcrates;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;

import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.stellarcrates.api.IStellarCrates;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateService;
import network.darkhelmet.stellarcrates.commands.AboutCommand;
import network.darkhelmet.stellarcrates.commands.CrateCommand;
import network.darkhelmet.stellarcrates.commands.ImportCommand;
import network.darkhelmet.stellarcrates.commands.ReloadCommand;
import network.darkhelmet.stellarcrates.injection.StellarCratesModule;
import network.darkhelmet.stellarcrates.listeners.BlockPlaceListener;
import network.darkhelmet.stellarcrates.listeners.PlayerInteractListener;
import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.crates.CrateInstance;
import network.darkhelmet.stellarcrates.services.crates.CrateService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class StellarCrates extends JavaPlugin implements IStellarCrates {
    /**
     * Cache static instance.
     */
    private static StellarCrates instance;

    /**
     * The logger.
     */
    private static final Logger logger = LogManager.getLogger("StellarCrates");

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
     * Cache the tick task used to tick crates.
     */
    private BukkitTask tickTask;

    /**
     * Get this instance.
     *
     * @return The plugin instance
     */
    public static StellarCrates getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    public StellarCrates() {
        instance = this;
    }

    @Override
    public void onLoad() {
        this.injector = Guice.createInjector(new StellarCratesModule(this, logger));
    }

    /**
     * On enable.
     */
    @Override
    public void onEnable() {
        String pluginName = this.getDescription().getName();
        String pluginVersion = this.getDescription().getVersion();
        logger.info("Initializing {} {} by viveleroi", pluginName, pluginVersion);

        // Load the configuration service (and files)
        configurationService = injector.getInstance(ConfigurationService.class);

        logger.info("Serializer version: {}", configurationService.stellarCratesConfig().serializerVersion());

        CrateService crateService = injector.getInstance(CrateService.class);

        if (isEnabled()) {
            // Register listeners
            getServer().getPluginManager().registerEvents(injector.getInstance(BlockPlaceListener.class), this);
            getServer().getPluginManager().registerEvents(injector.getInstance(PlayerInteractListener.class), this);

            // Register commands
            BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

            // Register online player command suggestions
            commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> {
                List<String> players = new ArrayList<>();
                for (Player player : getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }

                return players;
            });

            // Register crate command suggestions
            commandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) ->
                crateService.crates().keySet().stream().toList());

            commandManager.registerCommand(injector.getInstance(AboutCommand.class));
            commandManager.registerCommand(injector.getInstance(CrateCommand.class));
            commandManager.registerCommand(injector.getInstance(ImportCommand.class));
            commandManager.registerCommand(injector.getInstance(ReloadCommand.class));

            // Run our "play" task that handles repeating tasks like playing particles, etc.
            tickTask = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                crateService.crates().values().forEach(crate -> {
                    crate.crateInstances().values().forEach(crateInstance -> {
                        ((CrateInstance) crateInstance).tick();
                    });
                });
            }, 0, 5L);
        }
    }

    @Override
    public ICrateService crateService() {
        return injector.getInstance(CrateService.class);
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(StellarCrates.getInstance());

        logger.error("StellarCrates has to disable due to a fatal error.");
    }

    /**
     * Log a debug message to console.
     *
     * @param message String
     */
    public void debug(String message) {
        if (configurationService.stellarCratesConfig().debug()) {
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

    /**
     * On disable, shut down tasks.
     */
    @Override
    public void onDisable() {
        super.onDisable();

        tickTask.cancel();
    }
}
