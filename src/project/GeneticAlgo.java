package project;

public class GeneticAlgo implements Runnable{

	protected long executionTimeGreedy = 0; 
	protected long executionTimePlanification = 0; 
	protected long executionTimeExecution = 0; 
	
	
	
	@Override
	public void run() {
	}
	
	public void start() {                          
	}
	 
	public Population getPopulationInitial() {  
		/**  **/
        long startTime = System.currentTimeMillis();
		Population population = new Population(this.workers, this.jobs);
		int[] divs = new int[] {5, 8, 14, 17, 22, 28}; 
		int lastLimit = 0; 
		for (int i = 0; i < divs.length; i++) {  
			
			GreedyAlgo greedyAlgo = new GreedyAlgo(this.jobs, this.workers, this.costMatrix);
			                                                                  
			greedyAlgo.divideInGroup(lastLimit, divs[i]);
			
			Solution singleSolution = greedyAlgo.greedyIteration(); 
			//singleSolution.showSolution();
			
			if(singleSolution.jobs.size()==this.jobs.size()) {
				population.addSolution(singleSolution);
			} 
			
			lastLimit =  divs[i]-2;   
		}
		
        long endTime = System.currentTimeMillis();
        this.setExecutionTimeGreedy(endTime-startTime);
		
		return population; 
	}
	
	/** 
	 * @param p
	 */
	public Solution startGeneticAlg(Population p, int nbStopCriteria) {
        long startTime = System.currentTimeMillis();
		System.out.println("Taille de la population initiale: "+p.solutions.size());
		int conv = 0;
		Solution solutionFinal = null; 
		while(conv<nbStopCriteria) {
			//calculate score//  
			double scores [] = p.getScorePopulation();
			
			//Sort scores from the lowest to the highest
			p.sortScorePopulation();
			Population population = p.clone(); 
			
			//Make selection
			p.makeSelection(); 
			
			// Crossover
			p.doCrossOver(); 

			// Mutation
			//1st parent 
			p.doMutation();
			
			// Check score
			Solution p1p = p.parent1;
			Solution p2p = p.parent2;
			double score1 = p1p.getScore(this.workers);
			double score2 = p2p.getScore(this.workers);
			double scoreP1 = scores[0]; 
			double scoreP2 = scores[1]; 
			boolean oneSolution = false;  
			if((score1<scoreP1 || score1<scoreP2) &&  score1!=scoreP1 && score1 !=scoreP2) {
				System.out.println("add enfant 1 ");
				population.getSolutions().remove(population.getSolutions().size()-1);
				population.addSolution(p1p);
				oneSolution = true;
			}
			
			
			if((score2<scoreP1 || score2<scoreP2) && (score2!=scoreP1 && score2 !=scoreP2)) {
				System.out.println("add enfant 2 ");
				// Si le dernier parent était déjà enlevé //
				if(oneSolution) {   
					population.getSolutions().remove(population.getSolutions().size()-2);  
				}else {  
					population.getSolutions().remove(population.getSolutions().size()-1); 
				} 
				population.addSolution(p2p); 
				oneSolution = true;  
			} 
			
			if(!oneSolution) { 
				conv++;  
			}else { 
				conv=0; 
			}
			
			population.sortScorePopulation();
			p = population.clone();
			solutionFinal = population.getSolutions().get(0);

			 System.out.println("------ result process ------ ");

			System.out.println("Score parent 1: "+scoreP1);
			System.out.println("Score parent 2: "+scoreP2);
			System.out.println("Score Enfant 1: "+score1);
			System.out.println("Score Enfant 2: "+score2);
			System.out.println();
		}
        long endTime = System.currentTimeMillis();
        this.setExecutionTimePlanification(endTime-startTime);
        
        return solutionFinal;
	}



	public long getExecutionTimeGreedy() {
		return executionTimeGreedy;
	}

	public long getExecutionTimePlanification() {
		return executionTimePlanification;
	}

	public long getExecutionTimeExecution() {
		return executionTimeExecution;
	}

	public void setExecutionTimeGreedy(long executionTimeGreedy) {
		this.executionTimeGreedy = executionTimeGreedy;
	}

	public void setExecutionTimePlanification(long executionTimePlanification) {
		this.executionTimePlanification = executionTimePlanification;
	}

	public void setExecutionTimeExecution(long executionTimeExecution) {
		this.executionTimeExecution = executionTimeExecution;
	}
	
}
