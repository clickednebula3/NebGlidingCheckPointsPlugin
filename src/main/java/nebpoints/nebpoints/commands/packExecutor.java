package nebpoints.nebpoints.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class packExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player chosenPlayer;
        String mcPack ="https://www.dropbox.com/s/pkkkj17ajua69s6/Neb.zip?dl=1";

        if(args.length>=2) {
            if (args[1] == "neb") {
                mcPack = "https://www.dropbox.com/s/pkkkj17ajua69s6/Neb.zip?dl=1";
            } else if (args[1] == "Dafty") {
                mcPack = "https://www.dropbox.com/s/cwkgvrx9646zfbi/Dafty.zip?dl=1";
            } else if (args[1] == "none" || args[1] == "clear") {
                mcPack = "";
            }
        }

        if (args.length>=1) {
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[0]))) {
                chosenPlayer = Bukkit.getPlayer(args[0]);
                chosenPlayer.setResourcePack(mcPack);
            } else {
                sender.sendMessage("Could not find selected player");
            }
        } else {
            if (sender instanceof Player) {
                chosenPlayer = (Player) sender;
                chosenPlayer.setResourcePack(mcPack);
            } else {
                sender.sendMessage("A player has to run this command");
            }
        }


//        if (sender instanceof Player) {
//            Player plyr = (Player) sender;
//            if (args.length >= 1) {
//                if (args[0] == "1" || args[0] == "0") {
//                    plyr.setResourcePack("https://www.dropbox.com/s/pkkkj17ajua69s6/Neb.zip?dl=1");
//                    plyr.sendRawMessage("Thanks for using our pack! Hope you have fun :wink:");
//                } else {
//                    plyr.sendRawMessage("Error, no pack found by that ID");
//                }
//            } else {
//                plyr.setResourcePack("https://www.dropbox.com/s/pkkkj17ajua69s6/Neb.zip?dl=1");
//                plyr.sendRawMessage("Thanks for using our pack! Hope you have fun :wink:");
//            }
//        }
        return true;
    }
}
