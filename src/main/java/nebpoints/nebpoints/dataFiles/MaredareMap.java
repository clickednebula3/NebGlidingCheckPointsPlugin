package nebpoints.nebpoints.dataFiles;

import java.util.ArrayList;
import java.util.List;

//todo: fully make maps structures

public class MaredareMap {
    public String mapName;
    public double[] cpOff = new double[2];
    public double[] murdererSpawnOff = new double[2];//where to place murder away from the first player spawn
    public boolean isRanked;
    public List<MaredareCheckpoint> checkpoints = new ArrayList<>();
    public double[] boundingBoxCoords = new double[6];

    public MaredareMap(String mapName, double[] cpOff, double[] murdererSpawnOff, boolean isRanked, List<MaredareCheckpoint> checkpoints) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.murdererSpawnOff = murdererSpawnOff;
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
    }

    public MaredareMap(String mapName, double[] cpOff, double[] murdererSpawnOff, double[] boundingBoxCoords, boolean isRanked) {
        this.mapName = mapName;
        this.cpOff = cpOff;
        this.murdererSpawnOff = murdererSpawnOff;
        this.isRanked = isRanked;
        this.boundingBoxCoords = boundingBoxCoords;
    }

    public MaredareMap(String mapName, List<Double> cpOff, List<Double> murdererSpawnOff, Boolean isRanked, List<MaredareCheckpoint> checkpoints) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.murdererSpawnOff[0] = murdererSpawnOff.get(0);
        this.murdererSpawnOff[1] = murdererSpawnOff.get(1);
        this.isRanked = isRanked;
        this.checkpoints = checkpoints;
    }

    public MaredareMap(String mapName, List<Double> cpOff, List<Double> murdererSpawnOff, Boolean isRanked) {
        this.mapName = mapName;
        this.cpOff[0] = cpOff.get(0);
        this.cpOff[1] = cpOff.get(1);
        this.murdererSpawnOff[0] = murdererSpawnOff.get(0);
        this.murdererSpawnOff[1] = murdererSpawnOff.get(1);
        this.isRanked = isRanked;
    }

    public void addCheckpoint(MaredareCheckpoint checkpoint) {
        this.checkpoints.add(checkpoint);
    }
}
