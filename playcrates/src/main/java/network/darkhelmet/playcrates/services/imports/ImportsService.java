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

package network.darkhelmet.playcrates.services.imports;

import com.google.inject.Inject;

import network.darkhelmet.playcrates.services.configuration.ConfigurationService;
import network.darkhelmet.playcrates.services.crates.CrateService;
import network.darkhelmet.playcrates.services.imports.importers.SpecializedCratesImporter;

public class ImportsService {
    /**
     * The configuration service.
     */
    private final ConfigurationService configurationService;

    /**
     * The crate service.
     */
    private final CrateService crateService;

    /**
     * Construct the import service.
     *
     * @param configurationService The configuration service
     * @param crateService The crate service
     */
    @Inject
    public ImportsService(
            ConfigurationService configurationService,
            CrateService crateService) {
        this.configurationService = configurationService;
        this.crateService = crateService;
    }

    /**
     * Import from specialized crates.
     *
     * <p>Note, we can easily refactor this to expand which plugins we support.</p>
     */
    public void importData(String pluginName) {
        if (pluginName.equalsIgnoreCase("SpecializedCrates")) {
            SpecializedCratesImporter importer = new SpecializedCratesImporter(configurationService, crateService);
            importer.importData();
        }
    }
}
