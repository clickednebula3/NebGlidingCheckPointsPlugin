package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.dataFiles.gameData;
import nebpoints.nebpoints.game.gliding;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class hostJoinGlideExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;
    disasterData disasterData;
    ArrayList<Player> plyrs = new ArrayList<>();

    public hostJoinGlideExecutor(Nebpoints plugin, gameData gData, disasterData dData) {
        nebplugin = plugin;
        gameData = gData;
        disasterData = dData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Must run command as player");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage("Not enough arguments. Check syntax");
            return true;
        }
        if (((Player) sender).getPlayer().getScoreboardTags().contains("gameRunning")) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        String hostId = args[0];
        hostId = hostId.toLowerCase();

        if (Bukkit.getServer().getOnlinePlayers().contains(Bukkit.getServer().getPlayer(hostId))) {
            if (Bukkit.getServer().getPlayer(hostId).getScoreboardTags().contains("glide_host")) {
                Player player = (Player) sender;
                if (!player.getScoreboardTags().contains("glide_host")) {
                    player.addScoreboardTag("gameRunning");
                    player.addScoreboardTag("glide_joinhost");
                    player.addScoreboardTag("glide_joinhost_"+hostId);
                }
            }
        }

        return true;
    }
}
