package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class deleteGlidingMapExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public deleteGlidingMapExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Too little arguments! check command usage");
            return false;
        }

        //args:
            //0 mapName

        int mapID = -1;
        for (int map = 0; map < gameData.getMaps().length; map++) {
            if (Objects.equals(args[0], gameData.getMaps()[map])) {
                mapID = map;
                break;
            }
        }
        if (mapID == -1) {
            sender.sendMessage("Could not find map by the name: |" + args[0]+"|");
            return false;
        }

        gameData.gliding_maps_loaded.remove(mapID);
        gameData.LoadMapsIntoConfig(nebplugin);
        sender.sendMessage("Successfully deleted the map "+args[0]+" from config!");
        sender.sendMessage("If you've deleted a default map and want to restore it, delete the config and restart the server");
        return true;
    }
}
