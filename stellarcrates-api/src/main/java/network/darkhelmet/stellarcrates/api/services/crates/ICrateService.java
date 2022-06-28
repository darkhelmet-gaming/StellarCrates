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

import java.util.Map;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ICrateService {
    /**
     * Get a crate by identifier.
     *
     * @param identifier The crate identifier
     * @return The crate, if any
     */
    Optional<ICrate> crate(String identifier);

    /**
     * Get a crate instance for the given location.
     *
     * @param location The location
     * @return The crate instance, if any
     */
    Optional<ICrateInstance> crateInstance(Location location);

    /**
     * Get all loaded crates.
     */
    Map<String, ICrate> crates();

    /**
     * Crate a new crate.
     *
     * @param identifier The crate identifier
     * @param title The crate title
     * @return The crate
     */
    ICrate createCrate(String identifier, String title);

    /**
     * Delete a crate.
     *
     * @param crate The crate
     */
    void delete(ICrate crate);

    /**
     * Attempt to open a crate for the given player.
     *
     * @param crateInstance The crate instance
     * @param player The player
     */
    void openCrate(ICrateInstance crateInstance, Player player);
}
