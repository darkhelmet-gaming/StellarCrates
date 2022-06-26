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

package network.darkhelmet.stellarcrates.services.crates;

import network.darkhelmet.stellarcrates.services.configuration.RewardConfiguration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record Reward(RewardConfiguration config, ItemStack itemStack) {
    /**
     * The default weight.
     */
    public static final double DEFAULT_WEIGHT = 100;

    /**
     * Deliver the reward contents to the player.
     *
     * <p>If an item stack, delivers to their inventory.</p>
     *
     * @param player The player
     */
    public void deliverTo(Player player) {
        player.getInventory().addItem(itemStack);
    }

    /**
     * Check if an item stack is a valid reward.
     *
     * @param itemStack The item stack
     * @return True if valid
     */
    public static boolean isValidRewardItem(ItemStack itemStack) {
        return !itemStack.getType().equals(Material.AIR);
    }
}
