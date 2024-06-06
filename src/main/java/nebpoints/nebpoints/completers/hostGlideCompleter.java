package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class hostGlideCompleter implements TabCompleter {
    gameData gameData;
    public hostGlideCompleter(gameData gData) {
        gameData = gData;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> minPlayerCountTab = new ArrayList<>();
            for (int i=0; i<=8; i++) {
                minPlayerCountTab.add(String.valueOf(i));
            }
            return minPlayerCountTab;
        }

        if (args.length == 2) {
            String[] allMaps = gameData.getMaps();
            String[] allMapsR = gameData.getRankedMaps();

            List<String> mapTab = new ArrayList<>(Arrays.asList(allMaps));
            if (sender instanceof Player && !sender.hasPermission("nebpoints.gliding_ranked")) {
                for(int i = 0; i < allMapsR.length; i++) {
                    mapTab.remove(allMapsR[i]);
                }
            }

            return mapTab;
        }

        if (args.length == 3) {
            List<String> colorTab = new ArrayList<>();
            colorTab.add("red");
            colorTab.add("blue");
            colorTab.add("yellow");
            colorTab.add("green");
            return colorTab;
        }
        return null;
    }
}
