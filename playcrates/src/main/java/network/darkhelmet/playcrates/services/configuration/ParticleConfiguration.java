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

import org.bukkit.util.Vector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.RegularColor;

@ConfigSerializable
public class ParticleConfiguration {
    @Comment("Amount of particles to spawn per cycle.")
    private int amount = 1;

    @Comment("Particle color. Not used if color mode is RANDOM")
    private RegularColor color = new RegularColor(255, 255, 255);

    @Comment("Sets the color. Use `STATIC` to use the configured color.")
    private ParticleColorMode colorMode = ParticleColorMode.RANDOM;

    @Comment("The particle effect.")
    private ParticleEffect effect;

    @Comment("""
            Particle positions default to the center of the crate's block space.
            Use this offset to shift its location.
            """)
    private Vector positionOffset = new Vector(0, 1, 0);

    @Comment("The range of possible particle locations.")
    private Vector particleRange = new Vector(1, 1, 1);

    /**
     * Get the amount.
     *
     * @return The amount
     */
    public int amount() {
        return amount;
    }

    /**
     * Get the particle effect.
     *
     * @return The particle effect
     */
    public ParticleEffect effect() {
        return effect;
    }

    /**
     * Get the position offset.
     *
     * @return The position offset
     */
    public Vector positionOffset() {
        return positionOffset;
    }

    /**
     * Get the color.
     *
     * @return The color
     */
    public RegularColor color() {
        return color;
    }

    /**
     * Get the color mode.
     *
     * @return The color mode
     */
    public ParticleColorMode colorMode() {
        return colorMode;
    }

    /**
     * The particle range.
     *
     * @return The range
     */
    public Vector particleRange() {
        return particleRange;
    }
}
