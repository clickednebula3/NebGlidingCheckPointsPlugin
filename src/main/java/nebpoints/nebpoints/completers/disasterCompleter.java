package nebpoints.nebpoints.completers;

import nebpoints.nebpoints.dataFiles.disasterData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class disasterCompleter implements TabCompleter {

    disasterData disasterData;
    public disasterCompleter(disasterData dData) {
        disasterData = dData;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> arenaTab = new ArrayList<>();

            for(int i = 0; i < disasterData.ArenaList.length; i++) {
                arenaTab.add(disasterData.ArenaList[i].arenaName);
            }

            return arenaTab;
        }
        return null;
    }
}
