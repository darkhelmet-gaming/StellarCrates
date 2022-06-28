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

import org.bukkit.Sound;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class KeyRejectionEffectsConfigutation {
    @Comment("Toggle knockback effect.")
    private boolean knockbackEnabled = true;

    @Comment("Adjust the knockback strength.")
    private float knockbackMultiple = 0.5f;

    @Comment("List any sounds to be played.")
    private List<SoundConfiguration> sounds = new ArrayList<>();

    /**
     * Construct the key rejection configuration.
     */
    public KeyRejectionEffectsConfigutation() {
        sounds.add(new SoundConfiguration(Sound.ENTITY_VILLAGER_NO));
    }

    /**
     * Get whether knockback is enabled.
     *
     * @return True if knockback enabled.
     */
    public boolean knockbackEnabled() {
        return knockbackEnabled;
    }

    /**
     * Get the knockback multiple.
     *
     * @return The knockback multiple
     */
    public float knockbackMultiple() {
        return knockbackMultiple;
    }

    /**
     * Get sounds.
     *
     * @return The sounds
     */
    public List<SoundConfiguration> sounds() {
        return sounds;
    }
}
