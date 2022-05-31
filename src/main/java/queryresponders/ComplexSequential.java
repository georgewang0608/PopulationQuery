package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;


public class ComplexSequential extends QueryResponder {
    CensusGroup[] cenGroup;
    int cols;
    int rows;
    double[] corners;
    private static int[][] grid;
    private static int[][] grid1;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        cenGroup = censusData;
        rows = numRows;
        cols = numColumns;
        corners = getCorners(censusData);
        grid = new int[numColumns][numRows];
        grid1 = new int[numColumns][numRows];
        populatearr();
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

    public void populatearr() {
        double colInt = ((corners[1] - corners[0]) / cols);
        double rowInt = ((corners[3] - corners[2]) / rows);
        for (CensusGroup cd: cenGroup) {
            int x = (int) Math.floor((cd.longitude - corners[0]) / colInt);
            int y = (int) Math.floor((cd.latitude - corners[2]) / rowInt);
            if (x == cols && y == rows)
                grid[x - 1][y - 1] += cd.population;
            else if (x == cols)
                grid[x - 1][y] += cd.population;
            else if (y == rows)
                grid[x][y - 1] += cd.population;
            else
                grid[x][y] += cd.population;
        }
        populategrid();
    }

    public void populategrid() {
        for (int i = 1; i < grid[0].length; i++)
            grid[0][i] += grid[0][i - 1];
        for (int j = 1; j < grid.length; j++)
            grid[j][0] += grid[j - 1][0];
        for (int k = 1; k < grid[0].length; k++) {
            for (int l = 1; l < grid.length; l++)
                grid[l][k] += grid[l - 1][k] + grid[l][k - 1] - grid[l - 1][k - 1];
        }

    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        //r1 = west r2 = east c1= north c2 =south
        return grid[east][south] - grid[west-1][south] - grid[east][north-1] + grid[west-1][north-1];
        //return grid[south][east] - grid[south][west-1] - grid[north-1][east] + grid[north-1][west-1];
    }

    @Override
    public int getTotalPopulation() {
        return grid[cols-1][rows-1];
    }
}
