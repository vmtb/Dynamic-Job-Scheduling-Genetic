package project;

import java.util.ArrayList;
import java.util.Iterator;

public class Solution implements Cloneable{
	ArrayList<Job> jobs = new ArrayList<Job>();
	double costMatrix [][];
	int solution [];
	int NJ = 0; 
	int NW = 0;
	//une solution ==> [16, 8, 8, 2, 4, 2, 4, 16]

	
	public Solution(double[][] costMatrix) { 
		this.costMatrix = costMatrix;
		this.NJ = this.costMatrix[0].length; 
		this.NW = this.costMatrix.length;
		this.solution = new int[NJ]; //nbre de jobs 
	}

	protected void reconstituteSolution() {
		for (int i = 0; i < this.jobs.size(); i++) {
			Job job = this.jobs.get(i); 
			int jobIndex = job.ID-1; 
			int workerIndex = job.assignedWorker.ID-1; 
			int val = job.assignedWorker.getBase10Name();   
			this.solution[job.ID-1] = val;
		}
	}
	 
	
	public void addJob(Job j) {
		if(!this.jobs.contains(j)) { 
			this.jobs.add(j);
			reconstituteSolution();
		}
	}
	
	public void showSolution() {
		
		for (int i = 0; i < this.jobs.size(); i++) {
			Job job = this.jobs.get(i); 
			
			int jobIndex = job.ID-1; 
			int workerIndex = job.assignedWorker.ID-1; 
			int val = job.assignedWorker.getBase10Name(); 
			
			System.out.println("Job "+(job.ID)+" =====>  Worker "+job.assignedWorker.ID+" ("+val+")  ====> ("+this.costMatrix[job.assignedWorker.ID-1][job.ID-1]+")"); 
		}
	}

	public void showSolution2(ArrayList<Worker> workers) {

		System.out.println();
		for (int i = 0; i < this.solution.length; i++) {
			Job job = null;
			for (int j = 0; j < this.jobs.size(); j++) {
				if(this.jobs.get(j).ID==i+1) {
					job = this.jobs.get(j);
					break;
				}
			}
			int workerVal = this.solution[i]; 
			Worker worker = null; 
			for (int j = 0; j < workers.size(); j++) {
				if(workers.get(j).getBase10Name()==workerVal) {
					worker = workers.get(j);
					break;
				}
			} 
			
			job.setAssignedWorker(worker);
			System.out.print("// Job "+(job.ID)+" =====>  Worker "+job.assignedWorker.ID+"  ====> ("+this.costMatrix[job.assignedWorker.ID-1][job.ID-1]+")");
			System.out.println();  
		}
	}
	
	public double getScore(ArrayList<Worker> workers) {
		double[][] costs = cloneCostMatrix();
		double score = 0; //this.costMatrix// 
		double [] ssworkers = new double [workers.size()];  
		
		for (int i = 0; i < workers.size(); i++) {
			int tempGene = 0;  
			for (int k = 0; k < this.solution.length; k++) {
				int workerVal = this.solution[k];  
				if(workers.get(i).getBase10Name()==workerVal) {
					tempGene+=costs[workers.get(i).ID-1][k]; 
				}  
			} 
			ssworkers[i] = tempGene;  
		} 
		 
		double max = ssworkers[0]; 
		for (int i = 0; i < ssworkers.length; i++) { 
			// System.out.print(ssworkers[i]+" -" ); 
			if(ssworkers[i]>max) { 
				max = ssworkers[i]; 
			} 
		}   
		score = max;  
		
//		for (int i = 0; i < this.solution.length; i++) {
//			int workerVal = this.solution[i]; 
//			Worker worker = null; 
//			for (int j = 0; j < workers.size(); j++) {
//				if(workers.get(j).getBase10Name()==workerVal) {
//					worker = workers.get(j);
//					break;
//				}
//			} 
//			if(worker!=null) {
//				score += this.costMatrix[worker.ID-1][i];
//			} 
//		}
//		
		return score;
	}
	

	private double[][] cloneCostMatrix() {
		double[][] costMatrixHere =new  double[costMatrix.length][costMatrix[0].length];
		for (int i = 0; i < costMatrix.length; i++) {
			for (int j = 0; j < costMatrix[0].length; j++) {
				costMatrixHere[i][j] = costMatrix[i][j];
			}
		} 
		return costMatrixHere;
	}
	
    @Override
    public Solution clone() {
        try {
        	Solution clonedPopulation = (Solution) super.clone();
            clonedPopulation.solution = this.solution;
            return clonedPopulation;
        } catch (CloneNotSupportedException e) {
            // Gérer l'exception de manière appropriée
            return null;
        }
    }
    
    
	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}

	public int[] getSolution() {
		return solution;
	}

	public void setSolution(int[] solution) {
		this.solution = solution;
	}
	
	
	
}
