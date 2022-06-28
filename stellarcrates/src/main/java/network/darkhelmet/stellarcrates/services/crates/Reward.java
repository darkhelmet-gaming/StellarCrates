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

import network.darkhelmet.stellarcrates.api.services.configuration.RewardConfiguration;
import network.darkhelmet.stellarcrates.api.services.crates.IReward;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class Reward implements IReward {
    /**
     * The reward configuration.
     */
    private final RewardConfiguration config;

    /**
     * The item stack.
     */
    private final ItemStack itemStack;

    /**
     * Construct a reward.
     *
     * @param config The reward configuration
     * @param itemStack The item stack
     */
    public Reward(RewardConfiguration config, ItemStack itemStack) {
        this.config = config;
        this.itemStack = itemStack;
    }

    @Override
    public void deliverTo(Inventory inventory) {
        inventory.addItem(itemStack);
    }

    /**
     * Get the reward configuration.
     *
     * @return The reward configuration
     */
    public RewardConfiguration config() {
        return config;
    }

    @Override
    public ItemStack toItemStack() {
        return itemStack.clone();
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
