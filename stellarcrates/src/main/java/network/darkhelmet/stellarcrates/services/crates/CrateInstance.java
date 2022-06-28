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

package network.darkhelmet.stellarcrates.services.crates;

import java.util.ArrayList;
import java.util.List;

import network.darkhelmet.stellarcrates.api.services.configuration.HologramConfiguration;
import network.darkhelmet.stellarcrates.api.services.configuration.ParticleColorMode;
import network.darkhelmet.stellarcrates.api.services.crates.ICrateInstance;
import network.darkhelmet.stellarcrates.api.services.holograms.CrateHologram;
import network.darkhelmet.stellarcrates.api.services.holograms.HologramProvider;
import network.darkhelmet.stellarcrates.services.holograms.providers.DecentHologramsProvider;
import network.darkhelmet.stellarcrates.utils.RandomUtil;

import org.bukkit.Location;

import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.PropertyType;
import xyz.xenondevs.particle.data.color.RegularColor;
import xyz.xenondevs.particle.task.TaskManager;

public class CrateInstance implements ICrateInstance {
    /**
     * The crate.
     */
    private final Crate crate;

    /**
     * The holograms.
     */
    private final List<CrateHologram> holograms = new ArrayList<>();

    /**
     * The hologram provider.
     *
     * <p>Note: we only support one hologram provider
     * so for now, this is just hard-coded.</p>
     */
    private final HologramProvider hologramProvider = new DecentHologramsProvider();

    /**
     * The instance location.
     */
    private final Location instanceLocation;

    /**
     * Particle task IDs.
     */
    private final List<Integer> particleTaskIds = new ArrayList<>();

    /**
     * Construct a new crate instance.
     *
     * @param crate The crate
     */
    public CrateInstance(Crate crate, Location instanceLocation) {
        this.crate = crate;
        this.instanceLocation = instanceLocation;

        createHologram();
    }

    @Override
    public Crate crate() {
        return crate;
    }

    /**
     * Creates a hologram.
     */
    private void createHologram() {
        HologramConfiguration hologramConfiguration = crate.config().hologram();
        if (hologramConfiguration == null) {
            return;
        }

        List<String> lines = hologramConfiguration.lines();
        if (lines.isEmpty()) {
            lines.add(crate.config().title());
        }

        // Center location inside block
        Location location = instanceLocation.clone().add(0.5d, 0.5d, 0.5d);

        // Apply offset
        location = location.add(hologramConfiguration.positionOffset());

        String identifier = String.format("%scrate%d%d%d",
            crate.config().identifier(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        final Location hologramLocation = location;
        holograms.add(hologramProvider.create(identifier, hologramLocation, lines));
    }

    @Override
    public Location location() {
        return instanceLocation;
    }

    @Override
    public void unload() {
        holograms.forEach(CrateHologram::destroy);
        particleTaskIds.forEach(id -> TaskManager.getTaskManager().stopTask(id));
    }

    /**
     * Tick this crate instance.
     */
    public void tick() {
        tickParticles();
    }

    /**
     * Play particles.
     */
    private void tickParticles() {
        crate.config().particles().forEach(particleConfiguration -> {
            // Start at the center of the block
            Location location = instanceLocation.clone().add(0.5, 0.5, 0.5);
            location.add(particleConfiguration.positionOffset());

            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            // Range
            double xr = particleConfiguration.particleRange().getX();
            double yr = particleConfiguration.particleRange().getY();
            double zr = particleConfiguration.particleRange().getZ();
            if (xr + yr + zr != 0) {
                x = RandomUtil.randomInRange(x - xr, x + xr);
                y = RandomUtil.randomInRange(y - yr, y + yr);
                z = RandomUtil.randomInRange(z - zr, z + zr);
            }
            Location spawnLoc = new Location(instanceLocation.getWorld(), x, y, z);

            ParticleEffect effect = particleConfiguration.effect();
            ParticleBuilder particle = new ParticleBuilder(effect, spawnLoc);

            // Colors
            if (effect.hasProperty(PropertyType.COLORABLE)) {
                RegularColor color = null;

                if (particleConfiguration.colorMode().equals(ParticleColorMode.STATIC)) {
                    color = particleConfiguration.color();
                } else {
                    int r = RandomUtil.random.nextInt(255);
                    int b = RandomUtil.random.nextInt(255);
                    int g = RandomUtil.random.nextInt(255);
                    color = new RegularColor(r, g, b);
                }

                particle.setParticleData(color);
            }

            // Amount
            particle.setAmount(particleConfiguration.amount());

            particle.display(player ->
                player.getLocation().distanceSquared(instanceLocation) < particleConfiguration.visibilityRange());
        });
    }
}
