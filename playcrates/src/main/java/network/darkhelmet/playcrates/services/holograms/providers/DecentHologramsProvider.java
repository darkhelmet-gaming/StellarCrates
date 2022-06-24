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

package network.darkhelmet.playcrates.services.holograms.providers;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

import java.util.List;

import network.darkhelmet.playcrates.api.services.holograms.HologramProvider;

import org.bukkit.Location;

public class DecentHologramsProvider implements HologramProvider {
    /**
     * The hologram.
     */
    private Hologram hologram;

    @Override
    public void create(String identifier, Location location, List<String> lines) {
        this.hologram = DHAPI.createHologram(identifier, location, lines);
    }

    @Override
    public void destroy() {
        this.hologram.destroy();
    }
}
