package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private CornerFindingResult grid;
    private double colength;
    private double rolength;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
//        throw new NotYetImplementedException();
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.grid = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.colength = (grid.getMapCorners().east - grid.getMapCorners().west)/numColumns;
        this.rolength = (grid.getMapCorners().north - grid.getMapCorners().south)/numRows;
        this.totalPopulation = grid.getTotalPopulation();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        double westCord = grid.getMapCorners().west + (west - 1) * colength;
        double eastCord = grid.getMapCorners().west + east * colength;
        double southCord = grid.getMapCorners().south + (south - 1) * rolength;
        double northCord = grid.getMapCorners().south + north * rolength;
//        MapCorners input = new MapCorners(westCord, eastCord, northCord, southCord);
        return POOL.invoke(new GetPopulationTask(censusData, 0, censusData.length, westCord, southCord,
                eastCord, northCord, grid.getMapCorners()));
//        throw new NotYetImplementedException();
    }
}
