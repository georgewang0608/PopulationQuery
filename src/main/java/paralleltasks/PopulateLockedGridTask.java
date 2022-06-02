package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;


    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
//        throw new NotYetImplementedException();
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.corners = corners;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
//        throw new NotYetImplementedException();
        //            lockGrid[rowNum][colNum].lock();

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
            if (lockGrid[rowNum][colNum] == null) {
                lockGrid[rowNum][colNum] = new ReentrantLock();
            }
            lockGrid[rowNum][colNum].lock();
            populationGrid[rowNum][colNum] += cur.population;
            lockGrid[rowNum][colNum].unlock();
        }
    }
}
