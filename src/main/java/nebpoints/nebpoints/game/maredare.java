package nebpoints.nebpoints.game;

import nebpoints.nebpoints.commands.nebClearScheduledRepeatingTasksExecutor;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.MaredareCheckpoint;
import nebpoints.nebpoints.dataFiles.MaredareMap;
import nebpoints.nebpoints.dataFiles.gameData;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Random;


public class maredare {
    public boolean[] isRunning = {true};
    gameData gameData;

    double initialMareSpeed = 0.4;
    String tag = "maredare";
    String murder_tag = "maredare_murderer";
    String dead_tag = "maredare_dead";

    public maredare(Plugin nebplugin, ArrayList<Player> plyrs, ArrayList<String> sortColors, int mapID, gameData gameData) {
        this.gameData = gameData;

        ArrayList<Horse> mares = new ArrayList<>();
        ArrayList<Location> maresLastLocation = new ArrayList<>();
        ArrayList<ArrayList<Integer>> myCP = new ArrayList<>();//the cp of each player
        ArrayList<Integer> eCD = new ArrayList<>();//cp tp cooldown for each player //unneeded?
        ArrayList<Integer> winRank = new ArrayList<>();//order the winners

        double spawnX = gameData.getMaredareRespawnX(mapID, 0);
        double spawnY = gameData.getMaredareRespawnY(mapID, 0);
        double spawnZ = gameData.getMaredareRespawnZ(mapID, 0);
        double spawnLR = gameData.getMaredareRespawnLR(mapID, 0);
        double spawnUD = gameData.getMaredareRespawnUD(mapID, 0);
        double spawnXoff = gameData.getMaredareRespawnOffX(mapID);
        double spawnYoff = gameData.getMaredareRespawnOffZ(mapID);

        prepareRules(plyrs);

        {
            int p = 0;
            for (Player plyr : plyrs) {
                //give horse early
                Location spawnSpot = new Location(Bukkit.getWorld(gameData.mareWorld), spawnX + (spawnXoff * p), spawnY, spawnZ + (spawnYoff * p), (float) spawnLR, (float) spawnUD);
                maresLastLocation.add(spawnSpot);
                Horse myMare = makeMare(spawnSpot);
                myMare.addPassenger(plyr);
                mares.add(myMare);

                //ominous start sound
                Bukkit.dispatchCommand(gameData.console, "execute as " + plyr.getName() + " run playsound " + gameData.startSound + " master @s ~ ~ ~ 999999999999 1.15 1");

                //set cabinet //x, y, z of block to stand on
                prepareGlassBox(getStandBlock(spawnX, spawnY, spawnZ, spawnXoff, spawnYoff, p));

                //for each player set checkpoint at start, and e-cooldown at start
                ArrayList<Integer> myCps = new ArrayList<>();
                myCP.add(myCps);
                eCD.add(0);
                p++;
            }
        }

        final long[] time = {0};
        final long[] remainingTime = {-1};
        final boolean[] gameConsideredFinished = {false};

        final int[] myTaskIndex = {0};
        final int[] WINEVENTREMAININGTIME = {20 * 10};
        //final double[] checkpointDistance = {0.0, 0.0, 0.0, 0.0};
        //should run every tick
        int loopID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(nebplugin, () -> {
            if (isRunning[0]) {
                //remove disconnecters and "/stop"ers, and stop the game
                {
                    int p = 0;
                    for (Player plyr : plyrs) {
                        if (!plyr.getScoreboardTags().contains(tag) || !plyr.isOnline()) {
                            Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + plyr.getName() + " forfeited the mare dare race and left a trace\",\"color\":\"" + sortColors.get(p) + "\"}");
                            plyrs.remove(p);
                            sortColors.remove(p);
                            myCP.remove(p);
                            eCD.remove(p);
                            mares.get(p).remove();//discombobulate horse from physical realm
                            mares.remove(p);//evaporate every last nebulous memory of the horse's hopes and dreams
                            for (int winner : winRank) { if (winner == p) { winRank.remove(winner); } }
                            p--;
                            if (plyrs.size() == 0) {
                                isRunning[0] = false;
                                gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                                new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                            }
                        }

                        p++;
                    }
                }

                //todo point here

                //game end text
                if (remainingTime[0] > 0) { remainingTime[0]--; }
                else if (remainingTime[0] == 0) {
                    time[0] = 20L * gameData.gameLength;
                    Bukkit.dispatchCommand(gameData.console, "tellraw @a \"Game Complete! Ranking (sorted):\"");
                    for (int winner : winRank) { Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"-" + plyrs.get(winner).getName() + "\",\"color\":\"" + sortColors.get(winner) + "\"}"); }
                }

                //(non-player-based game)
                //start countdown
                if (time[0] <= 20L*gameData.timerLength)
                {
                    //sound every second
                    for (int p=0; p<plyrs.size(); p++) { countDownTick(plyrs.get(p), sortColors.get(p), gameData.timerTick, time[0], gameData.timerLength); }
                }
                //start frame
                else if (time[0] == 20L *(gameData.timerLength+1))
                {
                    int p = 0;
                    for (Player plyr : plyrs) {
                        String myName = plyr.getName();
                        Bukkit.dispatchCommand(gameData.console, "title " + myName + " title {\"text\":\"Go!!\",\"color\":\"" + sortColors.get(p) + "\"}");

                        destroyGlassBox(getStandBlock(spawnX, spawnY, spawnZ, spawnXoff, spawnYoff, p));

                        preparePlayerEffects(plyr);

                        playerRandomDisc(myName);

                        plyr.getInventory().clear();
                        plyr.getInventory().addItem(new ItemStack(Material.SNOWBALL));
                        if (plyr.getScoreboardTags().contains(murder_tag)) {
                            plyr.getInventory().addItem(new ItemStack(Material.CROSSBOW));
                            plyr.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                            plyr.getInventory().addItem(new ItemStack(Material.ARROW, 3*plyrs.size()));
//                            ItemStack blindnessPotion = new ItemStack(Material.SPLASH_POTION);
//                            PotionMeta blindnessPotionItemMeta = (PotionMeta) blindnessPotion.getItemMeta();
//                            blindnessPotionItemMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0, false, true, true), true);
//                            blindnessPotionItemMeta.setDisplayName("Murderer Blindness Potion");
//                            blindnessPotion.setItemMeta(blindnessPotionItemMeta);
//                            plyr.getInventory().addItem(blindnessPotion);
                        }
                        if (plyr.getScoreboardTags().contains(dead_tag)) { plyr.removeScoreboardTag(dead_tag); }

                        p++;
                    }
                }
                //every frame after the timer of the game
                else
                {
                    for (Player plyr : plyrs) {
                        //bounding box check
                        if (!withenBoundBox(plyr.getLocation(), mapID) && !plyr.getScoreboardTags().contains(murder_tag)) { plyr.addScoreboardTag(dead_tag); }
                    }
                }

                //horse checks
                performHorseCheckUp(mares, plyrs, maresLastLocation);

                //game main part ig

                //count deaders and winners
                int deadFolkCount = 0;
                int raceCompleteFolkCount = 0;
                {
                    int dp = 0;
                    for (Player plyr : plyrs) {
                        if (plyr.getScoreboardTags().contains(dead_tag) && !plyr.getScoreboardTags().contains(murder_tag)) { deadFolkCount++; }
                        if (!plyr.getScoreboardTags().contains(murder_tag) && myCP.get(dp).size() == gameData.maredare_maps_loaded.get(mapID).checkpoints.size()) { raceCompleteFolkCount++; }
                        dp++;
                    }
                }

                //racers win
                int fakeBool_murdererExists = 1;
                if (plyrs.size() == 1) { fakeBool_murdererExists = 0; }
                if (raceCompleteFolkCount >= plyrs.size()-fakeBool_murdererExists && !gameConsideredFinished[0]) {
                    Bukkit.dispatchCommand(gameData.console, "tellraw @a \"[MareDare] All MareDare Racers Collected All Flags.\"");
                    gameConsideredFinished[0] = true;
                    remainingTime[0] = WINEVENTREMAININGTIME[0];
                }

                {
                    int p = 0;
                    for (Player plyr : plyrs) {

                        //checkpoint checks
                        int myCPsize = myCP.get(p).size();
                        myCP.set(p, performCheckpointUpdate(myCP.get(p), plyr, mapID));

                        if (myCPsize != myCP.get(p).size()) {//got a new flag
                            int myCheckpointScore = performCheckpointCheckUp(myCP.get(p), mapID);

                            if (myCheckpointScore == 0) {
                                Bukkit.dispatchCommand(gameData.console, "tellraw " + plyr.getName() + " \"Almost done! Acquire the final flag!\"");
                                Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + plyr.getName() + " has almost collected all flags! Hurry up!\",\"color\":\"" + sortColors.get(p) + "\"}");
                            } else if (myCheckpointScore == 1) {
                                Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + plyr.getName() + " finished the Mare Dare! Rank: " + winRank.size() + "\",\"color\":\"" + sortColors.get(p) + "\"}");
                                if (!plyr.getScoreboardTags().contains(murder_tag)) { winRank.add(p); }
                            }
                        }

                        //murderer win
                        if (deadFolkCount >= plyrs.size()-1 && !gameConsideredFinished[0] && plyr.getScoreboardTags().contains(murder_tag) && raceCompleteFolkCount < plyrs.size()-1) {
                            Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + plyr.getName() + " Successfully MareDared The Racers.\",\"color\":\"" + sortColors.get(p) + "\"}");
                            remainingTime[0] = WINEVENTREMAININGTIME[0];
                            gameConsideredFinished[0] = true;
                        }

                        //for each cp other than first one, mark state with blocks
                        for (int cp = 1; cp < gameData.maredare_maps_loaded.get(mapID).checkpoints.size(); cp++) {
                            sendFakeBlockFlags(plyr, gameData.maredare_maps_loaded.get(mapID).checkpoints.get(cp), myCP.get(p).contains(cp));
                        }

                        //show action bar (Murderer/Racer status) - (flags/kill count) "[Role] - [Progress]"
                        String actionBarText = "[Racer] - Flags: " + (myCP.get(p).size() - 1) + "/" + (gameData.maredare_maps_loaded.get(mapID).checkpoints.size() - 1);
                        if (plyr.getScoreboardTags().contains(dead_tag)) { actionBarText = "Eliminated - " + actionBarText; }
                        if (plyr.getScoreboardTags().contains(murder_tag)) { actionBarText = "[Murderer] - Eliminated: " + deadFolkCount + "/" + (plyrs.size() - 1); }
                        if (remainingTime[0] > 0) { actionBarText += " - Remaining Time: " + remainingTime[0] + "/" + WINEVENTREMAININGTIME[0]; }

                        String actionBarJson = "{\"text\":\"" + actionBarText + "\",\"color\":\"" + sortColors.get(p) + "\"}";
                        Bukkit.dispatchCommand(gameData.console, "title " + plyr.getName() + " actionbar "+actionBarJson);

                        p++;
                    }
                }

                if (remainingTime[0] == 0) {
                    int p = 0;
                    for (Player plyr : plyrs) {
                        plyr.removeScoreboardTag("gameRunning");
                        //plyr.removeScoreboardTag(tag); //the stopper should remove this and give effects
                        plyr.removeScoreboardTag(murder_tag);
                        plyr.removeScoreboardTag(dead_tag);
                        plyr.removeScoreboardTag(tag+sortColors.get(p));
                        mares.get(p).remove();
                        stopExecutor stopper = new stopExecutor(gameData);
                        stopper.stopPlayerGames(plyr);
                        p++;
                    }
                    isRunning[0] = false;
                    gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                    new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                }

                time[0]++;
            }
        }, 0L, 1L);

        myTaskIndex[0] = gameData.ScheduledRepeatingTasks.size();
        gameData.ScheduledRepeatingTasks.add(loopID);
        gameData.FinishedRepeatingTasks.add(false);
    }

    void countDownTick(Player player, String myColor, String timerTickSound, long time, int timerLength) {
        Bukkit.dispatchCommand(gameData.console, "title "+player.getName()+" title {\"text\":\"Starting in: "+((timerLength) - (time / 20))+"...\",\"color\":\""+myColor+"\"}");
        player.getInventory().remove(Material.BOW);
        player.getInventory().remove(Material.ARROW);
        player.getInventory().remove(Material.CROSSBOW);
        player.getInventory().remove(Material.SNOWBALL);
        player.getInventory().remove(Material.DIAMOND_SWORD);
        player.getInventory().remove(Material.SADDLE);
        player.getInventory().remove(Material.LEATHER);
        player.removeScoreboardTag(dead_tag);
        if (time % 20 == 0) { Bukkit.dispatchCommand(gameData.console, "execute as " + player.getName() + " run playsound " + timerTickSound + " master @s ~ ~ ~ 1 1.6 1"); }
    }

    Horse makeMare(Location spawnSpot) {
        Horse myMare = spawnSpot.getWorld().spawn(spawnSpot, Horse.class);
        myMare.setAdult();
        myMare.setTamed(true);
        myMare.setJumpStrength(0.8);
        myMare.setAI(false);
        myMare.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        myMare.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(initialMareSpeed);//defaults:(players: 0.1, horses: 0.3) game:(mares: 0.4, max mares: 0.8)
        return myMare;
    }

    void prepareRules(ArrayList<Player> players) {
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " run gamerule commandBlockOutput false");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " run gamerule logAdminCommands false");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " run gamerule doImmediateRespawn true");

        for (Player plyr : players) {
            String myName = plyr.getName();
            Bukkit.dispatchCommand(gameData.console, "title "+myName+" reset");
            Bukkit.dispatchCommand(gameData.console, "title "+myName+" times 0 30 0");
        }

        if (players.size() >= 2) { players.get(new Random().nextInt(players.size())).addScoreboardTag(murder_tag); }
    }

    void prepareGlassBox(String blockThatPlayerStandsOn) {
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~ ~-1 ~1 ~ ~1 minecraft:gold_block");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-2 ~1 ~-1 ~-2 ~4 ~1 minecraft:glass");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~2 ~1 ~-1 ~2 ~4 ~1 minecraft:glass");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~1 ~-2 ~1 ~4 ~-2 minecraft:glass");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~1 ~2 ~1 ~4 ~2 minecraft:glass");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~5 ~-1 ~1 ~5 ~1 minecraft:gold_block");
    }

    void preparePlayerEffects(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
        PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, 20 * 1 * 60, 1);
        PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
        PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
        if (player.getWorld().getName().equals(gameData.mareWorld)) {
            player.addPotionEffect(glo);
            player.addPotionEffect(sat);
            player.addPotionEffect(reg);
        }
    }

    //make sure every mare has its player riding it - set the new GENERIC_MOVEMENT_SPEED
    void performHorseCheckUp(ArrayList<Horse> mares, ArrayList<Player> players, ArrayList<Location> maresLastLocation) {
        int p = 0;
        for (Horse mare : mares) {
            Player mareAssignedPlayer = players.get(p);

            //there's a passenger and it's not my player
            if (mare.getPassengers().size() > 0 && mare.getPassengers().get(0) != mareAssignedPlayer) {  mare.removePassenger(mare.getPassengers().get(0)); }
            //there's no passenger
            if (mare.getPassengers().size() == 0) {
                mare.addPassenger(mareAssignedPlayer);
                Bukkit.dispatchCommand(gameData.console, "execute as " + mareAssignedPlayer.getName() + " run playsound " + gameData.timerTick + " master @s ~ ~ ~ 1 0.3 1");
            }
            //oops
            if (mares.get(p).isDead()) {
                Horse myMare = makeMare(maresLastLocation.get(p));
                mareAssignedPlayer.teleport(maresLastLocation.get(p));//attempts to prevent client desync where it doesn't think the player is on the horse
                myMare.addPassenger(mareAssignedPlayer);
                mares.set(p, myMare);
            }
            //horse cool speed
            double deltaDistance = mare.getLocation().distance(maresLastLocation.get(p))*20;//it is multiplied by 20 to account for ticks (to make it blocksPerSecond)
            mare.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(initialMareSpeed * (Math.min(Math.max(initialMareSpeed*20, deltaDistance), initialMareSpeed*80))/(initialMareSpeed*35));
            maresLastLocation.set(p, mare.getLocation());
            mare.setHealth(mare.getMaxHealth());

            p++;
        }
    }

    void destroyGlassBox(String blockThatPlayerStandsOn) {
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~ ~-1 ~1 ~ ~1 minecraft:emerald_block");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-2 ~1 ~-1 ~-2 ~4 ~1 minecraft:air");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~2 ~1 ~-1 ~2 ~4 ~1 minecraft:air");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~1 ~-2 ~1 ~4 ~-2 minecraft:air");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~1 ~2 ~1 ~4 ~2 minecraft:air");
        Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.mareWorld + " positioned " + blockThatPlayerStandsOn + " run fill ~-1 ~5 ~-1 ~1 ~5 ~1 minecraft:air");
    }

    void sendFakeBlockFlags(Player player, MaredareCheckpoint checkpoint, boolean flagGot) {
        if (flagGot) {
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.REDSTONE_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1]+1, checkpoint.coordsCenterBlock[2]), Material.COAL_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1]+2, checkpoint.coordsCenterBlock[2]), Material.COAL_BLOCK.createBlockData());
        } else {
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]-1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]-1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0]+1, checkpoint.coordsCenterBlock[1], checkpoint.coordsCenterBlock[2]+1), Material.DIAMOND_BLOCK.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1]+1, checkpoint.coordsCenterBlock[2]), Material.BEACON.createBlockData());
            player.sendBlockChange(new Location(player.getWorld(), checkpoint.coordsCenterBlock[0], checkpoint.coordsCenterBlock[1]+2, checkpoint.coordsCenterBlock[2]), Material.LIGHT_BLUE_STAINED_GLASS.createBlockData());
        }
    }

    ArrayList<Integer> performCheckpointUpdate(ArrayList<Integer> collectedCPs, Player player, int mapID) {
        MaredareMap thisMap = gameData.maredare_maps_loaded.get(mapID);
        for (int i=0; i<thisMap.checkpoints.size(); i++){
            MaredareCheckpoint thisCp = thisMap.checkpoints.get(i);
            if (player.getLocation().distance(thisCp.getCpRespawnLocation(player.getWorld())) < thisCp.radius) {
                //They are treated less like checkpoints and more like collectables - collect all to win (a.k.a. flags)
                if (!collectedCPs.contains(i)) { collectedCPs.add(i); }
//                //NEXT CHECKPOINT
//                if (i == lastCp+1) {
//                    //LAST CP
//                    if (i == thisMap.checkpoints.size()-1) {
//
//                    }
//                    //BEFORE LAST CP
//                    else if (i == thisMap.checkpoints.size()-2) {
//
//                    }
//                    lastCp = i;
//                }
//                //WRONG WAY CHECKPOINT
//                else if (i <= lastCp-1) {
//                    //put "WRONG WAY" text
//                }
//                //MISSED CHECKPOINT
//                else if (i > lastCp+1) {
//                    //put "MISSED CHECKPOINT" text
//                }
//                //ELSE: SAME CHECKPOINT - DO NOTHING
//                else {
//                    //remove "WRONG WAY"/"MISSED CHECKPOINT" text
//                }
            }
        }


        return collectedCPs;
    }

    boolean withenBoundBox(Location playerLocation, int mapID) {
        double[] boundingBox = gameData.maredare_maps_loaded.get(mapID).boundingBoxCoords;
        return Math.min(boundingBox[0], boundingBox[3]) < playerLocation.getX() &&
                Math.max(boundingBox[0], boundingBox[3]) > playerLocation.getX() &&

                Math.min(boundingBox[1], boundingBox[4]) < playerLocation.getY() &&
                Math.max(boundingBox[1], boundingBox[4]) > playerLocation.getY() &&
                Math.min(boundingBox[2], boundingBox[5]) < playerLocation.getZ() &&
                Math.max(boundingBox[2], boundingBox[5]) > playerLocation.getZ();
    }

    int performCheckpointCheckUp(ArrayList<Integer> collectedCPs, int mapID) {
        MaredareMap thisMap = gameData.maredare_maps_loaded.get(mapID);
        if (collectedCPs.size() == thisMap.checkpoints.size()) { return 1; }//FINAL FLAG
        else if (collectedCPs.size() == thisMap.checkpoints.size()-1) { return 0; }//BEFORE FINAL FLAG
        else if (collectedCPs.size() == 0) { return -2; }//NO FLAGS
        return -1;//ANY OTHER AMOUNT
    }

    String playerRandomDisc(String playerName) {
        String chosenDisc = gameData.getRandomMusic();
        if (chosenDisc != "cat" && chosenDisc != "otherside") { Bukkit.dispatchCommand(gameData.console, "tellraw " + playerName + " {\"text\":\"Music by YousifGaming\",\"color\":\"dark_red\"}"); }
        else if (chosenDisc == "cat") { Bukkit.dispatchCommand(gameData.console, "tellraw " + playerName + " {\"text\":\"Music C418 - Dog\",\"color\":\"dark_red\"}"); }
        else if (chosenDisc == "otherside") { Bukkit.dispatchCommand(gameData.console, "tellraw " + playerName + " {\"text\":\"Music Nebby - Mystical Cave\",\"color\":\"dark_red\"}"); }
        Bukkit.dispatchCommand(gameData.console, "tellraw " + playerName + " {\"text\":\"If you don't hear the correct music, do /pack\",\"color\":\"dark_red\"}");
        Bukkit.dispatchCommand(gameData.console, "playsound minecraft:music_disc." + chosenDisc + " record " + playerName + " ~ ~ ~ 999999999999 1 1");
        return chosenDisc;
    }

    //x, y, z of the block to stand on when starting
    String getStandBlock(double spawnX, double spawnY, double spawnZ, double spawnXoff, double spawnYoff, int n) {
        return (spawnX + (spawnXoff * n)) + " " + (spawnY - 1) + " " + (spawnZ + (spawnYoff * n));
    }
}
