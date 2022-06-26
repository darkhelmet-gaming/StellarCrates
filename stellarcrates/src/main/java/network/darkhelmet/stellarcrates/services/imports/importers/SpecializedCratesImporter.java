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

package network.darkhelmet.stellarcrates.services.imports.importers;

import java.util.ArrayList;
import java.util.List;

import me.PM2.customcrates.crates.PlacedCrate;

import network.darkhelmet.stellarcrates.services.configuration.ConfigurationService;
import network.darkhelmet.stellarcrates.services.configuration.CrateItemConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.HologramConfiguration;
import network.darkhelmet.stellarcrates.services.configuration.SoundConfiguration;
import network.darkhelmet.stellarcrates.services.crates.Crate;
import network.darkhelmet.stellarcrates.services.crates.CrateService;
import network.darkhelmet.stellarcrates.services.crates.Reward;
import network.darkhelmet.stellarcrates.services.imports.AbstractImporter;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class SpecializedCratesImporter extends AbstractImporter {
    /**
     * Construct the importer.
     *
     * @param configurationService The configuration service
     * @param crateService The crate service
     */
    public SpecializedCratesImporter(
            ConfigurationService configurationService,
            CrateService crateService) {
        super(configurationService, crateService);
    }

    @Override
    public void importData() {
        me.PM2.customcrates.crates.Crate.getLoadedCrates().values().forEach(scCrate -> {
            String identifier = scCrate.getName();
            String title = scCrate.getDisplayName();

            // Create the crate
            Crate crate = crateService.createCrate(identifier, title);

            // Crate item
            ItemStack crateItem = scCrate.getSettings().getCrateItemHandler().getItem(1);
            crate.config().crateItem(new CrateItemConfiguration(crate.config(), crateItem));

            // Key item
            ItemStack keyItem = scCrate.getSettings().getKeyItemHandler().getItem(1);
            crate.createKey(keyItem);

            // Hologram
            List<String> hologramLines = scCrate.getSettings().getHologram().getLines();
            double y = scCrate.getSettings().getHologramOffset();
            Vector positionOffset = new Vector(0, 1 + y, 0);
            HologramConfiguration hologramConfiguration = new HologramConfiguration(hologramLines, positionOffset);
            crate.config().hologram(hologramConfiguration);

            // Open sounds
            scCrate.getSettings().getSound().getSounds().forEach((key, value) -> {
                if (key.equalsIgnoreCase("OPEN")) {
                    value.forEach(soundData -> {
                        Sound sound = soundData.getSound().parseSound();
                        float pitch = soundData.getPitch();
                        float volume = soundData.getVolume();

                        SoundConfiguration soundConfiguration = new SoundConfiguration(sound, pitch, volume);
                        crate.config().onRewardSounds().add(soundConfiguration);
                    });
                }
            });

            // Rewards
            for (me.PM2.customcrates.crates.options.rewards.Reward scReward :
                    scCrate.getSettings().getReward().getCrateRewards()) {
                ItemStack rewardItem = scReward.getDisplayBuilder().getStack();

                // SC had a silly way of showing "chances" as lore
                // but only for items that didn't already have lore.
                // This is an attempt strip the crap out of the items,
                // that should never be hard-coded.
                ItemMeta meta = rewardItem.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    boolean shouldWipeLore = false;
                    for (String lore : meta.getLore()) {
                        if (lore.contains("Chance:")) {
                            shouldWipeLore = true;

                            break;
                        }
                    }

                    if (shouldWipeLore) {
                        meta.setLore(new ArrayList<>());
                        rewardItem.setItemMeta(meta);
                    }
                }

                Reward reward = crate.addReward(rewardItem, scReward.getChance());
                reward.config().givesDisplayItem(scReward.isGiveDisplayItem());

                scReward.getCommands().forEach(scCommand -> {
                    // Convert placeholders to PAPI
                    String cmd = scCommand.replace("%player%", "%player_name%");
                    reward.config().commands().add(cmd);
                });
            }
        });

        // Placement locations
        PlacedCrate.getPlacedCrates().values().forEach(placedCrate -> {
            String identifier = placedCrate.getCrate().getName();

            crateService.crate(identifier).ifPresent(crate -> crate.addLocation(placedCrate.getLocation()));
        });

        configurationService.saveAll(false);
    }
}
