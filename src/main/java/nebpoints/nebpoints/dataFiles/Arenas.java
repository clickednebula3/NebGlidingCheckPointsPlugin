package nebpoints.nebpoints.dataFiles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arenas {
    //main vars
    public String arenaName;
    public String arenaShape;
    public String metRequirements;

    public double[] arenaCenterCoords;
    public double arenaEdgeOffset;
    public double[][] arenaSpawns;

    public int[] arenaCloneCoord;
    public int[] safeArena;
    public int[][] warningArenas;
    public int[][] dangerArenas;

    public int[] miniPlatform;
    public int[] miniPlatformCloneCoord;
    public double[][] skeletonRooms;

    //CANCELLED?
    //    public int[][] slimeblockFillCoords = {
    //            //x1, y1, z1, x2, y2, z2
    //            {21, 41, -143, 21, 42, -134},
    //            {21, 41, -130, 21, 42, 121},
    //            {55, 41, -121, 55, 52, -130},
    //            {55, 41, -134, 55, 42, -143},
    //    };
    //    public int[][] slimeblockMoveIncrementPerHalfSecond = {
    //            //deltaX, deltaZ
    //            {1, 0},
    //            {1, 0},
    //            {-1, 0},
    //            {-1, 0},
    //    };

    //result of main vars
    public double arenaXstart;
    public double arenaYstart;
    public double arenaZstart;
    public double arenaXend;
    public double arenaYend;
    public double arenaZend;

    public Arenas(
            String name, String shape, String metRequires,
            double[] centerCoords, double edgeOffset, double height, double[][] spawns,
            int[] cloneCoord, int[] safePlat, int[][] warningPlats, int[][] dangerPlats,
            int[] miniPlat, int[] miniPlatCloneCoord,
            double[][] skelerooms
    ) {
        arenaName = name;
        arenaShape = shape;
        metRequirements = metRequires;

        arenaCenterCoords = centerCoords;
        arenaEdgeOffset = edgeOffset;
        arenaSpawns = spawns;

        arenaCloneCoord = cloneCoord;
        safeArena = safePlat;
        warningArenas = warningPlats;
        dangerArenas = dangerPlats;

        miniPlatform = miniPlat;
        miniPlatformCloneCoord = miniPlatCloneCoord;
        skeletonRooms = skelerooms;

        if (shape == "box") {
            arenaXstart = arenaCenterCoords[0] - edgeOffset;
            arenaYstart = arenaCenterCoords[1];
            arenaZstart = arenaCenterCoords[2] - edgeOffset;
            arenaXend = arenaCenterCoords[0] + edgeOffset;
            arenaYend = arenaCenterCoords[1] + height;
            arenaZend = arenaCenterCoords[2] + edgeOffset;
        } else if (shape == "cyl") {
            arenaYstart = arenaCenterCoords[1];
            arenaYend = arenaCenterCoords[1] + height;
        }
    }

    public boolean isInBounds(Arenas arena, Player player){
        if (arena.arenaShape == "cyl") {
            //check x,z point distance
            Location arenaLocation = new Location(player.getWorld(), arena.arenaCenterCoords[0], player.getLocation().getY(), arena.arenaCenterCoords[2]);
            double distance = player.getLocation().distance(arenaLocation);
            if (distance <= arena.arenaEdgeOffset) {
                return true;
            }

        }
        else if (arena.arenaShape == "box") {
            //check within box
            if (player.getLocation().getX() > arena.arenaXstart && player.getLocation().getX() < arena.arenaXend) {
                if (player.getLocation().getZ() > arena.arenaZstart && player.getLocation().getZ() < arena.arenaZend) {
                    if (player.getLocation().getY() < arena.arenaYend) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String miniPlatToggleStr(Arenas arena, boolean wantItRemoved) {
        String executeStr;
        int platX = arena.miniPlatform[0];
        int platY = arena.miniPlatform[1];
        int platZ = arena.miniPlatform[2];
        int platX2 = arena.miniPlatform[3];
        int platY2 = arena.miniPlatform[4];
        int platZ2 = arena.miniPlatform[5];
        int newX = arena.miniPlatformCloneCoord[0];
        int newY = arena.miniPlatformCloneCoord[1];
        int newZ = arena.miniPlatformCloneCoord[2];
        if (wantItRemoved) {
            platY--;
            platY2--;
        }
        executeStr = "execute in minecraft:overworld run clone "+platX+" "+platY+" "+platZ+" "+platX2+" "+platY2+" "+platZ2+" "+newX+" "+newY+" "+newZ;
        return executeStr;
    }

    public String platCloneCoordStr(Arenas arena) {
        int platX = arena.arenaCloneCoord[0];
        int platY = arena.arenaCloneCoord[1];
        int platZ = arena.arenaCloneCoord[2];
        return platX+" "+platY+" "+platZ;
    }

    public String platCoordStr(Arenas arena, String dangerLevel, int platDangerLevel) {
        int platX;
        int platY;
        int platZ;
        int platX2;
        int platY2;
        int platZ2;
        if (dangerLevel == "warning") {
            platX = arena.warningArenas[platDangerLevel][0];
            platY = arena.warningArenas[platDangerLevel][1];
            platZ = arena.warningArenas[platDangerLevel][2];
            platX2 = arena.warningArenas[platDangerLevel][3];
            platY2 = arena.warningArenas[platDangerLevel][4];
            platZ2 = arena.warningArenas[platDangerLevel][5];
        } else if (dangerLevel == "danger") {
            platX = arena.dangerArenas[platDangerLevel][0];
            platY = arena.dangerArenas[platDangerLevel][1];
            platZ = arena.dangerArenas[platDangerLevel][2];
            platX2 = arena.dangerArenas[platDangerLevel][3];
            platY2 = arena.dangerArenas[platDangerLevel][4];
            platZ2 = arena.dangerArenas[platDangerLevel][5];
        } else {
            platX = arena.safeArena[0];
            platY = arena.safeArena[1];
            platZ = arena.safeArena[2];
            platX2 = arena.safeArena[3];
            platY2 = arena.safeArena[4];
            platZ2 = arena.safeArena[5];
        }
        return platX+" "+platY+" "+platZ+" "+platX2+" "+platY2+" "+platZ2;
    }
    //these 3 going to be deprectated for the one above
    public String platSafeStr(Arenas arena) {
        int platX = arena.safeArena[0];
        int platY = arena.safeArena[1];
        int platZ = arena.safeArena[2];
        int platX2 = arena.safeArena[3];
        int platY2 = arena.safeArena[4];
        int platZ2 = arena.safeArena[5];
        return platX+" "+platY+" "+platZ+" "+platX2+" "+platY2+" "+platZ2;
    }
    public String platWarnStr(Arenas arena) {
        int platX = arena.warningArenas[0][0];
        int platY = arena.warningArenas[0][1];
        int platZ = arena.warningArenas[0][2];
        int platX2 = arena.warningArenas[0][3];
        int platY2 = arena.warningArenas[0][4];
        int platZ2 = arena.warningArenas[0][5];
        return platX+" "+platY+" "+platZ+" "+platX2+" "+platY2+" "+platZ2;
    }
    public String platDangerStr(Arenas arena) {
        int platX = arena.dangerArenas[0][0];
        int platY = arena.dangerArenas[0][1];
        int platZ = arena.dangerArenas[0][2];
        int platX2 = arena.dangerArenas[0][3];
        int platY2 = arena.dangerArenas[0][4];
        int platZ2 = arena.dangerArenas[0][5];
        return platX+" "+platY+" "+platZ+" "+platX2+" "+platY2+" "+platZ2;
    }
}
