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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.clip.placeholderapi.PlaceholderAPI;

import network.darkhelmet.stellarcrates.api.services.configuration.CrateConfiguration;
import network.darkhelmet.stellarcrates.api.services.configuration.KeyRejectionEffectsConfigutation;
import network.darkhelmet.stellarcrates.api.services.configuration.SoundConfiguration;
import network.darkhelmet.stellarcrates.api.services.crates.ICrate;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateInstance;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateService;
import network.darkhelmet.stellarcrates.api.services.crates.IReward;
import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.messages.MessageService;
import network.darkhelmet.stellarcrates.utils.InventoryUtil;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateService implements ICrateService {
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
    private final Map<String, ICrate> crates = new HashMap<>();

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
    private Crate addCrate(CrateConfiguration crateConfiguration) {
        Crate crate = new Crate(crateConfiguration);
        crates.put(crateConfiguration.identifier(), crate);

        return crate;
    }

    @Override
    public Optional<ICrate> crate(String identifier) {
        return Optional.ofNullable(crates.get(identifier));
    }

    @Override
    public Optional<ICrateInstance> crateInstance(Location location) {
        for (ICrate crate : crates().values()) {
            return crate.crateInstance(location);
        }

        return Optional.empty();
    }

    @Override
    public Map<String, ICrate> crates() {
        return crates;
    }

    @Override
    public ICrate createCrate(String identifier, String title) {
        ItemStack defaultKey = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = defaultKey.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title + " Key");
            defaultKey.setItemMeta(meta);
        }

        // Create the crate and register it
        CrateConfiguration crateConfig = new CrateConfiguration(identifier, title, defaultKey);
        configurationService.crateConfigurations().add(crateConfig);

        return addCrate(crateConfig);
    }

    @Override
    public void delete(ICrate crate) {
        crate.unloadInstances();

        crates.remove(crate.config().identifier());

        configurationService.crateConfigurations().remove(crate.config());
    }

    @Override
    public void openCrate(ICrateInstance crateInstance, Player player) {
        Optional<IReward> rewardOptional = crateInstance.crate().randomReward();
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

        IReward reward = rewardOptional.get();

        // Give the reward item
        if (reward.config().givesDisplayItem()) {
            reward.deliverTo(player.getInventory());
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
    private void playKeyRejectionEffects(ICrateInstance crateInstance, Player player) {
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
        crates.values().forEach(ICrate::unloadInstances);

        crates.clear();

        for (CrateConfiguration crateConfiguration : configurationService.crateConfigurations()) {
            addCrate(crateConfiguration);
        }
    }
}
