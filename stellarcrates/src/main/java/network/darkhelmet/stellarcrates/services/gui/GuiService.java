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

package network.darkhelmet.stellarcrates.services.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import network.darkhelmet.stellarcrates.services.crates.Crate;
import network.darkhelmet.stellarcrates.services.crates.Reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiService {
    /**
     * Open a crate's inventory.
     *
     * @param crate The crate
     * @param player The player
     */
    public void open(Crate crate, Player player) {
        Gui gui = Gui.gui()
            .title(Component.text(crate.config().title()))
            .rows(3)
            .create();

        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .hexColors().useUnusualXRepeatedCharacterHexFormat().build();

        double weightsTotal = 0;
        for (Reward reward : crate.rewards()) {
            weightsTotal += reward.config().weight();
        }

        for (Reward reward : crate.rewards()) {
            double winChance = (reward.config().weight() / weightsTotal) * 100;
            ItemStack itemStack = reward.toItemStack();

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                Component text = Component.text("Win Chance: ", TextColor.fromCSSHexString("#aaf786"))
                    .append(Component.text(String.format("%.2f%%", winChance), NamedTextColor.GOLD));

                lore.add("");
                lore.add(serializer.serialize(text));
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }

            gui.addItem(ItemBuilder.from(itemStack).asGuiItem());
        }

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });

        gui.open(player);
    }
}
