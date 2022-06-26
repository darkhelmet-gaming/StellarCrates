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

package network.darkhelmet.stellarcrates.listeners;

import com.google.inject.Inject;

import java.util.Optional;

import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.crates.Crate;
import network.darkhelmet.stellarcrates.services.crates.CrateService;
import network.darkhelmet.stellarcrates.services.gui.GuiService;
import network.darkhelmet.stellarcrates.services.messages.MessageService;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Optional<Crate> crateOptional = crateService.crate(block.getLocation());
            crateOptional.ifPresent(crate -> {
                event.setCancelled(true);

                // Attempt to open the crate and reward player
                crateService.openCrate(crate, player);
            });
        }
    }
}
