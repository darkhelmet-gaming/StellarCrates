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

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.configuration.CrateConfiguration;
import network.darkhelmet.playcrates.services.configuration.RewardConfiguration;

public class CrateService {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * Cache of crates.
     */
    private Map<String, Crate> crates = new HashMap<>();

    /**
     * Construct the crate service.
     *
     * @param configurationService The configuration service.
     */
    @Inject
    public CrateService(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        for (CrateConfiguration crateConfiguration : configurationService.cratesConfiguration().crates()) {
            addCrate(crateConfiguration);
        }
    }

    /**
     * Add a new crate from a crate configuration.
     *
     * @param crateConfiguration The crate configuration
     */
    public void addCrate(CrateConfiguration crateConfiguration) {
        List<Reward> rewards = new ArrayList<>();
        for (RewardConfiguration rewardConfiguration : crateConfiguration.rewards()) {
            rewards.add(rewardConfiguration.toReward());
        }

        Crate crate = new Crate(crateConfiguration, rewards);
        crates.put(crateConfiguration.identifier(), crate);
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
}
