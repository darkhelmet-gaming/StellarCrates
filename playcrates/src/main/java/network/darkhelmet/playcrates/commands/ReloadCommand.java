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

package network.darkhelmet.playcrates.commands;

import com.google.inject.Inject;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;

import java.io.IOException;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.messages.MessageService;
import network.darkhelmet.playcrates.services.translation.TranslationKey;
import network.darkhelmet.playcrates.services.translation.TranslationService;

import org.bukkit.command.CommandSender;

@Command(value = "playcrates", alias = {"pc"})
public class ReloadCommand extends BaseCommand {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The translation service.
     */
    private final TranslationService translationService;

    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * Construct the reload command.
     *
     * @param messageService The message service
     */
    @Inject
    public ReloadCommand(
            MessageService messageService,
            TranslationService translationService,
            ConfigurationService configurationService) {
        this.messageService = messageService;
        this.translationService = translationService;
        this.configurationService = configurationService;
    }

    /**
     * Reload the config.
     *
     * @param sender The command sender
     */
    @SubCommand("reloadconfig")
    @Permission("playcrates.admin")
    public void onReloadConfig(final CommandSender sender) {
        configurationService.loadConfigurations();

        messageService.reloadedConfig(sender);
    }

    /**
     * Reload the locale files.
     *
     * @param sender The command sender
     */
    @SubCommand("reloadlocales")
    @Permission("playcrates.admin")
    public void onReloadLocales(final CommandSender sender) {
        try {
            translationService.reloadTranslations();

            messageService.reloadedLocales(sender);
        } catch (IOException e) {
            messageService.error(sender, new TranslationKey("reload-locale-error"));
            e.printStackTrace();
        }
    }
}