package nsga2;
import java.util.Comparator;
import matrix2017.TCMatrix;
import java.util.function.Function;
import java.util.ArrayList;
import multiObj.*;

/**
 * comparator for TIndividual using rank and crowded-distance
 *
 */
public class TRankAndCDComparator implements Comparator<TIndividual> {
	public TRankAndCDComparator() {
	}
	
	@Override
	public int compare(TIndividual o1, TIndividual o2) {
		int o1Rank = o1.getRank();
		int o2Rank = o2.getRank();
		double o1CDistance = o1.getCDistance();
		double o2CDistance = o2.getCDistance();

		/** first compare front rank(less ,better). same then, compare crowded-distance(more, better) */
		if(o1Rank > o2Rank) {
			return 1;
		}else if(o1Rank < o2Rank) {
			return -1;
		}else if(o1CDistance < o2CDistance) {
			return 1;
		}else if(o1CDistance > o2CDistance) {
			return -1;
		}else {
			return 0;
		}
	}
}
