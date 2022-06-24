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
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.SubCommand;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.playcrates.services.configuration.RewardConfiguration;
import network.darkhelmet.playcrates.services.crates.Crate;
import network.darkhelmet.playcrates.services.crates.CrateService;
import network.darkhelmet.playcrates.services.gui.GuiService;
import network.darkhelmet.playcrates.services.messages.MessageService;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(value = "playcrates", alias = {"pc"})
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
    private Set<Material> transparent = new HashSet<>();

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
        transparent.add(Material.WATER);
        transparent.add(Material.LAVA);
    }

    /**
     * Run the create command.
     *
     * @param player The player
     * @param identifier The crate identifier
     * @param title The crate title
     */
    @SubCommand("addcrate")
    @Permission("playcrates.admin")
    public void onAddCrate(final Player player, String identifier, @Join(" ") String title) {
        Block target = player.getTargetBlock(transparent, 3);

        if (target.getType().equals(Material.AIR)) {
            // @todo use messages
            player.sendMessage("Invalid block");
        }

        CrateConfiguration crateConfig = new CrateConfiguration(identifier, title);
        configurationService.cratesConfiguration().crates().add(crateConfig);
        configurationService.saveAll();
        crateService.addCrate(crateConfig);

        // @todo use messages
        player.sendMessage("Crate created!");
    }

    /**
     * Run the addreward command.
     *
     * @param player The player
     * @param identifier The crate identifier
     */
    @SubCommand("addreward")
    @Permission("playcrates.admin")
    public void onAddReward(final Player player, final String identifier) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        RewardConfiguration reward = new RewardConfiguration(itemStack);
        Optional<CrateConfiguration> crateConfigurationOptional = configurationService
            .cratesConfiguration().crate(identifier);
        if (crateConfigurationOptional.isEmpty()) {
            // @todo use messages
            player.sendMessage("Invalid crate identifier");
            return;
        }

        crateConfigurationOptional.get().rewards().add(reward);
        configurationService.saveAll();

        // @todo use messages
        player.sendMessage("Reward added!");
    }

    /**
     * Run the givekey command.
     *
     * @param player The player
     * @param identifier The crate identifier
     */
    @SubCommand("givekey")
    @Permission("playcrates.admin")
    public void onGiveKey(final Player player, final String identifier) {
        Optional<Crate> crateOptional = crateService.crate(identifier);
        if (crateOptional.isEmpty()) {
            // @todo use messages
            player.sendMessage("Invalid crate identifier");
            return;
        }

        Crate crate = crateOptional.get();
        ItemStack crateKey = crate.config().key().toItemStack();

        player.getInventory().addItem(crateKey);

        player.sendMessage("Giving you a crate key");
    }

    /**
     * Run the open command.
     *
     * @param player The player
     * @param identifier The crate identifier
     */
    @SubCommand("open")
    @Permission("playcrates.open")
    public void onOpen(final Player player, final String identifier) {
        Optional<Crate> crateOptional = crateService.crate(identifier);
        if (crateOptional.isEmpty()) {
            // @todo use messages
            player.sendMessage("Invalid crate identifier");
            return;
        }

        Crate crate = crateOptional.get();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!crate.keyMatches(itemStack)) {
            // @todo use messages
            player.sendMessage("You don't have a valid crate key");
            return;
        }

        player.sendMessage("Opening crate!");
    }

    /**
     * Run the preview command.
     *
     * @param player The player
     * @param identifier The crate identifier
     */
    @SubCommand("preview")
    @Permission("playcrates.preview")
    public void onPreview(final Player player, final String identifier) {
        Optional<Crate> crateOptional = crateService.crate(identifier);
        if (crateOptional.isEmpty()) {
            // @todo use messages
            player.sendMessage("Invalid crate identifier");
            return;
        }

        guiService.open(crateOptional.get(), player);
    }

    /**
     * Run the setkey command.
     *
     * @param player The player
     * @param identifier The crate identifier
     */
    @SubCommand("setkey")
    @Permission("playcrates.preview")
    public void onSetKey(final Player player, final String identifier) {
        Optional<Crate> crateOptional = crateService.crate(identifier);
        if (crateOptional.isEmpty()) {
            // @todo use messages
            player.sendMessage("Invalid crate identifier");
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        crateOptional.get().createKey(itemStack);
        configurationService.saveAll();

        // @todo use messages
        player.sendMessage("Key created & set");
    }
}