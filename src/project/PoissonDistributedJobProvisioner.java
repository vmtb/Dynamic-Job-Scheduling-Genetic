package project; 

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

class PoissonDistributedJobProvisioner implements Runnable {
	protected JobScheduler jobScheduler = null;
	protected double poissonDistrMeanValue = 15.0;
	protected int csvFileIterationCount = 1;

	public PoissonDistributedJobProvisioner(JobScheduler jobScheduler, double poissonDistrMeanValue) {
		this.jobScheduler = jobScheduler;
		this.poissonDistrMeanValue = poissonDistrMeanValue;
	}

	public JobScheduler getJobScheduler() {
		return this.jobScheduler;
	}

	public void setJobScheduler(JobScheduler jobScheduler) {
		this.jobScheduler = jobScheduler;
	}

	public double getPoissonDistrMeanValue() {
		return this.poissonDistrMeanValue;
	}

	public void setPoissonDistrMeanValue(double poissonDistrMeanValue) {
		this.poissonDistrMeanValue = poissonDistrMeanValue;
	}

	public void run() /* start provisioning Poisson distributed jobs */
	{
		/* job CSV file parsing */
		try {
			Reader csvData = new FileReader(".\\data\\jobs.csv");
			CSVParser parser = null;

			try {
				parser = CSVParser.parse(csvData, CSVFormat.EXCEL);
				List<CSVRecord> csvRecord = parser.getRecords();
				String letters[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
						"q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
				int jobNameColumn = 0, standardProcessingDurationPC1Column = 0, standardProcessingDurationPC2Column = 0,
						standardProcessingDurationPC3Column = 0, standardProcessingDurationPC4Column = 0,
						standardProcessingDurationPC5Column = 0;
				int requiredMemorySizeForExecutionColumn = 0, requiredDiskSizeForExecutionColumn = 0,
						dockerFileSizeColumn = 0, dockerFileGenerationDurationOnMasterPCColumn = 0;
				int estimatedResultFileSizeColumn = 0, threadProcessCountColumn = 0, iterationCounter = 0;

				while (iterationCounter++ < csvFileIterationCount) /*
																	 * we provision all jobs in the CSV file
																	 * csvFileIterationCount times
																	 */
				{
					String jobNameSuffix = "_";

					for (int i = 0; i <= iterationCounter / 26; ++i) {
						if (iterationCounter % 26 == 0) {
							jobNameSuffix = jobNameSuffix + letters[25];

							if (i == (iterationCounter / 26) - 1) {
								break;
							}
						} else {
							jobNameSuffix = jobNameSuffix + letters[(iterationCounter % 26) - 1];
						}
					}

					for (int csvFileLine = 0; csvFileLine < csvRecord.size(); ++csvFileLine) {
						if (csvFileLine == 0) /*
												 * we first get the CSV file column indexes thanks to column names
												 * available on the first CSV file line
												 */
						{
							for (int csvFileColumn = 0; csvFileColumn < csvRecord.get(csvFileLine)
									.size(); ++csvFileColumn) {
								if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("job name")) {
									jobNameColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("standard processing duration pc1")) {
									standardProcessingDurationPC1Column = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("standard processing duration pc2")) {
									standardProcessingDurationPC2Column = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("standard processing duration pc3")) {
									standardProcessingDurationPC3Column = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("standard processing duration pc4")) {
									standardProcessingDurationPC4Column = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("standard processing duration pc5")) {
									standardProcessingDurationPC5Column = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("required memory size for execution")) {
									requiredMemorySizeForExecutionColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("required disk size for execution")) {
									requiredDiskSizeForExecutionColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("docker file size")) {
									dockerFileSizeColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("docker file generation duration on master pc")) {
									dockerFileGenerationDurationOnMasterPCColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("estimated result file size")) {
									estimatedResultFileSizeColumn = csvFileColumn;
								} else if (csvRecord.get(csvFileLine).get(csvFileColumn).toString().toLowerCase()
										.equals("thread process count")) {
									threadProcessCountColumn = csvFileColumn;
								}
							}

							if (jobNameColumn == 0 && (standardProcessingDurationPC1Column == 0
									|| standardProcessingDurationPC2Column == 0
									|| standardProcessingDurationPC3Column == 0
									|| standardProcessingDurationPC4Column == 0
									|| standardProcessingDurationPC5Column == 0
									|| requiredMemorySizeForExecutionColumn == 0
									|| requiredDiskSizeForExecutionColumn == 0 || dockerFileSizeColumn == 0
									|| dockerFileGenerationDurationOnMasterPCColumn == 0
									|| estimatedResultFileSizeColumn == 0 || threadProcessCountColumn == 0)) {
								System.out.println(
										"Some column headers are missing in your jobs CSV file. Please make sure your file complies with the defined standards and try again.");
								return;
							}
						} else /*
								 * we get and process each cell data thanks to column indexes and the current
								 * CSV file line we're dealing with
								 */
						{
							SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
							Job newJob = new Job(); /*
													 * we create a new job for each new CSV file line and populate it
													 * with that line's data
													 */
							long standardProcessingDurationPC1 = 0, standardProcessingDurationPC2 = 0,
									standardProcessingDurationPC3 = 0, standardProcessingDurationPC4 = 0,
									standardProcessingDurationPC5 = 0;
							double dockerFileGenerationDurationOnMasterPC = 0.0, requiredMemorySizeForExecution = 0.0,
									requiredDiskSizeForExecution = 0.0;
							double dockerFileSize = 0.0, estimatedResultFileSize = 0.0;

							/* we set the new job's attributes based on the current CSV file line data */
							newJob.setArrivalTime(System.currentTimeMillis() / 10);
							newJob.setCurrentlyAssignedToWorker(false);
							newJob.setID(++JobScheduler.lastJobID);
							newJob.setFinishedBeingProcessedOnAssignedWorker(false);
							newJob.setName(csvRecord.get(csvFileLine).get(jobNameColumn) + jobNameSuffix);
							newJob.setInducedCPUUsageIncreasePercentage(0.80);
							newJob.setJobCurrentCPUTime(0.0);

							if (Integer.valueOf(csvRecord.get(csvFileLine).get(threadProcessCountColumn)) == -1) /*
																													 * we
																													 * assume
																													 * the
																													 * job
																													 * to
																													 * be
																													 * multithreaded
																													 * if
																													 * that
																													 * column
																													 * is
																													 * set
																													 * to
																													 * -1
																													 * in
																													 * the
																													 * CSV
																													 * file
																													 */
							{
								newJob.setThreadProcessCount(Integer.MAX_VALUE);
							} else {
								newJob.setThreadProcessCount(
										Integer.valueOf(csvRecord.get(csvFileLine).get(threadProcessCountColumn)));
							}

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(standardProcessingDurationPC1Column).toString().trim());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									standardProcessingDurationPC1 = 3600 * calendar.get(Calendar.HOUR)
											+ 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									standardProcessingDurationPC1 = Long.valueOf(csvRecord.get(csvFileLine)
											.get(standardProcessingDurationPC1Column).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(standardProcessingDurationPC2Column).toString().trim());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									standardProcessingDurationPC2 = 3600 * calendar.get(Calendar.HOUR)
											+ 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									standardProcessingDurationPC2 = Long.valueOf(csvRecord.get(csvFileLine)
											.get(standardProcessingDurationPC2Column).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(standardProcessingDurationPC3Column).toString().trim());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									standardProcessingDurationPC3 = 3600 * calendar.get(Calendar.HOUR)
											+ 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									standardProcessingDurationPC3 = Long.valueOf(csvRecord.get(csvFileLine)
											.get(standardProcessingDurationPC3Column).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(standardProcessingDurationPC4Column).toString().trim());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									standardProcessingDurationPC4 = 3600 * calendar.get(Calendar.HOUR)
											+ 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									standardProcessingDurationPC4 = Long.valueOf(csvRecord.get(csvFileLine)
											.get(standardProcessingDurationPC4Column).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(standardProcessingDurationPC5Column).toString().trim());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									standardProcessingDurationPC5 = 3600 * calendar.get(Calendar.HOUR)
											+ 60 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									standardProcessingDurationPC5 = Long.valueOf(csvRecord.get(csvFileLine)
											.get(standardProcessingDurationPC5Column).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							newJob.getStandardProcessingDurations().put("core-i9-20", standardProcessingDurationPC5);
							newJob.getStandardProcessingDurations().put("core-i9-16", standardProcessingDurationPC4);
							newJob.getStandardProcessingDurations().put("core-i7-8", standardProcessingDurationPC3);
							newJob.getStandardProcessingDurations().put("core-i5-4", standardProcessingDurationPC2);
							newJob.getStandardProcessingDurations().put("core-i3-4", standardProcessingDurationPC1);

							try { /* we convert all time data into seconds */
								Date date = dateFormat.parse(csvRecord.get(csvFileLine)
										.get(dockerFileGenerationDurationOnMasterPCColumn).toString());

								if (date != null) {
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(date);
									dockerFileGenerationDurationOnMasterPC = 3600.0 * calendar.get(Calendar.HOUR)
											+ 60.0 * calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
								} else {
									dockerFileGenerationDurationOnMasterPC = Double.valueOf(csvRecord.get(csvFileLine)
											.get(dockerFileGenerationDurationOnMasterPCColumn).toString());
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}

							newJob.setDockerFileGenerationDurationOnMasterPC(dockerFileGenerationDurationOnMasterPC);

							/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
							if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
									.contains("GB")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("gb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("Gb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("gB")) {
								requiredMemorySizeForExecution = 1024 * 1024 * 1024
										* Double.valueOf(
												csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn)
														.toString().trim().split("gb|gB|Gb|GB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
									.contains("MB")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("mb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("Mb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("mB")) {
								requiredMemorySizeForExecution = 1024 * 1024
										* Double.valueOf(
												csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn)
														.toString().trim().split("mb|mB|Mb|MB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
									.contains("KB")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("kb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("Kb")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("kB")) {
								requiredMemorySizeForExecution = 1024 * Double
										.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn)
												.toString().trim().split("kb|kB|Kb|KB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
									.contains("B")
									|| csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn).toString()
											.contains("b")) {
								requiredMemorySizeForExecution = Double
										.valueOf(csvRecord.get(csvFileLine).get(requiredMemorySizeForExecutionColumn)
												.toString().trim().split("b|B")[0].trim());
							}

							newJob.setRequiredMemorySizeForExecution(requiredMemorySizeForExecution);

							/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
							if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
									.contains("GB")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("gb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("Gb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("gB")) {
								requiredDiskSizeForExecution = 1024 * 1024 * 1024
										* Double.valueOf(
												csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn)
														.toString().trim().split("gb|gB|Gb|GB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
									.contains("MB")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("mb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("Mb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("mB")) {
								requiredDiskSizeForExecution = 1024 * 1024
										* Double.valueOf(
												csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn)
														.toString().trim().split("mb|mB|Mb|MB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
									.contains("KB")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("kb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("Kb")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("kB")) {
								requiredDiskSizeForExecution = 1024 * Double
										.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn)
												.toString().trim().split("kb|kB|Kb|KB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
									.contains("B")
									|| csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn).toString()
											.contains("b")) {
								requiredDiskSizeForExecution = Double
										.valueOf(csvRecord.get(csvFileLine).get(requiredDiskSizeForExecutionColumn)
												.toString().trim().split("b|B")[0].trim());
							}

							newJob.setRequiredDiskSizeForExecution(requiredDiskSizeForExecution);

							/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
							if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("GB")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("gb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("Gb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("gB")) {
								dockerFileSize = 1024 * 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine)
										.get(dockerFileSizeColumn).toString().trim().split("gb|gB|Gb|GB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("MB")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("mb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("Mb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("mB")) {
								dockerFileSize = 1024 * 1024 * Double.valueOf(csvRecord.get(csvFileLine)
										.get(dockerFileSizeColumn).toString().trim().split("mb|mB|Mb|MB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("KB")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("kb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("Kb")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("kB")) {
								dockerFileSize = 1024 * Double.valueOf(csvRecord.get(csvFileLine)
										.get(dockerFileSizeColumn).toString().trim().split("kb|kB|Kb|KB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("B")
									|| csvRecord.get(csvFileLine).get(dockerFileSizeColumn).toString().contains("b")) {
								dockerFileSize = Double.valueOf(csvRecord.get(csvFileLine).get(dockerFileSizeColumn)
										.toString().trim().split("b|B")[0].trim());
							}

							newJob.setDockerFileSize(dockerFileSize);

							/* we accept GB, Mb, Kb or byte data and convert them all into bytes */
							if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString().contains("GB")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("gb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("Gb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("gB")) {
								estimatedResultFileSize = 1024 * 1024 * 1024
										* Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn)
												.toString().trim().split("gb|gB|Gb|GB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
									.contains("MB")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("mb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("Mb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("mB")) {
								estimatedResultFileSize = 1024 * 1024
										* Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn)
												.toString().trim().split("mb|mB|Mb|MB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
									.contains("KB")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("kb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("Kb")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("kB")) {
								estimatedResultFileSize = 1024
										* Double.valueOf(csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn)
												.toString().trim().split("kb|kB|Kb|KB")[0].trim());
							} else if (csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
									.contains("B")
									|| csvRecord.get(csvFileLine).get(estimatedResultFileSizeColumn).toString()
											.contains("b")) {
								estimatedResultFileSize = Double.valueOf(csvRecord.get(csvFileLine)
										.get(estimatedResultFileSizeColumn).toString().trim().split("b|B")[0].trim());
							}

							newJob.setEstimatedResultFileSize(estimatedResultFileSize);// newJob.print();

							synchronized (System.out) {
								/* we add the newly populated job to the queue of waiting jobs */
								synchronized (this.jobScheduler.getJobGroups()) {
									this.jobScheduler.addNewJobToGroup(newJob, false);
									System.out.println("<<<<< " + newJob.getName()
											+ " <<<<< (new job arrival) / Elapsed Time: "
											+ String.format("%02d:%02d:%02d",
													Math.round((System.currentTimeMillis() - Main.timer) / 10) / 3600,
													(Math.round((System.currentTimeMillis() - Main.timer) / 10) % 3600)
															/ 60,
													Math.round((System.currentTimeMillis() - Main.timer) / 10) % 60)
											+ " <<<<<");
								}
								;

								System.out.flush();
							}

							try {
								Thread.sleep(this.getPoisson(this.getPoissonDistrMeanValue()) * 1000); /*
																										 * we sleep the
																										 * job arrival
																										 * process in
																										 * order to get
																										 * a Poisson
																										 * distribution
																										 */
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (parser != null) {
					try {
						parser.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private int getPoisson(double poissonDistrMeanValue) {
		double L = Math.exp(-poissonDistrMeanValue);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
	}
}
