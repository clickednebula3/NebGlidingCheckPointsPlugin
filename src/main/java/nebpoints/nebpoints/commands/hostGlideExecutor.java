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
        if (sender instanceof Player) {
            if (args.length < 2) {
                sender.sendMessage("Not Enough Arguments, Check Syntax.");
                return true;
            }

            if (
                ((Player) sender).getPlayer().getScoreboardTags().contains("gameRunning") ||
                ((Player) sender).getPlayer().getScoreboardTags().contains("glide_host")
            ) {
                sender.sendMessage("You are already in a game! Do /leave to forfeit.");
                return true;
            }

            ArrayList<Player> plyrs = new ArrayList<>();
            ArrayList<String> sortColors = new ArrayList<>();

            //get variables
            ConsoleCommandSender console = gameData.console;
            String[] joinColor = {"red", "blue", "yellow", "green"};
            World lbbywrld;
            if (gameData.lobbyWorld == "overworld") {
                lbbywrld = Bukkit.getServer().getWorld("hub");
            } else {
                lbbywrld = Bukkit.getServer().getWorld(gameData.lobbyWorld);
            }
            World gamewrld = Bukkit.getServer().getWorld(gameData.gameWorld);
            Player player1 = ((Player) sender).getPlayer();



            int minPlayerCount = Integer.parseInt(args[0]);
            minPlayerCount = Math.max(1, Math.min(12, minPlayerCount)); //a.k.k "minPlayerCount = clamp(minPlayerCount, 1, 12);"


            int mapID = 0;
            String[] maps = gameData.getMaps();
            String[] mapsRanked = gameData.getRankedMaps();
            int mapIndex = 0;
            for (String map : maps) {
                if (args[1].equals(map)) {
                    boolean isRankedMap = false;
                    for (String mapR : mapsRanked) {
                        if (args[1].equals(mapR)) {
                            isRankedMap = true;
                            break;
                        }
                    }
                    if (isRankedMap) {
                        if (sender.hasPermission("nebpoints.gliding_ranked")) {
                            mapID = mapIndex;
                        }
                    } else {
                        mapID = mapIndex;
                    }
                    break;
                }
                mapIndex++;
            }


            //just add first player
            plyrs.add(player1);
            player1.addScoreboardTag("gameRunning");
            player1.addScoreboardTag("glide_host");
            String hostId = player1.getName().toLowerCase();

            String hostingRace = "[{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\" is hosting a \\u2691 Gliding Race on map:"+gameData.getMapName(mapID)+"\",\"color\":\"gold\"},{\"text\":\" (1/"+minPlayerCount+")\\n\",\"color\":\"aqua\"},{\"text\":\"Click Here\",\"underlined\":true,\"color\":\"aqua\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/glidehostjoin "+player1.getName()+"\"}},{\"text\":\" to join, or type\",\"color\":\"gold\"},{\"text\":\" /glidehostjoin "+player1.getName()+"\",\"color\":\"white\"}]";
            Bukkit.dispatchCommand(console, "tellraw @a "+hostingRace);


            int finalMinPlayerCount = minPlayerCount;
            final int[] TimerSeconds = {0};
            int finalMinPlayerCount1 = minPlayerCount;
            int finalMapID = mapID;
            final int[] myTaskIndex = {0};
            int checkForJoinersID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(nebplugin, new Runnable() {//should run every tick
                public void run() {
                    if (TimerSeconds[0] <= 20) {TimerSeconds[0] +=1;}//don't want it to tick forever

                    //before game starts
                    if (TimerSeconds[0] < 20)
                    {

                        //check for new players
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getScoreboardTags().contains("glide_joinhost") && player.getScoreboardTags().contains("glide_joinhost_"+hostId)) {
                                if (!plyrs.contains(player)) {
                                    boolean found = false;
                                    for (Player alreadyPlayer : plyrs) {
                                        if (alreadyPlayer.getName().equals(player.getName())) {
                                            found = true;
                                        }
                                    }
                                    if (!found) {
                                        String playerOptIn = "[{\"text\":\""+player.getName()+"\",\"color\":\""+joinColor[plyrs.size()%4]+"\"},{\"text\":\" opted into \",\"color\":\"gold\"},{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Race!\",\"color\":\"gold\"},{\"text\":\" ("+(plyrs.size()+1)+"/"+ finalMinPlayerCount +")\",\"color\":\"aqua\"}]";
                                        Bukkit.dispatchCommand(console, "tellraw @a "+playerOptIn);
                                        plyrs.add(player);
                                    }
                                }
                            }
                        }

                        //show timer progress
                        String timeUntilStart;
                        if (TimerSeconds[0] == 5) {
                            timeUntilStart = "[\"\",{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\"15 \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+timeUntilStart);
                        }
                        else if (TimerSeconds[0] == 13) {
                            timeUntilStart = "[\"\",{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\"7 \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+timeUntilStart);
                        }
                        else if (TimerSeconds[0] == 17) {
                            timeUntilStart = "[\"\",{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\"3 \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+timeUntilStart);
                        }
                        else if (TimerSeconds[0] == 18) {
                            timeUntilStart = "[\"\",{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\"2 \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+timeUntilStart);
                        }
                        else if (TimerSeconds[0] == 19) {
                            timeUntilStart = "[\"\",{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting in \",\"color\":\"gold\"},{\"text\":\"1 \",\"color\":\"aqua\"},{\"text\":\"seconds...\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+timeUntilStart);
                        }
                    }
                    else if (TimerSeconds[0] == 20)
                    {
                        //after timer is done

                        //make sure list contains unique online players
                        ArrayList<Player> uniquePlyrs = new ArrayList<>();
                        uniquePlyrs.add(player1);
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
                            String abortGameStart = "[{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race was canceled. Not enough players.\",\"color\":\"gold\"}]";
                            Bukkit.dispatchCommand(console, "tellraw @a "+abortGameStart);
                            for (Player p : uniquePlyrs) {
                                stopExecutor stopper = new stopExecutor(gameData);
                                stopper.stopPlayerGames(p);
                            }
                            return;
                        }

                        String gameStartAnnounce = "[{\"text\":\""+player1.getName()+"\",\"color\":\"red\"},{\"text\":\"'s Gliding Race is starting with \",\"color\":\"gold\"},{\"text\":\""+uniquePlyrs.size()+"\",\"color\":\"aqua\"},{\"text\":\" players!...\",\"color\":\"gold\"}]";
                        Bukkit.dispatchCommand(console, "tellraw @a "+gameStartAnnounce);

                        //log joins and teams
                        for (int pCount = 0; pCount < uniquePlyrs.size(); pCount++) {
                            String myName = uniquePlyrs.get(pCount).getName();
                            String myColor = joinColor[pCount%4];
                            sortColors.add(myColor);
                            //tag players
                            uniquePlyrs.get(pCount).addScoreboardTag("glider");
                            uniquePlyrs.get(pCount).addScoreboardTag("glider_"+myColor);
                            //tp with offsets
                            double spawnX = gameData.getRespawnX(finalMapID, 0);
                            double spawnY = gameData.getRespawnY(finalMapID, 0);
                            double spawnZ = gameData.getRespawnZ(finalMapID, 0);
                            double spawnLR = gameData.getRespawnLR(finalMapID, 0);
                            double spawnUD = gameData.getRespawnUD(finalMapID, 0);
                            double spawnXoff = gameData.getRespawnOffX(finalMapID);
                            double spawnZoff = gameData.getRespawnOffZ(finalMapID);
                            double x = spawnX + (spawnXoff * pCount);
                            double z = spawnZ + (spawnZoff * pCount);

                            Location loc = new Location(gamewrld, x, spawnY, z);
                            //plyrs.get(pCount).teleport(loc);
                            //has -> wants pack
                            //active -> already applied it
                            if (uniquePlyrs.get(pCount).getScoreboardTags().contains("hasResourcePack") && !uniquePlyrs.get(pCount).getScoreboardTags().contains("activeResourcePack")) {
                                uniquePlyrs.get(pCount).addScoreboardTag("activeResourcePack");
                                uniquePlyrs.get(pCount).performCommand("pack");
                            }
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run tp " + myName + " " + x + " " + spawnY + " " + z + " " + spawnLR + " " + spawnUD);
                        }
                        Bukkit.dispatchCommand(console, "say [Gliding] Starting game with " + uniquePlyrs.size() + " player(s)");
                        new gliding(nebplugin, uniquePlyrs, sortColors, finalMapID, console, gameData);
                        gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                        new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                    }
                }
            }, 0L, 20L);
            myTaskIndex[0] = gameData.ScheduledRepeatingTasks.size();
            gameData.ScheduledRepeatingTasks.add(checkForJoinersID);
            gameData.FinishedRepeatingTasks.add(false);
            return true;
        }

        return false;
    }
}
