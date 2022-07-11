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

package network.darkhelmet.stellarcrates.services.holograms.providers;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import network.darkhelmet.stellarcrates.api.services.holograms.CrateHologram;

public class DecentHologram implements CrateHologram {
    /**
     * The hologram.
     */
    private final Hologram hologram;

    /**
     * Construct a new hologram.
     *
     * @param hologram The hologram
     */
    public DecentHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public void destroy() {
        DHAPI.removeHologram(this.hologram.getName());
        this.hologram.destroy();
        this.hologram.delete();
    }
}
