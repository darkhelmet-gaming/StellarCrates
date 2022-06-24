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

import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class SoundConfiguration {
    @Comment("The sound. View a list at https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
    private Sound sound;

    private float pitch = 1;

    private float volume = 1;

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public SoundConfiguration() {}

    /**
     * Construct a sound configuration.
     *
     * @param sound The sound
     */
    public SoundConfiguration(Sound sound) {
        this.sound = sound;
    }

    /**
     * Get the sound.
     *
     * @return The sound
     */
    public Sound sound() {
        return sound;
    }

    /**
     * Get the pitch.
     *
     * @return The pitch
     */
    public float pitch() {
        return pitch;
    }

    /**
     * Get the volume.
     *
     * @return The volume
     */
    public float volume() {
        return volume;
    }
}
