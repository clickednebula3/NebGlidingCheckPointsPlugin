package nebpoints.nebpoints.game;

import nebpoints.nebpoints.commands.nebClearScheduledRepeatingTasksExecutor;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.Disasters;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

/*
 * TODO:
 * -Make arenas selectable with the cmd
 * -Add more disaster events
 * -Add more arena maps
    //todo: easy to add Arenas
    //todo: make it possible to add phases for deteriorate
 */

public class disastrophe {
    public boolean[] isRunning = {true};
    gameData gameData;


    public disastrophe(Plugin nebplugin, ArrayList<Player> plyrs, ArrayList<String> sortColors, int ArenaID, gameData gameData, disasterData disasterData) {
        this.gameData = gameData;

        Disasters disasters = new Disasters();
        ArrayList<Integer> winRank = new ArrayList<>();//order the winners (unnecessary)

        //show platform if it doesn't start there
        boolean[] platformOn = {false};
        boolean[] mainPlatformSafe = {true};
        int[] deteriorateCurrentDanger = {0};

        prepareRules(plyrs);


        final long[] time = {0};
        final long[] remainingTime = {-1};
        final long[] untilNextEvent = {-1};
        final int[] round = {0};
        final int[] currentDisaster = {0};
        boolean[] finalisingGame = {false};

        final long[] GAMEOVERTIME = {20L * 120L};
        final long[] HARDMODETIME = {20L * 80L};
        final int[] myTaskIndex = {0};
        int loopID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(nebplugin, () -> {
            //constants
            if (isRunning[0]) {/// in-game:

                //check for /leave or disconnect
                int ptemp=0;
                for (Player plyr : plyrs) {
                    if (!plyr.getScoreboardTags().contains("disaster") || !plyr.isOnline()) {
                        String myName = plyrs.get(ptemp).getName();
                        String myColor = sortColors.get(ptemp);

                        Bukkit.dispatchCommand(gameData.console, "tellraw @a {\"text\":\""+myName+" forfeited the disastrophe challenge\",\"color\":\""+myColor+"\"}");
                        plyr.addScoreboardTag("disaster_ghost");
                        destablishPlayer(plyr, gameData.console, myName, myColor, gameData.finishSound, gameData);

                        plyrs.remove(ptemp);
                        sortColors.remove(ptemp);
                        for (int winner : winRank) {
                            if (winner == ptemp) {
                                winRank.remove(winner);
                            }
                        }
                        ptemp--;
                    }
                    ptemp++;
                }

                //Count to 3
                if (time[0] <= 20L*gameData.timerLength) {
                    int p=0;
                    for (Player plyr : plyrs) {
                        String myName = plyr.getName();
                        String myColor = sortColors.get(p);

                        countDownTick(plyr, gameData.console, myName, myColor, gameData.timerTick, time[0], gameData.timerLength);

                        p++;
                    }
                }
                //At obj.timerLength+1 seconds, start game (GO!!)
                else if (time[0] == 20L *(gameData.timerLength+1)) {
                    Bukkit.dispatchCommand(gameData.console, disasterData.ArenaList[ArenaID].miniPlatToggleStr(disasterData.ArenaList[ArenaID], false));
                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platCoordStr(disasterData.ArenaList[ArenaID], "safe", 0)+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                    int p=0;
                    for (Player plyr : plyrs) {
                        String myName = plyr.getName();
                        //prepare
                        int chosenDiscId = gameData.getRandomMusicId();
                        String chosenDisc = gameData.musicDiscs[chosenDiscId];
                        if (chosenDisc != "cat" && chosenDisc != "otherside") {
                            Bukkit.dispatchCommand(gameData.console, "tellraw "+myName+" {\"text\":\"Music by YousifGaming\",\"color\":\"dark_red\"}");
                        } else if (chosenDisc == "cat") {
                            Bukkit.dispatchCommand(gameData.console, "tellraw "+myName+" {\"text\":\"Music C418 - Dog\",\"color\":\"dark_red\"}");
                        } else if (chosenDisc == "otherside") {
                            Bukkit.dispatchCommand(gameData.console, "tellraw "+myName+" {\"text\":\"Music Nebby - Mystical Cave\",\"color\":\"dark_red\"}");
                        }
                        plyr.playSound(plyr.getLocation(), gameData.musicDiscIds[chosenDiscId], 99999999, 1);
                        //Bukkit.dispatchCommand(console, "playsound minecraft:music_disc."+chosenDisc+" record "+myName+" ~ ~ ~ 999999999999 1 1");
                        //Bukkit.dispatchCommand(console, "execute at " + myName + " run playsound " + objgamedata.timerGo + " master @s ~ ~ ~ 1 1.85 1");
                        plyr.playSound(plyr.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 99999999F, 1.85F);
                        Bukkit.dispatchCommand(gameData.console, "title "+myName+" title {\"text\":\"Go!!\",\"color\":\""+sortColors.get(p)+"\"}");
                        //Bukkit.dispatchCommand(console, "minecraft:give "+myName+" bow");
                        //Bukkit.dispatchCommand(console, "minecraft:give "+myName+" arrow 16");
                        //put effects in place
                        plyr.removeScoreboardTag("disaster_ghost");
                        plyr.setGameMode(GameMode.ADVENTURE);
                        plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                        PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, (int) (GAMEOVERTIME[0] - time[0]), 1);
                        PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
                        PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
                        if (plyr.getWorld().getName().equals(disasterData.gameWorld)) {
                            plyr.addPotionEffect(glo);
                            plyr.addPotionEffect(sat);
                            plyr.addPotionEffect(reg);
                        }
                        p++;
                    }
                    untilNextEvent[0] = 3*20;

                }
                //during game, after start timer
                else {

                    //prepare next event, 10 ticks for reaction time
                    if (untilNextEvent[0] == 10) {
                        //prepare arena for next event
                        if ((time[0] < HARDMODETIME[0])) {
                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty peaceful");
                        }

                        //keep looking for a new disaster until you find one that meets the requirements
                        currentDisaster[0] = disasters.getRandomDisasterId();
                        round[0]++;

                        //sound and effects
                        int p=0;
                        for (Player plyr : plyrs) {
                            String myName = plyr.getName();
                            Bukkit.dispatchCommand(gameData.console, "playsound "+gameData.timerGo+" master "+myName+" ~ ~ ~ 2 1.85 1");
                            Bukkit.dispatchCommand(gameData.console, "title "+myName+" title {\"text\":\""+disasters.disasterNames[currentDisaster[0]]+"\",\"color\":\""+sortColors.get(p)+"\"}");
                            //heal every number of turns
                            if (round[0] % 15 == 0) {
                                //put effects in place
                                plyr.setGameMode(GameMode.ADVENTURE);
                                plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                                PotionEffect glo = new PotionEffect(PotionEffectType.GLOWING, (int) (GAMEOVERTIME[0] - time[0]), 1);
                                PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
                                PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
                                if (plyr.getWorld().getName().equals(disasterData.gameWorld)) {
                                    plyr.addPotionEffect(glo);
                                    plyr.addPotionEffect(sat);
                                    plyr.addPotionEffect(reg);
                                }
                            }
                            p++;
                        }
                    }
                    //apply next event
                    if (untilNextEvent[0] == 0) {
                        if (disasters.disasterId[currentDisaster[0]] == "zombie") {
                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty easy");
                            for(int i=0; i<4; i++) {
                                double spawnX = disasterData.ArenaList[ArenaID].arenaSpawns[i][0];
                                double spawnY = disasterData.ArenaList[ArenaID].arenaSpawns[i][1];
                                double spawnZ = disasterData.ArenaList[ArenaID].arenaSpawns[i][2];
                                if (time[0] > HARDMODETIME[0]) {
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon husk ~ ~ ~ {Attributes:[{Name:\"generic.followRange\",Base:70}],IsBaby:0,HandItems:[{id:\"minecraft:slime_ball\",Count:1,tag:{Enchantments:[{id:\"knockback\",lvl:1},{id:\"vanishing\",lvl:1}]}},{}]}");
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon potion ~ ~1 ~ {Item:{id:\"minecraft:splash_potion\",Count:1b,tag:{Potion:\"minecraft:water\",CustomPotionEffects:[{Id:1b,Amplifier:3b,Duration:200}]}}}");
                                } else {
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon husk ~ ~ ~ {Attributes:[{Name:\"generic.followRange\",Base:70}],IsBaby:0,HandItems:[{id:\"minecraft:slime_ball\",Count:1,tag:{Enchantments:[{id:\"knockback\",lvl:3},{id:\"vanishing\",lvl:1}]}},{}]}");
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon potion ~ ~1 ~ {Item:{id:\"minecraft:splash_potion\",Count:1b,tag:{Potion:\"minecraft:water\",CustomPotionEffects:[{Id:1b,Amplifier:2b,Duration:200}]}}}");
                                }
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "slime") {
                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty easy");
                            for(int i=0; i<1; i++) {
                                double spawnX = disasterData.ArenaList[ArenaID].arenaSpawns[i][0];
                                double spawnY = disasterData.ArenaList[ArenaID].arenaSpawns[i][1];
                                double spawnZ = disasterData.ArenaList[ArenaID].arenaSpawns[i][2];
                                if (time[0] > HARDMODETIME[0]) {
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon slime ~ ~ ~ {Size:6}");
                                } else {
                                    Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon slime ~ ~ ~ {Size:4}");
                                }
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "levitation") {
                            for (Player plyr : plyrs) {
                                ItemStack potionStack = new ItemStack(Material.SPLASH_POTION);
                                PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
                                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LEVITATION, 2*20, 8), true);
                                potionStack.setItemMeta(potionMeta);
                                ThrownPotion potion = (ThrownPotion) plyr.getWorld().spawnEntity(plyr.getLocation().add(0, 1, 0), EntityType.POTION);
                                potion.setItem(potionStack);
//                                Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon potion ~ ~1 ~ {Item:{id:\"minecraft:splash_potion\",Count:1b,tag:{Potion:\"minecraft:water\",CustomPotionEffects:[{Id:25b,Amplifier:8b,Duration:40}]}}}");
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "skeleton") {
                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty easy");
                            for(int i=0; i<4; i++) {
                                double spawnX = disasterData.ArenaList[ArenaID].skeletonRooms[i][0];
                                double spawnY = disasterData.ArenaList[ArenaID].skeletonRooms[i][1];
                                double spawnZ = disasterData.ArenaList[ArenaID].skeletonRooms[i][2];
                                Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld positioned "+spawnX+" "+spawnY+" "+spawnZ+" run summon skeleton ~ ~ ~ {Attributes:[{Name:\"generic.followRange\",Base:100}],IsBaby:0,HandItems:[{id:\"minecraft:bow\",Count:1,tag:{Enchantments:[{id:\"punch\",lvl:5}]}},{}]}");
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "chillbreak") {}
                        if (disasters.disasterId[currentDisaster[0]] == "toggleplatform") {
                            platformOn[0] = !platformOn[0];
                            Bukkit.dispatchCommand(gameData.console, disasterData.ArenaList[ArenaID].miniPlatToggleStr(disasterData.ArenaList[ArenaID], platformOn[0]));
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "creepers") {
                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty easy");
                            for (Player plyr : plyrs) {
                                if (time[0] > HARDMODETIME[0]) {
                                    Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon minecraft:creeper ~ ~3 ~");
                                } else {
                                    Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon minecraft:creeper ~ ~4 ~");
                                }
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "fireball") {
                            for (Player plyr : plyrs) {
                                if (time[0] > HARDMODETIME[0]) {
                                    Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon minecraft:fireball ~ ~15 ~ {ExplosionPower:0,Motion:[0d,-1.8d]}");
                                } else {
                                    Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon minecraft:fireball ~ ~15 ~ {ExplosionPower:0,Motion:[0d,-1.2d]}");
                                }
                            }
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "togglemainplatform") {
                            deteriorateCurrentDanger[0]++;
                            mainPlatformSafe[0] = !mainPlatformSafe[0];
                        }
                        if (disasters.disasterId[currentDisaster[0]] == "blindness") {
                            for (Player plyr : plyrs) {
                                ItemStack potionStack = new ItemStack(Material.SPLASH_POTION);
                                PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();
                                potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8*20, 8), true);
                                potionStack.setItemMeta(potionMeta);
                                ThrownPotion potion = (ThrownPotion) plyr.getWorld().spawnEntity(plyr.getLocation().add(0, 1, 0), EntityType.POTION);
                                potion.setItem(potionStack);
//                                Bukkit.dispatchCommand(gameData.console, "execute at "+plyr.getName()+" run summon potion ~ ~1 ~ {Item:{id:\"minecraft:splash_potion\",Count:1b,tag:{Potion:\"minecraft:water\",CustomPotionEffects:[{Id:33b,Amplifier:8b,Duration:400}]}}}");
                            }
                        }

                        untilNextEvent[0] = disasters.disasterTimes[currentDisaster[0]];
                        //untilNextEvent[0] = (objdisasters.disasterTimes[currentDisaster[0]] / ((GAMEOVERTIME - time[0])/GAMEOVERTIME)  ) + 5;
                        //untilNextEvent[0] = objdisasters.disasterTimes[currentDisaster[0]] - (GAMEOVERTIME[0]/(time[0]+1));
                        if (untilNextEvent[0] < 12) { untilNextEvent[0] = 12; }
                    }
                    //ongoing events
                    if (untilNextEvent[0] > 10) {
                        if (disasters.disasterId[currentDisaster[0]] == "togglemainplatform") {
                            if (untilNextEvent[0] <= 20*5) {
                                if (untilNextEvent[0] > 12) {
                                    if (untilNextEvent[0]%10==0 && untilNextEvent[0]%20!=0) {
                                        //warning
                                        for (Player plyr : plyrs) {
                                            //Bukkit.dispatchCommand(console, "playsound "+objgamedata.timerTick+" master "+plyr.getName()+" ~ ~ ~ 2 1 1");
                                            plyr.playSound(plyr.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 99999999F, 1F);
                                        }
                                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platWarnStr(disasterData.ArenaList[ArenaID])+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                                    }
                                    if (untilNextEvent[0]%20==0) {
                                        //flicker back into old
                                        if (!mainPlatformSafe[0]) {
                                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platSafeStr(disasterData.ArenaList[ArenaID])+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                                        } else {
                                            Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platDangerStr(disasterData.ArenaList[ArenaID])+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                                        }
                                    }
                                } else {
                                    //flick and stick into the new shtick
                                    for (Player plyr : plyrs) {
                                        //Bukkit.dispatchCommand(console, "playsound "+objgamedata.timerTick+" master "+plyr.getName()+" ~ ~ ~ 2 0.9 1");
                                        plyr.playSound(plyr.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 99999999F, 0.9F);
                                    }
                                    if (mainPlatformSafe[0]) {
                                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platSafeStr(disasterData.ArenaList[ArenaID])+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                                    } else {
                                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platDangerStr(disasterData.ArenaList[ArenaID])+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));
                                    }
                                }
                            }
                        }
                    }
                    untilNextEvent[0]--;

                    //if everyone is ghosts, start count down
                    if (!finalisingGame[0]) {
                        int ghosts=0;
                        for (Player plyr : plyrs) {
                            if (plyr.getScoreboardTags().contains("disaster_ghost")) { ghosts++; }
                        }
                        if (ghosts == plyrs.size()) {
                            remainingTime[0] = 20*10;
                            time[0] = GAMEOVERTIME[0]-(remainingTime[0]+20);//make sure the game only ends with remainingTime
                            finalisingGame[0] = true;
                        }
                    }

                    //game over
                    if (time[0] >= GAMEOVERTIME[0] || remainingTime[0] == 0) {

                        if (finalisingGame[0]) {
                            Bukkit.dispatchCommand(gameData.console, "tellraw @a \"[Disastrophe] Game Ended With No Survivors\"");
                        } else {
                            Bukkit.dispatchCommand(gameData.console, "tellraw @a \"[Disastrophe] Game Ended. Survivors:\"");
                        }

                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run clone "+disasterData.ArenaList[ArenaID].platCoordStr(disasterData.ArenaList[ArenaID], "safe", 0)+" "+disasterData.ArenaList[ArenaID].platCloneCoordStr(disasterData.ArenaList[ArenaID]));

                        int p=0;
                        for (Player plyr : plyrs) {
                            String myName = plyr.getName();
                            String myColor = sortColors.get(p);

                            if (!plyr.getScoreboardTags().contains("disaster_ghost")) {
                                Bukkit.dispatchCommand(gameData.console, "title "+myName+" title {\"text\":\"You survived!\",\"color\":\""+myColor+"\"}");
                            }
                            destablishPlayer(plyr, gameData.console, myName, myColor, gameData.finishSound, gameData);

                            p++;
                        }
                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty peaceful");

                        isRunning[0] = false;
                        gameData.FinishedRepeatingTasks.set(myTaskIndex[0], true);
                        new nebClearScheduledRepeatingTasksExecutor(gameData).clearScheduledRepeatingTasks();
                    }
                }


                ///during game, during the entirety the game (even during timer)

                //show action bar
                if (remainingTime[0] > 0) {
                    long remainingTimeSeconds = remainingTime[0]/20;
                    int p=0;
                    for (Player plyr : plyrs) {
                        String myName = plyr.getName();
                        //ominous start sound
                        if (!plyr.getScoreboardTags().contains("disaster_ghost")) {
                            Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar \"Remaining Time: "+(remainingTimeSeconds)+"\"");
                        } else {
                            Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar [{\"text\":\"You're a ghost \",\"color\":\""+sortColors.get(p)+"\"},{\"text\":\"Remaining Time: "+(remainingTimeSeconds)+"\",\"color\":\"white\"}]");
                        }
                        p++;
                    }
                    remainingTime[0]--;
                }
                else {
                    long timeSeconds = time[0]/20;
                    int p=0;
                    for (Player plyr : plyrs) {
                        String myName = plyr.getName();
                        //hard mode
                        if (time[0] > HARDMODETIME[0]) {
                            if (!plyr.getScoreboardTags().contains("disaster_ghost")) {
                                Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar {\"text\":\"Danger Time: "+(timeSeconds)+"\",\"color\":\"dark_red\"}");
                            } else {
                                Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar [{\"text\":\"You're a ghost \",\"color\":\""+sortColors.get(p)+"\"},{\"text\":\"Danger Time: "+(timeSeconds)+"\",\"color\":\"dark_red\"}]");
                            }
                        }
                        //ok mode
                        else {
                            if (!plyr.getScoreboardTags().contains("disaster_ghost")) {
                                Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar \"Time: "+(timeSeconds)+"\"");
                            } else {
                                Bukkit.dispatchCommand(gameData.console, "title " + myName + " actionbar [{\"text\":\"You're a ghost \",\"color\":\""+sortColors.get(p)+"\"},{\"text\":\"Time: "+(timeSeconds)+"\",\"color\":\"white\"}]");
                            }
                        }
                        p++;
                    }
                }

                //check out of bounds
                for (int pCount = 0; pCount < plyrs.size(); pCount++) {
                    String myName = plyrs.get(pCount).getName();
                    String myColor = sortColors.get(pCount);
                    //if they aren't within bounds
                    if (plyrs.get(pCount).getLocation().getY() < disasterData.ArenaList[ArenaID].arenaYstart -3 || !disasterData.ArenaList[ArenaID].isInBounds(disasterData.ArenaList[ArenaID], plyrs.get(pCount))) {
                        //tp to arena
                        int spawnIndex = setSpawnIndex(myColor);

                        double spawnX = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][0];
                        double spawnY = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][1];
                        double spawnZ = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][2];
                        double spawnLR = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][3];
                        double spawnUD = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][4];

                        World gamewrld = Bukkit.getServer().getWorld(disasterData.gameWorld);
                        Location loc = new Location(gamewrld, spawnX, spawnY, spawnZ);

                        plyrs.get(pCount).teleport(loc);
                        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run tp " + myName + " " + spawnX + " " + spawnY + " " + spawnZ + " " + spawnLR + " " + spawnUD);

                        //if the game started, they're a ghost now
                        if (time[0] > 20*(gameData.timerLength+1L)) {
                            plyrs.get(pCount).addScoreboardTag("disaster_ghost");
                            PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, (int) (GAMEOVERTIME[0] - time[0]), 0);
                            if (plyrs.get(pCount).getWorld().getName().equals(disasterData.gameWorld)) {
                                plyrs.get(pCount).addPotionEffect(invis);
                            }
                            Bukkit.dispatchCommand(gameData.console, "title " + myName + " title {\"text\":\"You Died!\",\"color\":\"dark_red\"}");
                        }
                    }
                }


                time[0]++;
            }
        }, 0L, 1L);
        myTaskIndex[0] = gameData.ScheduledRepeatingTasks.size();
        gameData.ScheduledRepeatingTasks.add(loopID);
        gameData.FinishedRepeatingTasks.add(false);
    }

    //give an int for each color
    int setSpawnIndex(String myColor) {
        if (Objects.equals(myColor, "red")) {return 0;}
        else if (Objects.equals(myColor, "blue")) {return 1;}
        else if (Objects.equals(myColor, "yellow")) {return 2;}
        else if (Objects.equals(myColor, "green")) {return 3;}
        else {return 0;}
    }

    void destablishPlayer(Player player, ConsoleCommandSender console, String myName, String myColor, String FinishSound, gameData gameData) {

        Bukkit.dispatchCommand(console, "stopsound "+myName+" record");
        Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + FinishSound + " master @s ~ ~ ~ 999 1.15 1");
        Bukkit.dispatchCommand(console, "title "+myName+" title {\"text\":\"Game Concluded\",\"color\":\""+myColor+"\"}");
        if (!player.getScoreboardTags().contains("disaster_ghost")) {
            Bukkit.dispatchCommand(console, "tellraw @a [\"-\",{\"text\":\""+myName+"\",\"color\":\""+myColor+"\"}]");
        }

        player.removeScoreboardTag("disasterRunning");
        player.removeScoreboardTag("disaster_ghost");
        player.removeScoreboardTag("disaster_"+myColor);

        stopExecutor stopper = new stopExecutor(gameData);
        stopper.stopPlayerGames(player);
        player.performCommand("spawn");
    }

    void countDownTick(Player player, ConsoleCommandSender console, String myName, String myColor, String timerTickSound, long time, int timerLength) {
        Bukkit.dispatchCommand(console, "title "+myName+" title {\"text\":\"Starting in: "+((timerLength) - (time / 20))+"...\",\"color\":\""+myColor+"\"}");
        player.getInventory().remove(Material.BOW);
        player.getInventory().remove(Material.ARROW);
        player.getInventory().remove(Material.SLIME_BALL);
        if (time % 20 == 0) {
            Bukkit.dispatchCommand(console, "execute as " + myName + " run playsound " + timerTickSound + " master @s ~ ~ ~ 1 1.6 1");
        }
    }

    void prepareRules(ArrayList<Player> players) {
        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run gamerule mobGriefing false");
        Bukkit.dispatchCommand(gameData.console, "execute in minecraft:overworld run difficulty peaceful");

        for (Player plyr : players) {
            plyr.resetTitle();
            String myName = plyr.getName();
            Bukkit.dispatchCommand(gameData.console, "title "+myName+" times 0 30 0");
            Bukkit.dispatchCommand(gameData.console, "execute as "+myName+" run playsound "+gameData.startSound+" master @s ~ ~ ~ 999 1.15 1");
        }
    }
}
