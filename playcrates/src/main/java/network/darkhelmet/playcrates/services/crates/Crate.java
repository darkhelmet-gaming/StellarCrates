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

package network.darkhelmet.playcrates.services.crates;

import java.util.List;

import network.darkhelmet.playcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.playcrates.services.configuration.KeyConfiguration;
import network.darkhelmet.playcrates.services.configuration.RewardConfiguration;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public record Crate(CrateConfiguration config, List<Reward> rewards) {
    /**
     * Add a location.
     *
     * @param location The location
     */
    public void addLocation(Location location) {
        config.locations().add(location);
    }

    /**
     * Add an itemstack as a reward.
     *
     * @param itemStack The item stack
     */
    public void addReward(ItemStack itemStack) {
        RewardConfiguration rewardConfiguration = new RewardConfiguration(itemStack);
        config.rewards().add(rewardConfiguration);
        rewards.add(rewardConfiguration.toReward());
    }

    /**
     * Crate a new key configuration for this crate.
     *
     * @param itemStack The item stack
     * @return The key configuration
     */
    public KeyConfiguration createKey(ItemStack itemStack) {
        KeyConfiguration keyConfiguration = new KeyConfiguration(config.identifier(), itemStack);

        config.key(keyConfiguration);

        return keyConfiguration;
    }

    /**
     * Check if crate instance exists a given location.
     *
     * @param location The location
     * @return True if crate instance exists at location
     */
    public boolean hasLocation(Location location) {
        return config.locations().contains(location);
    }

    /**
     * Check an item stack against a crate key.
     *
     * @param itemStack The item stack
     * @return True if key matches
     */
    public boolean keyMatches(ItemStack itemStack) {
        return config.key() == null || config.key().toItemStack().isSimilar(itemStack);
    }

    /**
     * Choose a random weighted reward.
     *
     * @return The reward
     */
    public Reward chooseWeightedRandomReward() {
        double totalWeight = 0.0;
        for (Reward reward : rewards) {
            totalWeight += reward.weight();
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < rewards.size() - 1; ++idx) {
            r -= rewards.get(idx).weight();
            if (r <= 0.0) {
                break;
            }
        }

        return rewards.get(idx);
    }
}
