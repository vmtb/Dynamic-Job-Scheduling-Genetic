package project;

import java.util.ArrayList;
import java.util.Random;

/**
 * REDEFINIR LA NOTION DE COUT
 * */

public class Population  implements Cloneable {
	
	protected ArrayList<Solution> solutions = new ArrayList<Solution>();  //Ensemble de solutions
	protected double scores [];
	protected ArrayList<Worker> workers; 
	protected ArrayList<Job> jobs; 
	protected Solution parent1; 
	protected Solution parent2;  
	
	public Population(ArrayList<Worker> workers, ArrayList<Job> jobs) {
		this.workers = workers;
		this.jobs = jobs;
	}
	
	//Gettter et Setter de solutions
	public ArrayList<Solution> getSolutions() {
		return solutions;
	}
	
	public void setSolutions(ArrayList<Solution> solutions) {
		this.solutions = solutions;
	}
	
	
	public void addSolution(Solution solution) { 
		this.solutions.add(solution);
	}

	
    @Override
    public Population clone() {
        try {
            Population clonedPopulation = (Population) super.clone();
            clonedPopulation.solutions = new ArrayList<>(solutions);
            return clonedPopulation;
        } catch (CloneNotSupportedException e) {
            // Gérer l'exception de manière appropriée
            return null;
        }
    }
	  
	 
	 public double[] getScorePopulation() {
		double scores [] = new double[this.solutions.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = this.solutions.get(i).getScore(this.workers);
			//this.solutions.get(i).showSolution();
			System.out.println("Pop. initiale score parent "+(i+1)+": "+scores[i]);
		}
		this.scores = scores;
		return scores;
	 }
	 
	 
	 public void sortScorePopulation() {
		 ArrayList<Solution> solutions = this.solutions; 
		 for (int i = 0; i < this.scores.length; i++) {
			double min = this.scores[i]; 
			int pos = i; 
			for (int j = i; j < this.scores.length; j++) {
				if(this.scores[j]<min) {
					min= this.scores[j]; 
					pos = j; 
				}
			}
			this.scores[pos] = this.scores[i];
			this.scores[i] = min; 
			Solution s = solutions.get(i);
			solutions.set(i, solutions.get(pos)); 
			solutions.set(pos, s);
		}
		this.setSolutions(solutions);
	 }
	 
	 public void makeSelection() {
		 System.out.println("------ result selection ------ ");
		 this.parent1 = this.solutions.get(0).clone();
		 this.parent2 = this.solutions.get(1).clone();
		 System.out.println("Parent 1: "+this.parent1.getScore(this.workers));
		 System.out.println("Parent 2: "+this.parent2.getScore(this.workers));
	 }
	 
	 public void doCrossOver() {  
		int c = (new Random()).nextInt(this.parent1.solution.length);
		System.out.println("------ result crossover ------ ");
		int s1[] = this.parent1.solution.clone(); 
		int s2[] = this.parent2.solution.clone(); 
		for (int i = c; i < this.parent1.solution.length; i++) {

			Worker worker = this.workers.get((new Random()).nextInt(this.workers.size()));
			int val = s1[i];
			s1[i] = s2[i];  //s2[i] ; 
			if(!changeRespectsConstrains(s1)) {
				s1[i] = val;  
			}
			int tempS2 = s2[i];
			s2[i] = val;	
			if(!changeRespectsConstrains(s2)) {	
				s2[i] = tempS2; 
			}
		}
		
		this.parent1.setSolution(s1);
		this.parent2.setSolution(s2); 
		
		System.out.println("Parent 1: "+this.parent1.getScore(this.workers));
		System.out.println("Parent 2: "+this.parent2.getScore(this.workers));
	 }

	 
	 public void doMutation() {  
		System.out.println("------ result mutation ------ ");
		
		int s1[] = this.parent1.getSolution() ; 
		int s2[] = this.parent2.getSolution() ; 
		
		//1st parent
		int m = (new Random()).nextInt(this.parent1.solution.length-1);
		int s1p[] = s1.clone(); 
		int tempS1m = s1[m]; 
		s1p[m] = s1[m+1];
		s1p[m+1] = tempS1m;  
		if(changeRespectsConstrains(s1p)) {
			this.parent1.setSolution(s1p);
		}
		
		
		//2nd parent
		m = (new Random()).nextInt(this.parent1.solution.length-1);
		int s2p[] = s2.clone();
		int tempS2m = s2[m]; 
		s2p[m] = s2[m+1];
		s2p[m+1] = tempS2m;  
		if(changeRespectsConstrains(s2p)) { 
			this.parent2.setSolution(s2p); 
		} 
		
		System.out.println("Parent 1: "+this.parent1.getScore(this.workers));
		System.out.println("Parent 2: "+this.parent2.getScore(this.workers));
	 }
	 
	 
	 
	 
	 
	 /**
	  * Verify whether a solution respects problem's constrains 
	  * @param solutions
	  * @return 
	  */
	 protected boolean changeRespectsConstrains(int[] solutions) { 
		ArrayList<Worker> workers = this.workers; 
	
		for (int jobIndex = 0; jobIndex < solutions.length; jobIndex++) {
			Job job = null; 
			for (int i = 0; i < this.jobs.size(); i++) {
				if(this.jobs.get(i).ID==jobIndex+1) {
					job = this.jobs.get(i);
				}
			} 
			Worker worker = null; 
			int workerIndex = -1;
			for (int j = 0; j < workers.size(); j++) { 
				if(workers.get(j).getBase10Name()==solutions[jobIndex]) {  
					worker = workers.get(j);  
					workerIndex = j; 
					break;  
				 }  
			} 
			
			if(worker.getAvailableDiskSize() >= job.getRequiredDiskSizeForExecution() && 
				worker.getAvailableMemorySize() >= job.getRequiredMemorySizeForExecution()) {
				
 				//worker.setAvailableMemorySize(worker.getAvailableMemorySize() - job.getRequiredMemorySizeForExecution());
 				//worker.setAvailableDiskSize(worker.getAvailableMemorySize()-job.getRequiredMemorySizeForExecution());
				//workers.set(workerIndex, worker);
			}else {
				return false;
			}
			
		} 
		
		return true;
	}
	
	
}
