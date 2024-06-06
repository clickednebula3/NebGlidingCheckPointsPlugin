package nebpoints.nebpoints.dataFiles;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class MaredareCheckpoint {
    public double[] coordsCenterBlock = new double[5];
    public double radius = 5.0;

    public MaredareCheckpoint(double[] coordsCenterBlock) {
        this.coordsCenterBlock = coordsCenterBlock;
    }
    public MaredareCheckpoint(double[] coordsCenterBlock, double radius) {
        this.coordsCenterBlock = coordsCenterBlock;
        this.radius = radius;
    }
    public MaredareCheckpoint(List<Double> coordsCenterBlock) {
        this.coordsCenterBlock[0] = coordsCenterBlock.get(0);
        this.coordsCenterBlock[1] = coordsCenterBlock.get(1);
        this.coordsCenterBlock[2] = coordsCenterBlock.get(2);
        this.coordsCenterBlock[3] = coordsCenterBlock.get(3);
        this.coordsCenterBlock[4] = coordsCenterBlock.get(4);
    }
    public MaredareCheckpoint(List<Double> coordsCenterBlock, double radius) {
        this.coordsCenterBlock[0] = coordsCenterBlock.get(0);
        this.coordsCenterBlock[1] = coordsCenterBlock.get(1);
        this.coordsCenterBlock[2] = coordsCenterBlock.get(2);
        this.coordsCenterBlock[3] = coordsCenterBlock.get(3);
        this.coordsCenterBlock[4] = coordsCenterBlock.get(4);
        this.radius = radius;
    }

    public Location getCpRespawnLocation(World playerWorld) {
        Location myLocation = new Location(playerWorld, coordsCenterBlock[0], coordsCenterBlock[1], coordsCenterBlock[2]);
        return myLocation;
    }
}
