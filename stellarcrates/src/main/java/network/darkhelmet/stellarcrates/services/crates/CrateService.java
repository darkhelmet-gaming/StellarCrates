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

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.clip.placeholderapi.PlaceholderAPI;

import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.KeyRejectionEffectsConfigutation;
import network.darkhelmet.stellarcrates.services.configuration.RewardConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.SoundConfiguration;
import network.darkhelmet.stellarcrates.services.messages.MessageService;
import network.darkhelmet.stellarcrates.utils.InventoryUtil;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrateService {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * Cache of crates.
     */
    private final Map<String, Crate> crates = new HashMap<>();

    /**
     * Construct the crate service.
     *
     * @param configurationService The configuration service
     * @param messageService The message service
     */
    @Inject
    public CrateService(
            ConfigurationService configurationService,
            MessageService messageService) {
        this.configurationService = configurationService;
        this.messageService = messageService;

        reload();
    }

    /**
     * Add a new crate from a crate configuration.
     *
     * @param crateConfiguration The crate configuration
     */
    public Crate addCrate(CrateConfiguration crateConfiguration) {
        List<Reward> rewards = new ArrayList<>();
        for (RewardConfiguration rewardConfiguration : crateConfiguration.rewards()) {
            rewards.add(rewardConfiguration.toReward());
        }

        Crate crate = new Crate(crateConfiguration, rewards);
        crates.put(crateConfiguration.identifier(), crate);

        return crate;
    }

    /**
     * Get a crate by identifier.
     *
     * @param identifier The crate identifier
     * @return The crate, if any
     */
    public Optional<Crate> crate(String identifier) {
        return Optional.ofNullable(crates.get(identifier));
    }

    /**
     * Get a crate by location.
     *
     * @param location The location
     * @return The crate, if any
     */
    public Optional<Crate> crate(Location location) {
        return crates.values().stream().filter(c -> c.hasLocation(location)).findFirst();
    }

    /**
     * Get a crate instance by location.
     *
     * @param location The location
     * @return The crate, if any
     */
    public Optional<CrateInstance> crateInstance(Location location) {
        Optional<Crate> crateOptional = crate(location);
        if (crateOptional.isPresent()) {
            return crateOptional.get().crateInstance(location);
        }

        return Optional.empty();
    }

    /**
     * Crate a new crate.
     *
     * @param identifier The identifier
     * @param title The title
     * @return The crate
     */
    public Crate createCrate(String identifier, String title) {
        // Create the crate and register it
        CrateConfiguration crateConfig = new CrateConfiguration(identifier, title);
        configurationService.cratesConfiguration().crates().add(crateConfig);

        return addCrate(crateConfig);
    }

    /**
     * Get all loaded crates.
     */
    public Map<String, Crate> crates() {
        return crates;
    }

    /**
     * Attempt to open a crate for the given player.
     *
     * @param crateInstance The crate instance
     * @param player The player
     */
    public void openCrate(CrateInstance crateInstance, Player player) {
        Optional<Reward> rewardOptional = crateInstance.crate().chooseWeightedRandomReward();
        if (rewardOptional.isEmpty()) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        // Match key
        if (!crateInstance.crate().keyMatches(itemStack)) {
            messageService.errorInvalidCrateKey(player, crateInstance.crate());

            playKeyRejectionEffects(crateInstance, player);

            return;
        }

        // Ensure inventory has room
        if (InventoryUtil.isInventoryFull(player.getInventory())) {
            messageService.errorInventoryFull(player);
            return;
        }

        // Deduct key
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        Reward reward = rewardOptional.get();

        // Give the reward item
        if (reward.config().givesDisplayItem()) {
            reward.deliverTo(player);
        }

        // Execute commands
        for (String command : reward.config().commands()) {
            String parsed = PlaceholderAPI.setPlaceholders(player, command);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }

        // Play sounds
        for (SoundConfiguration onRewardSound : crateInstance.crate().config().onRewardSounds()) {
            if (onRewardSound != null) {
                player.playSound(
                    player.getLocation(), onRewardSound.sound(), onRewardSound.volume(), onRewardSound.pitch());
            }
        }

        // Message
        messageService.rewardGivenSelf(player, reward);
    }

    /**
     * Play key rejection effects.
     *
     * @param crateInstance The crate instance
     * @param player The player
     */
    private void playKeyRejectionEffects(CrateInstance crateInstance, Player player) {
        KeyRejectionEffectsConfigutation keyRejectConfig =
            configurationService.stellarCratesConfig().keyRejectionEffects();

        // Sounds
        keyRejectConfig.sounds().forEach(soundConfiguration -> {
            player.playSound(crateInstance.location(),
                soundConfiguration.sound(), soundConfiguration.volume(), soundConfiguration.pitch());
        });

        if (keyRejectConfig.knockbackEnabled()) {
            player.setVelocity(player.getLocation().getDirection().multiply(-1 * keyRejectConfig.knockbackMultiple()));
        }
    }

    /**
     * Reloads all crate items from their configs.
     */
    public void reload() {
        crates.values().forEach(Crate::onReload);

        crates.clear();

        for (CrateConfiguration crateConfiguration : configurationService.cratesConfiguration().crates()) {
            addCrate(crateConfiguration);
        }
    }
}
