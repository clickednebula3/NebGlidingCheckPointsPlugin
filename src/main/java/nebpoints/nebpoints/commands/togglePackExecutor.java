package nebpoints.nebpoints.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class togglePackExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { return false; }

        Player plyr = (Player) sender;
        if (plyr.getScoreboardTags().contains("hasResourcePack")) {
            plyr.removeScoreboardTag("hasResourcePack");
            plyr.sendMessage("PackMode toggled off.");
        } else {
            plyr.addScoreboardTag("hasResourcePack");
            plyr.sendMessage("PackMode toggled on.");
            BaseComponent[] packClickComponent = new ComponentBuilder("[Click Here to get the pack]").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "pack")).create();
            plyr.spigot().sendMessage(packClickComponent);
        }
        return true;
    }
}
