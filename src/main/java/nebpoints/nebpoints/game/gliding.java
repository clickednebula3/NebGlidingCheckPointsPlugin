package nebpoints.nebpoints.game;

import nebpoints.nebpoints.commands.nebClearScheduledRepeatingTasksExecutor;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.gameData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
 *  //() Get how fast you finished the map
 *  //() Save best time to a new file that is not config.yml
 *  //() Leaderboards with PAPI maybe?
 */

public class gliding {
    public boolean[] isRunning = {true};

//    String[] tag_color = {"glider_red", "glider_blue", "glider_yellow", "glider_green"};//not sure if implementing serves anything, and how to do so if so

    private ItemStack getCoolerElytra() {
        ItemStack coolerElytra = new ItemStack(Material.ELYTRA);
        coolerElytra.addEnchantment(Enchantment.UNBREAKING, 3);
        coolerElytra.addEnchantment(Enchantment.BINDING_CURSE, 1);
        coolerElytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        coolerElytra.getItemMeta().setUnbreakable(true);
        return coolerElytra;
    }

    private void applyCoolerElytra(Player player, World world) {
        if (player.getWorld().equals(world)) {
            player.getInventory().remove(Material.FIREWORK_ROCKET);
            player.getInventory().setChestplate(getCoolerElytra());
        }
    }

    private void preparePlayerAndEffects(Player player, World world) {
        player.setGameMode(GameMode.ADVENTURE);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
        player.setHealth(6);
        player.setFireTicks(0);
        player.getInventory().remove(Material.FIREWORK_ROCKET);
        PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, 20 * 3 * 60, 1);
        PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
        PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
        if (player.getWorld().equals(world)) {
            player.addPotionEffect(glo);
            player.addPotionEffect(sat);
            player.addPotionEffect(reg);
        }
    }

    private void unpreparePlayers(List<Player> plyrs, gameData gameData, ArrayList<Integer> sortColorIndexes) {
        int p = 0;
        for (Player plyr : plyrs) {
            plyr.getInventory().remove(Material.ELYTRA);
            plyr.getInventory().remove(Material.FIREWORK_ROCKET);

            plyr.setGameMode(GameMode.ADVENTURE);
            plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            plyr.setHealth(20);

            plyr.removePotionEffect(PotionEffectType.GLOWING);
            plyr.removePotionEffect(PotionEffectType.LEVITATION);
            PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
            PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
            plyr.addPotionEffect(sat);
            plyr.addPotionEffect(reg);

            plyr.teleport(gameData.glideLobbyLocation);

            plyr.removeScoreboardTag(gameData.tag_in_game);
            plyr.removeScoreboardTag(gameData.tag_glide+"_"+gameData.simpleColor(sortColorIndexes, p));
            p++;
        }
    }

    private void prepareGameRules(World world) {
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }

    public gliding(Plugin nebplugin, ArrayList<Player> plyrs, ArrayList<Integer> sortColorsIndexes, int mapID, gameData gameData) {

        ArrayList<Integer> myCP = new ArrayList<>();//the cp of each player
        ArrayList<Integer> eCD = new ArrayList<>();//cp tp cooldown for each player (will not tp unless cooldown finished; useful for levitation)
        ArrayList<Integer> winRank = new ArrayList<>();//order the winners

        World gameWorld = Bukkit.getWorld(gameData.gameWorld);
        prepareGameRules(Objects.requireNonNull(gameWorld));

//        Bukkit.dispatchCommand(gameData.console, "title @a reset");
//        Bukkit.dispatchCommand(gameData.console, "title @a times 0 30 0");

        //set up spawn box
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
            //elytra
            applyCoolerElytra(plyr, gameWorld);
            plyr.resetTitle();
            gameData.checkApplyPack(plyr);
            plyr.playSound(plyr.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 9999, 1.15f);
            gameData.placeGlidingCabinet(mapID, i, plyr, false);
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
            int H = 0;
            float S = 1, V = 1;
            if (isRunning[0]) {
                H=(H+1)%360;

                //remove disconnecters and "/stop"ers, and stop the game
                int ptemp = 0;
                for (Player plyr : plyrs) {//todo: ERROR HERE
                    if (!plyr.getScoreboardTags().contains(gameData.tag_glide) || !plyr.isOnline()) {
                        Bukkit.broadcastMessage(gameData.chatColor(sortColorsIndexes, ptemp) + plyr.getName()+" forfeited the gliding race");
//                        Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + plyr.getName() + " forfeited the gliding race\",\"color\":\"" + gameData.simpleColor(sortColorsIndexes, ptemp) + "\"}");
                        plyrs.remove(ptemp);
                        sortColorsIndexes.remove(ptemp);
                        myCP.remove(ptemp);
                        eCD.remove(ptemp);
                        for (int winner : winRank) { if (winner == ptemp) { winRank.remove(winner); } }
                        ptemp--;
                        if (plyrs.isEmpty()) {
                            isRunning[0] = false;
                            gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                            new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks(false);
                        }
                    }

                    ptemp++;
                }


                //game end text
                if (remainingTime[0] > 0) { remainingTime[0]--; }
                else if (remainingTime[0] == 0) {
                    time[0] = 20L * gameData.gameLength;
                    Bukkit.broadcastMessage("[Nebpoints] Gliding ended with winners:");
//                    Bukkit.dispatchCommand(gameData.console, "tellraw @a \"Game ended with a determined winner! Ranking (sorted):\"");
                    for (int winner : winRank) {
                        Bukkit.broadcastMessage(ChatColor.RESET+"- "+gameData.chatColor(sortColorsIndexes, winner) + plyrs.get(winner).getName() + " ()");//todo ADD TIME TAKEN
//                        Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"-" + plyrs.get(winner).getName() + "\",\"color\":\"" + sortColors.get(winner) + "\"}");
                    }
                }


                //repeat for each plyr (plyr-based game)
                int p = 0;
                for (Player plyr : plyrs) {

                    String myName = plyr.getName();
                    String myColor = gameData.simpleColor(sortColorsIndexes, p);
                    ChatColor myChatColor = gameData.chatColor(sortColorsIndexes, p);
//                    String unstandBlock;//x, y, z of block to stop standing on
//                    unstandBlock = (spawnX + (spawnXoff * p)) + " " + (spawnY - 1) + " " + (spawnZ + (spawnZoff * p));

                    //Count to 3
                    if (time[0] <= 20L * gameData.timerLength) {
                        long t = (gameData.timerLength - (time[0] / 20));
                        plyr.sendTitle(myChatColor+"Starting in: "+t+"...", "", 0, 30, 0);
//                        Bukkit.dispatchCommand(gameData.console, "title " + myName + " title {\"text\":\"Starting in: " +  ((gameData.timerLength * 1L) - (time[0] / 20)) + "...\",\"color\":\"" + sortColors.get(p) + "\"}");
                        if (time[0] % 20 == 0) {
                            plyr.playSound(plyr.getLocation(), gameData.snd_tick, SoundCategory.MASTER, 1f, 1.6f, 1);
//                            Bukkit.dispatchCommand(gameData.console, "execute as " + myName + " run playsound " + gameData.timerTick + " master @s ~ ~ ~ 1 1.6 1");
                        }
                        eCD.set(p, 3);

                    }
                    //At (obj.timerLength+1) seconds, start game (GO!!)
                    else if (time[0] <= 20 * (gameData.timerLength + 1L)) {
                        plyr.sendTitle(myChatColor+"Go!!", "", 0, 30, 0);
//                        Bukkit.dispatchCommand(gameData.console, "title " + myName + " title {\"text\":\"Go!!\",\"color\":\"" + sortColors.get(p) + "\"}");
                        if (time[0] % 20 == 1) {
                            //break cabinet

                            gameData.placeGlidingCabinet(mapID, p, plyr, true);
                            plyr.playSound(plyr.getLocation(), gameData.snd_go, SoundCategory.MASTER, 1f, 1.85f, 1);
//                            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + gameData.timerGo + " master @s ~ ~ ~ 1 1.85 1");
                            //give elytra
                            applyCoolerElytra(plyr, gameWorld);

                            //put effects in place
                            preparePlayerAndEffects(plyr, gameWorld);
                        }

                        if (time[0] == 20 * (gameData.timerLength + 1L)) { gameData.playGameMusic(plyr); }

                        if (eCD.get(p) > 0) {
                            eCD.set(p, eCD.get(p) - 1);
                            if (eCD.get(p) == 1) { plyr.setGliding(true); }
                        } else { plyr.setGliding(true); }

                    }
                    //During game After the timer
                    else {

                        boolean winnersContainsMe = false;
                        for (int winner : winRank) { if (winner == p) { winnersContainsMe = true; break; } }

                        if (plyr.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET)
                        { plyr.setVelocity(new Vector(plyr.getVelocity().getX() * 1.075, plyr.getVelocity().getY() * 1.05, plyr.getVelocity().getZ() * 1.075)); }

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
//                            String respawnCoords = rX + " " + rY + " " + rZ + " " + rLR + " " + rUD;

                            if (rX != -1.0 && rY != -1.0 && rZ != -1.0 && rLR != -1.0 && rUD != -1.0) {
                                if (!winnersContainsMe) {
                                    plyr.sendMessage("You Fell! Teleporting to last checkpoint...");
//                                    Bukkit.dispatchCommand(gameData.console, "tellraw " + myName + " \"You Fell! Teleporting to last checkpoint...\"");
//                                    Bukkit.dispatchCommand(gameData.console, "execute in " + gameData.gameWorld + " run tp " + myName + " " + respawnCoords);
                                    plyr.teleport(gameData.getRespawnLocation(mapID, myCP.get(p)));
                                    applyCoolerElytra(plyr, gameWorld);
                                    preparePlayerAndEffects(plyr, gameWorld);

//                                    PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, 20 * 1 * 60, 1);
//                                    PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
//                                    PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
//                                    plyr.addPotionEffect(glo);
//                                    plyr.addPotionEffect(sat);
//                                    plyr.addPotionEffect(reg);

                                    //Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with " + obj.coolElytra);
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:saturation 3 255 false");
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:regeneration 3 255 false");
                                    //Bukkit.dispatchCommand(console, "effect give " + myName + " minecraft:glowing 300 1 true");
                                    plyr.setGliding(true);
                                }
                            }
                            eCD.set(p, 10);
                        }
                        else if (plyr.isGliding() && plyr.hasPermission(gameData.perm_glide_ranked)) {
                            //credits to Mortaza Alhames for the HSV->RGB formula
                            float R, G, B;
                            if (H < 60 || H >= 300) {//R max
                                R = V;
                                if (H < 60) {//B min
                                    B = -V*(S-1);
                                    G = ((float) H/60)*(R-B)+B;//
                                    while (G<0){G+=1;} while (G>1){G-=1;}
                                } else {//G min
                                    G = -V*(S-1);
                                    B = -(((float) H/60)*(R-G)-G);
                                    while (B<0){B+=1;} while (B>1){B-=1;}
                                }
                            } else if (H < 180) {//G max
                                G = V;
                                if (H < 120) {//B min
                                    B = -V*(S-1);
                                    R = -((((float) H/60)-2)*(G-B)-B);
                                    while (R<0){R+=1;} while (R>1){R-=1;}
                                } else {//R min
                                    R = -V*(S-1);
                                    B = (((float) H/60)-2)*(G-R)+R;//
                                    while (B<0){B+=1;} while (B>1){B-=1;}
                                }
                            } else {//B max
                                B = V;
                                if (H < 240) {//R min
                                    R = -V*(S-1);
                                    G = -((((float) H/60)-4)*(B-R)-R);
                                    while (G<0){G+=1;} while (G>1){G-=1;}
                                } else {//G min
                                    G = -V*(S-1);
                                    R = (((float) H/60)-4)*(B-G)+G;//
                                    while (R<0){R+=1;} while (R>1){R-=1;}
                                }
                            }
                            plyr.getWorld().spawnParticle(
                                    Particle.DUST, plyr.getLocation(), 0, 0, 0, 0,
                                    new Particle.DustOptions(Color.fromRGB(
                                        (int) (255*Math.clamp(R,0,1)),
                                        (int) (255*Math.clamp(G,0,1)),
                                        (int) (255*Math.clamp(B,0,1))
                            ),1f));
                        }

                        if (plyr.isSneaking() && plyr.hasPotionEffect(PotionEffectType.LEVITATION)) {
                            plyr.removePotionEffect(PotionEffectType.LEVITATION);
//                            Bukkit.dispatchCommand(gameData.console, "effect clear " + myName + " minecraft:levitation");
                        }

                        if (eCD.get(p) > 0) {
                            eCD.set(p, eCD.get(p) - 1);
                            if (eCD.get(p) == 1) { if (!winnersContainsMe) { plyr.setGliding(true); } }
                        } else if (!winnersContainsMe) { plyr.setGliding(true); }


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
                                plyr.sendTitle(ChatColor.DARK_RED + "Wrong Way!", "", 0, 30, 0);
                                plyr.playSound(plyr.getLocation(), gameData.snd_wrong, SoundCategory.MASTER, 2f, 0.3f, 1);
//                                Bukkit.dispatchCommand(gameData.console, "title " + myName + " title {\"text\":\"Wrong way!\",\"color\":\"red\"}");
//                                Bukkit.dispatchCommand(gameData.console, "playsound " + gameData.wrongWay + " master " + myName + " ~ ~ ~ 2 0.3 1");
                                plyr.setGliding(false);
                            }
                            //GOING FORWARDS
                            else if (myCP.get(p) < m) {
                                //THE END CHECKPOINT
                                if (m <= myCP.get(p)+4 && myCP.get(p) == (gameData.getCpCount(mapID) - 2)) {
                                    gameWorld.playSound(plyr, gameData.snd_finish, SoundCategory.MASTER, 1f, 1.65f, 1);
//                                    Bukkit.dispatchCommand(gameData.console, "playsound " + gameData.finishSound + " master " + myName + " ~ ~ ~ 2 1.65 1");
                                    winRank.add(p);
                                    Bukkit.broadcastMessage(myChatColor+myName+" finished gliding! Time: "+(time[0]/20f));
//                                    Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + myName + " finished gliding! Rank: " + winRank.size() + "\",\"color\":\"" + myColor + "\"}");
                                    remainingTime[0] = WINEVENTREMAININGTIME[0];
                                    if (winRank.size() == plyrs.size()) { remainingTime[0] = WINEVENTREMAININGTIME[0]/5; }
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //ALMOST TEHRE
                                else if (m <= myCP.get(p)+4 && myCP.get(p) == (gameData.getCpCount(mapID) - 3)) {
                                    gameWorld.playSound(plyr, gameData.snd_go, SoundCategory.MASTER, 2f, 1.85f, 1);
                                    plyr.sendMessage("Almost there! Traverse the final stretch!");
                                    Bukkit.broadcastMessage(myChatColor+myName+" is at the final checkpoint! Hurry up!");
//                                    for (Player pl : plyrs) {
//                                        Bukkit.dispatchCommand(gameData.console, "playsound " + gameData.timerGo + " master " + pl.getName() + " ~ ~ ~ 2 1.85 1");
//                                    }
//                                    Bukkit.dispatchCommand(gameData.console, "tellraw " + myName + " \"Almost there! Traverse the final stretch!\"");
//                                    Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\"" + myName + " is at the final checkpoint! Hurry up!\",\"color\":\"" + myColor + "\"}");
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //NORMAL CHECKPOINTS
                                else if (m <= myCP.get(p)+4) {
                                    gameWorld.playSound(plyr, gameData.snd_go, SoundCategory.MASTER, 1f, 1.85f, 1);
//                                    Bukkit.dispatchCommand(gameData.console, "playsound " + gameData.timerGo + " master " + myName + " ~ ~ ~ 2 1.85 1");
                                    //for (Player pl : plyrs) {
                                    //    Bukkit.dispatchCommand(console, "title "+pl.getName()+" subtitle {\"text\":\""+myName+" reached checkpoint "+m+"\",\"color\":\""+myColor+"\"}");
                                    //    Bukkit.dispatchCommand(console, "title "+pl.getName()+" title \"\"");
                                    //}
                                    myCP.set(p, m);//p = index of player, m = index of checkpoint, myCp.get(o) = index of last reached checkpoint
                                }
                                //MISSED TOO MANY CHECKPOINTS
                                else {
                                    plyr.playSound(plyr.getLocation(), gameData.snd_go, SoundCategory.MASTER, 2f, 0.4f, 1);
                                    plyr.sendMessage("You missed too many checkpoints. Pass through the beacons.");
//                                    Bukkit.dispatchCommand(gameData.console, "playsound " + gameData.timerGo + " master " + myName + " ~ ~ ~ 2 0.4 1");
//                                    Bukkit.dispatchCommand(gameData.console, "tellraw " + myName + " \"You missed too many checkpoints!\"");
                                    plyr.setGliding(false);
                                }
                            }

                        }
                    }

                    //minititle checkpoint shower
                    if (remainingTime[0] > 0) {
                        plyr.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RESET+"Remaining Time: "+Math.ceil((double) remainingTime[0] / 20)));
//                        Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar \"Remaining Time: " + Math.ceil((double) remainingTime[0] / 20) + "\"");
                    }
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

//                            String positionText = "";//[Red-1 99.9] [Blue-1 99.9] [Yellow-1 99.9] [Green-1 99.9]
//                            //[{"text":"[A]","color":"red"},{"text":" [B]","color":"blue"}];
//
//                            int p_mini = 0;
//                            for (Player plyr_mini : plyrs) {
//                                String myName_mini = plyr_mini.getName();
//                                String myColor_mini = sortColors.get(p_mini);
//                                if (p_mini > 0) { positionText += ","; }
//                                positionText += "{\"text\":\"[" + myName_mini + "-" + myCP.get(p_mini) + "] \",\"color\":\"" + myColor_mini + "\"}";
//                                p_mini++;
//                            }
//
//                            Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar [" + positionText + "]");
//

                            StringBuilder checkpointActionbarString = new StringBuilder();
                            checkpointActionbarString
                                    .append(ChatColor.RESET)
                                    .append("Time: ")
                                    .append(Math.round(Math.ceil(time[0]/20f)))
                                    .append(" ");
                            int p_mini = 0;
                            for (Player plyr_mini : plyrs) {
                                checkpointActionbarString
                                        .append(gameData.chatColor(sortColorsIndexes, p_mini))
                                        .append("[")
                                        .append(plyr_mini.getName())
                                        .append(" <")
                                        .append(myCP.get(p_mini)).append(">] ");
                                p_mini++;
                            }
                            plyr.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(checkpointActionbarString.toString()));
                        }
                    }

                    //time over
                    if (time[0] >= 20L * gameData.gameLength || remainingTime[0] == 0) {
                        if (remainingTime[0] >= 0) {//at least 1 winner
                            //ending game
                            plyr.playSound(plyr.getLocation(), gameData.snd_finish, SoundCategory.MASTER,0.15f, 1.65f, 1);
//                            Bukkit.dispatchCommand(gameData.console, "execute as " + myName + " run playsound " + gameData.finishSound + " master @s ~ ~ ~ 0.15 1.65 1");
                        } else {
                            //game ended with no winner
                            Bukkit.broadcastMessage("[NebPoints] Gliding timer ended with no winners.");
//                            Bukkit.dispatchCommand(gameData.console, "tellraw @a \"Game timer ended with no winners\"");
                        }
                        remainingTime[0] = -1;

                        //clear elytra
//                        ItemStack coolerAir = new ItemStack(Material.AIR);
//                        plyr.getInventory().setChestplate(coolerAir);
//                        //Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with minecraft:air");
//                        //take effects away
//                        plyr.setGameMode(GameMode.ADVENTURE);
//                        plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
//                        plyr.removePotionEffect(PotionEffectType.GLOWING);
//                        plyr.removePotionEffect(PotionEffectType.LEVITATION);
//                        PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
//                        PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
//                        plyr.addPotionEffect(sat);
//                        plyr.addPotionEffect(reg);
//                        //Bukkit.dispatchCommand(console, "gamemode adventure "+myName);
//                        //Bukkit.dispatchCommand(console, "attribute "+myName+" minecraft:generic.max_health base set 20");
//                        //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:saturation 3 255 false");
//                        //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:regeneration 3 255 false");
//                        //Bukkit.dispatchCommand(console, "effect clear " + myName + " minecraft:glowing");

                        unpreparePlayers(plyrs, gameData, sortColorsIndexes);
                        //back to lobby
//                        Location lobbyLocation = new Location(Bukkit.getWorld(gameData.lobbyWorld), gameData.lobbyCoords[0], gameData.lobbyCoords[1], gameData.lobbyCoords[2], (float) gameData.lobbyCoords[3], (float) gameData.lobbyCoords[4]);

//                        Bukkit.dispatchCommand(console, "execute in " + gameData.lobbyWorld + " run tp " + myName + " " + gameData.lobbyCoords[0] + " " + gameData.lobbyCoords[1] + " " + gameData.lobbyCoords[2] + " " + gameData.lobbyCoords[3] + " " + gameData.lobbyCoords[4]);
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
                        stopExecutor stopper = new stopExecutor(gameData);
                        stopper.stopPlayerGames(plyr);
                        isRunning[0] = false;
                        gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                        new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks(false);
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
