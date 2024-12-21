package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.GlidingMap;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.game.gliding;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class glideExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;
    disasterData disasterData;

    public glideExecutor(Nebpoints plugin, gameData gData, disasterData dData) {
        nebplugin = plugin;
        gameData = gData;
        disasterData = dData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) { return false; }

        if (Objects.requireNonNull(((Player) sender).getPlayer()).getScoreboardTags().contains(gameData.tag_in_game)) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        //get variables
        World lbbywrld = Bukkit.getServer().getWorld(gameData.lobbyWorld);
        World gamewrld = Bukkit.getServer().getWorld(gameData.gameWorld);


        //get map
        int mapID = 0;
        if (args.length >= 1) { mapID = gameData.getGlideMapIDFromInputWithRespectToRank(args[0], sender.hasPermission(gameData.perm_glide_ranked)); }
        else {
            //open GUI Menu for Gliding Maps
            Player player = ((Player) sender).getPlayer();
            Inventory mainInv = Bukkit.createInventory(player, 9*3, "Gliding Maps");

            for (int i=0; i<gameData.gliding_maps_loaded.size(); i++) {
                GlidingMap thisMap = gameData.gliding_maps_loaded.get(i);
                String lore = "Select to play this map!";
                if (thisMap.isRanked) {
                    if (player.hasPermission(gameData.perm_glide_ranked)) { lore = "Requires RANK, which you have!"; }
                    else { lore = "Requires RANK, get it from the store!"; }
                }
                mainInv.setItem(i, gameData.generateItem(thisMap.icon, thisMap.mapName, lore));
            }

            player.openInventory(mainInv);
            return true;
        }

        //get players and prepare teams
        ArrayList<Player> plyrs = new ArrayList<>();
        ArrayList<Integer> sortColorsIndexes = new ArrayList<>();

        for (int i = 0; i < gameData.glideBlockColors.length; i++) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                assert lbbywrld != null;
                if (p.getWorld().getName().equals(lbbywrld.getName()) && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == gameData.glideBlockColors[i]) {
                    plyrs.add(p);
                    sortColorsIndexes.add(i);
                }
            }
        }

        //leave early if nobody joined
        if (!plyrs.contains((Player) sender)) {
            sender.sendMessage("You aren't standing on a color! Teleporting to lobby.");
            sender.sendMessage("Use this command again once all players chose their colors and are ready.");
            ((Player) sender).teleport(gameData.glideLobbyLocation);
            return true;
        }

        //tag joins and teams
        for (int pCount = 0; pCount < plyrs.size(); pCount++) {
            String myName = plyrs.get(pCount).getName();
            String myColor = gameData.simpleColor(sortColorsIndexes, pCount);
            ChatColor myChatColor = gameData.chatColor(sortColorsIndexes, pCount);
//            String myColor = sortColors.get(pCount%sortColors.size());

            //log player joins
            Bukkit.broadcastMessage(ChatColor.RESET + "[NebPoints] " +myChatColor+myName+":"+(pCount+1)+":"+myColor+ChatColor.RESET+" joined to glide on map:"+gameData.getMapName(mapID));
//            Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"[Gliding] " + myName + " joined as Player" + (pCount + 1) + " with color:" + myColor + " on map:"+ gameData.getMapName(mapID)+"\",\"color\":\""+myColor+"\"}");

            //tag players
            plyrs.get(pCount).addScoreboardTag(gameData.tag_in_game);
            plyrs.get(pCount).addScoreboardTag(gameData.tag_glide);
            plyrs.get(pCount).addScoreboardTag(gameData.tag_glide+"_"+myColor);
        }

        Bukkit.broadcastMessage(ChatColor.RESET+"[NebPoints] Starting "+ChatColor.YELLOW+"Gliding"+ChatColor.RESET+" with "+plyrs.size()+" player(s)");
//        Bukkit.dispatchCommand(gameData.console, "say [Gliding] Starting game with " + plyrs.size() + " player(s)");
        new gliding(nebplugin, plyrs, sortColorsIndexes, mapID, gameData);

        return true;
    }
}
