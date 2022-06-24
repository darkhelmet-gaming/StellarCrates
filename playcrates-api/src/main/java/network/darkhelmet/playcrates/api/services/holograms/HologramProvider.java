package network.darkhelmet.playcrates.api.services.holograms;

import org.bukkit.Location;

import java.util.List;

public interface HologramProvider {
    /**
     * Create the hologram.
     *
     * @param identifier String crate/hologram identifier
     * @param location The location
     * @param lines The lines
     */
    void create(String identifier, Location location, List<String> lines);

    /**
     * Destroy the hologram. Useful for reloads.
     */
    void destroy();
}
