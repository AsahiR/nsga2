package nsga2;
import matrix2017.*;
import multiObj.*;
import myUtil.MyMatrixUtil;
import java.util.ArrayList;
import java.util.Comparator;
import myUtil.*;

public class TNSEvaluator extends TEvaluator {
	private ArrayList<TFunction> fFunctions;
	private String fName;

	/** use for supremacy check */
	private Comparator<TIndividual> fComparator;

	public TNSEvaluator(ArrayList<TFunction> functions, Comparator<TIndividual> comparator) {
		fFunctions = functions;
		fComparator = comparator;
		setName();
	}
	
	private void setName() {
		fName = "NonSp" + TMultiObjUtil.makeFunctionsName(fFunctions);
	}
	
	public String getName() {
		return fName;
	}

	/** set indv.fEvaluationVector using fFunctions */
	public void evaluateIndv(TIndividual indv) {
		int frontDimension = fFunctions.size();
		TCMatrix evalVector = new TCMatrix(frontDimension);
		MyMatrixUtil.calcFront(indv.getVector(), fFunctions, evalVector);
		indv.setEvaluationVector(evalVector);
	}
	
	/** 
	 * @param pop set indv.{fEvaluationVector, fRank, fCDistance}
	 */
	public void evaluate(TPopulation pop) {
		int popSize  = pop.getPopulationSize();
		for(int i = 0; i < popSize; i++) {
			evaluateIndv(pop.getIndividual(i));
		}
		/** set for fronts*/
		ArrayList<ArrayList<Integer>> sF = new ArrayList<>();
		setRanks(pop, sF);
		setCDistances(pop, sF);
	}
	
	/**
	 * set first-Rank front 1-st element of sF
	 * @param pop
	 * @param sLT set for indice of indvs less than self
	 * @param sF set for fronts
	 * @param sGTNum set for number of indvs greater than self
	 * @param rank front's rank
	 */
	private void setFirstRank(TPopulation pop, 
			ArrayList<ArrayList<Integer>> sLT,
			ArrayList<ArrayList<Integer>> sF,
			ArrayList<Integer> sGTNum,
			int rank) {
		int popSize = pop.getPopulationSize();
		ArrayList<Integer> firstFront = new ArrayList<>();
		for(int i = 0; i < popSize; i++) {
			TIndividual indv1 = pop.getIndividual(i);
			int indv1Index = i;

			/** set for index of indvs less than indv1 */
			ArrayList<Integer> tmpSLT = new ArrayList<>();

			/** counter for indvs greater than indv1 */
			int gTNum = 0;

			for(int j = 0; j < popSize; j++) {
				TIndividual indv2 = pop.getIndividual(j);
				int indv2Index = j;
				int compare = fComparator.compare(indv1, indv2);
				if( compare < 0) {
					/** indv1 > indv2 */
					tmpSLT.add(indv2Index);
				}else if(compare > 0) {
					/** indv1 < indv2 */
					gTNum++;
				}
			}
			if(gTNum == 0) {
				/** no indv greater than indv1, put indv1 into top front */
				indv1.setRank(rank);
				firstFront.add(indv1Index);
			}
			sGTNum.add(gTNum);
			sLT.add(tmpSLT);
		}
		sF.add(firstFront);
	}
	
	/**
	 * set indv.Rank in pop and fronts into sF
	 * @param pop
	 * @param sF set of fronts
	 */
	private void setRanks(TPopulation pop, 
			ArrayList<ArrayList<Integer>> sF) {
		int rank = 0;
		/** set for indvs-index less than self */
		ArrayList<ArrayList<Integer>> sLT = new ArrayList<>();
		
		/** set for num of indvs greater than self */
		ArrayList<Integer> sGTNum = new ArrayList<>();

		setFirstRank(pop, sLT, sF, sGTNum, rank);
		while(true) {
			/** known front */
            ArrayList<Integer> nowFront = sF.get(rank);
            /** unknown front */
            ArrayList<Integer> nextFront = new ArrayList<>();
            setOneRank(nowFront, nextFront, sLT, sGTNum, pop, rank + 1);
            int nextFrontSize = nextFront.size();
            if(nextFrontSize == 0) {
            	/** all front is known */
            	break;
            }
            sF.add(nextFront);
            rank++;
		}
	}
	
	/**
	 * set nextRank-front into nextFront and pop.indv.fRank 
	 * @param nowFront
	 * @param nextFront
	 * @param sLT
	 * @param sGTNum
	 * @param pop
	 * @param nextRank
	 */
	private void setOneRank(
			ArrayList<Integer> nowFront, ArrayList<Integer> nextFront,
			ArrayList<ArrayList<Integer>> sLT, ArrayList<Integer> sGTNum,
			TPopulation pop, int nextRank) {

		int frontSize = nowFront.size();
		for(int i = 0; i < frontSize; i++) {
			/** set for indvs less than i-th indv of nowFront */
			int indvIndex = nowFront.get(i);
			ArrayList<Integer> tmpSLT = sLT.get(indvIndex);
			int tmpSLTSize = tmpSLT.size();
			for(int j = 0; j < tmpSLTSize; j++) {
				int lTIndvIndex = tmpSLT.get(j);
				int gTNum = sGTNum.get(lTIndvIndex);
				gTNum --;
				sGTNum.set(lTIndvIndex, gTNum);
				if(gTNum == 0) {
					TIndividual lTIndv = pop.getIndividual(lTIndvIndex);
					lTIndv.setRank(nextRank);
					nextFront.add(lTIndvIndex);
				}
			}
		}
	}
	
	/**
	 * set indv.fCDinstance in pop using fronts set sF
	 * @param pop
	 * @param sF
	 */
	private void setCDistances(TPopulation pop, ArrayList<ArrayList<Integer>> sF) {
		int sFSize = sF.size();
		for(int i = 0; i < sFSize; i++) {
			setCDistance(pop, sF.get(i));
		}
	}
	
	/**
	 * set indv.fCDistance in front
	 * @param pop
	 * @param front
	 */
	private void setCDistance(TPopulation pop, ArrayList<Integer> front) {
		int frontDimension = fFunctions.size();
		int frontSize = front.size();
		for(int i = 0; i < frontDimension; i++) {
			/** for reference from lComparator */
			final int evlIndex = i;

			/** comparator for indv-index using local function of fFunctions */
			Comparator<Integer> lComparator = new Comparator<Integer>(){
				@Override
				public int compare(Integer o1, Integer o2) {
					TCMatrix o1EvlV = pop.getIndividual(o1).getEvaluationVector();
					TCMatrix o2EvlV = pop.getIndividual(o2).getEvaluationVector();
					double eval1 = o1EvlV.getValue(evlIndex);
					double eval2 = o2EvlV.getValue(evlIndex);
					double tmp = eval1 - eval2;
					if(tmp > 0) {
						return 1;
					}else if(tmp < 0) {
						return -1;
					}else {
						return 0;
					}
				}
			};
				
			front.sort(lComparator);
			for(int j = 0; j < frontSize; j++) {
				int frontIndvIndex = front.get(j);
                TIndividual frontIndv = pop.getIndividual(frontIndvIndex);
                double cDistance = Double.MAX_VALUE;
                if(j != 0 && j != frontSize - 1) {
                  /** not start or end */
                    if(evlIndex == 0) {
                      /** no use invalid value */
                      cDistance = 0.0;
                    }else {
                    	/** use valid value from indv's field */
                          cDistance = frontIndv.getCDistance();
                    }

                    int beforeIndvIndex = front.get(j - 1);
                    int afterIndvIndex = front.get(j + 1);
                    TIndividual beforeIndv = pop.getIndividual(beforeIndvIndex);
                    TIndividual afterIndv = pop.getIndividual(afterIndvIndex);

                    double lDistance = Math.abs(beforeIndv.getVector().getValue(evlIndex)
                        - afterIndv.getVector().getValue(evlIndex));
                    /** check overflow */
                    cDistance = cDistance == Double.MAX_VALUE ? cDistance : cDistance + lDistance;
                }
                frontIndv.setCDistance(cDistance);
			}
		}
	}
}