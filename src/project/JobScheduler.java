package project;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random; 


public class JobScheduler {
	ArrayList<Job> jobs = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;
	protected double costMatrix [][]; 
	protected long executionTimeGreedy = 0; 
	protected long executionTimePlanification = 0; 
	protected long executionTimeExecution = 0; 
	
	

	public JobScheduler(ArrayList<Job> jobs, ArrayList<Worker> workers)  
	{
		this.jobs = jobs; 
		this.workers = workers; 
		this.costMatrix = new double[workers.size()][jobs.size()];
		initializeCostMatrix(); 
		 
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



	public void initializeCostMatrix() {
		for (int i = 0; i < this.workers.size(); i++) {
			Worker w = this.workers.get(i); 
			System.out.println(); 
			for (int j = 0; j < this.jobs.size(); j++) {  
				this.costMatrix[i][j] = this.jobs.get(j).getStandardProcessingDurations().
						get(w.getCpuInfo().getFamilyName() + "-" + w.getCpuInfo().getDenomination() + "-" + w.getCpuInfo().getNumberOfCores());
				//System.out.print(this.costMatrix[i][j]); 
				//System.out.print("-"); 
			}
		}
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

//Job 1 =====>  Worker 5  ====> (2169.0)
//Job 2 =====>  Worker 4  ====> (833.0)
//Job 3 =====>  Worker 4  ====> (765.0)
//Job 4 =====>  Worker 4  ====> (337.0)
//Job 5 =====>  Worker 3  ====> (427.0)
//Job 6 =====>  Worker 3  ====> (803.0)
//Job 7 =====>  Worker 3  ====> (341.0)
//Job 8 =====>  Worker 3  ====> (324.0)
//Job 9 =====>  Worker 2  ====> (885.0) 

//Execution time of Greedys: 4 milliseconds
//Execution time of Planification: 122 milliseconds
//MakeSpan 2169.0' ==> 00:36:09 milliseconds
//2309 soit 6%

//Job 1 =====>  Worker 5  ====> (2169.0)
//Job 2 =====>  Worker 4  ====> (833.0)
//Job 3 =====>  Worker 5  ====> (675.0)
//Job 4 =====>  Worker 1  ====> (1063.0)
//Job 5 =====>  Worker 2  ====> (1342.0)
//Job 6 =====>  Worker 4  ====> (479.0)
//Job 7 =====>  Worker 2  ====> (694.0)
//Job 8 =====>  Worker 2  ====> (518.0)
//Job 9 =====>  Worker 1  ====> (1520.0)
//Job 10 =====>  Worker 3  ====> (3261.0)
//Job 11 =====>  Worker 4  ====> (833.0)
//Job 12 =====>  Worker 4  ====> (765.0)
//Job 13 =====>  Worker 5  ====> (289.0)
//Job 14 =====>  Worker 5  ====> (287.0)
//Job 15 =====>  Worker 4  ====> (479.0)
//Job 16 =====>  Worker 5  ====> (255.0)
//Job 17 =====>  Worker 4  ====> (269.0)
//Job 18 =====>  Worker 2  ====> (885.0) 

//Execution time of Greedys: 7 milliseconds
//Execution time of Planification: 157 milliseconds
//MakeSpan 3675.0' ==> 01:01:15 milliseconds
//4150 soit 11.45%


//Job 1 =====>  Worker 5  ====> (2169.0)
//Job 2 =====>  Worker 5  ====> (790.0)
//Job 3 =====>  Worker 4  ====> (765.0)
//Job 4 =====>  Worker 5  ====> (289.0)
//Job 5 =====>  Worker 5  ====> (287.0)
//Job 6 =====>  Worker 4  ====> (479.0)
//Job 7 =====>  Worker 5  ====> (255.0)
//Job 8 =====>  Worker 2  ====> (518.0)
//Job 9 =====>  Worker 5  ====> (475.0)
//Job 10 =====>  Worker 3  ====> (3261.0)
//Job 11 =====>  Worker 4  ====> (833.0)
//Job 12 =====>  Worker 4  ====> (765.0)
//Job 13 =====>  Worker 3  ====> (423.0)
//Job 14 =====>  Worker 5  ====> (287.0)
//Job 15 =====>  Worker 4  ====> (479.0)
//Job 16 =====>  Worker 4  ====> (293.0)
//Job 17 =====>  Worker 1  ====> (771.0)
//Job 18 =====>  Worker 1  ====> (1520.0)
//Job 19 =====>  Worker 2  ====> (4004.0)
//Job 20 =====>  Worker 3  ====> (1227.0)
//Job 21 =====>  Worker 4  ====> (765.0)
//Job 22 =====>  Worker 4  ====> (337.0)
//Job 23 =====>  Worker 4  ====> (324.0)
//Job 24 =====>  Worker 5  ====> (414.0)
//Job 25 =====>  Worker 1  ====> (1029.0)
//Job 26 =====>  Worker 2  ====> (518.0)
//Job 27 =====>  Worker 1  ====> (1520.0)

//Execution time of Greedys: 7 milliseconds
//Execution time of Planification: 207 milliseconds
//MakeSpan 5040.0' ==> 01:24:00 milliseconds
//6394 soit 21.18%


