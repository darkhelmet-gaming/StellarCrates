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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import network.darkhelmet.stellarcrates.api.services.configuration.CrateConfiguration;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface ICrate {
    /**
     * Get the crate configuration.
     *
     * @return The crate configuration.
     */
    CrateConfiguration config();

    /**
     * Create a new crate instance at the given location.
     *
     * @param location The location
     */
    ICrateInstance addLocation(Location location);

    /**
     * Add a new item stack reward.
     *
     * @param itemStack The item stack
     * @param weight The weight
     * @return The reward
     */
    IReward addReward(ItemStack itemStack, double weight);

    /**
     * Get the crate item (used for placement).
     *
     * @return The crate item
     */
    ItemStack crateItem();

    /**
     * Set the crate item used to place new instances of this crate.
     *
     * @param itemStack The crate item
     */
    ItemStack crateItem(ItemStack itemStack);

    /**
     * Get the crate instance at a given location.
     *
     * @param location The location
     * @return The crate instance, if any
     */
    Optional<ICrateInstance> crateInstance(Location location);

    /**
     * Get a map of crate instances.
     *
     * @return The map of crate instances
     */
    Map<Location, ICrateInstance> crateInstances();

    /**
     * Get the crate key.
     *
     * @return The crate key
     */
    ItemStack crateKey();

    /**
     * Set a key for this crate.
     *
     * @param itemStack The item stack
     * @return The key item stack
     */
    ItemStack crateKey(ItemStack itemStack);

    /**
     * Check whether the reward slots are full.
     *
     * @return True if reward count less than inventory size
     */
    boolean isFull();

    /**
     * Check an item stack against a crate key.
     *
     * @param itemStack The item stack
     * @return True if key matches
     */
    boolean keyMatches(ItemStack itemStack);

    /**
     * Choose a random weighted reward.
     *
     * @return The reward
     */
    Optional<IReward> randomReward();

    /**
     * Get the rewards.
     *
     * @return The rewards
     */
    List<IReward> rewards();

    /**
     * Unload all intances from the world.
     */
    void unloadInstances();
}
