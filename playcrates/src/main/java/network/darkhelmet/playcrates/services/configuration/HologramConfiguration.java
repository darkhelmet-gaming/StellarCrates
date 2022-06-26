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

package network.darkhelmet.playcrates.services.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HologramConfiguration {
    @Comment("Set lines of text.")
    private List<String> lines = new ArrayList<>();

    @Comment("""
            Hologram positions default to the center of the crate's block space.
            Use this offset to shift its location.
            """)
    private Vector positionOffset = new Vector(0, 1, 0);

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public HologramConfiguration() {}

    /**
     * Construct a hologram configuration.
     *
     * @param lines The lines
     * @param positionOffset The position offset
     */
    public HologramConfiguration(List<String> lines, Vector positionOffset) {
        this.lines = lines;
        this.positionOffset = positionOffset;
    }

    /**
     * Get the lines.
     *
     * @return The lines
     */
    public List<String> lines() {
        return lines;
    }

    /**
     * Get the position offset.
     *
     * @return The position offset
     */
    public Vector positionOffset() {
        return positionOffset;
    }
}
