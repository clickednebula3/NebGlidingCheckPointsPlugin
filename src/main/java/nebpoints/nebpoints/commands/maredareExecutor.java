package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import nebpoints.nebpoints.game.gliding;
import nebpoints.nebpoints.game.maredare;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class maredareExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;
    public maredareExecutor(Nebpoints plugin, gameData gameData) {
        this.gameData = gameData;
        this.nebplugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {//todo: recheck later: is this important?
            sender.sendMessage("A player must run this command");
            return true;
        }
        if (((Player) sender).getPlayer().getScoreboardTags().contains("gameRunning")) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        //variables
        Material[] joinBlocks = {Material.RED_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA};
        String[] joinColor = {"red", "blue", "yellow", "green"};
        World lbbywrld;
        if (gameData.lobbyWorld == "overworld") { lbbywrld = Bukkit.getServer().getWorld("hub"); }
        else { lbbywrld = Bukkit.getServer().getWorld(gameData.lobbyWorld); }
        World gamewrld = Bukkit.getServer().getWorld(gameData.mareWorld);



        int mapID = 0;
        String[] maps = gameData.getMaredareMaps();
        String[] mapsRanked = gameData.getMaredareRankedMaps();
        int mapIndex = 0;
        if (args.length >= 1) {
            for (String map : maps) {
                if (args[0].equals(map)) {
                    boolean isRankedMap = false;
                    for (String mapR : mapsRanked) { if (args[0].equals(mapR)) { isRankedMap = true; break; } }
                    if (isRankedMap) { if (sender.hasPermission("nebpoints.maredare_ranked")) { mapID = mapIndex; } }
                    else { mapID = mapIndex; }
                    break;
                }
                mapIndex++;
            }
        }

        //get players and teams
        ArrayList<Player> plyrs = new ArrayList<>();
        ArrayList<String> sortColors = new ArrayList<>();
        for (int index = 0; index < joinBlocks.length; index++) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                assert lbbywrld != null;
                if (p.getWorld().getName().equals(lbbywrld.getName()) && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == joinBlocks[index]) {
                    plyrs.add(p);
                    sortColors.add(joinColor[index]);
                }
            }
        }

        //leave early if nobody joined
        if (!plyrs.contains((Player) sender)) {
            String myName = sender.getName();
            Bukkit.dispatchCommand(gameData.console, "tellraw "+myName+" \"You aren't standing on a color! Teleporting to lobby...\"");
            Bukkit.dispatchCommand(gameData.console, "tellraw "+myName+" \"Use this command again once all players chose their colors and are ready.\"");
            ((Player) sender).performCommand("warp maredare");
            return true;
        }

        //log joins and teams
        for (int pCount = 0; pCount < plyrs.size(); pCount++) {
            String myName = plyrs.get(pCount).getName();
            String myColor = sortColors.get(pCount%sortColors.size());
            //log player joins
            Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"[MareDare] " + myName + " joined as Player" + (pCount + 1) + " with color:" + myColor + " on map:"+ gameData.getMaredareMapName(mapID)+"\",\"color\":\""+myColor+"\"}");
            //tag players
            plyrs.get(pCount).addScoreboardTag("gameRunning");
            plyrs.get(pCount).addScoreboardTag("maredare");
            plyrs.get(pCount).addScoreboardTag("maredare_"+myColor);
            //tp with offsets
            double spawnX = gameData.getMaredareRespawnX(mapID, 0);
            double spawnY = gameData.getMaredareRespawnY(mapID, 0);
            double spawnZ = gameData.getMaredareRespawnZ(mapID, 0);
            double spawnLR = gameData.getMaredareRespawnLR(mapID, 0);
            double spawnUD = gameData.getMaredareRespawnUD(mapID, 0);
            double spawnXoff = gameData.getMaredareRespawnOffX(mapID);
            double spawnZoff = gameData.getMaredareRespawnOffZ(mapID);
            double x = spawnX + (spawnXoff * pCount);
            double z = spawnZ + (spawnZoff * pCount);

            Location loc = new Location(gamewrld, x, spawnY, z);
            //plyrs.get(pCount).teleport(loc);
            //has -> wants pack
            //active -> already applied it
            if (plyrs.get(pCount).getScoreboardTags().contains("hasResourcePack") && !plyrs.get(pCount).getScoreboardTags().contains("activeResourcePack")) {
                plyrs.get(pCount).addScoreboardTag("activeResourcePack");
                plyrs.get(pCount).performCommand("pack");
            }
            Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " run tp " + myName + " " + x + " " + spawnY + " " + z + " " + spawnLR + " " + spawnUD);
        }

        Bukkit.dispatchCommand(gameData.console, "say [Maredare] Starting game with " + plyrs.size() + " player(s)");
        new maredare(nebplugin, plyrs, sortColors, mapID, gameData);

        return true;
    }
}
