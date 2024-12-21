package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class stopExecutor implements CommandExecutor {

    gameData gameData;
    public stopExecutor(gameData gData) {
        gameData = gData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player plyr = ((Player) sender).getPlayer();
            stopPlayerGames(plyr);
        }

        return false;
    }

    public boolean stopPlayerGames(Player plyr) {
        if (plyr.getScoreboardTags().contains(gameData.tag_glide)) {
            plyr.removeScoreboardTag(gameData.tag_glide);

            //get data
            ConsoleCommandSender console = gameData.console;
            String myName = plyr.getName();

            //back to lobby
            if (plyr.getWorld().equals(Bukkit.getWorld(gameData.gameWorld))) {
                //clear elytra
//                Bukkit.dispatchCommand(console, "item replace entity " + myName + " armor.chest with minecraft:air");
                plyr.getInventory().remove(Material.ELYTRA);
//                Bukkit.dispatchCommand(console, "execute in "+ gameData.lobbyWorld +" run tp "+myName+" "+ gameData.lobbyCoords[0]+" "+ gameData.lobbyCoords[1]+" "+ gameData.lobbyCoords[2]+" "+ gameData.lobbyCoords[3]+" "+ gameData.lobbyCoords[4]);
            }
            plyr.teleport(new Location(Bukkit.getWorld(gameData.lobbyWorld), gameData.lobbyCoords[0], gameData.lobbyCoords[1], gameData.lobbyCoords[2], (float) gameData.lobbyCoords[3], (float) gameData.lobbyCoords[4]));
            //take effects away
            PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
            PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
            plyr.addPotionEffect(sat);
            plyr.addPotionEffect(reg);
            //Bukkit.dispatchCommand(console, "attribute "+myName+" minecraft:generic.max_health base set 20");
            //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:saturation 3 255 false");
            //Bukkit.dispatchCommand(console, "effect give "+myName+" minecraft:regeneration 3 255 false");
            //Bukkit.dispatchCommand(console, "effect clear " + myName + " minecraft:glowing");
        }
        if (plyr.getScoreboardTags().contains(gameData.tag_disaster)) {
            plyr.removeScoreboardTag(gameData.tag_disaster);

            //get data
            ConsoleCommandSender console = gameData.console;
            String myName = plyr.getName();

            //take effects away
            PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
            PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
            plyr.addPotionEffect(sat);
            plyr.addPotionEffect(reg);
        }
        if (plyr.getScoreboardTags().contains("maredare")) {
            plyr.removeScoreboardTag("maredare");
            PotionEffect sat = new PotionEffect(PotionEffectType.SATURATION, 3, 255);
            PotionEffect reg = new PotionEffect(PotionEffectType.REGENERATION, 3, 255);
            plyr.addPotionEffect(sat);
            plyr.addPotionEffect(reg);
        }

        plyr.setGameMode(GameMode.ADVENTURE);
        plyr.removePotionEffect(PotionEffectType.GLOWING);
        plyr.removePotionEffect(PotionEffectType.LEVITATION);
        plyr.removePotionEffect(PotionEffectType.INVISIBILITY);
        plyr.removePotionEffect(PotionEffectType.BLINDNESS);
        plyr.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);

        plyr.removeScoreboardTag(gameData.tag_in_game);
        plyr.removeScoreboardTag(gameData.tag_glide);
        plyr.removeScoreboardTag(gameData.tag_glide_host);
        plyr.removeScoreboardTag(gameData.tag_glide_host_join);
        plyr.removeScoreboardTag(gameData.tag_in_disaster);
        plyr.removeScoreboardTag(gameData.tag_disaster);
        for (String tag : plyr.getScoreboardTags()) {
            if (
                    tag.startsWith(gameData.tag_glide_host_join) ||
                    tag.startsWith(gameData.tag_glide) ||
                    tag.startsWith(gameData.tag_disaster)
            ) { plyr.removeScoreboardTag(tag); }
        }
//        plyr.removeScoreboardTag("glider_red");
//        plyr.removeScoreboardTag("glider_blue");
//        plyr.removeScoreboardTag("glider_yellow");
//        plyr.removeScoreboardTag("glider_green");

//        plyr.removeScoreboardTag("disaster_ghost");
//        plyr.removeScoreboardTag("disaster_red");
//        plyr.removeScoreboardTag("disaster_blue");
//        plyr.removeScoreboardTag("disaster_yellow");
//        plyr.removeScoreboardTag("disaster_green");

        plyr.removeScoreboardTag("maredare");
        plyr.removeScoreboardTag("maredare_murderer");
        plyr.removeScoreboardTag("maredare_red");
        plyr.removeScoreboardTag("maredare_blue");
        plyr.removeScoreboardTag("maredare_yellow");
        plyr.removeScoreboardTag("maredare_green");

        //back to lobby
        //plyr.performCommand("spawn"); it was me all along. I'm keeping this commented as a symbol.

        return true;
    }
}
