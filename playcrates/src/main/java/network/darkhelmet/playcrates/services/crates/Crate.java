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

import java.util.*;

import me.clip.placeholderapi.PlaceholderAPI;

import network.darkhelmet.playcrates.PlayCrates;
import network.darkhelmet.playcrates.api.services.holograms.CrateHologram;
import network.darkhelmet.playcrates.services.configuration.*;
import network.darkhelmet.playcrates.services.holograms.providers.DecentHologramsProvider;

import network.darkhelmet.playcrates.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.color.RegularColor;

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
     *
     * <p>Note: we only support one hologram provider
     * so for now, this is just hard-coded.</p>
     */
    private final DecentHologramsProvider hologramProvider = new DecentHologramsProvider();

    /**
     * The holograms.
     */
    private final List<CrateHologram> holograms = new ArrayList<>();

    /**
     * RNG
     */
    private Random random = new Random();

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
            playParticles();
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
        return addReward(itemStack, Reward.DEFAULT_WEIGHT);
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

        String identifier = String.format("%scrate%d%d%d",
            config.identifier(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        holograms.add(hologramProvider.create(identifier, location, lines));
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
     * Handle reloads.
     */
    public void onReload() {
        holograms.forEach(CrateHologram::destroy);
    }

    /**
     * Open the crate for a given player.
     *
     * @param player The player
     */
    public void open(Player player) {
        Optional<Reward> rewardOptional = chooseWeightedRandomReward();
        if (rewardOptional.isEmpty()) {
            return;
        }

        Reward reward = rewardOptional.get();

        // Give the item
        if (reward.config().givesDisplayItem()) {
            reward.deliverTo(player);
        }

        // Execute commands
        for (String command : reward.config().commands()) {
            String parsed = PlaceholderAPI.setPlaceholders(player, command);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }

        // Play sounds
        for (SoundConfiguration onRewardSound : config.onRewardSounds()) {
            if (onRewardSound != null) {
                player.playSound(
                    player.getLocation(), onRewardSound.sound(), onRewardSound.volume(), onRewardSound.pitch());
            }
        }
    }

    /**
     * Tick the crate. Plays particles or other running/repeating mechanics.
     */
    public void tick() {
        playParticles();
    }

    /**
     * Play particles. This executes every "tick".
     */
    private void playParticles() {
        config.particles().forEach(particleConfiguration -> {
            config.locations().forEach(loc -> {
                // Start at the center of the block
                Location location = loc.clone().add(0.5, 0.5, 0.5);
                location.add(particleConfiguration.positionOffset());

                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();

                // Range
                double xr = particleConfiguration.particleRange().getX();
                double yr = particleConfiguration.particleRange().getY();
                double zr = particleConfiguration.particleRange().getZ();
                if (xr + yr + zr != 0) {
                    x = RandomUtil.randomInRange(x - xr, x + xr);
                    y = RandomUtil.randomInRange(y - yr, y + yr);
                    z = RandomUtil.randomInRange(z - zr, z + zr);
                }
                Location spawnLoc = new Location(loc.getWorld(), x, y, z);

                ParticleEffect effect = particleConfiguration.effect();
                ParticleBuilder builder = new ParticleBuilder(effect, spawnLoc);

                // Colors
                if (effect.hasProperty(PropertyType.COLORABLE)) {
                    RegularColor color = null;

                    if (particleConfiguration.colorMode().equals(ParticleColorMode.STATIC)) {
                        color = particleConfiguration.color();
                    } else {
                        int r = random.nextInt(255);
                        int b = random.nextInt(255);
                        int g = random.nextInt(255);
                        color = new RegularColor(r, g, b);
                    }

                    builder.setParticleData(color);
                }

                // Amount
                builder.setAmount(particleConfiguration.amount());

                builder.display();
            });
        });
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
