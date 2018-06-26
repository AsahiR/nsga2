package nsga2;
import matrix2017.TCMatrix;
import java.util.Comparator;
import java.util.function.Function;
import java.util.ArrayList;
import multiObj.*;


/** comparator for only supremacy */
public class TSComparator implements Comparator<TIndividual>{
	@Override
	public int compare(TIndividual o1, TIndividual o2) {
		TCMatrix eval1 = o1.getEvaluationVector();
		TCMatrix eval2 = o2.getEvaluationVector();
		int frontDimension = eval1.getRowDimension();
		double sign = 0;
		for(int i = 0; i < frontDimension ; i++) {
			double tmp = Math.signum(eval1.getValue(i) - eval2.getValue(i));
			/** temporary for sign before update */
			double prevSign = sign;
			sign = (tmp == 0.0) ? sign : tmp;
			/** sign change , return */
			if(prevSign * sign < 0) {
				return 0;
			}
		}
		return (int)sign;
	}
	
	/** test */
	public static void main(String[] args) {
		TIndividual indv = new TIndividual();
		TCMatrix test = new TCMatrix(2);
		test.setValue(0, 15.494452802866167);
		test.setValue(1, 9.363625605449188);
		indv.setVector(test);
		
		TIndividual indv2 = new TIndividual();
		TCMatrix test2 = new TCMatrix(2);
		test2.setValue(0, 14.62799760543972);
		test2.setValue(1, 11.33807991741019);
		indv2.setVector(test2);
		TSComparator comp = new TSComparator();
		int result = comp.compare(indv, indv2);
		System.out.println(result);
	}

}
