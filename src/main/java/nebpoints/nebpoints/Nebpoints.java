package nebpoints.nebpoints;

import nebpoints.nebpoints.commands.*;
import nebpoints.nebpoints.completers.*;
import nebpoints.nebpoints.dataFiles.disasterData;
import nebpoints.nebpoints.dataFiles.gameData;
import nebpoints.nebpoints.handlers.ConcreteHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public final class Nebpoints extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello World! I'm NebPoints and I'll host your elytra gliding trips! Safe travels");
        //config and dataFiles
        saveDefaultConfig();

        gameData gameData = new gameData(this);
        disasterData disasterData = new disasterData();

        //event handlers
        new ConcreteHandler(this, gameData);
        //command executors
        this.getCommand("maredare").setExecutor(new maredareExecutor(this, gameData));//TODO: add this
        this.getCommand("nebshowmaps").setExecutor(new listGlidingMapExecutor(this, gameData));
        this.getCommand("nebmapdeletegliding").setExecutor(new deleteGlidingMapExecutor(this, gameData));
        this.getCommand("nebmapcreatecpa").setExecutor(new createGlidingCpBoxBoundExecutor(this, gameData));
        this.getCommand("nebmapcreatecpb").setExecutor(new createGlidingCpBoxEndBoundExecutor(this, gameData));
        this.getCommand("nebmapdeletecp").setExecutor(new deleteGlidingCpExecutor(this, gameData));
        this.getCommand("nebmapcreatecp").setExecutor(new createGlidingCpRespawnExecutor(this, gameData));
        this.getCommand("nebmapcreategliding").setExecutor(new createGlidingMapExecutor(this, gameData));
        this.getCommand("nebmaplist").setExecutor(new nebmapdataExecutor(this, gameData));
        this.getCommand("setneblobby").setExecutor(new setlobbyExecutor(this, gameData));
        this.getCommand("nebClearScheduledRepeatingTasks").setExecutor(new nebClearScheduledRepeatingTasksExecutor(gameData));

        this.getCommand("start").setExecutor(new startExecutor());
        this.getCommand("pack").setExecutor(new packExecutor());
        this.getCommand("togglepack").setExecutor(new togglePackExecutor());
        this.getCommand("nebmsg").setExecutor(new msgExecuter());

        this.getCommand("glide").setExecutor(new glideExecutor(this, gameData, disasterData));
        this.getCommand("disaster").setExecutor(new disasterExecutor(this, gameData, disasterData));
        this.getCommand("glidehost").setExecutor(new hostGlideExecutor(this, gameData, disasterData));
        this.getCommand("glidehostjoin").setExecutor(new hostJoinGlideExecutor(this, gameData, disasterData));
        this.getCommand("leave").setExecutor(new stopExecutor(gameData));

        //command completer
        this.getCommand("start").setTabCompleter(new startCompleter(gameData));
        this.getCommand("disaster").setTabCompleter(new disasterCompleter(disasterData));
        this.getCommand("glidehost").setTabCompleter(new hostGlideCompleter(gameData));
        this.getCommand("glide").setTabCompleter(new glideCompleter(gameData, false, false));
        this.getCommand("nebmapcreategliding").setTabCompleter(new createGlidingMapCompleter(gameData));
        this.getCommand("nebmapcreatecp").setTabCompleter(new glideCompleter(gameData, true, true));
        this.getCommand("nebmapcreatecpa").setTabCompleter(new glideCompleter(gameData, true, false));
        this.getCommand("nebmapcreatecpb").setTabCompleter(new glideCompleter(gameData, true, false));
        this.getCommand("nebmapdeletecp").setTabCompleter(new glideCompleter(gameData, false, false));
        this.getCommand("nebmapdeletegliding").setTabCompleter(new glideCompleter(gameData, false, false));

        File myObj = new File("glidingData.txt");
        try {
            if (myObj.createNewFile()) {
                Bukkit.getLogger().info("File created: " + myObj.getName());
            } else {
                Bukkit.getLogger().info("File " + myObj.getName() + " already exists.");
            }
        } catch (IOException e) {
            Bukkit.getLogger().info("An file-creating error occurred in nebplugin");
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Bye-bye-bye, Psy-eye-eye! But why-eye-eye... Did you leave my-yaye-yaye... Pie-eye-eye... To to die-eye-eye :(");
    }



}
