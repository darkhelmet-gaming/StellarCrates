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

package network.darkhelmet.stellarcrates.api.services.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class CratesConfiguration {
    private List<CrateConfiguration> crates = new ArrayList<>();

    /**
     * Get a crate configuration by its identifier.
     *
     * @param identifier The crate identifier
     * @return The crate configuration, if any
     */
    public Optional<CrateConfiguration> crate(final String identifier) {
        return crates.stream().filter(c -> c.identifier().equalsIgnoreCase(identifier)).findFirst();
    }

    /**
     * Get all crates.
     *
     * @return The crates
     */
    public List<CrateConfiguration> crates() {
        return crates;
    }
}
