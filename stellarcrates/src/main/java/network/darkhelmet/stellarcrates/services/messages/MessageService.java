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

import network.darkhelmet.stellarcrates.api.services.crates.ICrate;
import network.darkhelmet.stellarcrates.api.services.crates.IReward;

import org.bukkit.command.CommandSender;

public interface MessageService {
    @Message("about")
    void about(CommandSender receiver, @Placeholder String version);

    @Message("crate-created")
    void crateCreated(CommandSender receiver, @Placeholder ICrate crate);

    @Message("crate-deleted")
    void crateDeleted(CommandSender receiver, @Placeholder ICrate crate);

    @Message("crate-given-self")
    void crateGivenSelf(CommandSender receiver, @Placeholder ICrate crate);

    @Message("crate-key-created")
    void crateKeyCreated(CommandSender receiver, @Placeholder ICrate crate);

    @Message("crate-key-given-self")
    void crateKeyGivenSelf(CommandSender receiver, @Placeholder ICrate crate, @Placeholder Integer quantity);

    @Message("error-crate-exists")
    void errorCrateExists(CommandSender receiver);

    @Message("error-crate-full")
    void errorCrateFull(CommandSender receiver, @Placeholder ICrate crate);

    @Message("error-invalid-crate")
    void errorInvalidCrate(CommandSender receiver);

    @Message("error-invalid-crate-block")
    void errorInvalidCrateBlock(CommandSender receiver);

    @Message("error-invalid-crate-id")
    void errorInvalidCrateId(CommandSender receiver);

    @Message("error-invalid-crate-item")
    void errorInvalidCrateItem(CommandSender receiver);

    @Message("error-invalid-crate-key")
    void errorInvalidCrateKey(CommandSender receiver, @Placeholder ICrate crate);

    @Message("error-invalid-reward-item")
    void errorInvalidRewardItem(CommandSender receiver);

    @Message("error-inventory-full")
    void errorInventoryFull(CommandSender receiver);

    @Message("error-reload-locale")
    void errorReloadLocale(CommandSender receiver);

    @Message("import-complete")
    void importComplete(CommandSender receiver);

    @Message("location-added")
    void locationAdded(CommandSender receiver, @Placeholder ICrate crate);

    @Message("location-deleted")
    void locationDeleted(CommandSender receiver, @Placeholder ICrate crate);

    @Message("reloaded-config")
    void reloadedConfig(CommandSender receiver);

    @Message("reloaded-locales")
    void reloadedLocales(CommandSender receiver);

    @Message("reward-added")
    void rewardAdded(CommandSender receiver, @Placeholder ICrate crate);

    @Message("reward-given-self")
    void rewardGivenSelf(CommandSender receiver, @Placeholder IReward reward);
}
