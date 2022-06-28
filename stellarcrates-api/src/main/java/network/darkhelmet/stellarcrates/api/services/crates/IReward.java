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

package network.darkhelmet.stellarcrates.api.services.crates;

import network.darkhelmet.stellarcrates.api.services.configuration.RewardConfiguration;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface IReward {
    /**
     * Get the reward configuration.
     *
     * @return The reward configuration.
     */
    RewardConfiguration config();

    /**
     * Deliver the reward item to an inventory.
     *
     * @param inventory The inventory
     */
    void deliverTo(Inventory inventory);

    /**
     * Returns a new item stack.
     *
     * @return The item stack
     */
    ItemStack toItemStack();
}
