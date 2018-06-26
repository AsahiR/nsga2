package nsga2;

import matrix2017.*;
import random2013.*;
import java.util.Comparator;
import multiObj.*;

public class TNSGA2 {
	private TRex fRex;
	private TPopulation fPopulation;
	private TPopulation fKids;
	private TPopulation fParents;
	private int fGeneration;
	private int fFrontDimension;
	private TEvaluator fEvaluator;
	final private double fInitMax;
	final private double fInitMin;
	
	final private Comparator<TIndividual> fComparator;
	
	private int fEvaluationNum;
	
	/** for calc */
	final private int fPopulationSize;
	final private int fKidsSize;
	final private int fParentsSize;
	final private int fDimension;
	final private int fTournamentSize;
	private TPopulation fWorkSpace;
	private TPopulation fTournament;
	private ICRandom fParentsRng;

	public TNSGA2(double initMax, double initMin, int populationSize, int parentsSize, int kidsSize, int dimension,
			Comparator<TIndividual> comparator, TEvaluator evaluator, int frontDimension,
			int tournamentSize, TRex rex) {
		fInitMax = initMax;
		fInitMin = initMin;
		fPopulationSize = populationSize;
		fParentsSize = parentsSize;
		fKidsSize = kidsSize;
		fDimension = dimension;
		fComparator = comparator;
		fEvaluator = evaluator;
		fFrontDimension = frontDimension;
		fTournamentSize = tournamentSize;
		fRex = rex;
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
		fWorkSpace = new TPopulation(fPopulationSize + fKidsSize);
		fPopulation = new TPopulation(fPopulationSize);
		fKids = new TPopulation(fKidsSize);
		fParents = new TPopulation(fParentsSize);
		fTournament = new TPopulation(fTournamentSize);
		initPopulation(fInitMin, fInitMax, fDimension, fPopulation);
		initPopulation(fInitMin, fInitMax, fDimension, fKids);
		initPopulation(fInitMin, fInitMax, fDimension, fParents);
		initPopulation(fInitMin, fInitMax, fDimension, fWorkSpace);
		initPopulation(fInitMin, fInitMax, fDimension, fTournament);
		fEvaluationNum = 0;
		fParentsRng = new TCJava48BitLcg();
		fGeneration = 0;
		fRex.init();
	}
	
	public void execute() {
		selectParents();
		makeOffSpring();
		selectPopulation();
		fGeneration++;
		fEvaluationNum++;
	}
	
	private void evaluatePopulation(TPopulation population){
		fEvaluator.evaluate(population);
	}
	
	public void makeOffSpring() {
		fRex.makeOffSpring(fParents, fKids);
	}
	
	public void selectParents() {
		evaluatePopulation(fPopulation);
		for(int i = 0; i < fParentsSize; i++) {
			TIndividual parent = tournamentSelect(fPopulation, fTournamentSize, fTournament);
			fParents.getIndividual(i).copyFrom(parent);
		}
	}
	
	private TIndividual tournamentSelect(TPopulation pop, int tournamentSize, TPopulation tournament) {
		int populationSizeTmp = pop.getPopulationSize();
		for (int i = 0; i < tournamentSize; i++) {
			int index;
			index = fParentsRng.nextInt(populationSizeTmp);
			tournament.getIndividual(i).copyFrom(pop.getIndividual(index));
			/** swap index and populationSizeTmp - 1 */
			pop.getIndividual(index).copyFrom(pop.getIndividual(populationSizeTmp - 1));
			pop.getIndividual(populationSizeTmp - 1).copyFrom(tournament.getIndividual(i));
			populationSizeTmp --;
		}
		tournament.sort(fComparator);
		return tournament.getIndividual(0);
	}
	
	private void selectPopulation() {
		fWorkSpace.writeIndividuals(0, fPopulationSize, fPopulation);
		fWorkSpace.writeIndividuals(fPopulationSize - 1, fKidsSize, fKids);
		evaluatePopulation(fWorkSpace);
		fWorkSpace.sort(fComparator);
		fPopulation.writeIndividuals(0, fPopulationSize, fWorkSpace);
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
	
	public String makeName() {
		StringBuilder sb = new StringBuilder();
		sb.append("TNSGA2");
		sb.append(fEvaluator.getName());
		return sb.toString();
	}
}
