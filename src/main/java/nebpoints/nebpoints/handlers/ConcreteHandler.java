package nebpoints.nebpoints.handlers;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class ConcreteHandler implements Listener {
    gameData gameData;

    public ConcreteHandler(Nebpoints plugin, gameData gData) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gameData = gData;
    }

    @EventHandler
    public void onConcretePlace(BlockPlaceEvent event) {
//        Block block = event.getBlock();
//        if (block.getType() == Material.BLUE_CONCRETE) {
//            Player plyr = event.getPlayer();
//            Bukkit.dispatchCommand(gameData.console, "execute at @a run playsound minecraft:block.anvil.land master @p");
//            if (plyr.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BLUE_CONCRETE) {
//                Bukkit.dispatchCommand(gameData.console, "tellraw "+plyr.getName()+" \"[ConcreteEvidence] woah there partner, you tryna alert a blueness detector or somethin'?\"");
//            }
//        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaredareDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getScoreboardTags().contains(gameData.tag_in_game)) { event.setCancelled(true); }
    }

    @EventHandler
    public void onAttemptToUncraftNoisifierWand(CraftItemEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item != null && !item.getItemMeta().getLore().isEmpty() && Objects.equals(item.getItemMeta().getLore().get(0), "uncraftable"))
        { event.setCancelled(true); }
    }

    @EventHandler
    public void onRightClickWithItem(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null) {
            if (item.getItemMeta().getDisplayName().contains("Noisifier Wand"))
            { event.getPlayer().performCommand("noisifier"); event.setCancelled(true); }
            else if (item.getType() == Material.DIAMOND)
            { event.getPlayer().getWorld().playSound(event.getPlayer(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, 1f, 1); }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (event.getView().getTitle().startsWith("Gliding Maps")) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            String[] titleArgs = title.split(" ");
            ItemStack item = event.getCurrentItem();

            if (slot < gameData.gliding_maps_loaded.size() && item != null) { player.performCommand("glide "+item.getItemMeta().getDisplayName()); }

            event.setCancelled(true);
        }
        if (event.getView().getTitle().startsWith("Disastrophe Maps")) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            String[] titleArgs = title.split(" ");
            ItemStack item = event.getCurrentItem();

            if (slot < gameData.gliding_maps_loaded.size() && item != null) { player.performCommand("glide "+item.getItemMeta().getDisplayName()); }

            event.setCancelled(true);
        }
        if (event.getView().getTitle().startsWith("Noisifier")) {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            String[] titleArgs = title.split(" ");

            if (title.startsWith("Noisifierisor"))
            {
                if (slot < 9*2 && (slot & 1) == 0) {
                    ItemStack item = event.getCurrentItem();
                    if (item != null) { player.performCommand("noisifier "+item.getItemMeta().getDisplayName()); }
                } else if (slot == 9*2) {
                    player.stopAllSounds();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) < 10) { p.stopAllSounds(); }
                    }
                } else if (slot == 9*2+1) {
                    player.performCommand("noisifier wand");
                } else if (slot == (9*2)+8) { player.playSound(player.getLocation(), Sound.ENTITY_FOX_SLEEP, 100, 100); }
            }
            else if (title.startsWith("Noisifierating"))
            {
                String soundGroup = titleArgs[1];
                ItemStack item = event.getCurrentItem();
                if (slot < 9*3 && item != null) {
                    String soundString = item.getItemMeta().getDisplayName();
                    player.performCommand("noisifier "+soundGroup+" "+soundString);
                } else if (slot == 9*3) {
                    player.performCommand("noisifier");
                }
            }

            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onGamerJoin(PlayerJoinEvent event) {//we don't die, we respawn
        stopExecutor stopper = new stopExecutor(gameData);
        stopper.stopPlayerGames(event.getPlayer());
        event.getPlayer().removeScoreboardTag("activeResourcePack");

        //has -> wants pack
        //active -> already applied it
//        gameData.checkApplyPack(event.getPlayer());
//        if (event.getPlayer().getScoreboardTags().contains("hasResourcePack")) {
//            event.getPlayer().removeScoreboardTag("activeResourcePack");
//            //event.getPlayer().performCommand("pack");
//        } else {
//            event.getPlayer().removeScoreboardTag("activeResourcePack");
//        }
    }
}
