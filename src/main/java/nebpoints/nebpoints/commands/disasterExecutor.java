package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.dataFiles.Arenas;
import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.dataFiles.gameData;
import nebpoints.nebpoints.game.disastrophe;
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

public class disasterExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;
    disasterData disasterData;
    public disasterExecutor(Nebpoints plugin, gameData gData, disasterData dData) {
        nebplugin = plugin;
        gameData = gData;
        disasterData = dData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return disastropheActivate(sender, command, label, args);
        }
        return false;
    }

    public boolean disastropheActivate(CommandSender sender, Command command, String label, String[] args) {
        if (((Player) sender).getPlayer().getScoreboardTags().contains("gameRunning")) {
            sender.sendMessage("You are already in a game! Do /leave to forfeit.");
            return true;
        }

        ConsoleCommandSender console = disasterData.console;
        Material[] joinBlocks = {Material.RED_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER};
        String[] joinColor = {"red", "blue", "yellow", "green"};

        int ArenaID = 0;
        int arenaIndex = 0;
        if (args.length >= 1) {
            for (Arenas arena : disasterData.ArenaList) {
                if (args[0].equals(arena.arenaName)) {
                    ArenaID = arenaIndex;
                    break;
                }
                arenaIndex++;
            }
        }
        World lbbywrld = Bukkit.getServer().getWorld(disasterData.lobbyWorld);
        World gamewrld = Bukkit.getServer().getWorld(disasterData.gameWorld);


        //get players and teams
        ArrayList<Player> plyrs = new ArrayList<>();
        ArrayList<String> sortColors = new ArrayList<>();
        for (int index = 0; index < joinBlocks.length; index++) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                assert lbbywrld != null;
                if (p.getWorld().getName().equals(lbbywrld.getName()) && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == joinBlocks[index]) {
                    plyrs.add(p);
                    sortColors.add(joinColor[index]);
                }
            }
        }

        //leave early if nobody joined
        if (plyrs.size() <= 0) {
            String myName = sender.getName();
            Bukkit.dispatchCommand(console, "tellraw "+myName+" \"No Players. Teleporting to lobby\"");
            Bukkit.dispatchCommand(console, "tellraw "+myName+" \"Use this command again once all players chose their colors and are ready\"");
            ((Player) sender).performCommand("warp disaster");
            return true;
        }

        //handle joins and teams
        for (int pCount = 0; pCount < plyrs.size(); pCount++) {
            String myName = plyrs.get(pCount).getName();
            String myColor = sortColors.get(pCount);
            //log player joins
            Bukkit.dispatchCommand(console, "tellraw @a {\"text\":\"[Disastrophe] " + myName + " joined as Player" + (pCount + 1) + " with color:" + myColor + " on map:"+ disasterData.ArenaList[ArenaID].arenaName+"\",\"color\":\""+myColor+"\"}");
            //tag players
            plyrs.get(pCount).addScoreboardTag("disasterRunning");
            plyrs.get(pCount).addScoreboardTag("disaster");
            plyrs.get(pCount).addScoreboardTag("disaster_"+myColor);

            //tp to arena
            int spawnIndex;
            if (Objects.equals(myColor, "red")) {spawnIndex = 0;}
            else if (Objects.equals(myColor, "blue")) {spawnIndex = 1;}
            else if (Objects.equals(myColor, "yellow")) {spawnIndex = 2;}
            else if (Objects.equals(myColor, "green")) {spawnIndex = 3;}
            else {spawnIndex = 0;}

            double spawnX = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][0];
            double spawnY = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][1];
            double spawnZ = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][2];
            double spawnLR = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][3];
            double spawnUD = disasterData.ArenaList[ArenaID].arenaSpawns[spawnIndex][4];
            Location loc = new Location(gamewrld, spawnX, spawnY, spawnZ);

            plyrs.get(pCount).teleport(loc);
            //has -> wants pack
            //active -> already applied it
            if (plyrs.get(pCount).getScoreboardTags().contains("hasResourcePack") && !plyrs.get(pCount).getScoreboardTags().contains("activeResourcePack")) {
                plyrs.get(pCount).addScoreboardTag("activeResourcePack");
                plyrs.get(pCount).performCommand("pack");
            }
            Bukkit.dispatchCommand(console, "execute in minecraft:overworld run tp " + myName + " " + spawnX + " " + spawnY + " " + spawnZ + " " + spawnLR + " " + spawnUD);
        }

        Bukkit.dispatchCommand(console, "say [Disastrophe] Starting game with " + plyrs.size() + " player(s)");
        new disastrophe(nebplugin, plyrs, sortColors, ArenaID, gameData, disasterData);
        return true;
    }
}
