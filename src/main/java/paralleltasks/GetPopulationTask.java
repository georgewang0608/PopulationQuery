package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    double w, s, e, n;
    MapCorners grid;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n, MapCorners grid) {
//        throw new NotYetImplementedException();
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
//        throw new NotYetImplementedException();
        if (hi - lo <= SEQUENTIAL_CUTOFF) {
            return sequentialGetPopulation(censusGroups, lo, hi, w, s, e, n);
        }
        int mid = lo + (hi - lo) / 2;
        GetPopulationTask left = new GetPopulationTask(censusGroups, lo, mid, w, s, e, n, grid);
        GetPopulationTask right = new GetPopulationTask(censusGroups, mid, hi, w, s, e, n, grid);
        left.fork();
        int rightResult = right.compute();
        int leftResult = left.join();
        return rightResult + leftResult;
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int pop = 0;
        for (int i = lo; i < hi; i++) {
            CensusGroup cur = censusGroups[i];
            if (e == grid.east && n == grid.north) {
                if (cur.longitude >= w && cur.longitude <= e && cur.latitude >= s && cur.latitude <= n) {
                    pop += cur.population;
                }
            } else if (e == grid.east) {
                if (cur.longitude >= w && cur.longitude <= e && cur.latitude >= s && cur.latitude < n) {
                    pop += cur.population;
                }
            } else if (n == grid.north) {
                if (cur.longitude >= w && cur.longitude < e && cur.latitude >= s && cur.latitude <= n) {
                    pop += cur.population;
                }
            } else {
                if (cur.longitude >= w && cur.longitude < e && cur.latitude >= s && cur.latitude < n) {
                    pop += cur.population;
                }
            }
        }
        return pop;
    }
}
