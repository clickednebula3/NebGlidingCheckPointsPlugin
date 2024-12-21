package nebpoints.nebpoints.dataFiles;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlidingMap {
    public String mapName;
    public double[] cpOff = new double[2];
    public boolean isRanked;
    public ItemStack icon = new ItemStack(Material.ELYTRA);
    public List<GlidingCheckpoint> checkpoints = new ArrayList<>();

    public GlidingMap(String mapName, double[] cpOff, boolean isRanked, List<GlidingCheckpoint> checkpoints) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
    }

    public GlidingMap(String mapName, double[] cpOff, boolean isRanked, List<GlidingCheckpoint> checkpoints, ItemStack icon) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
        this.icon = icon;
    }

    public GlidingMap(String mapName, double[] cpOff, boolean isRanked) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.isRanked = isRanked;
    }

    public GlidingMap(String mapName, double[] cpOff, boolean isRanked, ItemStack icon) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.isRanked = isRanked;
        this.icon = icon;
    }

    public GlidingMap(String mapName, List<Double> cpOff, Boolean isRanked, List<GlidingCheckpoint> checkpoints) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
    }

    public GlidingMap(String mapName, List<Double> cpOff, Boolean isRanked, List<GlidingCheckpoint> checkpoints, ItemStack icon) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
        this.icon = icon;
    }

    public GlidingMap(String mapName, List<Double> cpOff, Boolean isRanked) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.isRanked = isRanked;
    }

    public GlidingMap(String mapName, List<Double> cpOff, Boolean isRanked, ItemStack icon) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.isRanked = isRanked;
        this.icon = icon;
    }

    public void addCheckpoint(GlidingCheckpoint checkpoint) {
        this.checkpoints.add(checkpoint);
    }
}
