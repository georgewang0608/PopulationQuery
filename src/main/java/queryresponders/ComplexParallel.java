package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private  CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private int[][] grid;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
//        throw new NotYetImplementedException();
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        CornerFindingResult corner = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        double cellWidth = (corner.getMapCorners().east - corner.getMapCorners().west)/numColumns;
        double cellHeight = (corner.getMapCorners().north - corner.getMapCorners().south)/numRows;
        this.totalPopulation = corner.getTotalPopulation();
        this.grid = POOL.invoke(new PopulateGridTask(censusData, 0, censusData.length, numRows,
                numColumns, corner.getMapCorners(), cellWidth, cellHeight));
        for (int i = 1; i < numColumns; i++) {
            grid[0][i] += grid[0][i-1];
        }
        for (int i = 1; i < numRows; i++) {
            grid[i][0] += grid[i-1][0];
        }
        for (int i = 1; i < numRows; i++) {
            for (int j = 1; j < numColumns; j++) {
                grid[i][j] = grid[i][j] + grid[i-1][j] + grid[i][j-1] - grid[i - 1][j - 1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
//        throw new NotYetImplementedException();
        if (west < 1 || south < 1 || east < west || north < south) {
            throw new IllegalArgumentException();
        }
        int ret = 0;
        if (west > 1 && south > 1 && east > 1 && north > 1) {
            ret = grid[north - 1][east - 1] - grid[north - 1][west - 2] - grid[south - 2][east - 1] + grid[south - 2][west - 2];
        } else if (west == 1 && south != 1) {
            ret = grid[north - 1][east - 1] - grid[south - 2][east - 1];
        } else if (south == 1 && west != 1) {
            ret = grid[north - 1][east - 1] - grid[north - 1][west - 2];
        } else {
            ret = grid[north - 1][east - 1];
        }
        return ret;
    }
}
