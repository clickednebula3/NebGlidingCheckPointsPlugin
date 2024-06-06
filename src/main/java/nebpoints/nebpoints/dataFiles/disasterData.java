package nebpoints.nebpoints.dataFiles;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Random;

public class disasterData {

    public ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    //try to use programming instead of console
    public String lobbyWorld = "hub";
    public String gameWorld = "hub";

    public Arenas PeaceArena = new Arenas(
            "plains", "cyl", "_default_ _2MainPlatformLevels_",
            new double[] {38.5, 37, -131.5}, 25, 35,
            new double[][] {
                {38.5, 40.0, -135.32, 0, 0},
                {42.5, 40.0, -131.51, 90, 0},
                {38.5, 40.0, -127.53, 180, 0},
                {34.5, 40.0, -131.33, 270, 0},
            },

            new int[] {23, 38, -147},
            new int[] {23, 28, -147, 53, 29, -117}, new int[][] {{23, 26, -147, 53, 27, -117}}, new int[][] {{23, 24, -147, 53, 25, -117}},

            new int[] {27, 31, -143, 49, 31, -121}, new int[] {27, 44, -143},
            new double[][] {
                    {38.5, 40.0, -110.5},
                    {59.5, 40.0, -131.5},
                    {38.5, 40.0, -152.5},
                    {17.5, 40.0, -131.5},
            }
    );
    public Arenas hoopMossArena = new Arenas(
            "hoopmoss", "cyl", "_defaults_ _2MainPlatformLevels_",//change to 3MainPlatformLevels
            new double[] {182.5, 65.0, -150.5}, 32, 30,
            new double[][] {
                {182.5, 65.0, -150.5, 0, 0},
                {182.5, 65.0, -150.5, 90, 0},
                {182.5, 65.0, -150.5, 180, 0},
                {182.5, 65.0, -150.5, 270, 0},
            },

            new int[] {164, 64, -169}, //cloneCoord
            new int[] {164, 45, -169, 200, 50, -133}, new int[][] {{164, 40, -169, 200, 42, -133}}, new int[][] {{164, 36, -169, 200, 38, -133}}, //danger level plats

            new int[] {169, 24, -164, 195, 24, -138}, new int[] {169, 71, -164}, //miniplat and miniplatclonecoord
            new double[][] { //skeles
                    {171.5, 67, -161.5},
                    {193.5, 67, -161.5},
                    {171.5, 67, -139.5},
                    {193.5, 67, -139.5},
            }
    );
    //CrippleStab Arena here
//    public Arenas aquaPaintArena = new Arenas(
//            "aquapaint", "box",//fill everything below
//            new double[] {182.5, 65.0, -150.5}, 32, 30,
//            new double[][] {
//                    {182.5, 65.0, -150.5, 0, 0},
//                    {182.5, 65.0, -150.5, 90, 0},
//                    {182.5, 65.0, -150.5, 180, 0},
//                    {182.5, 65.0, -150.5, 270, 0},
//            },
//
//            new int[] {164, 64, -169}, //cloneCoord
//            new int[] {164, 45, -169, 200, 50, -133}, new int[][] {{164, 40, -169, 200, 42, -133}}, new int[][] {{164, 36, -169, 200, 38, -133}}, //danger level plats
//
//            new int[] {169, 24, -164, 195, 24, -138}, new int[] {169, 71, -164}, //miniplat and miniplatclonecoord
//            new double[][] { //skeles
//                    {171.5, 67, -161.5},
//                    {193.5, 67, -161.5},
//                    {171.5, 67, -139.5},
//                    {193.5, 67, -139.5},
//            }
//    );

    public Arenas[] ArenaList = {
            PeaceArena,
            hoopMossArena,
            //aquapaint
    };

    //public Arena ColorArena = new Arena("box", -1, -2, -3, 5, 5);

    public String getMapName(int mapID){
        return ArenaList[mapID].arenaName;
    }
    public Integer getRandomArenaId() {
        return new Random().nextInt(ArenaList.length);
    }
};
