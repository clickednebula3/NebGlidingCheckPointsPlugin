package nebpoints.nebpoints.commands;

import nebpoints.nebpoints.dataFiles.gameData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class nebClearScheduledRepeatingTasksExecutor implements CommandExecutor {

    gameData gameData;
    public nebClearScheduledRepeatingTasksExecutor(gameData gData) {
        gameData = gData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (clearScheduledRepeatingTasks()) {
            sender.sendMessage("Cleared Finished NebPoints Repeating Tasks.\nThis may result in errors in running games.\nRejoining is Recommended.");
        } else {
            sender.sendMessage("There are no NebPoints Repeating Tasks.");
        }
        return true;
    }
    public boolean clearScheduledRepeatingTasks() {
        if (gameData.ScheduledRepeatingTasks.size() == 0) {
            return false;
        }

        for (int i=0; i<gameData.ScheduledRepeatingTasks.size(); i++) {
            if (gameData.FinishedRepeatingTasks.get(i)) {
                Bukkit.getScheduler().cancelTask( gameData.ScheduledRepeatingTasks.get(i) );
                gameData.ScheduledRepeatingTasks.remove(i);
                gameData.FinishedRepeatingTasks.remove(i);
            }
        }
        return true;
    }
}
