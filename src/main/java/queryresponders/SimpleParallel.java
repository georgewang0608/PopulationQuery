package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        throw new NotYetImplementedException();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        throw new NotYetImplementedException();
    }
}
