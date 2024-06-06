package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.GlidingCheckpoint;
import nebpoints.nebpoints.dataFiles.GlidingMap;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class createGlidingCpRespawnExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public createGlidingCpRespawnExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

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
            //4 <yaw>
            //5 <pitch>

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

        //autofill path
        if (args.length < 4) {
            if (sender instanceof Player) {
                Location location = ((Player) sender).getPlayer().getLocation();
                double[] coordsRespawn = {location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()};
                GlidingCheckpoint checkpoint = new GlidingCheckpoint(
                        coordsRespawn,
                        new double[]{coordsRespawn[0]-5, coordsRespawn[1]-5, coordsRespawn[2]-5},
                        new double[]{coordsRespawn[0]+5, coordsRespawn[1]+5, coordsRespawn[2]+5}
                );
                gameData.gliding_maps_loaded.get(mapID).checkpoints.add(checkpoint);
                gameData.LoadMapsIntoConfig(nebplugin);
                sender.sendMessage("Successfully created checkpoint and set its respawn to your position");
                sender.sendMessage("Remember to set box bounds!");
                return true;
            } else {
                sender.sendMessage("Too little arguments! Cannot set to your location if you're not a player");
                return false;
            }
        }

        //manual-fill path
        if (args.length < 6) {//without yaw/pitch
            double[] coordsRespawn = {Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), 0.0, 0.0};
            GlidingCheckpoint checkpoint = new GlidingCheckpoint(
                    coordsRespawn,
                    new double[]{coordsRespawn[0]-5, coordsRespawn[1]-5, coordsRespawn[2]-5},
                    new double[]{coordsRespawn[0]+5, coordsRespawn[1]+5, coordsRespawn[2]+5}
            );
            gameData.gliding_maps_loaded.get(mapID).checkpoints.add(checkpoint);
            gameData.LoadMapsIntoConfig(nebplugin);
            sender.sendMessage("Successfully created checkpoint and set its respawn to coords provided");
            sender.sendMessage("Remember to set box bounds!");
            return true;
        } else {//with yaw/pitch
            double[] coordsRespawn = {Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5])};
            GlidingCheckpoint checkpoint = new GlidingCheckpoint(
                    coordsRespawn,
                    new double[]{coordsRespawn[0]-5, coordsRespawn[1]-5, coordsRespawn[2]-5},
                    new double[]{coordsRespawn[0]+5, coordsRespawn[1]+5, coordsRespawn[2]+5}
            );
            gameData.gliding_maps_loaded.get(mapID).checkpoints.add(checkpoint);
            gameData.LoadMapsIntoConfig(nebplugin);
            sender.sendMessage("Successfully created checkpoint and set its respawn to location provided");
            sender.sendMessage("Remember to set box bounds!");
            return true;
        }
    }
}
