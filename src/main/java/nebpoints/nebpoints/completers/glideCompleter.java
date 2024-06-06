package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class glideCompleter implements TabCompleter {
    gameData gameData;
    boolean doCoords;
    boolean doYawPitch;
    public glideCompleter(gameData gData, boolean doCoords, boolean doYawPitch) {
        gameData = gData;
        this.doCoords = doCoords;
        this.doYawPitch = doYawPitch;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String[] allMaps = gameData.getMaps();
            String[] allMapsR = gameData.getRankedMaps();
            List<String> mapTab = new ArrayList<>();

            for(int i = 0; i < allMaps.length; i++) {
                mapTab.add(allMaps[i]);
            }
            if (sender instanceof Player && !sender.hasPermission("nebpoints.gliding_ranked")) {
                for(int i = 0; i < allMapsR.length; i++) {
                    mapTab.remove(allMapsR[i]);
                }
            }

            return mapTab;
        }
        if (args.length > 1 && args.length < 5 && doCoords) {
            List<String> coord = new ArrayList<>();
            coord.add("0");
            return coord;
        }
        if (args.length > 4 && args.length < 7 && doYawPitch) {
            List<String> direction = new ArrayList<>();
            direction.add("0.0");
            return direction;
        }
        return null;
    }
}
