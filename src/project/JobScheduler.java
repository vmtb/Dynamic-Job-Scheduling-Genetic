package project;
 
import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random; 


public class JobScheduler implements Runnable {
	ArrayList<Job> jobsQueue = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;
	protected double costMatrix [][]; 
	protected int jobNumber = 0;
	
	

	public JobScheduler(ArrayList<Worker> workers, int jobNumber)  
	{ 
		this.workers = workers; 
		this.jobNumber = jobNumber;
	}
	
	public void addJobToQueue(Job job) {
		jobsQueue.add(job);
	}
	
	
	public double[][] initializeAndGetCostMatrix(ArrayList<Job> jobs, ArrayList<Worker> workrs) {
	 
		int size = jobs.size(); 
		double costMatrix[][] = new double[workrs.size()][size];
		for (int i = 0; i < workrs.size(); i++) {
			Worker w = workrs.get(i);  
			workrs.get(i).setTEMPID(i+1);
			System.out.println("Worker "+i); 
			for (int j = 0; j < size; j++) {  
				//System.out.println("job "+j+" -- "+jobs.size()); 
				costMatrix[i][j] = jobs.get(j).getExecutionTimeByWorker(w);
				jobs.get(j).setTEMPID(j+1);
			}
		}
		return costMatrix;
	}
	
	
	
	public ArrayList<Job> getQueueJobs() {
		return this.jobsQueue;
	}
	
	ArrayList<Worker> getAvailableWorkers(){
		ArrayList<Worker> outsArrayList = new ArrayList<Worker>();
		for (int i = 0; i < this.workers.size(); i++) {
			Worker worker = this.workers.get(i);
			long endTime = worker.getEndingTime();
			System.out.println("Worker "+worker.getID()+ ":"+System.currentTimeMillis()+" - "+endTime);
			if(endTime<System.currentTimeMillis()) {
				outsArrayList.add(worker);
			}
		}
		return outsArrayList;
	}
	
	int treatedJobs = 0;


	@Override
	public void run() { 
		
		while(treatedJobs<this.jobNumber) { //true
			System.out.println("Iteration");
			ArrayList<Job> jobs = this.getQueueJobs();
			ArrayList<Worker> idleWorkers = this.getAvailableWorkers();
			System.out.println("Queue size: "+jobs.size());
			System.out.println("Available workers size: "+idleWorkers.size());
			
			if(!jobs.isEmpty() && !idleWorkers.isEmpty()) {
				/*static part*/
				ArrayList<Job> tempJobs = jobs; //cloneQueue(jobs);
				System.out.println("Job -- bef"+tempJobs.get(0).ID); 
				double[][] costs = initializeAndGetCostMatrix(tempJobs, idleWorkers);  

				System.out.println("Job -- aft (for temp)"+tempJobs.get(0).TEMPID); 
				GeneticAlgo geneticAlgo = new GeneticAlgo(jobs, idleWorkers, costs);
				Solution solution = geneticAlgo.executeAlgo(); 

				/*get assigned et let non assigned part*/
				int[] sols = solution.getSolution(); 
				ArrayList<String> affectedArrayList = new ArrayList<>();
				for (int i = 0; i < sols.length; i++) {
					//Liam Kurt Yonnan//
					Job j = jobs.get(i);
					if(!affectedArrayList.contains(sols[i]+"")) {
						Worker w=null;
						int wkIndexAt = -1;
						for (int c = 0; c <  this.workers.size(); c++) { 
							if(this.workers.get(c).getBase10Name() == sols[i]) {
								w = this.workers.get(c);
								wkIndexAt = c;
							}
						}
						treatedJobs++;
						j.setAssignmentTime(System.currentTimeMillis());
						j.setEndExecutionTime(System.currentTimeMillis()+j.getExecutionTimeByWorker(w)*10); //*1000
						w.setEndingTime(j.getEndExecutionTime());
						this.workers.set(wkIndexAt, w);
						this.jobsQueue.remove(j.TEMPID-1);
						affectedArrayList.add(w.getBase10Name()+"");
					}					
				}
				System.out.println("Queue size at the end: "+this.jobsQueue.size());
				
				System.out.println();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
			
		}
 		
	}
	private ArrayList<Job> cloneQueue(ArrayList<Job> alls) {
		ArrayList<Job> outsArrayList = new ArrayList<Job>();
		for (int i = 0; i < alls.size(); i++) {
			outsArrayList.add(alls.get(i));
		} 
		return outsArrayList;
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


