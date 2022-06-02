package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;
    private  CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private int[][] grid;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
//        throw new NotYetImplementedException();
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.grid = new int[numRows][numColumns];
        Lock[][] lockGrid = new Lock[numRows][numColumns];
        CornerFindingResult corner = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        double cellWidth = (corner.getMapCorners().east - corner.getMapCorners().west)/numColumns;
        double cellHeight = (corner.getMapCorners().north - corner.getMapCorners().south)/numRows;
        int seg = censusData.length / NUM_THREADS;
        int lo = 0;
        int hi = seg;
        Thread[] threads = new Thread[NUM_THREADS - 1];
        for (int i = 0; i < NUM_THREADS - 1; i++) {
            threads[i] = new PopulateLockedGridTask(censusData, lo, hi, numRows, numColumns,
                    corner.getMapCorners(), cellWidth, cellHeight, grid, lockGrid);
            threads[i].start();
            lo += seg;
            hi += seg;
        }
//        PopulateLockedGridTask thread = new PopulateLockedGridTask(censusData, lo, censusData.length, numRows, numColumns,
//                corner.getMapCorners(), cellWidth, cellHeight, grid, lockGrid);
//        thread.run();
//        Lock lock = new ReentrantLock();
        MapCorners corners = corner.getMapCorners();
        System.out.println(lo);
        for (int i = lo; i < censusData.length; i++) {
            CensusGroup cur = censusData[i];
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
            grid[rowNum][colNum] += cur.population;
            lockGrid[rowNum][colNum].unlock();
        }
        try {
            for (Thread i : threads) {
                i.join();
            }
//            thread.join();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.totalPopulation = corner.getTotalPopulation();
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
