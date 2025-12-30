package nebpoints.nebpoints.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class smp888deny implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof  Player)) {
            sender.sendMessage("A player must run this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Add a username to remove from the whitelist.");
            return false;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+args[0]+" permission unset group.smp888");
        sender.sendMessage("removed "+args[0]+" from smp888's whitelist");

        return true;
    }
}
