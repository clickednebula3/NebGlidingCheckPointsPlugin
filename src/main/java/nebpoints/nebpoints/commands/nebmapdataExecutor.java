package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.GlidingCheckpoint;
import nebpoints.nebpoints.dataFiles.GlidingMap;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class nebmapdataExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public nebmapdataExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (gameData.LoadMapsIntoConfig(nebplugin)) {
            sender.sendMessage("Successfully Listed Loaded Maps Into Config");
            return true;
        }
        sender.sendMessage("Failed Listing Loaded Maps Into Config Somehow");
        return false;
    }
}
