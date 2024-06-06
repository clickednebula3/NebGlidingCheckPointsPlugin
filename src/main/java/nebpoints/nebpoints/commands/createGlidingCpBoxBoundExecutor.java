package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.GlidingCheckpoint;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class createGlidingCpBoxBoundExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public createGlidingCpBoxBoundExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Too little arguments! check command usage");
            return false;
        }

        //args:
            //0 mapName
            //1 <x>
            //2 <y>
            //3 <z>

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

        if (args.length < 4) {//autofill path
            if (sender instanceof Player) {
                Location location = ((Player) sender).getPlayer().getLocation();
                double[] coordsA = {location.getX(), location.getY(), location.getZ()};
                int cpIndex = gameData.gliding_maps_loaded.get(mapID).checkpoints.size()-1;
                gameData.gliding_maps_loaded.get(mapID).checkpoints.get(cpIndex).coordsA = coordsA;
                gameData.LoadMapsIntoConfig(nebplugin);
                sender.sendMessage("Successfully set the last checkpoint's starting box bounds to your position");
                sender.sendMessage("Remember to set ending box bounds!");
                return true;
            } else {
                sender.sendMessage("Too little arguments! Cannot set to your location if you're not a player");
                return false;
            }
        } else {//manual-fill path
            double[] coordsA = {Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3])};
            int cpIndex = gameData.gliding_maps_loaded.get(mapID).checkpoints.size()-1;
            gameData.gliding_maps_loaded.get(mapID).checkpoints.get(cpIndex).coordsA = coordsA;
            gameData.LoadMapsIntoConfig(nebplugin);
            sender.sendMessage("Successfully set the last checkpoint's starting box bounds to coords provided");
            sender.sendMessage("Remember to set ending box bounds!");
            return true;
        }
    }
}
