package com.onarandombox.MultiverseNetherPortals.utils;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.enums.PortalType;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;

import java.util.logging.Level;

public class MVLinkChecker {
    private MultiverseNetherPortals plugin;
    private MVWorldManager worldManager;

    public MVLinkChecker(MultiverseNetherPortals plugin) {
        this.plugin = plugin;
        this.worldManager = this.plugin.getCore().getMVWorldManager();
    }

    public Location findNewTeleportLocation(Location fromLocation, String worldstring, Player p) {
        return findNewTeleportLocation(fromLocation, worldstring, p, null);
    }

    public Location findNewTeleportLocation(Location fromLocation, String worldstring, Player p, PortalType type) {
        MultiverseWorld tpto = this.worldManager.getMVWorld(worldstring);

        // create the new location from the old, and then modify as needed
        Location toLocation = fromLocation.clone();

        if (tpto == null) {
            this.plugin.log(Level.FINE, "Can't find world " + worldstring);
        } else if (!this.plugin.getCore().getMVPerms().canEnterWorld(p, tpto)) {
            this.plugin.log(Level.WARNING, "Player " + p.getName() + " can't enter world " + worldstring);
        } else if (!this.worldManager.isMVWorld(fromLocation.getWorld().getName())) {
            this.plugin.log(Level.WARNING, "World " + fromLocation.getWorld().getName() + " is not a Multiverse world");
        } else {
            this.plugin.log(Level.FINE, "Finding new teleport location for player " + p.getName() + " to world " + worldstring);

            if (type ==  PortalType.END) {
                // if portal is to the end, set the end spawn area
                toLocation.setX(0);
                toLocation.setY(62);
                toLocation.setZ(0);

            } else {
                // Set the output location to the same XYZ coords but different world
                double toScaling = this.worldManager.getMVWorld(tpto.getName()).getScaling();
                double fromScaling = this.worldManager.getMVWorld(fromLocation.getWorld().getName()).getScaling();

                toLocation = this.getScaledLocation(fromLocation, fromScaling, toScaling);
            }
            toLocation.setWorld(tpto.getCBWorld());
            return toLocation;
        }
        return null;
    }

    public void getNewTeleportLocation(PlayerPortalEvent event, Location fromLocation, String worldstring) {
        getNewTeleportLocation(event, fromLocation, worldstring, null);
    }

    public void getNewTeleportLocation(PlayerPortalEvent event, Location fromLocation, String worldstring, PortalType type) {
        MultiverseWorld tpto = this.worldManager.getMVWorld(worldstring);

        // create the new location from the old, and then modify as needed
        Location toLocation = fromLocation.clone();

        if (tpto == null) {
            this.plugin.log(Level.FINE, "Can't find " + worldstring);
        } else if (!this.plugin.getCore().getMVPerms().canEnterWorld(event.getPlayer(), tpto)) {
            this.plugin.log(Level.WARNING, "Player " + event.getPlayer().getName() + " can't enter world " + worldstring);
        } else if (!this.worldManager.isMVWorld(fromLocation.getWorld().getName())) {
            this.plugin.log(Level.WARNING, "World " + fromLocation.getWorld().getName() + " is not a Multiverse world");
        } else {
            this.plugin.log(Level.FINE, "Getting new teleport location for player " + event.getPlayer().getName() + " to world " + worldstring);

            if (type ==  PortalType.END) {
                // if portal is to the end, set the end spawn area
                toLocation.setX(0);
                toLocation.setY(62);
                toLocation.setZ(0);

            } else {
                // Set the output location to the same XYZ coords but different world
                double toScaling = this.worldManager.getMVWorld(tpto.getName()).getScaling();
                double fromScaling = this.worldManager.getMVWorld(event.getFrom().getWorld().getName()).getScaling();

                toLocation = this.getScaledLocation(fromLocation, fromScaling, toScaling);
            }
            toLocation.setWorld(tpto.getCBWorld());
        }
        event.setTo(toLocation);
    }

    private Location getScaledLocation(Location fromLocation, double fromScaling, double toScaling) {
        double scaling = fromScaling / toScaling;
        fromLocation.setX(fromLocation.getX() * scaling);
        fromLocation.setZ(fromLocation.getZ() * scaling);
        return fromLocation;
    }
}
