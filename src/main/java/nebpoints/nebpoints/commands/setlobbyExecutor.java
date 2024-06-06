package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class setlobbyExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public setlobbyExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Location location = ((Player) sender).getPlayer().getLocation();
            FileConfiguration config = nebplugin.getConfig();

            config.set("lobby.world", location.getWorld().getName());
            config.set("lobby.x", location.getBlockX());
            config.set("lobby.y", location.getBlockY());
            config.set("lobby.z", location.getBlockZ());
            config.set("lobby.yaw", location.getYaw());
            config.set("lobby.pitch", location.getPitch());

            nebplugin.saveConfig();
            sender.sendMessage("new nebpoints lobby set at world \"" + location.getWorld().getName() + "\"");

            return true;
        }
        return false;
    }
}
