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

package network.darkhelmet.playcrates.listeners;

import com.google.inject.Inject;

import java.util.Optional;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.crates.Crate;
import network.darkhelmet.playcrates.services.crates.CrateService;
import network.darkhelmet.playcrates.services.gui.GuiService;
import network.darkhelmet.playcrates.services.messages.MessageService;
import network.darkhelmet.playcrates.services.translation.TranslationKey;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener extends AbstractListener implements Listener {
    /**
     * The GUI service.
     */
    private final GuiService guiService;

    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param crateService The crate service
     * @param guiService The GUI service
     * @param messageService The message service
     */
    @Inject
    public PlayerInteractListener(
            ConfigurationService configurationService,
            CrateService crateService,
            GuiService guiService,
            MessageService messageService) {
        super(configurationService, crateService, messageService);

        this.guiService = guiService;
    }

    /**
     * Listen to player interact events.
     *
     * @param event Tne event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        // Ignore if block is null (can't get location)
        // or if the event is fired for an off-hand click.
        // (Block will be null when clicking air)
        if (block == null || (event.getHand() != null && !event.getHand().equals(EquipmentSlot.HAND))) {
            return;
        }

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Optional<Crate> crateOptional = crateService.crate(block.getLocation());
            crateOptional.ifPresent(crate -> {
                guiService.open(crate, player);

                event.setCancelled(true);
            });

            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.AIR)) {
            return;
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Optional<Crate> crateOptional = crateService.crate(block.getLocation());
            crateOptional.ifPresent(crate -> {
                if (!crate.keyMatches(itemStack)) {
                    messageService.error(player, new TranslationKey("error-invalid-crate-key"));
                    return;
                }

                // Deduct item
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }

                // Open crate and reward player
                crate.open(player);

                event.setCancelled(true);
            });
        }
    }
}
