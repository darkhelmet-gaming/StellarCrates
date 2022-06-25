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

import network.darkhelmet.playcrates.PlayCrates;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class CrateItemConfiguration {
    @Comment("The material.")
    private Material material;

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public CrateItemConfiguration() {}

    /**
     * Construct a new crate item configuration from an item stack.
     *
     * @param itemStack The item stack
     */
    public CrateItemConfiguration(ItemStack itemStack) {
        this.material = itemStack.getType();
    }

    /**
     * Construct a new crate item configuration from an item stack.
     *
     * @param crateId The crate id
     * @param itemStack The item stack
     */
    public CrateItemConfiguration(String crateId, ItemStack itemStack) {
        setPdc(crateId, itemStack);

        this.material = itemStack.getType();
    }

    /**
     * Create an item stack from the config.
     *
     * @return The item stack
     */
    public ItemStack toItemStack(String crateId) {
        return setPdc(crateId, new ItemStack(material));
    }

    /**
     * Set the persistent data container flag indicating this is a crate item.
     *
     * @param crateId The crate id
     * @param itemStack The item stack
     * @return The item stack
     */
    private ItemStack setPdc(String crateId, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        NamespacedKey usesKey = new NamespacedKey(PlayCrates.getInstance(), "crateitem");
        meta.getPersistentDataContainer().set(usesKey, PersistentDataType.STRING, crateId);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
