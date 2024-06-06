package nebpoints.nebpoints.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class togglePackExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player plyr = (Player) sender;
            if (plyr.getScoreboardTags().contains("hasResourcePack")) {
                plyr.removeScoreboardTag("hasResourcePack");
                plyr.sendMessage("Packmode toggled off");
            } else {
                plyr.addScoreboardTag("hasResourcePack");
                plyr.performCommand("pack");
                plyr.sendMessage("Packmode toggled on");
            }
        }
        return true;
    }
}
