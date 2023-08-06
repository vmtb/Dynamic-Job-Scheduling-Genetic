package project;

import java.util.HashMap;

public class Job implements Runnable  /* this class represents a UPC job */
{
	protected int ID = 0;  /* ID used to uniquely identify this job */
	protected int TEMPID = 0;  /* ID used to uniquely identify this job */
	protected int threadProcessCount = 1;  /* the number of threads or processes this job uses during its execution */
	protected Worker assignedWorker = null;  /* this job's assigned worker */
	protected HashMap<String, Long> standardProcessingDurations = new HashMap<String, Long>();  /* this job's standard processing durations on different types of CPU */
	protected double inducedCPUUsageIncreasePercentage = 0.0;  /* the CPU usage increase induced by the execution of this job on its assigned worker PC */
	protected double requiredMemorySizeForExecution = 0.0;  /* this job's required memory size to be processed */
	protected double requiredDiskSizeForExecution = 0.0;  /* this job's required disk size to be processed */
	protected double dockerFileSize = 0.0;  /* the size of the Docker file generated for this job */
	protected double dockerFileGenerationDurationOnMasterPC = 0.0;  /* the amount of time it takes to the master PC to generate this job's corresponding Docker file  */
	protected double estimatedResultFileSize = 0.0;  /* the size of the file resulting from this job's processing */
	protected double jobCurrentCPUTime = 0.0;  /* this job's current CPU time including execution time, transfer time, result transmission time, etc */
	protected long arrivalTime = 0;  /* the time this job has been submitted to the master PC (initially set to master PC's system time) */
	protected long assignmentTime = 0;  /* the time this job has been assigned to a worker PC */
	protected long endExecutionTime = 0;  /* the time this job has been assigned to a worker PC */
	protected boolean currentlyAssignedToWorker = false;  /* whether this job is currently assigned to a worker or not */
	protected boolean finishedBeingProcessedOnAssignedWorker = false;  /* whether this job has finished being processed on its assigned worker or not */
	protected boolean currentlyBeingProcessedOnAssignedWorker = false;  /* whether this job is being processed on its assigned worker or not */
	protected String name = "";  /* name of this job */
	protected double theta = 0;
	
	public Job(int ID, HashMap<String, Long> standardProcessingDurations, double requiredMemorySizeForExecution, double requiredDiskSizeForExecution, Worker assignedWorker, double jobCurrentCPUTime,
			double dockerFileSize, long assignmentTime, double estimatedResultFileSize, double dockerFileGenerationDurationOnMasterPC, boolean currentlyBeingProcessedOnAssignedWorker,
			int threadProcessCount, boolean currentlyAssignedToWorker, boolean finishedBeingProcessedOnAssignedWorker, String name, double inducedCPUUsageIncreasePercentage)  /* this constructor creates and 
			initializes a new job with the given parameters */
	{
		this.ID = ID;
		this.TEMPID = ID;
		this.standardProcessingDurations = standardProcessingDurations;
		this.requiredMemorySizeForExecution = requiredMemorySizeForExecution;
		this.requiredDiskSizeForExecution = requiredDiskSizeForExecution;
		this.dockerFileSize = dockerFileSize;
		this.assignmentTime = assignmentTime;
		this.endExecutionTime = 0;
		this.threadProcessCount = threadProcessCount;
		this.estimatedResultFileSize = estimatedResultFileSize;
		this.dockerFileGenerationDurationOnMasterPC = dockerFileGenerationDurationOnMasterPC;
		this.assignedWorker = assignedWorker;
		this.currentlyAssignedToWorker = currentlyAssignedToWorker;
		this.finishedBeingProcessedOnAssignedWorker = finishedBeingProcessedOnAssignedWorker;
		this.name = name;
		this.currentlyBeingProcessedOnAssignedWorker = currentlyBeingProcessedOnAssignedWorker;
		this.inducedCPUUsageIncreasePercentage = inducedCPUUsageIncreasePercentage;
		this.jobCurrentCPUTime = jobCurrentCPUTime;
	}

	public Job()  /* this constructor creates a new empty job */
	{
		
	}
	 

	public long getEndExecutionTime() {
		return endExecutionTime;
	}

	public void setEndExecutionTime(long endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}

	public int getTEMPID() {
		return TEMPID;
	}

	public void setTEMPID(int tEMPID) {
		TEMPID = tEMPID;
	}

	public long getExecutionTimeByWorker(Worker w) {
		return this.getStandardProcessingDurations().
				get(w.getCpuInfo().getFamilyName() + "-" + w.getCpuInfo().getDenomination() + "-" + w.getCpuInfo().getNumberOfCores());
	}
	
	public Job duplicate(Job job)  /* creates a new copy of the passed in job */
	{
		if (job != null)
		{
			return new Job(
					job.getID(),
					job.getStandardProcessingDurations(),
					job.getRequiredMemorySizeForExecution(),
					job.getRequiredDiskSizeForExecution(),
					new Worker().duplicate(job.getAssignedWorker()),
					job.getJobCurrentCPUTime(),
					job.getDockerFileSize(),
					job.getAssignmentTime(),
					job.getEstimatedResultFileSize(),
					job.getDockerFileGenerationDurationOnMasterPC(),
					job.getCurrentlyBeingProcessedOnAssignedWorker(),
					job.getThreadProcessCount(),
					job.getCurrentlyAssignedToWorker(),
					job.getFinishedBeingProcessedOnAssignedWorker(),
					job.getName(),
					job.getInducedCPUUsageIncreasePercentage()
				);
		}
		
		return null;
	}
	
	public void run()  /* runs this job */
	{
		try
		{
			this.currentlyBeingProcessedOnAssignedWorker = true;  /* we specify that this job is currently being processed on its assigned worker */
			System.out.println(this.assignedWorker.getName() + "---<< " + this.getName() + " <<---");
			Thread.sleep(Long.valueOf(1 * this.getStandardProcessingDurations().get(this.getAssignedWorker().getCpuInfo().getFamilyName() + "-" + this.getAssignedWorker().getCpuInfo().getDenomination() + "-" + this.getAssignedWorker().getCpuInfo().getNumberOfCores())));  /* we create a new thread and makes it sleep for the amount of time required to process this job on its assigned worker PC */
			this.finishedBeingProcessedOnAssignedWorker = true;  /* we specify that this job has finished being processed */
			this.currentlyBeingProcessedOnAssignedWorker = false;  /* we specify that this job is no more being processed on its assigned worker */
			System.out.println(this.assignedWorker.getName() + "--->> " + this.getName() + " >>---");
			this.assignedWorker.assignedJobs.remove(this);  /* we remove this job from the list of assigned jobs of its assigned worker */
			this.assignedWorker.setAvailableDiskSize(this.assignedWorker.getAvailableDiskSize() + this.getRequiredDiskSizeForExecution());  /* we set the assigned worker available disk size accordingly */
			this.assignedWorker.setAvailableMemorySize(this.assignedWorker.getAvailableMemorySize() + this.getRequiredMemorySizeForExecution());  /* we set the assigned worker available memory size accordingly */
			this.assignedWorker.setCPUUsageInPercentage(this.assignedWorker.getCPUUsageInPercentage() - this.inducedCPUUsageIncreasePercentage);  /* we set the assigned worker CPU usage accordingly  */
			this.currentlyAssignedToWorker = false;  /* we specify that this job is no more assigned to its worker */
			this.assignedWorker = null;  /* we reset this job's assigned worker */
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void print()  /* displays this job's attributes values */
	{
		System.out.println("++++++++++ Job Details ++++++++++");
		System.out.println("ID: " + this.ID);
		System.out.println("Name: " + this.name);
		System.out.println("Standard Processing Duration On PC1: " + this.standardProcessingDurations.get("core-i3-4"));
		System.out.println("Standard Processing Duration On PC2: " + this.standardProcessingDurations.get("core-i5-4"));
		System.out.println("Standard Processing Duration On PC3: " + this.standardProcessingDurations.get("core-i7-8"));
		System.out.println("Standard Processing Duration On PC4: " + this.standardProcessingDurations.get("core-i9-16"));
		System.out.println("Standard Processing Duration On PC5: " + this.standardProcessingDurations.get("core-i9-20"));
		System.out.println("Required Memory Size For Execution: " + this.requiredMemorySizeForExecution);
		System.out.println("Required Disk Size For Execution: " + this.requiredDiskSizeForExecution);
		System.out.println("Docker File Size: " + this.dockerFileSize);
		System.out.println("Arrival Time: " + this.arrivalTime);
		System.out.println("Thread Process Count: " + this.threadProcessCount);
		System.out.println("Estimated Result File Size: " + this.estimatedResultFileSize);
		System.out.println("Docker File Generation Duration On Master PC: " + this.dockerFileGenerationDurationOnMasterPC);
		System.out.println("Current CPU Time: " + this.jobCurrentCPUTime);
		System.out.println("Currently Assigned To Worker: " + this.currentlyAssignedToWorker);
		System.out.println("Currently Being Processed On Assigned Worker: " + this.currentlyBeingProcessedOnAssignedWorker);
		System.out.println("Finished Being Processed On Assigned Worker: " + this.finishedBeingProcessedOnAssignedWorker);
		
		if (this.assignedWorker != null)
		{
			System.out.println("Assigned Worker: ");this.assignedWorker.print();
		}
		
		System.out.println("++++++++++ End ++++++++++\n\n");
	}
	
	public int getID()  /* returns this job's ID */
	{
		return this.ID;
	}

	public void setID(int iD)  /* sets this job's ID */
	{
		this.ID = iD;
	}

	public String getName()  /* returns this job's name */
	{
		return this.name;
	}

	public void setName(String name)  /* sets this job's name */
	{
		this.name = name;
	}

	public Worker getAssignedWorker()  /* returns the worker in charge of processing this job */
	{
		return this.assignedWorker;
	}
	
	public void setAssignedWorker(Worker assignedWorker)  /* sets the worker in charge of processing this job */
	{
		this.assignedWorker = assignedWorker;
	}
	
	public int getThreadProcessCount()  /* returns the number of threads or processes this job uses during its execution */
	{
		return this.threadProcessCount;
	}
	
	public void setThreadProcessCount(int threadProcessCount)  /* sets the number of threads or processes this job uses during its execution */
	{
		this.threadProcessCount = threadProcessCount;
	}
	
	public HashMap<String, Long> getStandardProcessingDurations()  /* returns this job's standard processing durations on different types of CPU */
	{
		return this.standardProcessingDurations;
	}

	public void setStandardCPUProcessingDurations(HashMap<String, Long> standardCPUProcessingDurations)  /* sets this job's standard processing duration on 
	different types of CPU */
	{
		this.standardProcessingDurations = standardCPUProcessingDurations;
	}

	public double getRequiredMemorySizeForExecution()  /* returns this job's required memory size to be processed */
	{
		return this.requiredMemorySizeForExecution;
	}

	public void setRequiredMemorySizeForExecution(double requiredMemorySizeForExecution)  /* sets this job's required memory size to be processed */
	{
		this.requiredMemorySizeForExecution = requiredMemorySizeForExecution;
	}

	public double getRequiredDiskSizeForExecution()  /* returns this job's required disk size to be processed */
	{
		return this.requiredDiskSizeForExecution;
	}

	public void setRequiredDiskSizeForExecution(double requiredDiskSizeForExecution)  /* sets this job's required disk size to be processed */
	{
		this.requiredDiskSizeForExecution = requiredDiskSizeForExecution;
	}

	public double getDockerFileSize()  /* returns the size of the Docker file generated for this job */
	{
		return this.dockerFileSize;
	}

	public void setDockerFileSize(double dockerFileSize)  /* sets the size of the Docker file generated for this job */
	{
		this.dockerFileSize = dockerFileSize;
	}

	public double getDockerFileGenerationDurationOnMasterPC()  /* returns the amount of time it takes to the master PC to generate this job's corresponding Docker file  */
	{
		return this.dockerFileGenerationDurationOnMasterPC;
	}

	public void setDockerFileGenerationDurationOnMasterPC(double dockerFileGenerationDurationOnMasterPC)  /* sets the amount of time it takes to the master PC to generate
	this job's corresponding Docker file  */
	{
		this.dockerFileGenerationDurationOnMasterPC = dockerFileGenerationDurationOnMasterPC;
	}

	public double getEstimatedResultFileSize()  /* returns the size of the file resulting from this job's processing */
	{
		return this.estimatedResultFileSize;
	}

	public void setEstimatedResultFileSize(double estimatedResultFileSize)  /* sets the size of the file resulting from this job's processing */
	{
		this.estimatedResultFileSize = estimatedResultFileSize;
	}
	
	public long getArrivalTime()  /* returns the time this job has been submitted to the master PC */
	{
		return this.arrivalTime;
	}

	public void setArrivalTime(long arrivalTime)  /* sets the time this job has been submitted to the master PC */
	{
		this.arrivalTime = arrivalTime;
	}

	public double getInducedCPUUsageIncreasePercentage()  /* returns the CPU usage increase induced by the execution of this job on its assigned worker PC */
	{
		return this.inducedCPUUsageIncreasePercentage;
	}

	public void setInducedCPUUsageIncreasePercentage(double inducedCPUUsageIncreasePercentage)  /* sets the CPU usage increase induced by the execution of this job on its assigned worker PC */
	{
		this.inducedCPUUsageIncreasePercentage = inducedCPUUsageIncreasePercentage;
	}
	
	public long getAssignmentTime()  /* returns the time this job has been assigned to a worker PC */
	{
		return this.assignmentTime;
	}

	public void setAssignmentTime(long assignmentTime)  /* sets the time this job has been assigned to a worker PC */
	{
		this.assignmentTime = assignmentTime;
	}

	public double getJobCurrentCPUTime()  /* returns this job's current CPU time */
	{
		return this.jobCurrentCPUTime;
	}

	public void setJobCurrentCPUTime(double jobCurrentCPUTime)  /* sets this job's current CPU time */
	{
		this.jobCurrentCPUTime = jobCurrentCPUTime;
	}
	
	public int getImplementsMultithreading()  /* returns the number of threads or processes this job creates during its execution */
	{
		return this.threadProcessCount;
	}

	public void setImplementsMultithreading(int threadProcessCount)  /* sets the number of threads or processes this job creates during its execution */
	{
		this.threadProcessCount = threadProcessCount;
	}
	
	public boolean getCurrentlyAssignedToWorker()  /* returns whether this job is currently assigned to a worker or not */
	{
		return this.currentlyAssignedToWorker;
	}
	
	public void setCurrentlyAssignedToWorker(boolean currentlyAssignedToWorker)  /* sets whether this job is currently assigned to a worker or not */
	{
		this.currentlyAssignedToWorker = currentlyAssignedToWorker;
	}
	
	public boolean getFinishedBeingProcessedOnAssignedWorker()  /* returns  whether this job has finished being processed on its assigned worker or not */
	{
		return this.finishedBeingProcessedOnAssignedWorker;
	}
	
	public void setFinishedBeingProcessedOnAssignedWorker(boolean finishedBeingProcessedOnAssignedWorker)  /* sets  whether this job has finished being 
	processed on its assigned worker or not */
	{
		this.finishedBeingProcessedOnAssignedWorker = finishedBeingProcessedOnAssignedWorker;
	}
	
	public boolean getCurrentlyBeingProcessedOnAssignedWorker()  /* returns  whether this job is being processed on its assigned worker or not */
	{
		return this.currentlyBeingProcessedOnAssignedWorker;
	}
	
	public void setCurrentlyBeingProcessedOnAssignedWorker(boolean currentlyBeingProcessedOnAssignedWorker)  /* sets  whether this job is being 
	processed on its assigned worker or not */
	{
		this.currentlyBeingProcessedOnAssignedWorker = currentlyBeingProcessedOnAssignedWorker;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}
	
	
}
