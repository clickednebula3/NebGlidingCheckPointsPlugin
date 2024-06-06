package nebpoints.nebpoints.game;

import nebpoints.nebpoints.commands.nebClearScheduledRepeatingTasksExecutor;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/* TODO:
 *  //(CHECK) Freeze players when start timer
 *  //(CHECK) Fix levitation fire making you un-fly and therefore respawned
 *  //(CHECK) Program an actual ending
 *  //(CHECK) Teleport players back to lobby when game ends (and remember onPlayerJoin)
 *  //(CHECK) Make maps tab-able in command
 *  //(CHECK) Fix Checkpoint respawning
 *  //(10/12) Set checkpoints for all maps :) (pain) (this is WAY less pain now that it is with commands)
 *  //(CHECK) Make firework boosters auto-boost (check off-hand for firework maybe?) also lower their boost (and maybe not a firework because that has more power?)
 *  //(CHECK) Show Checkpoint of each player in the low title place
 *  //() Fix Checkpoint respawning, again
 */

public class gliding {
    public boolean[] isRunning = {true};

    public gliding(Plugin nebplugin, ArrayList<Player> plyrs, ArrayList<String> sortColors, int mapID, ConsoleCommandSender console, gameData gameData) {

        ArrayList<Integer> myCP = new ArrayList<>();//the cp of each player
        ArrayList<Integer> eCD = new ArrayList<>();//cp tp cooldown for each player
        ArrayList<Integer> winRank = new ArrayList<>();//order the winners

        double spawnX = gameData.getRespawnX(mapID, 0);
        double spawnY = gameData.getRespawnY(mapID, 0);
        double spawnZ = gameData.getRespawnZ(mapID, 0);
        double spawnXoff = gameData.getRespawnOffX(mapID);
        double spawnYoff = gameData.getRespawnOffZ(mapID);

        Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run gamerule commandBlockOutput false");
        Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run gamerule logAdminCommands false");
        Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run gamerule doImmediateRespawn true");

        //set up spawn box
        Bukkit.dispatchCommand(console, "title @a reset");
        Bukkit.dispatchCommand(console, "title @a times 0 30 0");

        //make sure everyone is online and is added once (just as a bug prevention measure)
//        ArrayList<Player> uniquePlyrs = new ArrayList<>();
//        for (Player plyr : plyrs) {
//            if (plyr.isOnline()) {
//                boolean didntFind = true;
//                for (Player uplyr : uniquePlyrs) {
//                    if (uplyr.getName().equals(plyr.getName())) {
//                        didntFind = false;
//                    }
//                }
//                if (didntFind) {
//                    uniquePlyrs.add(plyr);
//                }
//            }
//        }
//        plyrs = uniquePlyrs;


        //prepare players
        int i = 0;
        for (Player plyr : plyrs) {

            String myName = plyr.getName();

            //give elytra early
            ItemStack coolerElytra = new ItemStack(Material.ELYTRA);
            coolerElytra.addEnchantment(Enchantment.DURABILITY, 3);
            coolerElytra.addEnchantment(Enchantment.BINDING_CURSE, 1);
            coolerElytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
            coolerElytra.getItemMeta().setUnbreakable(true);
            if (plyr.getWorld().getName().equals(gameData.gameWorld)) {
                plyr.getInventory().setChestplate(coolerElytra);
            }

            //Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with " + obj.coolElytra);

            //ominous start sound
            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + gameData.startSound + " master @s ~ ~ ~ 999999999999 1.15 1");

            //set cabinet
            String standBlock = (spawnX + (spawnXoff * i)) + " " + (spawnY - 1) + " " + (spawnZ + (spawnYoff * i));//x, y, z of block to stand on
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~ ~ minecraft:gold_block");

            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~1 ~1 ~ minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~1 ~1 minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~-1 ~1 ~ minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~1 ~-1 minecraft:glass");

            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~1 ~2 ~ minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~2 ~1 minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~-1 ~2 ~ minecraft:glass");
            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~2 ~-1 minecraft:glass");

            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + standBlock + " run setblock ~ ~3 ~ minecraft:gold_block");

            //Bukkit.dispatchCommand(console, "gamemode adventure "+myName);
            //Bukkit.dispatchCommand(console, "attribute "+myName+" minecraft:generic.max_health base set 6");
            //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:saturation 3 255 false");
            //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:regeneration 3 255 false");

            //for each player set checkpoint at start, and elytra-cooldown at start
            myCP.add(0);
            eCD.add(0);
            i++;
        }

        final long[] time = {0};
        final long[] remainingTime = {-1};

        final int[] myTaskIndex = {0};
        final int[] WINEVENTREMAININGTIME = {20 * 30};
        //final double[] checkpointDistance = {0.0, 0.0, 0.0, 0.0};
        //should run every tick
        int loopID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(nebplugin, () -> {
            if (isRunning[0]) {
                //remove disconnecters and "/stop"ers, and stop the game
                int ptemp = 0;
                for (Player plyr : plyrs) {
                    String myName = plyr.getName();
                    String myColor = sortColors.get(ptemp);
                    if (!plyr.getScoreboardTags().contains("glider") || !plyr.isOnline()) {
                        Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"" + myName + " forfeited the gliding race\",\"color\":\"" + myColor + "\"}");
                        plyrs.remove(ptemp);
                        sortColors.remove(ptemp);
                        myCP.remove(ptemp);
                        eCD.remove(ptemp);
                        for (int winner : winRank) {
                            if (winner == ptemp) {
                                winRank.remove(winner);
                            }
                        }
                        ptemp--;
                        if (plyrs.size() == 0) {
                            isRunning[0] = false;
                            gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                            new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                        }
                    }

                    ptemp++;
                }


                //game end text
                if (remainingTime[0] > 0) { remainingTime[0]--; }
                else if (remainingTime[0] == 0) {
                    time[0] = 20 * gameData.gameLength;
                    Bukkit.dispatchCommand(console, "tellraw @a \"Game ended with a determined winner! Ranking (sorted):\"");
                    for (int winner : winRank) {
                        Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"-" + plyrs.get(winner).getName() + "\",\"color\":\"" + sortColors.get(winner) + "\"}");
                    }
                }

                //repeat for each plyr (plyr-based game)
                int p = 0;
                for (Player plyr : plyrs) {

                    String myName = plyr.getName();
                    String myColor = sortColors.get(p);
                    String unstandBlock;//x, y, z of block to stop standing on
                    unstandBlock = (spawnX + (spawnXoff * p)) + " " + (spawnY - 1) + " " + (spawnZ + (spawnYoff * p));

                    //Count to 3
                    if (time[0] <= 20L * gameData.timerLength) {

                        Bukkit.dispatchCommand(console, "title " + myName + " title {\"text\":\"Starting in: " + ((gameData.timerLength * 1L) - (time[0] / 20)) + "...\",\"color\":\"" + sortColors.get(p) + "\"}");
                        if (time[0] % 20 == 0) {
                            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + gameData.timerTick + " master @s ~ ~ ~ 1 1.6 1");
                        }
                        eCD.set(p, 3);

                    }
                    //At (obj.timerLength+1) seconds, start game (GO!!)
                    else if (time[0] <= 20 * (gameData.timerLength + 1L)) {

                        Bukkit.dispatchCommand(console, "title " + myName + " title {\"text\":\"Go!!\",\"color\":\"" + sortColors.get(p) + "\"}");
                        if (time[0] % 20 == 1) {
                            //break cabinet
                            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + gameData.timerGo + " master @s ~ ~ ~ 1 1.85 1");

                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~ ~ minecraft:air");

                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~1 ~1 ~ minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~1 ~1 minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~-1 ~1 ~ minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~1 ~-1 minecraft:air");

                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~1 ~2 ~ minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~2 ~1 minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~-1 ~2 ~ minecraft:air");
                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~2 ~-1 minecraft:air");

                            Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " positioned " + unstandBlock + " run setblock ~ ~3 ~ minecraft:air");

                            //give elytra
                            ItemStack coolerElytra = new ItemStack(Material.ELYTRA);
                            coolerElytra.addEnchantment(Enchantment.DURABILITY, 3);
                            coolerElytra.addEnchantment(Enchantment.BINDING_CURSE, 1);
                            coolerElytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
                            coolerElytra.getItemMeta().setUnbreakable(true);
                            if (plyr.getWorld().getName().equals(gameData.gameWorld)) {
                                plyr.getInventory().setChestplate(coolerElytra);
                            }

                            //put effects in place
                            plyr.setGameMode(GameMode.ADVENTURE);
                            plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
                            PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, 20 * 1 * 60, 1);
                            PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
                            PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
                            if (plyr.getWorld().getName().equals(gameData.gameWorld)) {
                                plyr.addPotionEffect(glo);
                                plyr.addPotionEffect(sat);
                                plyr.addPotionEffect(reg);
                            }
                        }

                        if (time[0] == 20 * (gameData.timerLength + 1L)) {
                            String chosenDisc = gameData.getRandomMusic();
                            if (chosenDisc != "cat" && chosenDisc != "otherside") {
                                Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music by YousifGaming\",\"color\":\"dark_red\"}");
                            } else if (chosenDisc == "cat") {
                                Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music C418 - Dog\",\"color\":\"dark_red\"}");
                            } else if (chosenDisc == "otherside") {
                                Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music Nebby - Mystical Cave\",\"color\":\"dark_red\"}");
                            }
                            Bukkit.dispatchCommand(console, "playsound minecraft:music_disc." + chosenDisc + " record " + myName + " ~ ~ ~ 999999999999 1 1");
                        }

                        if (eCD.get(p) > 0) {
                            eCD.set(p, eCD.get(p) - 1);
                            if (eCD.get(p) == 1) {
                                plyr.setGliding(true);
                            }
                        } else {
                            plyr.setGliding(true);
                        }

                    }
                    //During game After the timer
                    else {

                        boolean winnersContainsMe = false;
                        for (int winner : winRank) { if (winner == p) { winnersContainsMe = true; } }

                        if (plyr.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
                            plyr.setVelocity(new Vector(plyr.getVelocity().getX() * 1.075, plyr.getVelocity().getY() * 1.05, plyr.getVelocity().getZ() * 1.075));
                        }

                        if (plyr.hasPotionEffect(PotionEffectType.LEVITATION)) {
                            plyr.setVelocity(new Vector(plyr.getVelocity().getX() * 1.075, plyr.getVelocity().getY(), plyr.getVelocity().getZ() * 1.075));//make levitating less of a slowerizer
                            eCD.set(p, 5);
                        }
                        else if (!plyr.isGliding() && eCD.get(p) <= 0) {

                            double rX = gameData.getRespawnX(mapID, myCP.get(p));
                            double rY = gameData.getRespawnY(mapID, myCP.get(p));
                            double rZ = gameData.getRespawnZ(mapID, myCP.get(p));
                            double rLR = gameData.getRespawnLR(mapID, myCP.get(p));
                            double rUD = gameData.getRespawnUD(mapID, myCP.get(p));
                            String respawnCoords = rX + " " + rY + " " + rZ + " " + rLR + " " + rUD;

                            if (rX == -1.0 || rY == -1.0 || rZ == -1.0 || rLR == -1.0 || rUD == -1.0) {

                            } else {
                                if (!winnersContainsMe) {
                                    Bukkit.dispatchCommand(console, "tellraw " + myName + " \"You Fell! Teleporting to last checkpoint...\"");
                                    Bukkit.dispatchCommand(console, "execute in " + gameData.gameWorld + " run tp " + myName + " " + respawnCoords);

                                    ItemStack coolerElytra = new ItemStack(Material.ELYTRA);
                                    ItemStack airItem = new ItemStack(Material.AIR);
                                    coolerElytra.addEnchantment(Enchantment.DURABILITY, 3);
                                    coolerElytra.addEnchantment(Enchantment.BINDING_CURSE, 1);
                                    coolerElytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
                                    plyr.getInventory().setChestplate(coolerElytra);
                                    plyr.getInventory().setItemInOffHand(airItem);

                                    PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, 20 * 1 * 60, 1);
                                    PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
                                    PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
                                    plyr.addPotionEffect(glo);
                                    plyr.addPotionEffect(sat);
                                    plyr.addPotionEffect(reg);

                                    //Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with " + obj.coolElytra);
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:saturation 3 255 false");
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:regeneration 3 255 false");
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:glowing 300 1 true");
                                    plyr.setGliding(true);
                                }
                            }
                            eCD.set(p, 10);
                        }

                        if (plyr.isSneaking()) { Bukkit.dispatchCommand(console, "effect clear " + myName + " minecraft:levitation"); }

                        if (eCD.get(p) > 0) {
                            eCD.set(p, eCD.get(p) - 1);
                            if (eCD.get(p) == 1) { if (!winnersContainsMe) { plyr.setGliding(true); } }
                        } else {
                            if (!winnersContainsMe) { plyr.setGliding(true);  }
                        }


                    }

                    //During game, all the game

                    //checkpoint handler
                    double plyrX = plyrs.get(p).getLocation().getX();
                    double plyrY = plyrs.get(p).getLocation().getY();
                    double plyrZ = plyrs.get(p).getLocation().getZ();
                    for (int m = 0; m < gameData.getCpCount(mapID); m++) {
                        if (gameData.isInBox(mapID, m, plyrX, plyrY, plyrZ)) {
                            //GOING BACK
                            if (myCP.get(p) > m) {
                                Bukkit.dispatchCommand(console, "title " + myName + " title {\"text\":\"Wrong way!\",\"color\":\"red\"}");
                                Bukkit.dispatchCommand(console, "playsound " + gameData.wrongWay + " master " + myName + " ~ ~ ~ 2 0.3 1");
                            }
                            //GOING FORWARDS
                            else if (myCP.get(p) < m) {
                                //THE END CHECKPOINT
                                if (myCP.get(p) == (gameData.getCpCount(mapID) - 2)) {
                                    Bukkit.dispatchCommand(console, "playsound " + gameData.finishSound + " master " + myName + " ~ ~ ~ 2 1.65 1");
                                    winRank.add(p);
                                    Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"" + myName + " finished gliding! Rank: " + winRank.size() + "\",\"color\":\"" + myColor + "\"}");
                                    remainingTime[0] = WINEVENTREMAININGTIME[0];
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //ALMOST TEHRE
                                else if (myCP.get(p) == (gameData.getCpCount(mapID) - 3)) {
                                    for (Player pl : plyrs) {
                                        Bukkit.dispatchCommand(console, "playsound " + gameData.timerGo + " master " + pl.getName() + " ~ ~ ~ 2 1.85 1");
                                    }
                                    Bukkit.dispatchCommand(console, "tellraw " + myName + " \"Almost there! Traverse the final stretch!\"");
                                    Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"" + myName + " is at the final checkpoint! Hurry up!\",\"color\":\"" + myColor + "\"}");
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //NORMAL CHECKPOINTS
                                else if (m <= myCP.get(p)+4) {
                                    Bukkit.dispatchCommand(console, "playsound " + gameData.timerGo + " master " + myName + " ~ ~ ~ 2 1.85 1");
                                    //for (Player pl : plyrs) {
                                    //    Bukkit.dispatchCommand(console, "title "+pl.getName()+" subtitle {\"text\":\""+myName+" reached checkpoint "+m+"\",\"color\":\""+myColor+"\"}");
                                    //    Bukkit.dispatchCommand(console, "title "+pl.getName()+" title \"\"");
                                    //}
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //MISSED TOO MANY CHECKPOINTS
                                else {
                                    Bukkit.dispatchCommand(console, "playsound " + gameData.timerGo + " master " + myName + " ~ ~ ~ 2 0.4 1");
                                    Bukkit.dispatchCommand(console, "tellraw " + myName + " \"You missed too many checkpoints!\"");
                                    plyr.setGliding(false);
                                }
                            }

                        }
                    }

                    //minititle checkpoint shower
                    if (remainingTime[0] > 0) { Bukkit.dispatchCommand(console, "title " + myName + " actionbar \"Remaining Time: " + (remainingTime[0]) + "\""); }
                    else {
                        if (myCP.get(p) < gameData.getCpCount(mapID) - 1) {
                            //double lastX = obj.getRespawnX(mapID, myCP.get(p));
                            //double lastY = obj.getRespawnY(mapID, myCP.get(p));
                            //double lastZ = obj.getRespawnZ(mapID, myCP.get(p));
                            //double nextX = obj.getRespawnX(mapID, myCP.get(p)+1);
                            //double nextY = obj.getRespawnY(mapID, myCP.get(p)+1);
                            //double nextZ = obj.getRespawnZ(mapID, myCP.get(p)+1);
                            //Location lastCheckpoint = new Location(plyr.getWorld(), lastX, lastY, lastZ);
                            //Location nextCheckpoint = new Location(plyr.getWorld(), nextX, nextY, nextZ);
                            //double lastDistance = plyr.getLocation().distance(lastCheckpoint);
                            //double nextDistance = plyr.getLocation().distance(nextCheckpoint);
                            //double Percentile = 1000*(nextDistance/(lastDistance+nextDistance));
                            //double finalPercentile = ((Math.floor(Percentile))/10);
//
                            //if (myColor == "red") {checkpointDistance[0] = finalPercentile;}
                            //else if (myColor == "blue") {checkpointDistance[1] = finalPercentile;}
                            //else if (myColor == "yellow") {checkpointDistance[2] = finalPercentile;}
                            //else if (myColor == "green") {checkpointDistance[3] = finalPercentile;}

                            String positionText = "";//[Red-1 99.9] [Blue-1 99.9] [Yellow-1 99.9] [Green-1 99.9]
                            //[{"text":"[A]","color":"red"},{"text":" [B]","color":"blue"}]

                            int p_mini = 0;
                            for (Player plyr_mini : plyrs) {
                                String myName_mini = plyr_mini.getName();
                                String myColor_mini = sortColors.get(p_mini);
                                if (p_mini > 0) {
                                    positionText += ",";
                                }
                                positionText += "{\"text\":\"[" + myName_mini + "-" + myCP.get(p_mini) + "] \",\"color\":\"" + myColor_mini + "\"}";
                                p_mini++;
                            }

                            Bukkit.dispatchCommand(console, "title " + myName + " actionbar [" + positionText + "]");
                        }
                    }

                    //time over
                    if (time[0] >= 20L * gameData.gameLength || remainingTime[0] == 0) {
                        if (remainingTime[0] >= 0) {//at least 1 winner
                            //ending game
                            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + gameData.finishSound + " master @s ~ ~ ~ 0.15 1.65 1");
                        } else {
                            //game ended with no winner
                            Bukkit.dispatchCommand(console, "tellraw @a \"Game timer ended with no winners\"");
                        }

                        //clear elytra
                        ItemStack coolerAir = new ItemStack(Material.AIR);
                        plyr.getInventory().setChestplate(coolerAir);
                        //Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with minecraft:air");
                        //take effects away
                        plyr.setGameMode(GameMode.ADVENTURE);
                        plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                        plyr.removePotionEffect(PotionEffectType.GLOWING);
                        plyr.removePotionEffect(PotionEffectType.LEVITATION);
                        PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
                        PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
                        plyr.addPotionEffect(sat);
                        plyr.addPotionEffect(reg);
                        //Bukkit.dispatchCommand(console, "gamemode adventure "+myName);
                        //Bukkit.dispatchCommand(console, "attribute "+myName+" minecraft:generic.max_health base set 20");
                        //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:saturation 3 255 false");
                        //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:regeneration 3 255 false");
                        //Bukkit.dispatchCommand(console, "effect clear " + myName + " minecraft:glowing");

                        //back to lobby
                        Bukkit.dispatchCommand(console, "execute in " + gameData.lobbyWorld + " run tp " + myName + " " + gameData.lobbyCoords[0] + " " + gameData.lobbyCoords[1] + " " + gameData.lobbyCoords[2] + " " + gameData.lobbyCoords[3] + " " + gameData.lobbyCoords[4]);
//                            try {
//                                FileWriter myWriter = new FileWriter("glidingData.txt");
//                                myWriter.write("Files in Java might be tricky, but it is fun enough!");
//                                myWriter.close();
//                                System.out.println("Successfully wrote to the file.");
//                            } catch (IOException e) {
//                                System.out.println("An error occurred.");
//                                e.printStackTrace();
//                            }

                        //untag players
                        plyr.removeScoreboardTag("gameRunning");
                        plyr.removeScoreboardTag("glider_" + myColor);
                        stopExecutor stopper = new stopExecutor(gameData);
                        stopper.stopPlayerGames(plyr);
                        isRunning[0] = false;
                        gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                        new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                    }

                    p++;
                }
                time[0]++;
            }
        }, 0L, 1L);
        myTaskIndex[0] = gameData.ScheduledRepeatingTasks.size();
        gameData.ScheduledRepeatingTasks.add(loopID);
        gameData.FinishedRepeatingTasks.add(false);
    }
}
