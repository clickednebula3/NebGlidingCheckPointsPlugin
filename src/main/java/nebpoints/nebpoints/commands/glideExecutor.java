package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.game.gliding;
import nebpoints.nebpoints.dataFiles.gameData;
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
        if (!(sender instanceof Player)) {
            return false;
        }

        if (((Player) sender).getPlayer().getScoreboardTags().contains("gameRunning")) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        //get variables
        ConsoleCommandSender console = gameData.console;
        Material[] joinBlocks = {Material.RED_CONCRETE, Material.BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE};
        String[] joinColor = {"red", "blue", "yellow", "green"};
        World lbbywrld;
        if (gameData.lobbyWorld == "overworld") { lbbywrld = Bukkit.getServer().getWorld("hub"); }
        else { lbbywrld = Bukkit.getServer().getWorld(gameData.lobbyWorld); }
        World gamewrld = Bukkit.getServer().getWorld(gameData.gameWorld);


        int mapID = 0;
        String[] maps = gameData.getMaps();
        String[] mapsRanked = gameData.getRankedMaps();
        int mapIndex = 0;
        if (args.length >= 1) {
            for (String map : maps) {
                if (args[0].equals(map)) {
                    boolean isRankedMap = false;
                    for (String mapR : mapsRanked) { if (args[0].equals(mapR)) { isRankedMap = true; break; } }
                    if (isRankedMap) { if (sender.hasPermission("nebpoints.gliding_ranked")) { mapID = mapIndex; } }
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
            Bukkit.dispatchCommand(console, "tellraw "+myName+" \"You aren't standing on a color! Teleporting to lobby...\"");
            Bukkit.dispatchCommand(console, "tellraw "+myName+" \"Use this command again once all players chose their colors and are ready.\"");
            ((Player) sender).performCommand("warp elytra");
            return true;
        }

        //log joins and teams
        for (int pCount = 0; pCount < plyrs.size(); pCount++) {
            String myName = plyrs.get(pCount).getName();
            String myColor = sortColors.get(pCount%sortColors.size());
            //log player joins
            Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"[Gliding] " + myName + " joined as Player" + (pCount + 1) + " with color:" + myColor + " on map:"+ gameData.getMapName(mapID)+"\",\"color\":\""+myColor+"\"}");
            //tag players
            plyrs.get(pCount).addScoreboardTag("gameRunning");
            plyrs.get(pCount).addScoreboardTag("glider");
            plyrs.get(pCount).addScoreboardTag("glider_"+myColor);
            //tp with offsets
//            double spawnX = gameData.getRespawnX(mapID, 0);
//            double spawnY = gameData.getRespawnY(mapID, 0);
//            double spawnZ = gameData.getRespawnZ(mapID, 0);
//            double spawnLR = gameData.getRespawnLR(mapID, 0);
//            double spawnUD = gameData.getRespawnUD(mapID, 0);
//            double spawnXoff = gameData.getRespawnOffX(mapID);
//            double spawnZoff = gameData.getRespawnOffZ(mapID);
//            double x = spawnX + (spawnXoff * pCount);
//            double z = spawnZ + (spawnZoff * pCount);
//
//            Location loc = new Location(gamewrld, x, spawnY, z);
            //plyrs.get(pCount).teleport(loc);
//            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run minecraft:tp " + myName + " " + x + " " + spawnY + " " + z + " " + spawnLR + " " + spawnUD);
        }
        Bukkit.dispatchCommand(console, "say [Gliding] Starting game with " + plyrs.size() + " player(s)");
        new gliding(nebplugin, plyrs, sortColors, mapID, console, gameData);

        return true;
    }
}
