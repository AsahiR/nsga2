package nsga2;

import java.io.PrintWriter;
import multiObj.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import nsga2.TRex.TProbabilityDistribution;
import matrix2017.*;
import myUtil.*;

public class Main {
	private TNSGA2 fNSGA2;
	private int fMaxEvaluationNum;
	private int fTrialNum;
	private String fPopulationLogName;
	private String fPopulationLogDirName = "popLog/";
	
	Main(TNSGA2 nSGA2, int maxEvaluationNum, int trialNum){
		fNSGA2 = nSGA2;
		fMaxEvaluationNum = maxEvaluationNum;
		fTrialNum = trialNum;
		fPopulationLogName = fPopulationLogDirName + getName();
	}
	
	public String getName() {
		StringBuilder sb = new StringBuilder();
		sb.append(fNSGA2.makeName());
		sb.append("_trial");
		sb.append(fTrialNum);
		return sb.toString();
	}
	
	/**
	 *  execute one trial and log population in last generation 
	 */
	public void execute() {
        int evaluationNum = 0;
        fNSGA2.init();
        while(evaluationNum < fMaxEvaluationNum ) {
        	fNSGA2.execute();
        	evaluationNum = fNSGA2.getEvaluationNum();
        }
        TPopulation pop = fNSGA2.getPopulation();
        try(PrintWriter pw = new PrintWriter(fPopulationLogName + "_Evl" + evaluationNum)){
            pop.writeTo(pw);
            System.out.println(pop);
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	public static void main(String argv[]) {
		int maxEvaluationNum = (int)1e5;

		ArrayList<TFunction> functions = new ArrayList<>();
		functions.add(new TFunction1());
		functions.add(new TFunction2());

		Comparator<TIndividual> sComparator = new TSComparator(); 
		int frontDimension = 2;
		TEvaluator evaluator = new TNSEvaluator(functions, sComparator);
		int dimension = 2;
		double initMax = 5.0;
		double initMin = -5.0;
		int populationSize = 10;
		int kidsSize = populationSize / 2;
		int parentsSize = populationSize / 3;
		Comparator<TIndividual> comparator = new TRankAndCDComparator(); 
		int tournamentSize = parentsSize;

		TRex  rex = new TRex(dimension, parentsSize, TProbabilityDistribution.NORMAL);
		TNSGA2 nSGA2 = new TNSGA2(initMax, initMin, populationSize, parentsSize, kidsSize, dimension,
				comparator, evaluator, frontDimension, tournamentSize, rex);
		int maxTrialNum = 1;
		for(int i = 0; i < maxTrialNum; i++) {
			Main trial = new Main(nSGA2, maxEvaluationNum, i);
			trial.execute();
		}
	}

}
