package nebpoints.nebpoints.handlers;

import nebpoints.nebpoints.Nebpoints;
import nebpoints.nebpoints.commands.stopExecutor;
import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConcreteHandler implements Listener {
    gameData gameData;

    public ConcreteHandler(Nebpoints plugin, gameData gData) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        gameData = gData;
    }

    @EventHandler
    public void onConcretePlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.BLUE_CONCRETE) {
            Player plyr = event.getPlayer();
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "execute at @a run playsound minecraft:block.anvil.land master @p");
            if (plyr.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.BLUE_CONCRETE) {
                Bukkit.dispatchCommand(console, "tellraw "+plyr.getName()+" \"[ConcreteEvidence] woah there partner, you tryna alert a blueness detector or somethin'?\"");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMaredareDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getScoreboardTags().contains("gameRunning")) { event.setCancelled(true); }
    }

    @EventHandler
    public void onGamerJoin(PlayerJoinEvent event) {//we don't die, we respawn
        stopExecutor stopper = new stopExecutor(gameData);
        stopper.stopPlayerGames(event.getPlayer());

        //has -> wants pack
        //active -> already applied it
        if (event.getPlayer().getScoreboardTags().contains("hasResourcePack")) {
            event.getPlayer().removeScoreboardTag("activeResourcePack");
            //event.getPlayer().performCommand("pack");
        } else {
            event.getPlayer().removeScoreboardTag("activeResourcePack");
        }
    }
}
