package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {

    private CensusGroup[] cenGroup;
    private int cols;
    private int rows;
    private double[] corners;

    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        cenGroup = censusData;
        rows = numRows;
        cols = numColumns;
        corners = getCorners(censusData);
    }

    public double[] getCorners (CensusGroup[] cend) {
        MapCorners one = new MapCorners(cend[0]);
        for(int i=0; i<cend.length; i++) {
            MapCorners two = new MapCorners(cend[i]);
            one = one.encompass(two);
        }
        double[] ret = {one.west, one.east, one.south, one.north};
        return ret;
    }

    @Override
    public int getTotalPopulation() {
        for (CensusGroup cg: cenGroup) {
            this.totalPopulation += cg.population;
        }
        return this.totalPopulation;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (west < 1 || south < 1 || east < west || north < south) {
            throw new IllegalArgumentException();
        }
        int pop = 0;
        double colInt = (corners[1] - corners[0]) / cols;
        double rowInt = (corners[3] - corners[2]) / rows;
        for (CensusGroup cd: cenGroup) {
            int x = (int) ((cd.longitude - corners[0]) / colInt + 1);
            int y = (int) ((cd.latitude - corners[2]) / rowInt + 1);
            if (inGrid(x,y,west,south,east,north)) {
                pop += cd.population;
            }
        }
        return pop;
    }

    public boolean inGrid(double x, double y, int west, int south, int east, int north) {
        return ((x >= west && x < east + 1 && y >= south && y < north + 1) || (x == east + 1 && x == cols + 1 && y == north + 1 && y == rows + 1) || (y == north + 1 && y == rows + 1 && x >= west && x < east + 1) || (x == east + 1 && x == cols + 1 && y >= south && y < north + 1));
    }
}
