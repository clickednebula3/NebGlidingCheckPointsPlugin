package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class createGlidingMapCompleter implements TabCompleter {
    gameData gameData;
    public createGlidingMapCompleter(gameData gData) {
        gameData = gData;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> mapNameTab = new ArrayList<>();
            mapNameTab.add("mapNameHere");
            return mapNameTab;
        }
        if (args.length == 2) {
            List<String> isRankedTab = new ArrayList<>();
            isRankedTab.add("true");
            isRankedTab.add("false");
            return isRankedTab;
        }
        if (args.length == 3) {
            List<String> cpOffX = new ArrayList<>();
            cpOffX.add("0.0");
            return cpOffX;
        }
        if (args.length == 4) {
            List<String> cpOffY = new ArrayList<>();
            cpOffY.add("0.0");
            return cpOffY;
        }
        return null;
    }
}
