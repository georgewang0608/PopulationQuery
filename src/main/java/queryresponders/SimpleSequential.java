package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;

public class SimpleSequential extends QueryResponder {

    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        throw new NotYetImplementedException();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        throw new NotYetImplementedException();
    }
}
