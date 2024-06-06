package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.GlidingMap;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class createGlidingMapExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public createGlidingMapExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Too little arguments! check usage");
            return true;
        }

        GlidingMap newMap = new GlidingMap(args[0], new double[]{Double.parseDouble(args[2]), Double.parseDouble(args[3])}, Boolean.parseBoolean(args[1]));

        gameData.gliding_maps_loaded.add(newMap);

        if (gameData.LoadMapsIntoConfig(nebplugin)) {
            sender.sendMessage("Successfully created "+args[0]+" map and added it to config!");
            sender.sendMessage("Remember to add checkpoints with /nebmapcreatecheckpoint");
            return true;
        }
        return false;
    }
}
