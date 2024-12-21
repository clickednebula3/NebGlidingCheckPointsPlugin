package nebpoints.nebpoints.dataFiles;

import java.util.List;

public class GlidingCheckpoint {
    public double[] coordsRespawn = new double[5];
    public double[] coordsA = new double[3];
    public double[] coordsB = new double[3];

    public GlidingCheckpoint(double[] coordsRespawn, double[] coordsA, double[] coordsB) {
        this.coordsRespawn = coordsRespawn;
        this.coordsA = coordsA;
        this.coordsB = coordsB;
    }

    public GlidingCheckpoint(List<Double> coordsRespawn, List<Double>  coordsA, List<Double>  coordsB) {
        if (coordsRespawn != null) {
            this.coordsRespawn[0] = coordsRespawn.get(0);
            this.coordsRespawn[1] = coordsRespawn.get(1);
            this.coordsRespawn[2] = coordsRespawn.get(2);
            this.coordsRespawn[3] = coordsRespawn.get(3);
            this.coordsRespawn[4] = coordsRespawn.get(4);
        }

        if (coordsA != null) {
            this.coordsA[0] = coordsA.get(0);
            this.coordsA[1] = coordsA.get(1);
            this.coordsA[2] = coordsA.get(2);
        }

        if (coordsB != null) {
            this.coordsB[0] = coordsB.get(0);
            this.coordsB[1] = coordsB.get(1);
            this.coordsB[2] = coordsB.get(2);
        }
    }
}
