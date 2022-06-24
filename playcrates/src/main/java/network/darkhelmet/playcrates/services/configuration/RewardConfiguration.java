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

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;

import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.playcrates.services.crates.Reward;

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
            The weight to use in random calculations. Higher weights make the item more common.
            However, the final chance percentage is reliant on how many items are in the crate.
            """)
    private short weight = 100;

    /**
     * Argument-less constructor, needed for deserialization.
     */
    public RewardConfiguration() {}

    /**
     * Construct a new reward configuration from an item stack.
     *
     * @param itemStack The item stack
     */
    public RewardConfiguration(ItemStack itemStack) {
        nbtString = NBTItem.convertItemtoNBT(itemStack).toString();
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
     * Create a Reward object from this config.
     *
     * @return The reward
     */
    public Reward toReward() {
        return new Reward(this, toItemStack());
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
     * Get the NBT string.
     *
     * @return The NBT string
     */
    public String nbtString() {
        return nbtString;
    }

    /**
     * Get the weight.
     *
     * @return The weight
     */
    public short weight() {
        return weight;
    }
}
