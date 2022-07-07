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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import network.darkhelmet.stellarcrates.StellarCrates;
import network.darkhelmet.stellarcrates.api.services.configuration.CrateConfiguration;
import network.darkhelmet.stellarcrates.api.services.configuration.KeyConfiguration;
import network.darkhelmet.stellarcrates.api.services.configuration.RewardConfiguration;
import network.darkhelmet.stellarcrates.api.services.crates.ICrate;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateInstance;
import network.darkhelmet.stellarcrates.api.services.crates.IReward;
import network.darkhelmet.stellarcrates.utils.NamespacedKeys;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class Crate implements ICrate {
    /**
     * The crate configuration.
     */
    private final CrateConfiguration config;

    /**
     * The rewards.
     */
    private final List<IReward> rewards = new ArrayList<>();

    /**
     * A map of crate instances.
     */
    private final Map<Location, ICrateInstance> crateInstances = new HashMap<>();

    /**
     * Construct a new crate.
     *
     * @param config The crate configuration
     */
    public Crate(CrateConfiguration config) {
        this.config = config;

        config.rewards().forEach(rewardConfiguration -> {
            rewards.add(new Reward(rewardConfiguration, rewardConfiguration.toItemStack()));
        });

        config.locations().forEach(loc -> {
            createCrateInstance(loc);

            String msg = String.format("Placing crate `%s` at %s", config.identifier(), loc.toString());
            StellarCrates.getInstance().debug(msg);
        });
    }

    @Override
    public ICrateInstance addLocation(Location location) {
        config.locations().add(location);

        return createCrateInstance(location);
    }

    @Override
    public IReward addReward(ItemStack itemStack, double weight) {
        RewardConfiguration rewardConfiguration = new RewardConfiguration(itemStack, weight);
        config.rewards().add(rewardConfiguration);

        IReward reward = new Reward(rewardConfiguration, rewardConfiguration.toItemStack());
        rewards.add(reward);

        return reward;
    }

    @Override
    public CrateConfiguration config() {
        return config;
    }

    @Override
    public Optional<ICrateInstance> crateInstance(Location location) {
        return Optional.ofNullable(crateInstances.get(location));
    }

    @Override
    public Map<Location, ICrateInstance> crateInstances() {
        return crateInstances;
    }

    @Override
    public ItemStack crateItem() {
        return config.crateItem().toItemStack();
    }

    @Override
    public ItemStack crateItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            // Set PDC
            meta.getPersistentDataContainer().set(NamespacedKeys.CRATE_ITEM,
                PersistentDataType.STRING, config.identifier());

            // Set display name
            meta.setDisplayName(config.title());

            // Set meta
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    @Override
    public ItemStack crateKey() {
        return config.key().toItemStack();
    }

    @Override
    public ItemStack crateKey(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(NamespacedKeys.CRATE_KEY, PersistentDataType.STRING, config.identifier());
        itemStack.setItemMeta(meta);

        KeyConfiguration keyConfiguration = new KeyConfiguration(itemStack);

        config.key(keyConfiguration);

        return itemStack;
    }

    /**
     * Crate a new crate instance at the given location.
     *
     * @param location The location
     * @return The crate instance
     */
    private ICrateInstance createCrateInstance(Location location) {
        ICrateInstance crateInstance = new CrateInstance(this, location);
        crateInstances.put(location, crateInstance);

        return crateInstance;
    }

    @Override
    public boolean isFull() {
        return rewards().size() >= config.inventoryRows() * 9;
    }

    @Override
    public boolean keyMatches(ItemStack itemStack) {
        return config.key() == null || config.key().toItemStack().isSimilar(itemStack);
    }

    @Override
    public Optional<IReward> randomReward() {
        if (rewards.isEmpty()) {
            return Optional.empty();
        }

        double totalWeight = 0.0;
        for (IReward reward : rewards) {
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

    @Override
    public List<IReward> rewards() {
        return rewards;
    }

    @Override
    public void unloadInstances() {
        crateInstances.values().forEach(ICrateInstance::unload);
        crateInstances.clear();
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
