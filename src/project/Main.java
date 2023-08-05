package project;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Main {

	static final long timer = System.currentTimeMillis();  /* to set up a timeline for events */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start Dynamic Case...");
		
		try {
//			ArrayList<Job> jobs = getJobs("2");
//			ArrayList<Worker> wksArrayList = getWorkers();
//			
//			JobScheduler jobScheduler = new JobScheduler(jobs, wksArrayList);
//			
//			//Get initial population
//			Population population = jobScheduler.getPopulationInitial(); 
//			
//			// Planification 
//			Solution solution = jobScheduler.startGeneticAlg(population, 1000);
//			
//			// Exécution 
//			
//			
//			//Display results  
//			System.out.print("Solution finale ");
//			solution.showSolution2(wksArrayList);
//			double makespan = solution.getScore(wksArrayList); 
//			System.out.println();
//			
//	        System.out.println("Execution time of Greedys: " + jobScheduler.getExecutionTimeGreedy() + " milliseconds");
//	        System.out.println("Execution time of Planification: " + jobScheduler.getExecutionTimePlanification() + " milliseconds");
//	        System.out.println("MakeSpan "+makespan+"' ==> " + convertSecondToHMS((long) makespan) + " milliseconds");
//			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	

	public double [][] getCostMatrix(ArrayList<Worker> workers, ArrayList<Job> jobs) {
		double costMatrix [][]= new double[workers.size()][jobs.size()];
		for (int i = 0; i < workers.size(); i++) {
			Worker w = workers.get(i); 
			System.out.println(); 
			for (int j = 0; j < jobs.size(); j++) {  
				costMatrix[i][j] = jobs.get(j).getStandardProcessingDurations().
						get(w.getCpuInfo().getFamilyName() + "-" + w.getCpuInfo().getDenomination() + "-" + w.getCpuInfo().getNumberOfCores());
			}
		}
		return costMatrix;
	}
	
	
	/**
	 * 
	 * @param type is a string between "1", "2", "3" for respectively 9, 18 and 27 jobs
	 * @return
	 */
	public static ArrayList<Job> getJobs(String type) {
		/* job CSV file parsing */
		ArrayList<Job> jobs = new ArrayList<Job>() ;
		try
		{
			Reader csvData = new FileReader(".\\data\\jobs"+type+".csv");
			CSVParser parser;
			
			try
			{
				parser = CSVParser.parse(csvData, CSVFormat.EXCEL);
				List<CSVRecord> csvRecord = parser.getRecords();
				int jobNameColumn = 0, standardProcessingDurationPC1Column = 0, standardProcessingDurationPC2Column = 0, standardProcessingDurationPC3Column = 0, standardProcessingDurationPC4Column = 0, standardProcessingDurationPC5Column = 0;
				int requiredMemorySizeForExecutionColumn = 0, requiredDiskSizeForExecutionColumn = 0, dockerFileSizeColumn = 0, dockerFileGenerationDurationOnMasterPCColumn = 0;
				int estimatedResultFileSizeColumn = 0, threadProcessCountColumn = 0;
				
				for (int csvFileLine = 0; csvFileLine < csvRecord.size(); ++csvFileLine)
				{
					if (csvFileLine == 0)  /* we first get the CSV file column indexes thanks to column names available on the first CSV file line */
					{
						for (int csvFileColumn = 0; csvFileColumn < csvRecord.get(csvFileLine).size(); ++csvFileColumn)
						{
							if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("job name"))
							{
								jobNameColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("standard processing duration pc1"))
							{
								standardProcessingDurationPC1Column = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("standard processing duration pc2"))
							{
								standardProcessingDurationPC2Column = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("standard processing duration pc3"))
							{
								standardProcessingDurationPC3Column = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("standard processing duration pc4"))
							{
								standardProcessingDurationPC4Column = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("standard processing duration pc5"))
							{
								standardProcessingDurationPC5Column = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("required memory size for execution"))
							{
								requiredMemorySizeForExecutionColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("required disk size for execution"))
							{
								requiredDiskSizeForExecutionColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("docker file size"))
							{
								dockerFileSizeColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("docker file generation duration on master pc"))
							{
								dockerFileGenerationDurationOnMasterPCColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("estimated result file size"))
							{
								estimatedResultFileSizeColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("thread process count"))
							{
								threadProcessCountColumn = csvFileColumn;
							}
						}
						
						if (jobNameColumn == 0 && (standardProcessingDurationPC1Column == 0 || standardProcessingDurationPC2Column == 0 || standardProcessingDurationPC3Column == 0 || standardProcessingDurationPC4Column == 0
								|| standardProcessingDurationPC5Column == 0 || requiredMemorySizeForExecutionColumn == 0 || requiredDiskSizeForExecutionColumn == 0 || dockerFileSizeColumn == 0 || dockerFileGenerationDurationOnMasterPCColumn == 0
								|| estimatedResultFileSizeColumn == 0 || threadProcessCountColumn == 0))
						{
							System.out.println("Some column headers are missing in your jobs CSV file. Please make sure your file complies with the defined standards and try again.");
							return jobs;
						}
					}
					else  /* we get and process each cell data thanks to column indexes and the current CSV file line we're dealing with */
					{
						SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
						Job newJob = new Job();  /* we create a new job for each new CSV file line and populate it with that line's data */
						long standardProcessingDurationPC1 = 0, standardProcessingDurationPC2 = 0, standardProcessingDurationPC3 = 0, standardProcessingDurationPC4 = 0, standardProcessingDurationPC5 = 0;
						double dockerFileGenerationDurationOnMasterPC = 0.0, requiredMemorySizeForExecution = 0.0, requiredDiskSizeForExecution = 0.0;
						double dockerFileSize = 0.0, estimatedResultFileSize = 0.0;
						
						/* we set the new job's attributes based on the current CSV file line data */
						newJob.setArrivalTime(System.currentTimeMillis() / 1000);
						newJob.setCurrentlyAssignedToWorker(false);
						newJob.setID(csvFileLine);
						newJob.setFinishedBeingProcessedOnAssignedWorker(false);
						newJob.setName(csvRecord.get(csvFileLine).get(jobNameColumn));
						newJob.setInducedCPUUsageIncreasePercentage(0.80);
						newJob.setJobCurrentCPUTime(0.0);
						
						if (Integer.valueOf(csvRecord.get(csvFileLine).get(threadProcessCountColumn)) == -1)  /* we assume the job to be multithreaded if that column is set to -1 in the CSV file */
						{
							newJob.setThreadProcessCount(Integer.MAX_VALUE);
						}
						else
						{
							newJob.setThreadProcessCount(Integer.valueOf(csvRecord.get(csvFileLine).get(threadProcessCountColumn)));
						}
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(standardProcessingDurationPC1Column).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        standardProcessingDurationPC1 = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								standardProcessingDurationPC1 = Long.valueOf(csvRecord.get(csvFileLine).get(standardProcessingDurationPC1Column).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(standardProcessingDurationPC2Column).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        standardProcessingDurationPC2 = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								standardProcessingDurationPC2 = Long.valueOf(csvRecord.get(csvFileLine).get(standardProcessingDurationPC2Column).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(standardProcessingDurationPC3Column).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        standardProcessingDurationPC3 = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								standardProcessingDurationPC3 = Long.valueOf(csvRecord.get(csvFileLine).get(standardProcessingDurationPC3Column).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(standardProcessingDurationPC4Column).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        standardProcessingDurationPC4 = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								standardProcessingDurationPC4 = Long.valueOf(csvRecord.get(csvFileLine).get(standardProcessingDurationPC4Column).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(standardProcessingDurationPC5Column).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        standardProcessingDurationPC5 = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								standardProcessingDurationPC5 = Long.valueOf(csvRecord.get(csvFileLine).get(standardProcessingDurationPC5Column).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						newJob.getStandardProcessingDurations().put("core-i9-20", standardProcessingDurationPC5);
						newJob.getStandardProcessingDurations().put("core-i9-16", standardProcessingDurationPC4);
						newJob.getStandardProcessingDurations().put("core-i7-8", standardProcessingDurationPC3);
						newJob.getStandardProcessingDurations().put("core-i5-4", standardProcessingDurationPC2);
						newJob.getStandardProcessingDurations().put("core-i3-4", standardProcessingDurationPC1);
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(dockerFileGenerationDurationOnMasterPCColumn).toString());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        dockerFileGenerationDurationOnMasterPC = 3600.0 * calendar.get(Calendar.HOUR) + 60.0 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								dockerFileGenerationDurationOnMasterPC = Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileGenerationDurationOnMasterPCColumn).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						newJob.setDockerFileGenerationDurationOnMasterPC(dockerFileGenerationDurationOnMasterPC);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().toLowerCase().contains("gb"))
						{
							requiredMemorySizeForExecution = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().toLowerCase().contains("mb") )
						{
							requiredMemorySizeForExecution = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().toLowerCase().contains("kb") )
						{
							requiredMemorySizeForExecution = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().toLowerCase().contains("b") )
						{
							requiredMemorySizeForExecution = Double.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newJob.setRequiredMemorySizeForExecution(requiredMemorySizeForExecution);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().toLowerCase().contains("gb") )
						{
							requiredDiskSizeForExecution = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().toLowerCase().contains("mb") )
						{
							requiredDiskSizeForExecution = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().toLowerCase().contains("kb") )
						{
							requiredDiskSizeForExecution = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().toLowerCase().contains("b") )
						{
							requiredDiskSizeForExecution = Double.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newJob.setRequiredDiskSizeForExecution(requiredDiskSizeForExecution);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().toLowerCase().contains("gb") )
						{
							dockerFileSize = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().toLowerCase().contains("mb") )
						{
							dockerFileSize = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().toLowerCase().contains("kb") )
						{
							dockerFileSize = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().toLowerCase().contains("b") )
						{
							dockerFileSize = Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newJob.setDockerFileSize(dockerFileSize);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().toLowerCase().contains("gb") )
						{
							estimatedResultFileSize = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().toLowerCase().contains("mb") )
						{
							estimatedResultFileSize = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().toLowerCase().contains("kb") )
						{
							estimatedResultFileSize = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().toLowerCase().contains("b") )
						{
							estimatedResultFileSize = Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newJob.setEstimatedResultFileSize(estimatedResultFileSize);//newJob.print();
						
						/* process to create the current job replicas */
						String newJobOriginalName = newJob.getName();
						String letters[] = {"", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
						int maxJobReplicaCount = 3;
						boolean incrementPrefixSize = true;
						
						newJob.setName(newJobOriginalName + "_a");
						
						if (maxJobReplicaCount > 5)  /* if the number of job instances is more than 5 for each job, we randomly assign them to job groups  */
						{
						}
						
						for (int i = 0; i < letters.length; ++i)
						{
							if (incrementPrefixSize)
							{
								incrementPrefixSize = false;
							} 
						}
						newJob.print();
						jobs.add(newJob);
					}
				}

				parser.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return jobs;
	}
	
	public static ArrayList<Worker> getWorkers() {

		ArrayList<Worker> workers =   new ArrayList<Worker>() ;
		try {
			Reader csvData = new FileReader(".\\data\\workers.csv");
			System.out.println("----");
			CSVParser parser;
			try
			{
				parser = CSVParser.parse(csvData, CSVFormat.EXCEL);
				List<CSVRecord> csvRecord = parser.getRecords();
				int workerPCNameColumn = 0, availableCPUCoreNumberColumn = 0, cpuFamilyNameColumn = 0, cpuDenominationColumn = 0, availableMemorySizeColumn = 0;
				int availableDiskSizeColumn = 0, connectionBandwidthWithMasterPCColumn = 0, connectionDelayWithMasterPCColumn = 0, cpuClockRateColumn = 0;
				
				for (int csvFileLine = 0; csvFileLine < csvRecord.size(); ++csvFileLine)
				{
					if (csvFileLine == 0)  /* we first get the CSV file column indexes thanks to column names available on the first CSV file line */
					{
						for (int csvFileColumn = 0; csvFileColumn < csvRecord.get(csvFileLine).size(); ++csvFileColumn)
						{
							if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("worker pc name"))
							{
								workerPCNameColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("available cpu core number"))
							{
								availableCPUCoreNumberColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("cpu family name"))
							{
								cpuFamilyNameColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("cpu denomination"))
							{
								cpuDenominationColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("cpu clock rate"))
							{
								cpuClockRateColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("available memory size"))
							{
								availableMemorySizeColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("available disk size"))
							{
								availableDiskSizeColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("connection bandwidth with master pc"))
							{
								connectionBandwidthWithMasterPCColumn = csvFileColumn;
							}
							else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase().equals("connection delay with master pc"))
							{
								connectionDelayWithMasterPCColumn = csvFileColumn;
							}
						}
						
						if (workerPCNameColumn == 0 && (availableCPUCoreNumberColumn == 0 || cpuFamilyNameColumn == 0 || cpuDenominationColumn == 0 || availableMemorySizeColumn == 0
								|| availableDiskSizeColumn == 0 || connectionBandwidthWithMasterPCColumn == 0 || connectionDelayWithMasterPCColumn == 0 || cpuClockRateColumn == 0))
						{
							System.out.println("Some column headers are missing in your workers CSV file. Please make sure your file complies with the defined standards and try again.");
							return workers;
						}
					}
					else  /* we get and process each cell data thanks to column indexes and the current CSV file line we're dealing with */
					{
						CPU cpuInfo = new CPU();
						SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
						Worker newWorker = new Worker();  /* we create a new worker for each new CSV file line and populate it with that line's data */
						double availableMemorySize = 0.0, availableDiskSize = 0.0, connectionBandwidthWithMasterPC = 0.0, connectionDelayWithMasterPC = 0.0, cpuClockRate = 0.0;
						
						/* we set the new worker's attributes based on the current CSV file line data */
						newWorker.setID(csvFileLine);
						newWorker.setName(csvRecord.get(csvFileLine).get(workerPCNameColumn));
						newWorker.setCPUUsageInPercentage(0.0);
						newWorker.setCurrentGlobalCPUTime(0.0);
						
						/* we accept GHz, MHz, KHz or Hz data and convert them all into Hertz */
						if (csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().toLowerCase().contains("ghz") )
						{
							cpuClockRate = 1000 * 1000 * 1000 * Double.valueOf(csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().trim().split("GHZ|GHz|GhZ|Ghz|gHZ|gHz|ghZ|ghz")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().toLowerCase().contains("mhz") )
						{
							cpuClockRate = 1000 * 1000 * Double.valueOf(csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().trim().split("MHZ|MHz|MhZ|Mhz|mHZ|mHz|mhZ|mhz")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().toLowerCase().contains("khz"))
						{
							cpuClockRate = 1000 * Double.valueOf(csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().trim().split("KHZ|KHz|KhZ|Khz|kHZ|kHz|khZ|khz")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().toLowerCase().contains("hz"))
						{
							cpuClockRate = Double.valueOf(csvRecord.get(csvFileLine).get(cpuClockRateColumn).toString().trim().split("HZ|Hz|hZ|hz")[0].trim());
						}
						
						cpuInfo.setNumberOfCores(Integer.valueOf(csvRecord.get(csvFileLine).get(availableCPUCoreNumberColumn).toString().trim()));
						cpuInfo.setClockRateInHz(cpuClockRate);
						cpuInfo.setFamilyName(csvRecord.get(csvFileLine).get(cpuFamilyNameColumn).toString().trim());
						cpuInfo.setDenomination(csvRecord.get(csvFileLine).get(cpuDenominationColumn).toString().trim());
						newWorker.setCpuInfo(cpuInfo);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toLowerCase().toString().contains("gb"))
						{
							availableMemorySize = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().toLowerCase().contains("mb"))
						{
							availableMemorySize = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().toLowerCase().contains("kb"))
						{
							availableMemorySize = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().toLowerCase().contains("b"))
						{
							availableMemorySize = Double.valueOf(csvRecord.get(csvFileLine).get(availableMemorySizeColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newWorker.setAvailableMemorySize(availableMemorySize);
						
						/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
						if (csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().toLowerCase().contains("gb") )
						{
							availableDiskSize = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().toLowerCase().contains("mb") )
						{
							availableDiskSize = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().toLowerCase().contains("kb") )
						{
							availableDiskSize = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
						}
						else if ( csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().toLowerCase().contains("b"))
						{
							availableDiskSize = Double.valueOf(csvRecord.get(csvFileLine).get(availableDiskSizeColumn).toString().trim().split("b|B")[0].trim());
						}
						
						newWorker.setAvailableDiskSize(availableDiskSize);
						
						/* we accept GB/s, MB/s, KB/s or B/s data and convert them all into B/s */
						if (csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().toLowerCase().contains("gb/s"))
						{
							connectionBandwidthWithMasterPC = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().trim().split("GB/S|GB/s|Gb/S|Gb/s|gB/S|gB/s|gb/S|gb/s")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().toLowerCase().contains("mb/s")
								)
						{
							connectionBandwidthWithMasterPC = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().trim().split("MB/S|MB/s|Mb/S|Mb/s|mB/S|mB/s|mb/S|mb/s")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().toLowerCase().contains("kb/s")
								){
							connectionBandwidthWithMasterPC = 1024 * Double.valueOf(csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().trim().split("KB/S|KB/s|Kb/S|Kb/s|kB/S|kB/s|kb/S|kb/s")[0].trim());
						}
						else if (csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().toLowerCase().contains("b/s"))
						{
							connectionBandwidthWithMasterPC = Double.valueOf(csvRecord.get(csvFileLine).get(connectionBandwidthWithMasterPCColumn).toString().trim().split("B/S|B/s|b/S|b/s")[0].trim());
						}
						
						newWorker.setConnectionBandwidthWithMasterPC(connectionBandwidthWithMasterPC);
						
						try
						{  /* we convert all time data into seconds */
							Date date = dateFormat.parse(csvRecord.get(csvFileLine).get(connectionDelayWithMasterPCColumn).toString().trim());
							
							if (date != null)
							{
								Calendar calendar = Calendar.getInstance();
						        calendar.setTime(date);
						        connectionDelayWithMasterPC = 3600 * calendar.get(Calendar.HOUR) + 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
							}
							else
							{
								connectionDelayWithMasterPC = Long.valueOf(csvRecord.get(csvFileLine).get(connectionDelayWithMasterPCColumn).toString());
							}
						}
						catch (ParseException e)
						{
							e.printStackTrace();
						}
						
						newWorker.setConnectionDelayWithMasterPC(connectionDelayWithMasterPC); 
						newWorker.print();
						workers.add(newWorker);
}
				}

				parser.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return workers;
	}
	 
	
	
	public static String convertSecondToHMS(long seconds) { 

        // Calculate the hours, minutes, and remaining seconds
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        // Format the time as HH:mm:ss
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
        return formattedTime;
	}
}
