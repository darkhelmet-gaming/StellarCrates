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

package network.darkhelmet.playcrates.services.configuration.serializers;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import xyz.xenondevs.particle.data.color.RegularColor;

public class RegularColorSerializerConfigurate implements TypeSerializer<RegularColor> {
    private static final String R = "r";
    private static final String G = "g";
    private static final String B = "b";

    private ConfigurationNode nonVirtualNode(
            final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public RegularColor deserialize(final Type type, final ConfigurationNode source) throws SerializationException {
        final float r = nonVirtualNode(source, R).getFloat();
        final float g = nonVirtualNode(source, G).getFloat();
        final float b = nonVirtualNode(source, B).getFloat();

        return new RegularColor((int)r * 255, (int)g * 255, (int)b * 255);
    }

    @Override
    public void serialize(
            final Type type, final RegularColor color, final ConfigurationNode target) throws SerializationException {
        if (color == null) {
            target.raw(null);
            return;
        }

        target.node(R).set(color.getRed());
        target.node(G).set(color.getGreen());
        target.node(B).set(color.getBlue());
    }
}