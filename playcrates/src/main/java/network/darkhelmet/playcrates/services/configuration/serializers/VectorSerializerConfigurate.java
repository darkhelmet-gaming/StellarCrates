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

import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class VectorSerializerConfigurate implements TypeSerializer<Vector> {
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private ConfigurationNode nonVirtualNode(
            final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public Vector deserialize(final Type type, final ConfigurationNode source) throws SerializationException {
        final double x = nonVirtualNode(source, X).getDouble();
        final double y = nonVirtualNode(source, Y).getDouble();
        final double z = nonVirtualNode(source, Z).getDouble();

        return new Vector(x, y, z);
    }

    @Override
    public void serialize(
            final Type type, final Vector loc, final ConfigurationNode target) throws SerializationException {
        if (loc == null) {
            target.raw(null);
            return;
        }

        target.node(X).set(loc.getX());
        target.node(Y).set(loc.getY());
        target.node(Z).set(loc.getZ());
    }
}