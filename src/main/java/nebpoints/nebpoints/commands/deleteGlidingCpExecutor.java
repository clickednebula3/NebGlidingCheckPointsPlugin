package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class deleteGlidingCpExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public deleteGlidingCpExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

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
            sender.sendMessage("Could not find map by the name: " + args[0]);
            return false;
        }

        if (gameData.gliding_maps_loaded.get(mapID).checkpoints.size() > 0){
            int cpIndex = gameData.gliding_maps_loaded.get(mapID).checkpoints.size()-1;
            gameData.gliding_maps_loaded.get(mapID).checkpoints.remove(cpIndex);
            gameData.LoadMapsIntoConfig(nebplugin);
            sender.sendMessage("Successfully removed the last checkpoint from "+args[0]);
            return true;
        } else {
            sender.sendMessage("There's not enough checkpoints to remove one from "+args[0]+"!");
            return false;
        }
    }
}
