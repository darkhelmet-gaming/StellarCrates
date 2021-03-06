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

package network.darkhelmet.stellarcrates.commands;

import com.google.inject.Inject;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;

import java.io.IOException;

import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.crates.CrateService;
import network.darkhelmet.stellarcrates.services.messages.MessageService;
import network.darkhelmet.stellarcrates.services.translation.TranslationService;

import org.bukkit.command.CommandSender;

@Command(value = "stellarcrates", alias = {"crates"})
public class ReloadCommand extends BaseCommand {
    /**
     * The crate service.
     */
    private final CrateService crateService;

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
     * @param crateService The crate service
     * @param messageService The message service
     * @param translationService The translation service
     * @param configurationService The configuration service
     */
    @Inject
    public ReloadCommand(
            CrateService crateService,
            MessageService messageService,
            TranslationService translationService,
            ConfigurationService configurationService) {
        this.crateService = crateService;
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
    @Permission("stellarcrates.admin")
    public void onReloadConfig(final CommandSender sender) {
        configurationService.loadConfigurations();

        crateService.reload();

        messageService.reloadedConfig(sender);
    }

    /**
     * Reload the locale files.
     *
     * @param sender The command sender
     */
    @SubCommand("reloadlocales")
    @Permission("stellarcrates.admin")
    public void onReloadLocales(final CommandSender sender) {
        try {
            translationService.reloadTranslations();

            messageService.reloadedLocales(sender);
        } catch (IOException e) {
            messageService.errorReloadLocale(sender);
            e.printStackTrace();
        }
    }
}