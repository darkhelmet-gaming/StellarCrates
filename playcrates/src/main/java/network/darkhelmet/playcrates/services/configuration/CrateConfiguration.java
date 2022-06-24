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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class CrateConfiguration {
    @Comment("An identifier is a one-word name for a create, to be used in commands etc.")
    private String identifier;

    @Comment("A list of rewards in this crate.")
    private List<RewardConfiguration> rewards = new ArrayList<>();

    @Comment("Title shown in the UI and any holograms.")
    private String title;

    public CrateConfiguration() {}

    /**
     * Construct a crate config.
     *
     * @param identifier The identifier
     * @param title The title
     */
    public CrateConfiguration(String identifier, String title) {
        this.identifier = identifier;
        this.title = title;
    }

    /**
     * Get the identifier.
     *
     * @return The identifier
     */
    public String identifier() {
        return identifier;
    }

    /**
     * Get all rewards.
     *
     * @return The rewards
     */
    public List<RewardConfiguration> rewards() {
        return rewards;
    }

    /**
     * Get the title.
     *
     * @return The title
     */
    public String title() {
        return title;
    }
}
