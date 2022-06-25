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

import network.darkhelmet.playcrates.services.imports.ImportsService;
import network.darkhelmet.playcrates.services.messages.MessageService;

import org.bukkit.command.CommandSender;

@Command(value = "playcrates", alias = {"pc", "crates"})
public class ImportCommand extends BaseCommand {
    /**
     * The import service.
     */
    private final ImportsService importsService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Construct the import command.
     *
     * @param importsService The import service
     * @param messageService The message service
     */
    @Inject
    public ImportCommand(
            ImportsService importsService,
            MessageService messageService) {
        this.importsService = importsService;
        this.messageService = messageService;
    }

    /**
     * Run the import command.
     *
     * @param sender The command sender
     */
    @SubCommand("import")
    @Permission("playcrates.admin")
    public void onImport(final CommandSender sender) {
        importsService.importData("SpecializedCrates");

        // @todo use messages
        sender.sendMessage("Import complete");
    }
}
