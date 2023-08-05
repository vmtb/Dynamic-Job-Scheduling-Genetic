package project;

import java.util.ArrayList;

public class GreedyAlgo {
	 
	ArrayList<Job> jobs = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;
	double costMatrix [][];
	

	protected ArrayList<ArrayList<Job>> jobClassesJobs = new ArrayList<ArrayList<Job>>();
	protected ArrayList<ArrayList<Worker>> workersClasses = new ArrayList<ArrayList<Worker>>();
	
	public GreedyAlgo(ArrayList<Job> jobs, ArrayList<Worker> workers, double costMatrix [][])  
	{
		this.jobs = jobs; 
		this.workers = workers; 
		this.costMatrix = costMatrix; 
	}
 
	
	public void divideInGroup(int min, int max) {

		this.jobClassesJobs = new ArrayList<ArrayList<Job>>();
		this.workersClasses = new ArrayList<ArrayList<Worker>>();
		  
		this.workersClasses.add(new ArrayList<Worker>());
		this.workersClasses.add(new ArrayList<Worker>());

		this.jobClassesJobs.add(new ArrayList<Job>());
		this.jobClassesJobs.add(new ArrayList<Job>());
		for (int k = 0; k < this.workers.size(); k++) {
			Worker  w = this.workers.get(k);
			if(min <= w.cpuInfo.numberOfCores && w.cpuInfo.numberOfCores<max) {
				this.workersClasses.get(0).add(w);
			} else { 
				this.workersClasses.get(1).add(w);
			}  
		} 

		for (int k = 0; k < this.jobs.size(); k++) {
			Job  jo = this.jobs.get(k);
			if(min <= jo.threadProcessCount && jo.threadProcessCount<max) {
				this.jobClassesJobs.get(0).add(jo);
			} else { 
				this.jobClassesJobs.get(1).add(jo);
			}  
		} 

		System.out.println();
		System.out.println("Pour j(max) = "+max);
	}
	
	
	public Solution greedyIteration() {
		
		double[][]  costMatrixSimul = cloneCostMatrix(); 
		Solution singleSolution = new Solution(this.costMatrix); 
		
		
		for (int k = 0; k < 2; k++) { 

			ArrayList<Worker> wksArrayList = this.workersClasses.get(k);
			wksArrayList = sortWorkers(wksArrayList); 
			ArrayList<Worker> wksArrayListRest = new ArrayList<Worker>();
			
			ArrayList<Job> jbsArrayList = this.jobClassesJobs.get(k); 
			for (int l = 0; l < jbsArrayList.size(); l++) {
				double theta = 0;
				for (int l2 = 0; l2 < wksArrayList.size()-1; l2++) {
					int newIndexL = wksArrayList.get(l2).ID-1;
					int newIndexLplusUN = wksArrayList.get(l2+1).ID-1;
					theta += costMatrixSimul[newIndexL][l]/costMatrixSimul[newIndexLplusUN][l];
				}
				Job job = jbsArrayList.get(l); 
				job.setTheta(theta);
				jbsArrayList.set(l, job);
			}
			
			jbsArrayList = sortJobs(jbsArrayList); 
			for (int l = 0; l < jbsArrayList.size(); l++) {
				Job job = jbsArrayList.get(l);  
				double miniMum = Double.MAX_VALUE; 
				int currentIndex = -1 ;
				int newIndexL = jbsArrayList.get(l).ID-1;
				for (int l2 = 0; l2 < wksArrayList.size(); l2++) {
					int newIndexL2 = wksArrayList.get(l2).ID-1;
					if(miniMum>=costMatrixSimul[newIndexL2][newIndexL] 
							&& 
							wksArrayList.get(l2).getAvailableDiskSize() >= job.getRequiredDiskSizeForExecution() 
							&& 
							wksArrayList.get(l2).getAvailableMemorySize() >= job.getRequiredMemorySizeForExecution() ) {
						miniMum = costMatrixSimul[newIndexL2][newIndexL]; 
						currentIndex = l2; 
					}
				}
				
				System.out.print(job.ID+"-");
				if(currentIndex>=0) {
					Worker worker = wksArrayList.get(currentIndex); 
					job.setAssignedWorker(worker);				
					jbsArrayList.set(l, job);
					singleSolution.addJob(job); 
					
					for (int i = 0; i < costMatrix[0].length; i++) {  
						costMatrixSimul[worker.ID-1][i] = costMatrixSimul[worker.ID-1][i]+costMatrixSimul[worker.ID-1][job.ID-1] ;
					}
					
					
					//wksArrayList.remove(currentIndex); 
					//wksArrayListRest.add(worker);	
					
					//	if(wksArrayList.size()==0) {
					//		wksArrayList.addAll(wksArrayListRest);
					//		wksArrayListRest.clear();
					//		wksArrayList = sortWorkers(wksArrayList);						
					//	}
				} else { 
					System.out.println("Not found "+wksArrayList.size());
					System.out.println("Not found "+wksArrayList.size());
					l--;  
				} 
			}   
		} 
		System.out.println();
		singleSolution.showSolution();
		return singleSolution;  
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


	public ArrayList<Worker> sortWorkers(ArrayList<Worker> worker){ 
		for (int i = 0; i < worker.size(); i++) {
			int minTime = worker.get(i).cpuInfo.getNumberOfCores();
			int pos = i; 
			for (int j = i; j < worker.size(); j++) {
				int processTime = worker.get(j).cpuInfo.getNumberOfCores();
				if(minTime>processTime) {
					minTime = processTime;
					pos = j;
				}
				
			}
			Worker tempWorker = worker.get(i);
			worker.set(i, worker.get(pos));
			worker.set(pos, tempWorker);
		}
		
		return worker;
	}

	public ArrayList<Job> sortJobs(ArrayList<Job> worker){ 
		for (int i = 0; i < worker.size(); i++) {
			double minTime = worker.get(i).getTheta();
			int pos = i; 
			for (int j = i; j < worker.size(); j++) {
				double processTime = worker.get(j).getTheta();
				if(minTime>processTime) {
					minTime = processTime;
					pos = j;
				} 
			}
			Job tempWorker = worker.get(i);
			worker.set(i, worker.get(pos));
			worker.set(pos, tempWorker);
		}
		
		return worker;
	}
	 
}
