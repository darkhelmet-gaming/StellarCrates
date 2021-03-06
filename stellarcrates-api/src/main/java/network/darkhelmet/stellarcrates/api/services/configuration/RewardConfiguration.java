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

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class RewardConfiguration {
    @Comment("Commands to run when the award is given.")
    private List<String> commands = new ArrayList<>();

    @Comment("NBT string")
    private String nbtString;

    @Comment("""
            True if the item is what we give to players.
            If you use commands to give items, set this to false.
            """)
    private boolean givesDisplayItem = true;

    @Comment("""
            The weight to use in random calculations. Higher weights make the item more common.
            However, the final chance percentage is reliant on how many items are in the crate.
            """)
    private double weight;

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public RewardConfiguration() {}

    /**
     * Construct a new reward configuration from an item stack.
     *
     * @param itemStack The item stack
     * @param weight The weight
     */
    public RewardConfiguration(ItemStack itemStack, double weight) {
        this.nbtString = NBTItem.convertItemtoNBT(itemStack).toString();
        this.weight = weight;
    }

    /**
     * Generate an item from the reward configuration.
     *
     * @return The item stack
     */
    public ItemStack toItemStack() {
        return NBTItem.convertNBTtoItem(new NBTContainer(nbtString));
    }

    /**
     * Get the commands.
     *
     * @return The commands
     */
    public List<String> commands() {
        return commands;
    }

    /**
     * Whether to give the display item.
     *
     * @return The give display item toggle
     */
    public boolean givesDisplayItem() {
        return givesDisplayItem;
    }

    /**
     * Sets the give display item toggle.
     *
     * @param givesDisplayItem Whether to give display item
     */
    public void givesDisplayItem(boolean givesDisplayItem) {
        this.givesDisplayItem = givesDisplayItem;
    }

    /**
     * Get the weight.
     *
     * @return The weight
     */
    public double weight() {
        return weight;
    }
}
