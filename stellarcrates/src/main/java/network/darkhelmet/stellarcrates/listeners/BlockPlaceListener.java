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

import network.darkhelmet.stellarcrates.api.services.crates.ICrate;
import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.crates.CrateService;
import network.darkhelmet.stellarcrates.services.messages.MessageService;
import network.darkhelmet.stellarcrates.utils.NamespacedKeys;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener extends AbstractListener implements Listener {
    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param crateService The crate service
     * @param messageService The message service
     */
    @Inject
    public BlockPlaceListener(
            ConfigurationService configurationService,
            CrateService crateService,
            MessageService messageService) {
        super(configurationService, crateService, messageService);
    }

    /**
     * Listen to block place events.
     *
     * @param event Tne event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType().equals(Material.AIR)) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(NamespacedKeys.CRATE_ITEM, PersistentDataType.STRING)) {
            String crateId = pdc.get(NamespacedKeys.CRATE_ITEM, PersistentDataType.STRING);

            Optional<ICrate> crateOptional = crateService.crate(crateId);
            if (crateOptional.isEmpty()) {
                messageService.errorInvalidCrate(player);
                return;
            }

            crateOptional.get().addLocation(event.getBlock().getLocation());
            configurationService.saveAll();
        }
    }
}
