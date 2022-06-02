package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
//        throw new NotYetImplementedException();
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    @Override
    protected int[][] compute() {
//        throw new NotYetImplementedException();
        if (hi - lo < SEQUENTIAL_CUTOFF) {
            return sequentialPopulateGrid();
        }
        int mid = lo + (hi - lo) / 2;
        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, mid, numRows, numColumns, corners, cellWidth, cellHeight);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns, corners, cellWidth, cellHeight);
        left.fork();
        int[][] rightResult = right.compute();
        int[][] leftResult = left.join();
        POOL.invoke(new MergeGridTask(leftResult, rightResult,0, numRows, 0, numColumns));
        return leftResult;
    }

    private int[][] sequentialPopulateGrid() {
//        throw new NotYetImplementedException();
        int[][] grid = new int[numRows][numColumns];

        for (int i = lo; i < hi; i++) {
            CensusGroup cur = censusGroups[i];
            int colNum = (int) Math.floor((cur.longitude - corners.west) / cellWidth);
            int rowNum = (int) Math.floor((cur.latitude - corners.south) / cellHeight);
            if (cur.longitude == corners.east || cur.latitude == corners.north) {
                if (cur.longitude == corners.east && cur.latitude == corners.north) {
                    colNum -= 1;
                    rowNum -= 1;
                } else if (cur.longitude == corners.east) {
                    colNum -= 1;
                } else if (cur.latitude == corners.north) {
                    rowNum -= 1;
                }
            }
            grid[rowNum][colNum] += cur.population;
        }
        return grid;
    }
}

