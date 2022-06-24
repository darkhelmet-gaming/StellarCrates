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
import java.util.Objects;

import network.darkhelmet.playcrates.PlayCrates;
import network.darkhelmet.playcrates.api.services.holograms.HologramProvider;
import network.darkhelmet.playcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.playcrates.services.configuration.HologramConfiguration;
import network.darkhelmet.playcrates.services.configuration.KeyConfiguration;
import network.darkhelmet.playcrates.services.configuration.RewardConfiguration;
import network.darkhelmet.playcrates.services.configuration.SoundConfiguration;
import network.darkhelmet.playcrates.services.holograms.providers.DecentHologramsProvider;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
     * The hologram provider.
     */
    private HologramProvider hologramProvider;

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
            String msg = String.format("Placing crate `%s` at %s", config.identifier(), loc.toString());
            PlayCrates.getInstance().debug(msg);
            createHologram(loc.clone());
        });
    }

    /**
     * Add a location.
     *
     * @param location The location
     */
    public void addLocation(Location location) {
        config.locations().add(location);
        createHologram(location.clone());
    }

    /**
     * Add an itemstack as a reward.
     *
     * @param itemStack The item stack
     * @return The reward
     */
    public Reward addReward(ItemStack itemStack) {
        RewardConfiguration rewardConfiguration = new RewardConfiguration(itemStack);
        config.rewards().add(rewardConfiguration);

        Reward reward = rewardConfiguration.toReward();
        rewards.add(reward);

        return reward;
    }

    /**
     * Creates a hologram.
     *
     * @param location The base location
     */
    private void createHologram(Location location) {
        HologramConfiguration hologramConfiguration = config.hologram();
        if (hologramConfiguration == null) {
            return;
        }

        List<String> lines = hologramConfiguration.lines();
        if (lines.isEmpty()) {
            lines.add(config.title());
        }

        // Center location inside block
        location = location.add(0.5d, 0.5d, 0.5d);

        // Apply offset
        location = location.add(hologramConfiguration.positionOffset());

        String identifier = config.identifier() + "crate";

        // Note: we only support one hologram provider
        // so for now, this is just hard-coded
        this.hologramProvider = new DecentHologramsProvider();
        this.hologramProvider.create(identifier, location, lines);
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
            totalWeight += reward.config().weight();
        }

        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < rewards.size() - 1; ++idx) {
            r -= rewards.get(idx).config().weight();
            if (r <= 0.0) {
                break;
            }
        }

        return rewards.get(idx);
    }

    /**
     * Handle reloads.
     */
    public void onReload() {
        hologramProvider.destroy();
    }

    /**
     * Open the crate for a given player.
     *
     * @param player The player
     */
    public void open(Player player) {
        Reward reward = chooseWeightedRandomReward();
        reward.deliverTo(player);

        for (String command : reward.config().commands()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        for (SoundConfiguration onRewardSound : config.onRewardSounds()) {
            if (onRewardSound != null) {
                player.playSound(
                        player.getLocation(), onRewardSound.sound(), onRewardSound.volume(), onRewardSound.pitch());
            }
        }
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
     * Get the rewards.
     *
     * @return The rewards
     */
    public List<Reward> rewards() {
        return rewards;
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
