package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class smp888allow implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof  Player)) {
            sender.sendMessage("A player must run this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Add a username to add to the whitelist.");
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sender.sendMessage("Could not find your player.\nCheck that the account exists and played here before.");
            return true;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+args[0]+" permission set group.smp888");
        sender.sendMessage("whitelisted "+args[0]+" to smp888");

        return true;
    }
}
