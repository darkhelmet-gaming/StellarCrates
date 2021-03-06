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
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import network.darkhelmet.stellarcrates.api.services.crates.ICrate;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateInstance;
import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.crates.Crate;
import network.darkhelmet.stellarcrates.services.crates.CrateService;
import network.darkhelmet.stellarcrates.services.crates.Reward;
import network.darkhelmet.stellarcrates.services.gui.GuiService;
import network.darkhelmet.stellarcrates.services.messages.MessageService;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(value = "stellarcrates", alias = {"crates"})
public class CrateCommand extends BaseCommand {
    /**
     * The message service.
     */
    private final ConfigurationService configurationService;

    /**
     * The crate service.
     */
    private final CrateService crateService;

    /**
     * The GUI service.
     */
    private final GuiService guiService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Cache a list of materials we consider transparent.
     */
    private final Set<Material> transparent = new HashSet<>();

    /**
     * Construct the crate command.
     *
     * @param configurationService The configuration service
     * @param crateService The crate service
     * @param guiService The GUI service
     * @param messageService The message service
     */
    @Inject
    public CrateCommand(
            ConfigurationService configurationService,
            CrateService crateService,
            GuiService guiService,
            MessageService messageService) {
        this.configurationService = configurationService;
        this.crateService = crateService;
        this.guiService = guiService;
        this.messageService = messageService;

        transparent.add(Material.AIR);
        transparent.add(Material.LAVA);
        transparent.add(Material.SNOW);
        transparent.add(Material.WATER);
    }

    /**
     * Lookup a crate using either an identifier (via command) or a block (player look).
     *
     * @param player The player
     * @param crateId The identifier
     * @return The crate, if any
     */
    private Optional<ICrate> crateFromIdOrTarget(Player player, String crateId) {
        ICrate crate = null;

        if (crateId != null) {
            Optional<ICrate> crateOptional = crateService.crate(crateId);
            if (crateOptional.isPresent()) {
                crate = crateOptional.get();
            }
        } else {
            Block block = player.getTargetBlock(transparent, 5);
            Optional<ICrateInstance> crateOptional = crateService.crateInstance(block.getLocation());
            if (crateOptional.isPresent()) {
                crate = crateOptional.get().crate();
            }
        }

        return Optional.ofNullable(crate);
    }

    /**
     * Run the create command.
     *
     * @param player The player
     * @param crateId The crate identifier
     * @param title The crate title
     */
    @SubCommand("addcrate")
    @Permission("stellarcrates.admin")
    public void onCreate(final Player player, String crateId, @Join(" ") String title) {
        if (crateService.crate(crateId).isPresent()) {
            messageService.errorCrateExists(player);
            return;
        }

        // Create the crate
        ICrate crate = crateService.createCrate(crateId, title);

        // Set the crate item
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!itemStack.getType().equals(Material.AIR)) {
            if (!Crate.isValidCrateItem(itemStack)) {
                messageService.errorInvalidCrateItem(player);
                return;
            }

            crate.crateItem(itemStack);
        }

        // Save
        configurationService.saveAll();

        messageService.crateCreated(player, crate);
    }

    /**
     * Run the addreward command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("addreward")
    @Permission("stellarcrates.admin")
    public void onAddReward(final Player player,
            @dev.triumphteam.cmd.core.annotation.Optional @Suggestion("crates") final String crateId) {
        Optional<ICrate> crateOptional = crateFromIdOrTarget(player, crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(player);
            return;
        }

        ICrate crate = crateOptional.get();
        if (crate.isFull()) {
            messageService.errorCrateFull(player, crate);
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!Reward.isValidRewardItem(itemStack)) {
            messageService.errorInvalidRewardItem(player);
            return;
        }

        crate.addReward(itemStack, configurationService.stellarCratesConfig().defaultWeight());
        configurationService.saveAll();

        messageService.rewardAdded(player, crate);
    }

    /**
     * Run the addloc command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("addloc")
    @Permission("stellarcrates.admin")
    public void onAddLocation(final Player player, final String crateId) {
        Block block = player.getTargetBlock(transparent, 5);
        if (block.getType().equals(Material.AIR)) {
            messageService.errorInvalidCrateBlock(player);
            return;
        }

        Optional<ICrate> crateOptional = crateService.crate(crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrateId(player);
            return;
        }

        crateOptional.get().addLocation(block.getLocation());
        configurationService.saveAll();

        messageService.locationAdded(player, crateOptional.get());
    }

    /**
     * Run the delete command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("deletecrate")
    @Permission("stellarcrates.admin")
    public void onDelete(final Player player,
         @dev.triumphteam.cmd.core.annotation.Optional @Suggestion("crates") final String crateId) {
        Optional<ICrate> crateOptional = crateFromIdOrTarget(player, crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(player);
            return;
        }

        ICrate crate = crateOptional.get();

        messageService.crateDeleted(player, crate);
        crateService.delete(crate);
        configurationService.saveAll();
    }

    /**
     * Run the deleteloc command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("deleteloc")
    @Permission("stellarcrates.admin")
    public void onDeleteLocation(final Player player, final String crateId) {
        Block block = player.getTargetBlock(transparent, 5);
        if (block.getType().equals(Material.AIR)) {
            messageService.errorInvalidCrateBlock(player);
            return;
        }

        Optional<ICrate> crateOptional = crateService.crate(crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrateId(player);
            return;
        }

        crateOptional.get().deleteLocation(block.getLocation());
        configurationService.saveAll();

        messageService.locationDeleted(player, crateOptional.get());
    }

    /**
     * Run the givecrate command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("givecrate")
    @Permission("stellarcrates.admin")
    public void onGiveCrate(final Player player,
          @dev.triumphteam.cmd.core.annotation.Optional @Suggestion("crates") final String crateId) {
        Optional<ICrate> crateOptional = crateFromIdOrTarget(player, crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(player);
            return;
        }

        player.getInventory().addItem(crateOptional.get().crateItem());

        messageService.crateGivenSelf(player, crateOptional.get());
    }

    /**
     * Run the givekey command.
     *
     * @param sender The sender
     * @param crateId The crate identifier
     */
    @SubCommand("givekey")
    @Permission("stellarcrates.admin")
    public void onGiveKey(
        final CommandSender sender,
        @Suggestion("crates") final String crateId,
        @Suggestion("players") final Player recipient,
        @dev.triumphteam.cmd.core.annotation.Optional Integer quantity
    ) {
        Optional<ICrate> crateOptional = crateService.crate(crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(sender);
            return;
        }

        quantity = quantity == null ? 1 : quantity;

        ItemStack itemStack = crateOptional.get().crateKey();
        itemStack.setAmount(quantity);
        recipient.getInventory().addItem(itemStack).values().forEach(drop -> {
            recipient.getWorld().dropItemNaturally(recipient.getLocation(), itemStack);
        });

        messageService.crateKeyGivenSelf(recipient, crateOptional.get(), quantity);
    }

    /**
     * Run the preview command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("preview")
    @Permission("stellarcrates.admin")
    public void onPreview(final Player player,
          @dev.triumphteam.cmd.core.annotation.Optional @Suggestion("crates") final String crateId) {
        Optional<ICrate> crateOptional = crateFromIdOrTarget(player, crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(player);
            return;
        }

        guiService.open((Crate) crateOptional.get(), player);
    }

    /**
     * Run the setkey command.
     *
     * @param player The player
     * @param crateId The crate identifier
     */
    @SubCommand("setkey")
    @Permission("stellarcrates.admin")
    public void onSetKey(final Player player,
             @dev.triumphteam.cmd.core.annotation.Optional @Suggestion("crates") final String crateId) {
        Optional<ICrate> crateOptional = crateFromIdOrTarget(player, crateId);
        if (crateOptional.isEmpty()) {
            messageService.errorInvalidCrate(player);
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        crateOptional.get().crateKey(itemStack);
        configurationService.saveAll();

        messageService.crateKeyCreated(player, crateOptional.get());
    }
}