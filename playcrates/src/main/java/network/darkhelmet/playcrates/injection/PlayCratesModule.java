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

package network.darkhelmet.playcrates.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import io.leangen.geantyref.TypeToken;

import java.nio.file.Path;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;

import network.darkhelmet.playcrates.PlayCrates;
import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.crates.CrateService;
import network.darkhelmet.playcrates.services.messages.MessageRenderer;
import network.darkhelmet.playcrates.services.messages.MessageSender;
import network.darkhelmet.playcrates.services.messages.MessageService;
import network.darkhelmet.playcrates.services.messages.ReceiverResolver;
import network.darkhelmet.playcrates.services.messages.resolvers.StringPlaceholderResolver;
import network.darkhelmet.playcrates.services.messages.resolvers.TranslatableStringPlaceholderResolver;
import network.darkhelmet.playcrates.services.translation.TranslationKey;
import network.darkhelmet.playcrates.services.translation.TranslationService;

import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;

public class PlayCratesModule extends AbstractModule {
    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * The data path.
     */
    private final Path dataPath;

    /**
     * The version.
     */
    private final String version;

    /**
     * Construct the module.
     *
     * @param playCrates PlayCrates
     * @param logger The logger
     */
    public PlayCratesModule(PlayCrates playCrates, Logger logger) {
        this.logger = logger;
        this.dataPath = playCrates.getDataFolder().toPath();
        this.version = playCrates.getDescription().getVersion();
    }

    @Provides
    @Named("version")
    String getVersion() {
        return version;
    }

    /**
     * Get the bukkit audiences.
     *
     * @return The bukkit audiences
     */
    @Provides
    @Singleton
    public BukkitAudiences getAudience() {
        return BukkitAudiences.create(PlayCrates.getInstance());
    }

    /**
     * Get the message service.
     *
     * @param translationService The translation service
     * @param messageRenderer The message renderer
     * @param messageSender The message sender
     * @param translatableStringPlaceholderResolver The translatable string resolver
     * @return The message service
     */
    @Provides
    @Singleton
    @Inject
    public MessageService getMessageService(
            TranslationService translationService,
            MessageRenderer messageRenderer,
            MessageSender messageSender,
            TranslatableStringPlaceholderResolver translatableStringPlaceholderResolver) {
        try {
            return Moonshine.<MessageService, CommandSender>builder(
                    TypeToken.get(MessageService.class))
                .receiverLocatorResolver(new ReceiverResolver(), 0)
                .sourced(translationService)
                .rendered(messageRenderer)
                .sent(messageSender)
                .resolvingWithStrategy(new StandardPlaceholderResolverStrategy<>(
                    new StandardSupertypeThenInterfaceSupertypeStrategy(false)))
                .weightedPlaceholderResolver(TranslationKey.class, translatableStringPlaceholderResolver, 0)
                .weightedPlaceholderResolver(String.class, new StringPlaceholderResolver(), 0)
                .create(this.getClass().getClassLoader());
        } catch (UnscannableMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void configure() {
        // Base
        bind(Logger.class).toInstance(this.logger);
        bind(Path.class).toInstance(dataPath);

        // Service - Configuration
        bind(ConfigurationService.class).in(Singleton.class);
        bind(CrateService.class).in(Singleton.class);

        // Service - Messages
        bind(MessageRenderer.class).in(Singleton.class);
        bind(MessageSender.class).in(Singleton.class);
        bind(TranslatableStringPlaceholderResolver.class).in(Singleton.class);

        // Service - Translation
        bind(TranslationService.class).in(Singleton.class);
    }
}
