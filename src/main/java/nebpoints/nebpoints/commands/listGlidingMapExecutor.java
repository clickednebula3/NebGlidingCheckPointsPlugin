package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class listGlidingMapExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public listGlidingMapExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(gameData.ListMaps());
        return true;
    }
}
