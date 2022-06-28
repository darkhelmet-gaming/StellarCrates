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

package network.darkhelmet.stellarcrates.services.configuration;

import com.google.inject.Inject;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;

import network.darkhelmet.stellarcrates.StellarCrates;
import network.darkhelmet.stellarcrates.api.services.configuration.CratesConfiguration;
import network.darkhelmet.stellarcrates.api.services.configuration.StellarCratesConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.serializers.BlockLocationSerializerConfigurate;
import network.darkhelmet.stellarcrates.services.configuration.serializers.LocaleSerializerConfigurate;
import network.darkhelmet.stellarcrates.services.configuration.serializers.RegularColorSerializerConfigurate;
import network.darkhelmet.stellarcrates.services.configuration.serializers.VectorSerializerConfigurate;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import xyz.xenondevs.particle.data.color.RegularColor;

public class ConfigurationService {
    /**
     * The plugin data path.
     */
    private final Path dataPath;

    /**
     * The primary plugin configuration.
     */
    private StellarCratesConfiguration playCratesConfiguration;

    /**
     * The crates configuration.
     */
    private CratesConfiguration cratesConfiguration;

    /**
     * Construct the configuration service.
     *
     * @param dataPath The plugin datapath
     */
    @Inject
    public ConfigurationService(Path dataPath) {
        this.dataPath = dataPath;

        loadConfigurations();
    }

    /**
     * Get the stellarcrates configuration.
     *
     * @return The stellarcrates configuration
     */
    public StellarCratesConfiguration stellarCratesConfig() {
        return playCratesConfiguration;
    }

    /**
     * Get the rewards configuration.
     *
     * @return The rewards configuration
     */
    public CratesConfiguration cratesConfiguration() {
        return cratesConfiguration;
    }

    /**
     * Load the configurations.
     */
    public void loadConfigurations() {
        File configFile = new File(dataPath.toFile(), "stellarcrates.conf");
        playCratesConfiguration = getOrWriteConfiguration(StellarCratesConfiguration.class, configFile);

        File cratesConfigFile = new File(dataPath.toFile(), "crates.conf");
        cratesConfiguration = getOrWriteConfiguration(CratesConfiguration.class, cratesConfigFile);
    }

    /**
     * Save all configurations.
     */
    public void saveAll() {
        saveAll(true);
    }

    /**
     * Save all configurations.
     */
    public void saveAll(boolean emitCrateComments) {
        File configFile = new File(dataPath.toFile(), "stellarcrates.conf");
        playCratesConfiguration = getOrWriteConfiguration(
            StellarCratesConfiguration.class, configFile, playCratesConfiguration, true);

        File cratesConfigFile = new File(dataPath.toFile(), "crates.conf");
        cratesConfiguration = getOrWriteConfiguration(
            CratesConfiguration.class, cratesConfigFile, cratesConfiguration, emitCrateComments);
    }

    /**
     * Build a hocon configuration loader with locale support.
     *
     * @param file The config file
     * @return The config loader
     */
    public ConfigurationLoader<?> configurationLoader(final Path file, boolean emitComments) {
        return HoconConfigurationLoader.builder()
            .prettyPrinting(true)
            .emitComments(emitComments)
            .defaultOptions(opts -> {
                final ConfigurateComponentSerializer serializer =
                    ConfigurateComponentSerializer.configurate();

                return opts.shouldCopyDefaults(true).serializers(serializerBuilder ->
                    serializerBuilder.registerAll(serializer.serializers())
                        .register(Locale.class, new LocaleSerializerConfigurate())
                        .register(Location.class, new BlockLocationSerializerConfigurate())
                        .register(Vector.class, new VectorSerializerConfigurate())
                        .register(RegularColor.class, new RegularColorSerializerConfigurate())
                );
            })
            .path(file)
            .build();
    }

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class.
     * @param file The file path we'll read/write to.
     * @param <T> The configuration class type.
     * @return The configuration class instance
     */
    public <T> T getOrWriteConfiguration(Class<T> clz, File file) {
        return getOrWriteConfiguration(clz, file, null, true);
    }

    /**
     * Get or create a configuration file.
     *
     * @param clz The configuration class
     * @param file The file path we'll read/write to
     * @param config The existing config object to write
     * @param <T> The configuration class type
     * @param emitComments Whether to emit comments
     * @return The configuration class instance
     */
    public <T> T getOrWriteConfiguration(Class<T> clz, File file, T config, boolean emitComments) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        final var loader = configurationLoader(file.toPath(), emitComments);

        try {
            final ConfigurationNode root = loader.load();

            // If config is not provided, load it
            if (config == null) {
                config = root.get(clz);
            }

            root.set(clz, config);
            loader.save(root);

            return config;
        } catch (final ConfigurateException e) {
            StellarCrates.getInstance().handleException(e);
        }

        return null;
    }
}
