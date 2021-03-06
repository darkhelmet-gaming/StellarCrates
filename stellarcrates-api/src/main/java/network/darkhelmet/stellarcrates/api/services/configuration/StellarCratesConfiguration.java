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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class StellarCratesConfiguration {
    @Comment("Enable plugin debug mode. Produces extra logging to help diagnose issues.")
    private boolean debug = false;

    @Comment("""
        The default locale for plugin messages. Messages given to players
        will use their client locale settings.
        """)
    private Locale defaultLocale = Locale.US;

    @Comment("The default weight used for rewards.")
    private double defaultWeight = 100;

    @Comment("Effects to play when a crate key is used incorrectly.")
    private KeyRejectionEffectsConfigutation keyRejectionEffects;

    @Comment("The version of the serializer.")
    private short serializerVersion;

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public StellarCratesConfiguration() {
        this.serializerVersion = mcVersion();
    }

    /**
     * Get the debug setting.
     *
     * @return True if debug enabled.
     */
    public boolean debug() {
        return debug;
    }

    /**
     * Get the default locale.
     */
    public Locale defaultLocale() {
        return defaultLocale;
    }

    /**
     * Get the default weight.
     *
     * @return The default weight.
     */
    public double defaultWeight() {
        return defaultWeight;
    }

    /**
     * Get the key rejection effects.
     *
     * @return The key rejection effects
     */
    public KeyRejectionEffectsConfigutation keyRejectionEffects() {
        return keyRejectionEffects;
    }

    /**
     * Get the serializer version.
     *
     * @return The serializer version
     */
    public short serializerVersion() {
        return serializerVersion;
    }

    /**
     * Parses the mc version as a short. Fed to nbt serializers.
     *
     * @return The mc version as a number
     */
    protected Short mcVersion() {
        Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]+)");
        Matcher matcher = pattern.matcher(Bukkit.getVersion());
        if (matcher.find()) {
            return Short.parseShort(matcher.group(1).replace(".", ""));
        }

        return null;
    }
}