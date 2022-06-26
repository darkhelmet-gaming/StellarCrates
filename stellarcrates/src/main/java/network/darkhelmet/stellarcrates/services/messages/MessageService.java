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

package network.darkhelmet.stellarcrates.services.messages;

import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;

import network.darkhelmet.stellarcrates.services.translation.TranslationKey;

import org.bukkit.command.CommandSender;

public interface MessageService {
    @Message("about")
    void about(CommandSender receiver, @Placeholder String version);

    @Message("crate-created")
    void crateCreated(CommandSender receiver);

    @Message("crate-given-self")
    void crateGivenSelf(CommandSender receiver);

    @Message("crate-key-created")
    void crateKeyCreated(CommandSender receiver);

    @Message("crate-key-given-self")
    void crateKeyGivenSelf(CommandSender receiver);

    @Message("error")
    void error(CommandSender receiver, @Placeholder TranslationKey message);

    @Message("import-complete")
    void importComplete(CommandSender receiver);

    @Message("location-added")
    void locationAdded(CommandSender receiver);

    @Message("reloaded-config")
    void reloadedConfig(CommandSender receiver);

    @Message("reloaded-locales")
    void reloadedLocales(CommandSender receiver);

    @Message("reward-added")
    void rewardAdded(CommandSender receiver);
}
