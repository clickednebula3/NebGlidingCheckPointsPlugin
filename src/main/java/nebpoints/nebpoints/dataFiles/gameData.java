package nebpoints.nebpoints.dataFiles;

import nebpoints.nebpoints.Nebpoints;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;


public class gameData {
    public ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public String lobbyWorld = "overworld";
    public String gameWorld = "glidemaps";
    public String mareWorld = "glidemaps";

    public String coolElytra = "minecraft:elytra{Enchantments:[{id:binding_curse,lvl:1},{id:vanishing_curse,lvl:1},{id:unbreaking,lvl:5}]}";

    public String[] musicDiscs = {"13", "blocks", "cat", "far", "mall", "otherside", "pigstep", "wait"};
    public Sound[] musicDiscIds = {Sound.MUSIC_DISC_13, Sound.MUSIC_DISC_BLOCKS, Sound.MUSIC_DISC_CAT, Sound.MUSIC_DISC_FAR, Sound.MUSIC_DISC_MALL, Sound.MUSIC_DISC_OTHERSIDE, Sound.MUSIC_DISC_PIGSTEP, Sound.MUSIC_DISC_WAIT};

    public String startSound = "minecraft:entity.experience_orb.pickup";//1.5
    public String finishSound = "minecraft:ui.toast.challenge_complete";//1.65
    public String timerTick = "minecraft:block.note_block.chime";//1.6
    public String timerGo = "minecraft:item.trident.thunder";//1.85
    public String wrongWay = "minecraft:block.note_block.bass";//0.3
    public Integer timerLength = 10;
    public Integer gameLength = 400;

    public List<GlidingMap> gliding_maps_loaded = new ArrayList<>();
    public List<MaredareMap> maredare_maps_loaded = new ArrayList<>();
        //temp; move to config todo: MOVE INTO maredareData
        MaredareMap firstMap = new MaredareMap("candy", new double[]{0.0, 0.0}, new double[]{0.0, 0.0}, new double[]{-10, 60, -10, 110, 80, 10}, false);
    public List<Integer> ScheduledRepeatingTasks = new ArrayList<>();
    public List<Boolean> FinishedRepeatingTasks = new ArrayList<>();

    public double[] lobbyCoords = {42.5, 65.00, -1.5, 90.0, 0.0};

    public gameData(Nebpoints nebplugin) {
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

    public boolean ReadMapsFromConfig(FileConfiguration config) {

        lobbyCoords[0] = config.getDouble("lobby.x");
        lobbyCoords[1] = config.getDouble("lobby.y");
        lobbyCoords[2] = config.getDouble("lobby.z");
        lobbyCoords[3] = config.getDouble("lobby.yaw");
        lobbyCoords[4] = config.getDouble("lobby.pitch");

        if (config.contains("gliding_maps")) {
            List<?> gliding_maps = config.getList("gliding_maps");
            gliding_maps_loaded = new ArrayList<>();

            for (Object gliding_map : gliding_maps) {
                HashMap<String, ?> ThisMap = (HashMap<String, ?>) gliding_map;

                String ThismapName = (String) ThisMap.get("mapName");
                Boolean ThisisRanked = (Boolean) ThisMap.get("isRanked");
                List<Double> ThiscpOff = (List<Double>) ThisMap.get("cpOff");

                GlidingMap thisGlidingMap = new GlidingMap(ThismapName, ThiscpOff, ThisisRanked);
                console.sendMessage(thisGlidingMap.mapName + " | " + thisGlidingMap.isRanked + " | " + thisGlidingMap.cpOff[0] + ", " + thisGlidingMap.cpOff[1] + "\n\n");

                List<?> checkpoints = (List<?>) ThisMap.get("checkpoints");
                for (Object checkpoint : checkpoints) {
                    HashMap<String, ?> ThisCheckpoint = (HashMap<String, ?>) checkpoint;
                    //console.sendMessage("\nCP ("+cp+"):\n" + ThisCheckpoint.toString() + "\n:CP\n");

                    List<Double> ThiscoordsRespawn = (List<Double>) ThisCheckpoint.get("coordsRespawn");
                    List<Double> ThiscoordsA = (List<Double>) ThisCheckpoint.get("coordsA");
                    List<Double> ThiscoordsB = (List<Double>) ThisCheckpoint.get("coordsB");

                    GlidingCheckpoint thisGlidingCheckpoint = new GlidingCheckpoint(ThiscoordsRespawn, ThiscoordsA, ThiscoordsB);
                    thisGlidingMap.addCheckpoint(thisGlidingCheckpoint);
                }

                gliding_maps_loaded.add(thisGlidingMap);
            }
        }
        return true;
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
        if (plyr.getScoreboardTags().contains("hasResourcePack") && !plyr.getScoreboardTags().contains("activeResourcePack")) {
            plyr.addScoreboardTag("activeResourcePack");
            plyr.performCommand("pack");
            return true;
        }
        return false;
    }

    public boolean placeGlidingCabinet(int mapID, int cabinetID, Player plyr) {
        Location cabinetLocation = new Location(
                Bukkit.getWorld(gameWorld),
                getRespawnX(mapID, 0) + getRespawnOffX(mapID)*cabinetID, getRespawnY(mapID, 0),
                getRespawnZ(mapID, 0) + getRespawnOffZ(mapID)*cabinetID,
                (float) getRespawnLR(mapID, 0), (float) getRespawnUD(mapID, 0)
        );

        plyr.teleport(cabinetLocation);

        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, -1, 0)).setType(Material.GOLD_BLOCK);//bottom
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, 2, 0)).setType(Material.GOLD_BLOCK);//top

        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(1, 0, 0)).setType(Material.GLASS);//leg-high
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(-1, 0, 0)).setType(Material.GLASS);
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, 0, 1)).setType(Material.GLASS);
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, 0, -1)).setType(Material.GLASS);
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(1, 1, 0)).setType(Material.GLASS);//head-high
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(-1, 1, 0)).setType(Material.GLASS);
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, 1, 1)).setType(Material.GLASS);
        Bukkit.getWorld(gameWorld).getBlockAt(cabinetLocation.add(0, 1, -1)).setType(Material.GLASS);
        return true;
    }

    public String getRandomMusic() {
        Random rand = new Random();
        int rando = rand.nextInt(musicDiscs.length);
        return musicDiscs[rando];
    }
    public int getRandomMusicId() {
        return new Random().nextInt(musicDiscs.length);
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
    public double getRespawnX(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[0];
    }
    public double getRespawnY(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[1];
    }
    public double getRespawnZ(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[2];
    }
    public double getRespawnLR(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[3];
    }
    public double getRespawnUD(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsRespawn[4];
    }
    //get maredare coord data (respawn coord)
    public double getMaredareRespawnX(int mapID, int cpID) {
        return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[0];
    }
    public double getMaredareRespawnY(int mapID, int cpID) {
        return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[1];
    }
    public double getMaredareRespawnZ(int mapID, int cpID) {
        return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[2];
    }
    public double getMaredareRespawnLR(int mapID, int cpID) {
        return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[3];
    }
    public double getMaredareRespawnUD(int mapID, int cpID) {
        return maredare_maps_loaded.get(mapID).checkpoints.get(cpID).coordsCenterBlock[4];
    }

    //get coord data (box corner A coord)
    public double getX(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[0];
    }
    public double getY(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[1];
    }
    public double getZ(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[2];
    }

    //get coord data (box corner B-A coord)
    public double getXOff(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[0] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[0];
    }
    public double getYOff(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[1] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[1];
    }
    public double getZOff(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[2] - gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsA[2];
    }

    //get coord data (box corner B coord)
    public double getXEnd(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[0];
    }
    public double getYEnd(int mapID, int cpID) {
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[1];
    }
    public double getZEnd(int mapID, int cpID){
        return gliding_maps_loaded.get(mapID).checkpoints.get(cpID).coordsB[2];
    }

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
    public boolean isInBox(int mapID, int cpID, double plyrX, double plyrY, double plyrZ) {
        return (isInX(mapID, cpID, plyrX) && isInY(mapID, cpID, plyrY) && isInZ(mapID, cpID, plyrZ));
    }
}
