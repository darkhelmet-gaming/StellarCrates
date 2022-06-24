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

package network.darkhelmet.playcrates.services.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;

import net.kyori.adventure.text.Component;

import network.darkhelmet.playcrates.services.crates.Crate;
import network.darkhelmet.playcrates.services.crates.Reward;

import org.bukkit.entity.Player;

public class GuiService {
    /**
     * Open a crate's inventory.
     *
     * @param crate The crate
     * @param player The player
     */
    public void open(Crate crate, Player player) {
        Gui gui = Gui.gui()
            .title(Component.text(crate.key()))
            .rows(6)
            .create();

        for (Reward reward : crate.rewards()) {
            gui.addItem(ItemBuilder.from(reward.item()).asGuiItem());
        }

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        gui.open(player);
    }
}
