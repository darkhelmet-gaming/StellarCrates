**StellarCrates**

A crates plugin for Spigot/Paper/etc servers, by viveleroi.

In development.

[Discord][discord]

## Features

- Add crates with weighted random rewards.
- Crate preview via left click/command.
- Create keys for crates.
- Sounds played on crate open.
- Localization.
- Robust configuration.
- Import from SpecializedCrates 2.x.

## Permissions

- `stellarcrates.admin` - Allows all commands.

## Planned

- Command structure revamp pending TriumphTeam support.
- Command permission expansion pending TriumphTeam support.
- Inventory animations.
- Crate particles/animations.

## Requirements

- [DecentHolograms][dh] 2.4.2+
- [PlaceholderAPI][papi] 2.11 
- Player extension:
  - `/papi ecloud download Player`
  - `/papi reload`

## Importing from SpecializedCrates 2.x

- You must run both plugins on a 1.18 server once.
- Use `/stellarcrates import` with SpecializedCrates 2.x active.
- When completed, removed SpecializedCrates and use MC 1.19 as desired.

Note: Please keep your SC2 configs as we'll import more data as features are added.

## API

*Maven repo coming soon.*

Your plugin can interface with StellarCrates via our API. In your plugins `onEnable` method, grab a reference through the plugin manager, and you're all set!

```
IStellarCrates stellarCrates = getServer().getPluginManager().getPlugin("StellarCrates");
ICrate crate = stellarCrates.crateService().createCrate("test", "Test Crate");
crate.addLocation(new Location(someWorld, 100, 64, 100));
crate.addReward(new ItemStack(Material.DIAMOND), 10);
```

Be sure to add `depend: [StellarCrates]` in your plugin.yml.

[discord]: https://discord.gg/Q6sHDfnMAc
[dh]: https://www.spigotmc.org/resources/decent-holograms-1-8-1-19-papi-support-no-dependencies.96927/
[papi]: https://www.spigotmc.org/resources/placeholderapi.6245/