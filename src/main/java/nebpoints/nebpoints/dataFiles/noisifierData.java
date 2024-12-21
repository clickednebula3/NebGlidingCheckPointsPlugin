package nebpoints.nebpoints.dataFiles;

import nebpoints.nebpoints.Nebpoints;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;


public class noisifierData {
    public ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public String lobbyWorld = "overworld";
    public String gameWorld = "glidemaps";
    public String mareWorld = "glidemaps";

    public String perm_glide_ranked = "nebpoints.gliding_ranked";

    //tags
    public String tag_wants_pack = "hasResourcePack";
    public String tag_pack_is_active = "activeResourcePack";
    public String tag_in_game = "gameRunning";
    public String tag_glide = "glider";
//    public String[] tag_glide_color = {"glider_red", "glider_blue", "glider_yellow", "glider_green"};
    public String tag_glide_host = "glide_host";
    public String tag_glide_host_join = "glide_joinhost";

    //color sort index tables
    public Material[] glideBlockColors = {Material.RED_CONCRETE, Material.BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE};
    public Material[] disasterBlockColors = {Material.RED_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER};
    public Material[] maredareBlockColors = {Material.RED_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA};
    public ChatColor[] gamerChatColors = {ChatColor.RED, ChatColor.BLUE, ChatColor.YELLOW, ChatColor.GREEN};
    public String[] gamerSimpleColors = {"red", "blue", "yellow", "green"};

    public String coolElytra = "minecraft:elytra{Enchantments:[{id:binding_curse,lvl:1},{id:vanishing_curse,lvl:1},{id:unbreaking,lvl:5}]}";

    public String[] musicDiscs = {"13", "blocks", "cat", "far", "mall", "otherside", "pigstep", "wait", "relic"};
    public Sound[] musicDiscIds = {Sound.MUSIC_DISC_13, Sound.MUSIC_DISC_BLOCKS, Sound.MUSIC_DISC_CAT, Sound.MUSIC_DISC_FAR, Sound.MUSIC_DISC_MALL, Sound.MUSIC_DISC_OTHERSIDE, Sound.MUSIC_DISC_PIGSTEP, Sound.MUSIC_DISC_WAIT, Sound.MUSIC_DISC_RELIC};
    public String[] packCredit = {"Music by YousifGaming", "Music by YousifGaming", "Music C418 - Dog", "Music by YousifGaming", "Music by YousifGaming", "Music Nebby - Mystical Cave", "Music by YousifGaming", "Music by YousifGaming", "Music Aaron Cherof - Relic"};

    public String startSound = "minecraft:entity.experience_orb.pickup";//1.5
    public String finishSound = "minecraft:ui.toast.challenge_complete";//1.65
    public String timerTick = "minecraft:block.note_block.chime";//1.6
    public String timerGo = "minecraft:item.trident.thunder";//1.85
    public String wrongWay = "minecraft:block.note_block.bass";//0.3
    public Sound snd_finish = Sound.UI_TOAST_CHALLENGE_COMPLETE;
    public Sound snd_tick = Sound.BLOCK_NOTE_BLOCK_CHIME;
    public Sound snd_go = Sound.ITEM_TRIDENT_THUNDER;
    public Sound snd_wrong = Sound.BLOCK_NOTE_BLOCK_BASS;
    public Integer timerLength = 10;
    public Integer gameLength = 400;

    public List<GlidingMap> gliding_maps_loaded = new ArrayList<>();
    public List<MaredareMap> maredare_maps_loaded = new ArrayList<>();
        //temp; move to config todo: MOVE INTO maredareData
        MaredareMap firstMap = new MaredareMap("candy", new double[]{0.0, 0.0}, new double[]{0.0, 0.0}, new double[]{-10, 60, -10, 110, 80, 10}, false);
    public List<Integer> ScheduledRepeatingTasks = new ArrayList<>();
    public List<Boolean> FinishedRepeatingTasks = new ArrayList<>();


    //noisifierData
    public List<String> soundGroups = new ArrayList<>(Arrays.asList("mobs", "items", "blocks", "music", "other")); //do not use "wand" or "null"
    public Map<String, Material> soundGroupIconMap = new HashMap<>();
    public Map<String, List<String>> soundMap = new HashMap<>();
    public Map<String, Sound> soundStringMap = new HashMap<>();
    public Map<Sound, Material> soundIconMap = new HashMap<>();



    public double[] lobbyCoords = {42.5, 65.00, -1.5, 90.0, 0.0};
    public Location spawnLocation = new Location(Bukkit.getWorld(lobbyWorld), 200.5, 80, 200.5, -90, 0);
    public Location glideLobbyLocation = new Location(Bukkit.getWorld("hub"), 42.5, 65, -1.5, 90, 0);
    public Location disasterLobbyLocation = new Location(Bukkit.getWorld("hub"), 47.5, 65, -1.5, -90, 0);
    public Location maredareLobbyLocation = new Location(Bukkit.getWorld("hub"), 45.5, 65, 6.5, 0, 0);

    public noisifierData(Nebpoints nebplugin) {
        ReadMapsFromConfig(nebplugin.getConfig());
        //temp; move to config todo: MOVE INTO maredareData
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-322.5, 119, 181.5, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-300, 118, 201, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-250, 124, 182, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-342, 118, 225, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-294, 127, 225, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-239, 118, 242, 0, 0}, 5.0));
        firstMap.addCheckpoint(new MaredareCheckpoint(new double[]{-362, 118, 247, 0, 0}, 5.0));
        maredare_maps_loaded.add(firstMap);

        //noisifierData
        soundGroupIconMap.put(soundGroups.get(0), Material.SPIDER_SPAWN_EGG);
        soundGroupIconMap.put(soundGroups.get(1), Material.GOAT_HORN);
        soundGroupIconMap.put(soundGroups.get(2), Material.DIAMOND_ORE);
        soundGroupIconMap.put(soundGroups.get(3), Material.NOTE_BLOCK);
        soundGroupIconMap.put(soundGroups.get(4), Material.KNOWLEDGE_BOOK);

        soundMap.put(soundGroups.get(0), new ArrayList<>(Arrays.asList("fox_ambient", "fox_sleep", "cat_ambient", "cat_purreow", "cat_purr", "cat_beg", "axolotl_splash", "allay_ambient_with_item", "creeper_primed_>:3", "arrow_shoot", "arrow_land")));
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(0), Sound.ENTITY_FOX_AMBIENT); soundIconMap.put(Sound.ENTITY_FOX_AMBIENT, Material.SWEET_BERRIES);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(1), Sound.ENTITY_FOX_SLEEP); soundIconMap.put(Sound.ENTITY_FOX_SLEEP, Material.SWEET_BERRIES);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(2), Sound.ENTITY_CAT_AMBIENT); soundIconMap.put(Sound.ENTITY_CAT_AMBIENT, Material.COD);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(3), Sound.ENTITY_CAT_PURREOW); soundIconMap.put(Sound.ENTITY_CAT_PURREOW, Material.COD);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(4), Sound.ENTITY_CAT_PURR); soundIconMap.put(Sound.ENTITY_CAT_PURR, Material.COD);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(5), Sound.ENTITY_CAT_BEG_FOR_FOOD); soundIconMap.put(Sound.ENTITY_CAT_BEG_FOR_FOOD, Material.COD);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(6), Sound.ENTITY_AXOLOTL_SPLASH); soundIconMap.put(Sound.ENTITY_AXOLOTL_SPLASH, Material.AXOLOTL_BUCKET);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(7), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM); soundIconMap.put(Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, Material.COOKIE);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(8), Sound.ENTITY_CREEPER_PRIMED); soundIconMap.put(Sound.ENTITY_CREEPER_PRIMED, Material.CREEPER_HEAD);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(9), Sound.ENTITY_ARROW_SHOOT); soundIconMap.put(Sound.ENTITY_ARROW_SHOOT, Material.ARROW);
        soundStringMap.put(soundMap.get(soundGroups.get(0)).get(10), Sound.ENTITY_ARROW_HIT); soundIconMap.put(Sound.ENTITY_ARROW_HIT, Material.ARROW);

        soundMap.put(soundGroups.get(1), new ArrayList<>(Arrays.asList("goat_horn_1", "goat_horn_2", "goat_horn_3", "goat_horn_4", "trident_return", "honey_bottle_drink")));
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(0), Sound.ITEM_GOAT_HORN_SOUND_1); soundIconMap.put(Sound.ITEM_GOAT_HORN_SOUND_1, Material.GOAT_HORN);
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(1), Sound.ITEM_GOAT_HORN_SOUND_2); soundIconMap.put(Sound.ITEM_GOAT_HORN_SOUND_2, Material.GOAT_HORN);
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(2), Sound.ITEM_GOAT_HORN_SOUND_3); soundIconMap.put(Sound.ITEM_GOAT_HORN_SOUND_3, Material.GOAT_HORN);
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(3), Sound.ITEM_GOAT_HORN_SOUND_4); soundIconMap.put(Sound.ITEM_GOAT_HORN_SOUND_4, Material.GOAT_HORN);
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(4), Sound.ITEM_TRIDENT_RETURN); soundIconMap.put(Sound.ITEM_TRIDENT_RETURN, Material.TRIDENT);
        soundStringMap.put(soundMap.get(soundGroups.get(1)).get(5), Sound.ITEM_HONEY_BOTTLE_DRINK); soundIconMap.put(Sound.ITEM_HONEY_BOTTLE_DRINK, Material.TRIDENT);

        soundMap.put(soundGroups.get(2), new ArrayList<>(Arrays.asList("amethyst_chime", "dispenser_dispense", "anvil_break")));
        soundStringMap.put(soundMap.get(soundGroups.get(2)).get(0), Sound.BLOCK_AMETHYST_BLOCK_CHIME); soundIconMap.put(Sound.BLOCK_AMETHYST_BLOCK_CHIME, Material.AMETHYST_BLOCK);
        soundStringMap.put(soundMap.get(soundGroups.get(2)).get(1), Sound.BLOCK_DISPENSER_DISPENSE); soundIconMap.put(Sound.BLOCK_DISPENSER_DISPENSE, Material.DISPENSER);
        soundStringMap.put(soundMap.get(soundGroups.get(2)).get(2), Sound.BLOCK_ANVIL_BREAK); soundIconMap.put(Sound.BLOCK_ANVIL_BREAK, Material.DAMAGED_ANVIL);

        soundMap.put(soundGroups.get(3), new ArrayList<>(Arrays.asList("music_disc_creator", "music_disc_creator_music_box", "music_disc_precipice", "music_creative", "music_menu", "music_game")));
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(0), Sound.MUSIC_DISC_CREATOR); soundIconMap.put(Sound.MUSIC_DISC_CREATOR, Material.MUSIC_DISC_CREATOR);
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(1), Sound.MUSIC_DISC_CREATOR_MUSIC_BOX); soundIconMap.put(Sound.MUSIC_DISC_CREATOR_MUSIC_BOX, Material.MUSIC_DISC_CREATOR_MUSIC_BOX);
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(2), Sound.MUSIC_DISC_PRECIPICE); soundIconMap.put(Sound.MUSIC_DISC_PRECIPICE, Material.MUSIC_DISC_PRECIPICE);
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(3), Sound.MUSIC_CREATIVE); soundIconMap.put(Sound.MUSIC_CREATIVE, Material.GRASS_BLOCK);
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(4), Sound.MUSIC_MENU); soundIconMap.put(Sound.MUSIC_MENU, Material.DIRT);
        soundStringMap.put(soundMap.get(soundGroups.get(3)).get(5), Sound.MUSIC_GAME); soundIconMap.put(Sound.MUSIC_GAME, Material.STONE);

        soundMap.put(soundGroups.get(4), new ArrayList<>(Arrays.asList("toast_in", "toast_challenge_complete", "ambient_cave")));
        soundStringMap.put(soundMap.get(soundGroups.get(4)).get(0), Sound.UI_TOAST_IN); soundIconMap.put(Sound.UI_TOAST_IN, Material.BREAD);
        soundStringMap.put(soundMap.get(soundGroups.get(4)).get(1), Sound.UI_TOAST_CHALLENGE_COMPLETE); soundIconMap.put(Sound.UI_TOAST_CHALLENGE_COMPLETE, Material.ENCHANTED_GOLDEN_APPLE);
        soundStringMap.put(soundMap.get(soundGroups.get(4)).get(2), Sound.AMBIENT_CAVE); soundIconMap.put(Sound.AMBIENT_CAVE, Material.CARVED_PUMPKIN);
    }

    public boolean LoadMapsIntoConfig(Nebpoints nebplugin) {
        FileConfiguration config = nebplugin.getConfig();

        List<HashMap<String, Object>> gliding_maps = new ArrayList<>();
        for (int map = 0; map < getMaps().length; map++) {
            String mapName = getMapName(map);
            double[] cpOff = {getRespawnOffX(map), getRespawnOffZ(map)};
            boolean isRanked = false;
            for (int rmap = 0; rmap < getRankedMaps().length; rmap++) {
                if (Objects.equals(mapName, getRankedMaps()[rmap])) {
                    isRanked = true;
                    break;
                }
            }

            List<HashMap<String, Object>> checkpoints = new ArrayList<>();
            for (int cp = 0; cp < getCpCount(map); cp++) {
                double[] coordsRespawn = {
                        getRespawnX(map, cp), getRespawnY(map, cp), getRespawnZ(map, cp),
                        getRespawnLR(map, cp), getRespawnUD(map, cp)
                };
                double[] coordsA = { getX(map, cp), getY(map, cp), getZ(map, cp) };
                double[] coordsB = { getXEnd(map, cp), getYEnd(map, cp), getZEnd(map, cp) };

                HashMap<String, Object> thisCp = new HashMap<>();
                thisCp.put("coordsRespawn", coordsRespawn);
                thisCp.put("coordsA", coordsA);
                thisCp.put("coordsB", coordsB);
                checkpoints.add(thisCp);
            }

            HashMap<String, Object> thisMap = new HashMap<>();
            thisMap.put("mapName", mapName);
            thisMap.put("cpOff", cpOff);
            thisMap.put("isRanked", isRanked);
            thisMap.put("checkpoints", checkpoints);
            gliding_maps.add(thisMap);
        }

        config.set("gliding_maps", gliding_maps);
        nebplugin.saveConfig();
        return true;
    }

    public void ReadMapsFromConfig(FileConfiguration config) {
        lobbyCoords[0] = config.getDouble("lobby.x");
        lobbyCoords[1] = config.getDouble("lobby.y");
        lobbyCoords[2] = config.getDouble("lobby.z");
        lobbyCoords[3] = config.getDouble("lobby.yaw");
        lobbyCoords[4] = config.getDouble("lobby.pitch");

        if (config.contains("gliding_maps")) {
            List<?> gliding_maps = config.getList("gliding_maps");
            gliding_maps_loaded = new ArrayList<>();

            assert gliding_maps != null;
            for (Object gliding_map : gliding_maps) {
                HashMap<String, ?> ThisMap = (HashMap<String, ?>) gliding_map;

                String ThismapName = (String) ThisMap.get("mapName");
                Boolean ThisisRanked = (Boolean) ThisMap.get("isRanked");
                List<Double> ThiscpOff = (List<Double>) ThisMap.get("cpOff");

                GlidingMap thisGlidingMap = new GlidingMap(ThismapName, ThiscpOff, ThisisRanked);
                console.sendMessage(thisGlidingMap.mapName + " | " + thisGlidingMap.isRanked + " | " + thisGlidingMap.cpOff[0] + ", " + thisGlidingMap.cpOff[1] + "\n\n");

                List<?> checkpoints = (List<?>) ThisMap.get("checkpoints");
                for (Object checkpoint : checkpoints) {
                    thisGlidingMap.addCheckpoint(getGlidingCheckpoint((HashMap<String, ?>) checkpoint));
                }

                gliding_maps_loaded.add(thisGlidingMap);
            }
        }
    }

    private static GlidingCheckpoint getGlidingCheckpoint(HashMap<String, ?> checkpoint) {
        //console.sendMessage("\nCP ("+cp+"):\n" + ThisCheckpoint.toString() + "\n:CP\n");
        List<Double> ThiscoordsRespawn = (List<Double>) checkpoint.get("coordsRespawn");
        List<Double> ThiscoordsA = (List<Double>) checkpoint.get("coordsA");
        List<Double> ThiscoordsB = (List<Double>) checkpoint.get("coordsB");
        return new GlidingCheckpoint(ThiscoordsRespawn, ThiscoordsA, ThiscoordsB);
    }

    public String ListMaps() {
        String finalText = "";

        for (int map = 0; map < gliding_maps_loaded.size(); map++) {
            finalText += "> mapName: " + gliding_maps_loaded.get(map).mapName + "\n";
            finalText += "  isRanked: " + gliding_maps_loaded.get(map).isRanked + "\n";
            finalText += "  cpOff: " + gliding_maps_loaded.get(map).cpOff[0] + ", " + gliding_maps_loaded.get(map).cpOff[1] + "\n";
            finalText += "  cpCount: " + gliding_maps_loaded.get(map).checkpoints.size() + "\n";
        }

        return finalText;
    }

    public boolean checkApplyPack(Player plyr) {
        //has -> wants pack (toggle=on)
        //active -> already applied it (literally has it)
        if (plyr.getScoreboardTags().contains(tag_wants_pack) && !plyr.getScoreboardTags().contains(tag_pack_is_active)) {
            plyr.addScoreboardTag(tag_pack_is_active);
            plyr.performCommand("pack");
            return true;
        }
        return false;
    }

    public ChatColor chatColor(ArrayList<Integer> sortColorsIndexes, int p) { return gamerChatColors[sortColorsIndexes.get(p%sortColorsIndexes.size())%gamerChatColors.length]; }
    public String simpleColor(ArrayList<Integer> sortColorsIndexes, int p) { return gamerSimpleColors[sortColorsIndexes.get(p%sortColorsIndexes.size())%gamerSimpleColors.length]; }

    public void placeGlidingCabinet(int mapID, int cabinetID, Player plyr, boolean breakGlidingCabinetInstead) {
        Location cabinetLocation = new Location(
                Bukkit.getWorld(gameWorld),
                getRespawnX(mapID, 0) + getRespawnOffX(mapID)*cabinetID, getRespawnY(mapID, 0),
                getRespawnZ(mapID, 0) + getRespawnOffZ(mapID)*cabinetID,
                (float) getRespawnLR(mapID, 0), (float) getRespawnUD(mapID, 0)
        );

        plyr.teleport(cabinetLocation);

        Material SolidBlock = Material.GOLD_BLOCK;
        Material SeeThroughBlock = Material.GLASS;
        if (breakGlidingCabinetInstead) {
            SolidBlock = Material.AIR;
            SeeThroughBlock = Material.AIR;
        }

        World world = Bukkit.getWorld(gameWorld);
        world.getBlockAt(cabinetLocation.clone().add(0, -1, 0)).setType(SolidBlock);//bottom
        world.getBlockAt(cabinetLocation.clone().add(0, 2, 0)).setType(SolidBlock);//top
        world.getBlockAt(cabinetLocation.clone().add(1, 0, 0)).setType(SeeThroughBlock);//leg-high
        world.getBlockAt(cabinetLocation.clone().add(-1, 0, 0)).setType(SeeThroughBlock);
        world.getBlockAt(cabinetLocation.clone().add(0, 0, 1)).setType(SeeThroughBlock);
        world.getBlockAt(cabinetLocation.clone().add(0, 0, -1)).setType(SeeThroughBlock);
        world.getBlockAt(cabinetLocation.clone().add(1, 1, 0)).setType(SeeThroughBlock);//head-high
        world.getBlockAt(cabinetLocation.clone().add(-1, 1, 0)).setType(SeeThroughBlock);
        world.getBlockAt(cabinetLocation.clone().add(0, 1, 1)).setType(SeeThroughBlock);
        world.getBlockAt(cabinetLocation.clone().add(0, 1, -1)).setType(SeeThroughBlock);
    }

    public int getGlideMapIDFromInputWithRespectToRank(String providedID, boolean has_rank) {
        int mapID = 0;
        int mapIndex = 0;

        for (int i=0; i<getMapCount(); i++) {
            String map = getMaps()[i];
            if (providedID.equals(map)) {
                boolean isRankedMap = false;
                for (String mapR : getRankedMaps()) { if (providedID.equals(mapR)) { isRankedMap = true; break; } }
                if (!isRankedMap || has_rank) { mapID = mapIndex; }
                break;
            }
            mapIndex++;
        }

        return mapID;
    }

    @Deprecated
    public String getRandomMusic() {
        Random rand = new Random();
        int rando = rand.nextInt(musicDiscs.length);
        return musicDiscs[rando];
    }
    @Deprecated
    public int getRandomMusicId() {
        return new Random().nextInt(musicDiscs.length);
    }

    public void playGameMusic(Player player) {
        int musicID = getRandomMusicId();
        if (player.getScoreboardTags().contains(tag_pack_is_active)) { player.sendMessage(ChatColor.DARK_RED + packCredit[musicID]); }
        else { player.sendMessage(ChatColor.DARK_RED + "Default Disc Music. Try /pack :)"); }
        player.playSound(player.getLocation(), musicDiscIds[musicID], SoundCategory.RECORDS, 9999999999999f, 1f, 1);
//        if (chosenDisc != "cat" && chosenDisc != "otherside" && chosenDisc != "relic") {
//            Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music by YousifGaming\",\"color\":\"dark_red\"}");
//        } else if (chosenDisc == "cat") {
//            Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music C418 - Dog\",\"color\":\"dark_red\"}");
//        } else if (chosenDisc == "otherside") {
//            Bukkit.dispatchCommand(console, "tellraw " + myName + " {\"text\":\"Music Nebby - Mystical Cave\",\"color\":\"dark_red\"}");
//        }
//        Bukkit.dispatchCommand(console, "playsound minecraft:music_disc." + chosenDisc + " record " + myName + " ~ ~ ~ 999999999999 1 1");
    }

    //get data depending on the map and checkpoint
    public String[] getMaps(){
        String[] mapNames = new String[getMapCount()];
        for (int map = 0; map < getMapCount(); map++) {
            mapNames[map] = gliding_maps_loaded.get(map).mapName;
        }
        return mapNames;
    }
    public String[] getMaredareMaps(){
        String[] mapNames = new String[getMaredareMapCount()];
        for (int map = 0; map < getMaredareMapCount(); map++) {
            mapNames[map] = maredare_maps_loaded.get(map).mapName;
        }
        return mapNames;
    }
    public String[] getRankedMaps(){
        List<String> rankedMapNamesList = new ArrayList<>();
        for (int map = 0; map < getMapCount(); map++) {
            if (gliding_maps_loaded.get(map).isRanked) {
                rankedMapNamesList.add(gliding_maps_loaded.get(map).mapName);
            }
        }
        String[] rankedMapNames = new String[rankedMapNamesList.size()];
        for (int map = 0; map < rankedMapNamesList.size(); map++) {
            rankedMapNames[map] = rankedMapNamesList.get(map);
        }
        return rankedMapNames;
    }
    public String[] getMaredareRankedMaps(){
        List<String> rankedMapNamesList = new ArrayList<>();
        for (int map = 0; map < getMaredareMapCount(); map++) {
            if (maredare_maps_loaded.get(map).isRanked) {
                rankedMapNamesList.add(maredare_maps_loaded.get(map).mapName);
            }
        }
        String[] rankedMapNames = new String[rankedMapNamesList.size()];
        for (int map = 0; map < rankedMapNamesList.size(); map++) {
            rankedMapNames[map] = rankedMapNamesList.get(map);
        }
        return rankedMapNames;
    }
    public String getMapName(int mapID){
        return gliding_maps_loaded.get(mapID).mapName;
    }
    public String getMaredareMapName(int mapID){
        return maredare_maps_loaded.get(mapID).mapName;
    }
    public int getCpCount(int mapID) {
        return gliding_maps_loaded.get(mapID).checkpoints.size();
    }
    public int getMapCount() {
        return gliding_maps_loaded.size();
    }
    public int getMaredareMapCount() { return maredare_maps_loaded.size(); }

    //get spawn offsets
    public double getRespawnOffX(int mapID) {
        return gliding_maps_loaded.get(mapID).cpOff[0];
    }
    public double getRespawnOffZ(int mapID){
        return gliding_maps_loaded.get(mapID).cpOff[1];
    }
    //get maredare spawn offsets
    public double getMaredareRespawnOffX(int mapID) {
        return maredare_maps_loaded.get(mapID).cpOff[0];
    }
    public double getMaredareRespawnOffZ(int mapID){
        return maredare_maps_loaded.get(mapID).cpOff[1];
    }

    //get coord data (respawn coord)
    public Location getRespawnLocation(int mapID, int cpID) { return new Location(Bukkit.getWorld(gameWorld), getRespawnX(mapID, cpID), getRespawnY(mapID, cpID), getRespawnZ(mapID, cpID), (float) getRespawnLR(mapID, cpID), (float) getRespawnUD(mapID, cpID)); }
    public double getRespawnX(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[0]; }
    public double getRespawnY(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[1]; }
    public double getRespawnZ(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[2]; }
    public double getRespawnLR(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[3]; }
    public double getRespawnUD(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[4]; }
    //get maredare coord data (respawn coord)
    public double getMaredareRespawnX(int mapID, int cpID) { return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[0]; }
    public double getMaredareRespawnY(int mapID, int cpID) { return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[1]; }
    public double getMaredareRespawnZ(int mapID, int cpID) { return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[2]; }
    public double getMaredareRespawnLR(int mapID, int cpID) { return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[3]; }
    public double getMaredareRespawnUD(int mapID, int cpID) { return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[4]; }

    //get coord data (box corner A coord)
    public double getX(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[0]; }
    public double getY(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[1]; }
    public double getZ(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[2]; }

    //get coord data (box corner B-A coord)
    public double getXOff(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[0] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[0]; }
    public double getYOff(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[1] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[1]; }
    public double getZOff(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[2] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[2]; }

    //get coord data (box corner B coord)
    public double getXEnd(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[0]; }
    public double getYEnd(int mapID, int cpID) { return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[1]; }
    public double getZEnd(int mapID, int cpID){ return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[2]; }

    //get coord data (check player in 1 dimensional box)
    public boolean isInX(int mapID, int cpID, double plyrX) {
        double x = getX(mapID, cpID);
        double xo = getXOff(mapID, cpID);
        double xe = getXEnd(mapID, cpID);

        if (xo >= 0.0){
            return (plyrX >= x && plyrX <= xe);
        } else {
            return (plyrX <= x && plyrX >= xe);
        }
    }
    public boolean isInY(int mapID, int cpID, double plyrY) {
        double y = getY(mapID, cpID);
        double yo = getYOff(mapID, cpID);
        double ye = getYEnd(mapID, cpID);

        if (yo >= 0.0){
            return (plyrY >= y && plyrY <= ye);
        } else {
            return (plyrY <= y && plyrY >= ye);
        }
    }
    public boolean isInZ(int mapID, int cpID, double plyrZ) {
        double z = getZ(mapID, cpID);
        double zo = getZOff(mapID, cpID);
        double ze = getZEnd(mapID, cpID);

        if (zo >= 0.0){
            return (plyrZ >= z && plyrZ <= ze);
        } else {
            return (plyrZ <= z && plyrZ >= ze);
        }
    }

    //get coord data (check player in 3 dimensional box)
    public boolean isInBox(int mapID, int cpID, double plyrX, double plyrY, double plyrZ) { return (isInX(mapID, cpID, plyrX) && isInY(mapID, cpID, plyrY) && isInZ(mapID, cpID, plyrZ)); }
}
