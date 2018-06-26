package nsga2;

import matrix2017.*;
import random2013.*;
import multiObj.*;

public class TRex {

	/** class use for fRng distribution */
	public enum TProbabilityDistribution{
		NORMAL,
		UNIFORM,
	}

	/** use for weight of each parent */
	private ICRandom fRng;
	/** center of parents */
	private TCMatrix fParentsMean;
	/** probability distribution of fRng */
	private TProbabilityDistribution fRngPD;
	/** parameter for weight of each parent */
	private final double fExpansionRatio;

	/** for calc */
	private TCMatrix fParentTmp;
	private int fDimension;
	
	public TRex(int dimension, int parentsSize, TProbabilityDistribution rngPD) {
		fRngPD = rngPD;
		fDimension = dimension;

		switch(fRngPD) {
          case NORMAL:
            fExpansionRatio = Math.sqrt(1.0 / (double)parentsSize); break;
          case UNIFORM:
            fExpansionRatio = Math.sqrt(3.0 / (double)parentsSize); break;
          default:
            fExpansionRatio = 0;
		}
	}
	
	public void init() {
		fParentsMean = new TCMatrix(fDimension);
		fParentTmp = fParentsMean.clone();
		fParentsMean.fill(0);
		fRng = new TCJava48BitLcg();
	}

	/**
	 * set fParentsMean from parents
	 * @param parents
	 */
	private void  setParentsMean(TPopulation parents) {
		int parentsSize = parents.getPopulationSize();
		fParentsMean.fill(0);
		for(int i = 0; i < parentsSize; i++) {
			fParentsMean.add(parents.getIndividual(i).getVector());
		}
		fParentsMean.div(parentsSize);
	}
	
	/**
	 * make kid indvs and set into kids set
	 * @param parents
	 * @param kids  set kid-indvs here 
	 */
	public void makeOffSpring(TPopulation parents, TPopulation kids) {
		int kidsSize = kids.getPopulationSize();
		int parentsSize = parents.getPopulationSize();

		setParentsMean(parents);

		for(int i = 0; i < kidsSize; i++) {
			TCMatrix kidVector = kids.getIndividual(i).getVector();
			kidVector.fill(0);
			for(int j = 0; j < parentsSize; j++) {
                double weight = getWeight();
                fParentTmp.copyFrom(parents.getIndividual(j).getVector());
                fParentTmp.sub(fParentsMean);
                fParentTmp.times(weight);
    			kidVector.add(fParentTmp);
			}
			kidVector.add(fParentsMean);
		}
	}
	
	/**
	 * @return random weight using fRng
	 */
	private double getWeight() {
		switch(fRngPD) {
          case UNIFORM:
              return fRng.nextDouble(-fExpansionRatio, fExpansionRatio);
          case NORMAL:
              return fRng.nextGaussian(0, fExpansionRatio);
          default:
            return 0;
		}
		
	}

}
