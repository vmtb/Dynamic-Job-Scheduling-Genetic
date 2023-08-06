package project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Worker  /* this class represents a UPC worker */
{
	protected int ID = 0;  /* ID used to uniquely identify this worker */
	protected int TEMPID = 0;
	protected CPU cpuInfo = null;  /* information related to this worker CPU(s) */
	protected List<Job> assignedJobs = Collections.synchronizedList(new ArrayList<Job>());  /* the jobs that have been assigned to this worker and have not finished being processed */
	protected double cpuUsageInPercentage = 0.0; /* the percentage of the CPU capacity that is being used on this worker */
	protected double availableMemorySize = 0.0;  /* available memory size for this worker */
	protected double availableDiskSize = 0.0;  /* available disk size for this worker */
	protected double connectionBandwidthWithMasterPC = 0.0;  /* the bandwidth of the connection between this worker and the master PC */
	protected double connectionDelayWithMasterPC = 0.0;  /* the delay of the connection between this worker and the master PC */
	protected double originalAvailableMemorySize = 0.0;  /* the original available memory size for this worker */
	protected double originalAvailableDiskSize = 0.0;  /* the original available disk size for this worker */
	protected double originalConnectionBandwidthWithMasterPC = 0.0;  /* the original bandwidth of the connection between this worker and the master PC */
	protected double originalConnectionDelayWithMasterPC = 0.0;  /* the original delay of the connection between this worker and the master PC */
	protected double currentGlobalCPUTime = 0.0;  /* the sum of the CPU time of all the jobs that are being executed on this worker PC */
	protected String name = "";  /* name of this worker */
	
	protected int base10Name = 0;  /* 5 => 10000 => 16 |  */
	protected long endingTime = 0;
	
	public int getTEMPID() {
		return TEMPID;
	}

	public void setTEMPID(int tEMPID) {
		TEMPID = tEMPID;
	}

	public int getBase10Name() {
		return base10Name;
	}

	public void setBase10Name(int base10Name) {
		this.base10Name = base10Name;
	} 
	
	public long getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(long endingTime) {
		this.endingTime = endingTime;
	}

	public Worker(int ID, CPU cpuInfo, double currentGlobalCPUTime, double availableMemorySize, double availableDiskSize, List<Job> assignedJobs,
			double connectionBandwidthWithMasterPC, double connectionDelayWithMasterPC, double cpuUsageInPercentage, String name,
			double originalAvailableMemorySize, double originalAvailableDiskSize, double originalConnectionBandwidthWithMasterPC, double originalConnectionDelayWithMasterPC)
	/* this constructor creates and initializes a new worker with the given parameters */
	{
		this.ID = ID;
		this.TEMPID = ID;
		this.cpuInfo = cpuInfo;
		this.availableMemorySize = availableMemorySize; 
		this.availableDiskSize = availableDiskSize; 
		this.connectionBandwidthWithMasterPC = connectionBandwidthWithMasterPC;
		this.connectionDelayWithMasterPC = connectionDelayWithMasterPC;
		this.originalAvailableMemorySize = originalAvailableMemorySize;
		this.originalAvailableDiskSize = originalAvailableDiskSize;								
		this.originalConnectionBandwidthWithMasterPC = originalConnectionBandwidthWithMasterPC;  
		this.originalConnectionDelayWithMasterPC = originalConnectionDelayWithMasterPC;      
		this.assignedJobs = assignedJobs;   
		this.cpuUsageInPercentage = cpuUsageInPercentage;
		this.name = name;
		this.currentGlobalCPUTime = currentGlobalCPUTime;
		//id =1
		//id  = 2; 
		//id = 3
		// 
	}
	
	public Worker()  /* this constructor creates a new empty worker */
	{
		
	}
	
	public Worker duplicate(Worker worker)  /* creates a new copy of the passed in worker */
	{
		if (worker != null)
		{
			Worker newWorker = new Worker();
			ArrayList<Job> assignedJobs = null;
			List<Job> jobs = worker.getAssignedJobs();
			
			if (jobs != null)
			{
				Iterator<Job> iterator = jobs.iterator();
				assignedJobs = new ArrayList<Job>();
				
				while (iterator.hasNext())
				{
					Job job = iterator.next();
					Job tempJob = new Job(job.getID(), job.getStandardProcessingDurations(), job.getRequiredMemorySizeForExecution(), 
							job.getRequiredDiskSizeForExecution(), job.getAssignedWorker(), job.getJobCurrentCPUTime(), job.getDockerFileSize(), 
							job.getAssignmentTime(), job.getEstimatedResultFileSize(), job.getDockerFileGenerationDurationOnMasterPC(), 
							job.getCurrentlyBeingProcessedOnAssignedWorker(), job.getThreadProcessCount(), job.getCurrentlyAssignedToWorker(), 
							job.getFinishedBeingProcessedOnAssignedWorker(), job.getName(), job.getInducedCPUUsageIncreasePercentage());
					assignedJobs.add(tempJob);
				}
			}
			
			newWorker.setAssignedJobs(assignedJobs);
			newWorker.setID(worker.getID());
			newWorker.setCpuInfo(new CPU().duplicate(worker.getCpuInfo()));
			newWorker.setCurrentGlobalCPUTime(worker.getCurrentGlobalCPUTime());
			newWorker.setAvailableMemorySize(worker.getAvailableMemorySize());
			newWorker.setAvailableDiskSize(worker.getAvailableDiskSize());
			newWorker.setConnectionBandwidthWithMasterPC(worker.getConnectionBandwidthWithMasterPC());
			newWorker.setOriginalAvailableMemorySize(worker.getOriginalAvailableMemorySize());
			newWorker.setOriginalAvailableDiskSize(worker.getOriginalAvailableDiskSize());
			newWorker.setOriginalConnectionBandwidthWithMasterPC(worker.getOriginalConnectionBandwidthWithMasterPC());
			newWorker.setOriginalConnectionDelayWithMasterPC(worker.getOriginalConnectionDelayWithMasterPC());
			newWorker.setCPUUsageInPercentage(worker.getCPUUsageInPercentage());
			newWorker.setName(worker.getName().intern());
			return newWorker;
		}
		
		return null;
	}
	
	public void print()  /* displays this worker's attributes values */
	{
		synchronized (System.out)
    	{
			System.out.println("********** Worker PC Details **********");
			System.out.println("ID: " + this.ID);
			System.out.println("Name: " + this.name);
			System.out.println("Current Global CPU Time: " + this.currentGlobalCPUTime);
			System.out.println("CPU Number Of Cores: " + this.cpuInfo.getNumberOfCores());
			System.out.println("CPU Clock Rate In GHz: " + this.cpuInfo.getClockRateInHz() / 1000000000);
			System.out.println("CPU Name: " + this.cpuInfo.getFamilyName() + " " + this.cpuInfo.getDenomination());
			System.out.println("Available Memory Size: " + this.availableMemorySize);
			System.out.println("Available Disk Size: " + this.availableDiskSize);
			System.out.println("Connection Bandwidth With Master PC: " + this.connectionBandwidthWithMasterPC);
			System.out.println("Connection Delay With Master PC: " + this.connectionDelayWithMasterPC);
			System.out.println("CPU Usage In Percentage: " + this.cpuUsageInPercentage);
			System.out.println("----- Assigned Jobs -----");
			
			for(int i = 0; i < this.assignedJobs.size(); ++i)
			{
				System.out.println("Job Name: " + this.assignedJobs.get(i).getName());
			}
			
			System.out.println("********** End **********\n\n");
    	    System.out.flush();
    	}
	}
	
	public void resetResourceUsage(Job job)  /* reinitializes a worker that was processing a job */
	{
		this.setCPUUsageInPercentage(0.0);
		this.setAvailableDiskSize(this.getAvailableDiskSize() + job.getRequiredDiskSizeForExecution());
		this.setAvailableMemorySize(this.getAvailableMemorySize() + job.getRequiredMemorySizeForExecution());
	}
	
	public void executeJob(Job job)  /* makes this worker execute the specified job */
	{
		Thread jobExecutionThread = new Thread(job);
		jobExecutionThread.start();
	}
	
	public int getID()  /* returns this worker's ID */
	{
		return this.ID;
	}
	
	public void setID(int iD)  /* sets this worker's ID */
	{
		this.ID = iD;

		int base10Temp = 0;
		for (int i = 0; i < ID; i++) {
			base10Temp = base10Temp +   (int) ((i==ID-1?1:0) * Math.pow(2, i)); 
		}
		System.out.println(base10Temp);
		this.base10Name = base10Temp; 
	}
	
	public double getCurrentGlobalCPUTime()  /* returns this worker's current global CPU time */
	{
		return this.currentGlobalCPUTime;
	}
	
	public void setCurrentGlobalCPUTime(double currentGlobalCPUTime)  /* sets this worker's current global CPU time */
	{
		this.currentGlobalCPUTime = currentGlobalCPUTime;
	}
	
	public String getName()  /* returns this worker's name */
	{
		return this.name;
	}
	
	public void setName(String name)  /* sets this worker's name */
	{
		this.name = name;
	}
	
	public CPU getCpuInfo()  /* returns this worker CPU(s) info */
	{
		return this.cpuInfo;
	}
	
	public void setCpuInfo(CPU cpuInfo)  /* set this worker CPU(s) info */
	{
		this.cpuInfo = cpuInfo;
	}
	
	public List<Job> getAssignedJobs()  /* returns the jobs that have been assigned to this worker */
	{
		return this.assignedJobs;
	}
	
	public void setAssignedJobs(List<Job> assignedJobs)  /* sets the jobs that have been assigned to this worker */
	{
		this.assignedJobs = assignedJobs;
	}
	
	public double getCPUUsageInPercentage()  /* returns the percentage of the CPU capacity that is currently used on this worker */
	{
		return this.cpuUsageInPercentage;
	}
	
	public void setCPUUsageInPercentage(double cpuUsageInPercentage)  /* sets the percentage of the CPU capacity that is currently used on this worker */
	{
		this.cpuUsageInPercentage = cpuUsageInPercentage;
	}
	
	public double getAvailableMemorySize()  /* returns this worker's available memory size */
	{
		return this.availableMemorySize;
	}
	
	public void setAvailableMemorySize(double availableMemorySize)  /* sets this worker's available memory size */
	{
		if (availableMemorySize != this.originalAvailableMemorySize && this.originalAvailableMemorySize == 0.0)
		{
			this.originalAvailableMemorySize = availableMemorySize;
		}
		
		this.availableMemorySize = availableMemorySize;
	}
	
	public double getAvailableDiskSize()  /* returns this worker's available disk size */
	{
		return this.availableDiskSize;
	}
	
	public void setAvailableDiskSize(double availableDiskSize)  /* sets this worker's available disk size */
	{
		if (availableDiskSize != this.originalAvailableDiskSize && this.originalAvailableDiskSize == 0.0)
		{
			this.originalAvailableDiskSize = availableDiskSize;
		}
		
		this.availableDiskSize = availableDiskSize;
	}
	
	public double getConnectionBandwidthWithMasterPC()  /* returns the bandwidth of the connection between this worker and the master PC */
	{
		return this.connectionBandwidthWithMasterPC;
	}
	
	public void setConnectionBandwidthWithMasterPC(double connectionBandwidthWithMasterPC)  /* sets the bandwidth of the connection between this worker 
	and the master PC */
	{
		if (connectionBandwidthWithMasterPC != this.originalConnectionBandwidthWithMasterPC && this.originalConnectionBandwidthWithMasterPC == 0.0)
		{
			this.originalConnectionBandwidthWithMasterPC = connectionBandwidthWithMasterPC;
		}
		
		this.connectionBandwidthWithMasterPC = connectionBandwidthWithMasterPC;
	}
	
	public double getConnectionDelayWithMasterPC()  /* returns the delay of the connection between this worker and the master PC  */
	{
		return this.connectionDelayWithMasterPC;
	}
	
	public void setConnectionDelayWithMasterPC(double connectionDelayWithMasterPC)  /* sets the delay of the connection between this worker and the master PC */
	{
		if (connectionDelayWithMasterPC != this.originalConnectionDelayWithMasterPC && this.originalConnectionDelayWithMasterPC == 0.0)
		{
			this.originalConnectionDelayWithMasterPC = connectionDelayWithMasterPC;
		}
		
		this.connectionDelayWithMasterPC = connectionDelayWithMasterPC;
	}

	public double getOriginalAvailableMemorySize()
	{
		return this.originalAvailableMemorySize;
	}

	public void setOriginalAvailableMemorySize(double originalAvailableMemorySize)
	{
		this.originalAvailableMemorySize = originalAvailableMemorySize;
	}

	public double getOriginalAvailableDiskSize()
	{
		return this.originalAvailableDiskSize;
	}

	public void setOriginalAvailableDiskSize(double originalAvailableDiskSize)
	{
		this.originalAvailableDiskSize = originalAvailableDiskSize;
	}

	public double getOriginalConnectionBandwidthWithMasterPC()
	{
		return this.originalConnectionBandwidthWithMasterPC;
	}

	public void setOriginalConnectionBandwidthWithMasterPC(double originalConnectionBandwidthWithMasterPC)
	{
		this.originalConnectionBandwidthWithMasterPC = originalConnectionBandwidthWithMasterPC;
	}

	public double getOriginalConnectionDelayWithMasterPC()
	{
		return this.originalConnectionDelayWithMasterPC;
	}

	public void setOriginalConnectionDelayWithMasterPC(double originalConnectionDelayWithMasterPC)
	{
		this.originalConnectionDelayWithMasterPC = originalConnectionDelayWithMasterPC;
	}
}
