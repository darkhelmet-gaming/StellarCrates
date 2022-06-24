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

package network.darkhelmet.playcrates.listeners;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.crates.CrateService;

public class AbstractListener {
    /**
     * The configuration service.
     */
    protected final ConfigurationService configurationService;

    /**
     * The crate service.
     */
    protected final CrateService crateService;

    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     */
    public AbstractListener(
            ConfigurationService configurationService,
            CrateService crateService) {
        this.configurationService = configurationService;
        this.crateService = crateService;
    }
}
