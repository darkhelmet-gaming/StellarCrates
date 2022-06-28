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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import network.darkhelmet.stellarcrates.StellarCrates;
import network.darkhelmet.stellarcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.KeyConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.RewardConfiguration;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class Crate {
    /**
     * The crate configuration.
     */
    private final CrateConfiguration config;

    /**
     * The rewards.
     */
    private final List<Reward> rewards;

    /**
     * A map of crate instances.
     */
    private final Map<Location, CrateInstance> crateInstances = new HashMap<>();

    /**
     * Construct a new crate.
     *
     * @param config The crate configuration
     * @param rewards The rewards
     */
    public Crate(CrateConfiguration config, List<Reward> rewards) {
        this.config = config;
        this.rewards = rewards;

        config.locations().forEach(loc -> {
            createCrateInstance(loc);

            String msg = String.format("Placing crate `%s` at %s", config.identifier(), loc.toString());
            StellarCrates.getInstance().debug(msg);
        });
    }

    /**
     * Add a location.
     *
     * @param location The location
     */
    public void addLocation(Location location) {
        config.locations().add(location);

        createCrateInstance(location);
    }

    /**
     * Add an itemstack as a reward.
     *
     * @param itemStack The item stack
     * @param weight The weight
     * @return The reward
     */
    public Reward addReward(ItemStack itemStack, double weight) {
        RewardConfiguration rewardConfiguration = new RewardConfiguration(itemStack, weight);
        config.rewards().add(rewardConfiguration);

        Reward reward = rewardConfiguration.toReward();
        rewards.add(reward);

        return reward;
    }

    /**
     * Get a crate instance by location.
     *
     * @param location The location
     * @return The crate instance
     */
    public Optional<CrateInstance> crateInstance(Location location) {
        return Optional.ofNullable(crateInstances.get(location));
    }

    /**
     * Get a map of crate instances.
     *
     * @return The map of crate instances
     */
    public Map<Location, CrateInstance> crateInstances() {
        return crateInstances;
    }

    /**
     * Get the crate item.
     *
     * @return The crate item
     */
    public ItemStack crateItem() {
        return config.crateItem().toItemStack();
    }

    /**
     * Get the crate key.
     *
     * @return The crate key
     */
    public ItemStack crateKey() {
        return config.key().toItemStack();
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
     * Crate a new crate instance at the given location.
     *
     * @param location The location
     * @return The crate instance
     */
    public CrateInstance createCrateInstance(Location location) {
        CrateInstance crateInstance = new CrateInstance(this, location);
        crateInstances.put(location, crateInstance);

        return crateInstance;
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
    public Optional<Reward> chooseWeightedRandomReward() {
        if (rewards.isEmpty()) {
            return Optional.empty();
        }

        double totalWeight = 0.0;
        for (Reward reward : rewards) {
            totalWeight += reward.config().weight();
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < rewards.size() - 1; ++idx) {
            r -= rewards.get(idx).config().weight();
            if (r <= 0.0) {
                break;
            }
        }

        return Optional.of(rewards.get(idx));
    }

    /**
     * Tear down all crate instances.
     */
    public void destroy() {
        crateInstances.values().forEach(CrateInstance::destroy);
    }

    /**
     * Get the crate configuration.
     *
     * @return The crate configuration
     */
    public CrateConfiguration config() {
        return config;
    }

    /**
     * Check whether the reward slots are full.
     *
     * @return True if reward count less than inventory size
     */
    public boolean isFull() {
        return rewards().size() >= config.inventoryRows() * 9;
    }

    /**
     * Get the rewards.
     *
     * @return The rewards
     */
    public List<Reward> rewards() {
        return rewards;
    }

    /**
     * Whether an item is a valid, place-able crate.
     *
     * @param itemStack The item stack
     * @return True if item can become a crate
     */
    public static boolean isValidCrateItem(ItemStack itemStack) {
        return itemStack.getType().isBlock();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Crate) obj;
        return Objects.equals(this.config, that.config) && Objects.equals(this.rewards, that.rewards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, rewards);
    }
}
