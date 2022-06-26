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

package network.darkhelmet.stellarcrates.utils;

import org.bukkit.inventory.Inventory;

public class InventoryUtil {
    /**
     * Prevent instantiation.
     */
    private InventoryUtil() {}

    /**
     * Check an inventory for empty slots.
     *
     * @param inventory The inventory
     * @return True if inventory is full
     */
    public static boolean isInventoryFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }
}
