package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.dataFiles.gameData;
import nebpoints.nebpoints.game.gliding;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class hostGlideExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;
    disasterData disasterData;

    public hostGlideExecutor(Nebpoints plugin, gameData gData, disasterData dData) {
        nebplugin = plugin;
        gameData = gData;
        disasterData = dData;
    }

//    public ArrayList<Player> getPlyrs() {
//        return plyrs;
//    }
//
//    public void setPlyrs(ArrayList<Player> plyrs) {
//        this.plyrs = plyrs;
//    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) { return false; }

        if (args.length < 2) {
            sender.sendMessage("Not Enough Arguments, Check Syntax.");
            return true;
        }

        if (
            ((Player) sender).getPlayer().getScoreboardTags().contains(gameData.tag_in_game) ||
            ((Player) sender).getPlayer().getScoreboardTags().contains(gameData.tag_glide_host)
        ) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        ArrayList<Player> plyrs = new ArrayList<>();
        ArrayList<Integer> sortColorsIndexes = new ArrayList<>();

        //get variables
        World lbbywrld = Bukkit.getServer().getWorld(gameData.lobbyWorld);
        World gamewrld = Bukkit.getServer().getWorld(gameData.gameWorld);
        Player hostPlayer = ((Player) sender).getPlayer();



        int minPlayerCount = Integer.parseInt(args[0]);
        minPlayerCount = Math.max(1, Math.min(12, minPlayerCount)); //a.k.k "minPlayerCount = clamp(minPlayerCount, 1, 12);"


        int mapID = gameData.getGlideMapIDFromInputWithRespectToRank(args[0], sender.hasPermission(gameData.perm_glide_ranked));


        //just add first player
        plyrs.add(hostPlayer);
        hostPlayer.addScoreboardTag(gameData.tag_in_game);
        hostPlayer.addScoreboardTag(gameData.tag_glide_host);
        String hostId = hostPlayer.getName().toLowerCase();

        String hostingRace = "[{\"text\":\""+hostPlayer.getName()+"\",\"color\":\"red\"},{\"text\":\" is hosting a \\u2691 Gliding Race on map:"+gameData.getMapName(mapID)+"\",\"color\":\"gold\"},{\"text\":\" (1/"+minPlayerCount+")\\n\",\"color\":\"aqua\"},{\"text\":\"Click Here\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/glidejoin "+hostPlayer.getName()+"\"}},{\"text\":\" to join, or type\",\"color\":\"gold\"},{\"text\":\" /glidejoin "+hostPlayer.getName()+"\",\"color\":\"white\"}]";
        Bukkit.dispatchCommand(gameData.console, "tellraw @a "+hostingRace);


        int finalMinPlayerCount = minPlayerCount;
        final int[] TimerSeconds = {0};
        int finalMinPlayerCount1 = minPlayerCount;
        int finalMapID = mapID;
        final int[] myTaskIndex = {0};
        int checkForJoinersID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(nebplugin, new Runnable() {//should run every tick
            public void run() {
                if (TimerSeconds[0] <= 20) {TimerSeconds[0] +=1;} //don't want it to tick forever

                if (TimerSeconds[0] < 20)
                {
                    //check for new players
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getScoreboardTags().contains(gameData.tag_glide_host_join)
                                && player.getScoreboardTags().contains(gameData.tag_glide_host_join+"_"+hostId)) {
                            if (!plyrs.contains(player)) {
                                boolean found = false;
                                for (Player alreadyPlayer : plyrs) {
                                    if (alreadyPlayer.getName().equals(player.getName())) {
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    String playerOptIn = "[{\"text\":\""+player.getName()+"\",\"color\":\""+gameData.gamerSimpleColors[plyrs.size()%4]+"\"},{\"text\":\" opted into \",\"color\":\"gold\"},{\"text\":\""+hostPlayer.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Race!\",\"color\":\"gold\"},{\"text\":\" ("+(plyrs.size()+1)+"/"+ finalMinPlayerCount +")\",\"color\":\"aqua\"}]";
                                    Bukkit.dispatchCommand(gameData.console, "tellraw @a "+playerOptIn);
                                    plyrs.add(player);
                                }
                            }
                        }
                    }

                    //show timer progress
                    String timeUntilStart;
                    if (TimerSeconds[0] == 10 || TimerSeconds[0] == 17 || TimerSeconds[0] == 18 || TimerSeconds[0] == 19) {
                        timeUntilStart = "[\"\",{\"text\":\""+hostPlayer.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\""+(20-TimerSeconds[0])+" \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                        Bukkit.dispatchCommand(gameData.console, "tellraw @a "+timeUntilStart);
                    }
                }
                else if (TimerSeconds[0] == 20)
                {
                    //after timer is done

                    //make sure list contains unique online players
                    ArrayList<Player> uniquePlyrs = new ArrayList<>();
                    uniquePlyrs.add(hostPlayer);
                    for (Player plyr : plyrs) {
                        if (!uniquePlyrs.contains(plyr) && plyr.isOnline()) {
                            boolean found = false;
                            for (Player uplyr : uniquePlyrs) {
                                if (uplyr.getName().equals(plyr.getName())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                uniquePlyrs.add(plyr);
                            }
                        }
                    }

                    //not enough players
                    if (uniquePlyrs.size() < finalMinPlayerCount1) {
                        sender.sendMessage("[Gliding] Not Enough Players. Aborting Game Start.");
                        Bukkit.broadcastMessage(ChatColor.RED+hostPlayer.getName()+ChatColor.GOLD+"'s Gliding Race was canceled. Not enough players.");
//                        String abortGameStart = "[{\"text\":\""+hostPlayer.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race was canceled. Not enough players.\",\"color\":\"gold\"}]";
//                        Bukkit.dispatchCommand(gameData.console, "tellraw @a "+abortGameStart);
                        for (Player p : uniquePlyrs) {
                            stopExecutor stopper = new stopExecutor(gameData);
                            stopper.stopPlayerGames(p);
                        }
                        return;
                    }

                    String gameStartAnnounce = "[{\"text\":\""+hostPlayer.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting with \",\"color\":\"gold\"},{\"text\":\""+uniquePlyrs.size()+"\",\"color\":\"aqua\"},{\"text\":\" players!...\",\"color\":\"gold\"}]";
                    Bukkit.dispatchCommand(gameData.console, "tellraw @a "+gameStartAnnounce);

                    //tag joins and teams
                    for (int pCount = 0; pCount < uniquePlyrs.size(); pCount++) {
                        String myName = uniquePlyrs.get(pCount).getName();
                        String myColor = gameData.simpleColor(sortColorsIndexes, pCount);
                        sortColorsIndexes.add(pCount);

                        //tag players
                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_in_game);
                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_glide);
                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_glide+"_"+myColor);
                    }
//                    for (int pCount = 0; pCount < uniquePlyrs.size(); pCount++) {
//                        String myName = uniquePlyrs.get(pCount).getName();
//                        String myColor = gameData.gamerSimpleColors[pCount%4];
//                        sortColorsIndexes.add(pCount);
//                        //tag players todo HERE
//                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_in_game);
//                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_glide);
//                        uniquePlyrs.get(pCount).addScoreboardTag(gameData.tag_glide+"_"+myColor);
//                        //tp with offsets
//                        double spawnX = gameData.getRespawnX(finalMapID, 0);
//                        double spawnY = gameData.getRespawnY(finalMapID, 0);
//                        double spawnZ = gameData.getRespawnZ(finalMapID, 0);
//                        double spawnLR = gameData.getRespawnLR(finalMapID, 0);
//                        double spawnUD = gameData.getRespawnUD(finalMapID, 0);
//                        double spawnXoff = gameData.getRespawnOffX(finalMapID);
//                        double spawnZoff = gameData.getRespawnOffZ(finalMapID);
//                        double x = spawnX + (spawnXoff * pCount);
//                        double z = spawnZ + (spawnZoff * pCount);
//
//                        Location loc = new Location(gamewrld, x, spawnY, z);
//                        //plyrs.get(pCount).teleport(loc);
//                        //has -> wants pack
//                        //active -> already applied it
//                        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.gameWorld + " run tp " + myName + " " + x + " " + spawnY + " " + z + " " + spawnLR + " " + spawnUD);
//                    }
                    Bukkit.broadcastMessage("[NebPoints] Starting gliding with "+uniquePlyrs.size()+" player(s)");
//                    Bukkit.dispatchCommand(gameData.console, "say [Nebpoints] Starting gliding with " + uniquePlyrs.size() + " player(s)");
                    new gliding(nebplugin, uniquePlyrs, sortColorsIndexes, finalMapID, gameData);
                    gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                    new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks(false);
                }
            }
        }, 0L, 20L);
        myTaskIndex[0] = gameData.ScheduledRepeatingTasks.size();
        gameData.ScheduledRepeatingTasks.add(checkForJoinersID);
        gameData.FinishedRepeatingTasks.add(false);
        return true;
    }
}
