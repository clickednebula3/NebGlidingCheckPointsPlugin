package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class noisifierExecutor implements CommandExecutor {
    Nebpoints nebplugin;
    gameData gameData;

    public noisifierExecutor(Nebpoints plugin, gameData gameData) { nebplugin = plugin; this.gameData = gameData; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { return false; }
        Player player = ((Player) sender).getPlayer();
        assert player != null;

        List<String> soundGroups = gameData.soundGroups;
        Map<String, Material> soundGroupIconMap = gameData.soundGroupIconMap;
        Map<String, List<String>> soundMap = gameData.soundMap;
        Map<String, Sound> soundStringMap = gameData.soundStringMap;
        Map<Sound, Material> soundIconMap = gameData.soundIconMap;

        String soundGroup = "null";
        Sound sound = null;
        if (args.length >= 1 && soundGroups.contains(args[0].toLowerCase())) { soundGroup = args[0].toLowerCase(); }
        if (args.length >= 2 && !soundGroup.equals("null") && soundMap.get(soundGroup).contains(args[1].toLowerCase())) { sound = soundStringMap.get(args[1].toLowerCase()); }

        if (args.length >= 1 && Objects.equals(args[0], "wand")) {
            //give wand
            ItemStack NoisifierWand = gameData.generateItem(new ItemStack(Material.BLAZE_ROD), ChatColor.DARK_PURPLE+"Noisifier Wand", "uncraftable");
            player.getInventory().addItem(NoisifierWand);
        } else if (soundGroup.equals("null")) {
            //open main GUI
            Inventory mainInv = Bukkit.createInventory(player, 9*3, "Noisifierisor");

            for (int i=0; i<soundGroups.size(); i++) {
                String sound_group = soundGroups.get(i);
                mainInv.setItem(i*2, gameData.generateItem(new ItemStack(soundGroupIconMap.get(sound_group)), sound_group));
            }
            mainInv.setItem((9*2)+8, gameData.generateItem(new ItemStack(Material.SWEET_BERRIES), "Credits", "Noisifier is by "+ChatColor.WHITE+"clickednebula3 \"Nebby\"", ChatColor.RESET+"", "type '/noisifier' for this menu.","You may use a macro."));
            mainInv.setItem((9*2)+1, gameData.generateItem(new ItemStack(Material.BLAZE_ROD), "Wand", "Get the Noisifier wand!"));
            mainInv.setItem(9*2, gameData.generateItem(new ItemStack(Material.STRUCTURE_VOID), "Clear Noises", "Irreversible Action.", "Stops all sounds from you and players in a 10 block radius"));

            player.openInventory(mainInv);
        } else if (sound == null) {
            //open soundGroup GUI
            Inventory mainInv = Bukkit.createInventory(player, 9*4, "Noisifierating "+soundGroup);

            for (int i=0; i<soundMap.get(soundGroup).size() && i<9*3; i++) {
                String sound_string = soundMap.get(soundGroup).get(i);
                Sound this_sound = soundStringMap.get(sound_string);
                mainInv.setItem(i, gameData.generateItem(new ItemStack(soundIconMap.get(this_sound)), sound_string));
            }
            mainInv.setItem(9*3, gameData.generateItem(new ItemStack(Material.REDSTONE_BLOCK), "Back"));
            for (int i=9*3+1; i<9*4; i++)
            { mainInv.setItem(i, gameData.generateItem(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE), " ")); }

            player.openInventory(mainInv);
        } else {
            //play sound
            //todo: get parameters
            float volume = 1f;
            float pitch = 1f;
            long min_volume = 1;
            SoundCategory soundCategory = SoundCategory.MASTER;
            if (soundGroup.toLowerCase().equals("music")) { soundCategory = SoundCategory.RECORDS; }
            player.getWorld().playSound(player.getLocation(), sound, soundCategory, volume, pitch, min_volume);
        }

        return true;
    }
}
