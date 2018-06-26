package nsga2;
import java.util.ArrayList;
import java.util.function.Function;
import myUtil.MyMatrixUtil;
import matrix2017.*;
import java.util.Random;
import random2013.*;
import java.util.Comparator;
import multiObj.*;

public class TJgg {
	private TRex fRex;
	private TPopulation fPopulation;
	private TPopulation fKids;
	private TPopulation fParents;
	private int fGeneration;
	private Random fParentsRng;
	private TEvaluator fEvaluator;
	final private int fInitMax;
	final private int fInitMin;
	
	final private ArrayList<Function<TCMatrix, Double>> fFunctions; 
	final private Comparator<TIndividual> fComparator;
	
	private int fEvaluationNum;
	
	/** for calc */
	final private int fPopulationSize;
	final private int fKidsSize;
	final private int fParentsSize;
	final private int fDimension;
	
	public TJgg(int initMax, int initMin, int populationSize, int parentsSize, int kidsSize, int dimension,
			ArrayList<Function<TCMatrix, Double>> functions, TRankAndCDComparator comparator,
			TEvaluator evaluator) {
		fInitMax = initMax;
		fInitMin = initMin;
		fPopulationSize = populationSize;
		fParentsSize = parentsSize;
		fKidsSize = kidsSize;
		fDimension = dimension;
		fFunctions = functions;
		fComparator = comparator;
		fEvaluator = evaluator;
	}

	private void initPopulation(double initMin, double initMax, int dimension,
			TPopulation population) {
		ICRandom initRng = new TCJava48BitLcg();
		int populationSize = population.getPopulationSize();
		for(int i = 0; i < populationSize; i++) {
			TCMatrix vector = new TCMatrix(dimension);
			TIndividual indv = population.getIndividual(i);
			for(int j = 0; j < dimension; j++) {
                double element = initRng.nextDouble(initMin, initMax);
    			vector.setValue(j, element);
			}
			indv.setVector(vector);
		}
	}
	
	public void init() {
		fPopulation = new TPopulation(fPopulationSize);
		fKids = new TPopulation(fKidsSize);
		fParents = new TPopulation(fParentsSize);
		initPopulation(fInitMin, fInitMax, fDimension, fPopulation);
		fEvaluationNum = 0;
		fGeneration = 0;
	}
	
	public void evalute() {
		evaluatePopulation(fPopulation);
	}
	
	private void evaluatePopulation(TPopulation population){
		fEvaluator.evaluate(population);
	}
	
	public void makeOffSpring() {
		fRex.makeOffSpring(fPopulation, fKids);
	}
	
	public void selectParents() {
		int populationSize = fPopulationSize;
		for (int i = 0; i < fParentsSize; i++) {
			int index;
			index = fParentsRng.nextInt(populationSize);
			fParents.getIndividual(i).copyFrom(fPopulation.getIndividual(index));
			fPopulation.getIndividual(index).copyFrom(fPopulation.getIndividual(populationSize - 1));
			populationSize--;
		}
	}
	
	public void selectPopulation() {
		evaluatePopulation(fKids);
		fKids.sort(fComparator);
		int kidsIndex = 0;
		for(int i = fPopulationSize - fParentsSize; i < fPopulationSize; i++) {
			fPopulation.getIndividual(i).copyFrom(fKids.getIndividual(kidsIndex));
			kidsIndex++;
		}
	}
	
	public TPopulation getPopulation() {
		return fPopulation;
	}
	
	public int getEvaluationNum() {
		return fEvaluationNum;
	}
	
	public int getGeneration() {
		return fGeneration;
	}
	
	public void setGeneration(int generation) {
		fGeneration = generation;
	}
	
	public void setEvaluationNum(int evaluationNum) {
		fEvaluationNum = evaluationNum;
	}
}
