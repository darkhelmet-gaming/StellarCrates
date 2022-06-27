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

import network.darkhelmet.stellarcrates.StellarCrates;

import org.bukkit.NamespacedKey;

public class NamespacedKeys {
    public static final NamespacedKey CRATE_ITEM = new NamespacedKey(StellarCrates.getInstance(), "crateitem");
    public static final NamespacedKey CRATE_KEY = new NamespacedKey(StellarCrates.getInstance(), "cratekey");
}
