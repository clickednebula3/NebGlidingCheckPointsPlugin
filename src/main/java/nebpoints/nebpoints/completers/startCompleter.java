package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class startCompleter implements TabCompleter {
    gameData gameData;
    public startCompleter(gameData gData) {
        gameData = gData;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> gameTab = new ArrayList<>();
            gameTab.add("glide");
            gameTab.add("disaster");
            return gameTab;
        }
        if (args.length == 2) {
            if (args[0] == "glide") {
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
            else if (args[0] == "disaster") {
                List<String> arenaTab = new ArrayList<>();
                arenaTab.add("peace");
                return arenaTab;
            }
        }
        return null;
    }
}
