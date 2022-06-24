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

import de.tr7zw.nbtapi.NBTItem;

import network.darkhelmet.playcrates.PlayCrates;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class KeyConfiguration {
    @Comment("NBT string")
    private String nbtString;

    /**
     * Empty constructor, needed for deserialization.
     */
    public KeyConfiguration() {}

    /**
     * Construct a new key configuration from an item stack.
     *
     * @param identifier The identifier
     * @param itemStack The item stack
     */
    public KeyConfiguration(String identifier, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        NamespacedKey usesKey = new NamespacedKey(PlayCrates.getInstance(), "cratekey");
        meta.getPersistentDataContainer().set(usesKey, PersistentDataType.STRING, identifier);
        itemStack.setItemMeta(meta);

        nbtString = NBTItem.convertItemtoNBT(itemStack).toString();
    }
}
