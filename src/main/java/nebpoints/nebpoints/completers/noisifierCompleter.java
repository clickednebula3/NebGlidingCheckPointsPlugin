package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class noisifierCompleter implements TabCompleter {
    gameData gameData;
    public noisifierCompleter(gameData gData) {
        gameData = gData;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabReturn = new ArrayList<>();
        if (args.length == 1) {
            tabReturn.add("wand");
            tabReturn.addAll(gameData.soundGroups);
        }
        if (args.length == 2) {
            if (gameData.soundGroups.contains(args[0])) { tabReturn.addAll(gameData.soundMap.get(args[0])); }
        }
        return tabReturn;
    }
}
