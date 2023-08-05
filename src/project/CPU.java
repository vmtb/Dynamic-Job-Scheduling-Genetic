package project;

public class CPU
{
	
	protected int ID = 0;  /* ID used to uniquely identify each CPU */
	protected int numberOfCores = 0;  /* the number of cores of this CPU */
	protected double clockRateInHz = 0;  /* the clock rate of this CPU */
	protected String familyName = "";  /* the name of the family this CPU belong to e.g: core, etc */
	protected String denomination = "";  /* this CPU denomination e.g: i3, i5, i7, etc */
	
	public CPU(int numberOfCores, double clockRateInHz, String familyName, String denomination)  /* this constructor creates and initializes a new CPU with
	the given parameters */
	{
		this.numberOfCores = numberOfCores;
		this.clockRateInHz = clockRateInHz;
		this.familyName = familyName;
		this.denomination = denomination;
	}

	public CPU()  /* this constructor creates a new empty CPU */
	{
		
	}

	public CPU duplicate(CPU cpu)  /* creates a new copy of the passed in CPU */
	{
		return new CPU(cpu.getNumberOfCores(), cpu.getClockRateInHz(), cpu.getFamilyName(), cpu.getDenomination());
	}
	
	public int getNumberOfCores()  /* returns the number of cores for this CPU */
	{
		return this.numberOfCores;
	}

	public void setNumberOfCores(int numberOfCores)  /* sets the number of cores for this CPU */
	{
		this.numberOfCores = numberOfCores;
	}

	public double getClockRateInHz()  /* returns the clock rate for this CPU */
	{
		return this.clockRateInHz;
	}

	public void setClockRateInHz(double clockRateInHz)  /* sets the clock rate for this CPU */
	{
		this.clockRateInHz = clockRateInHz;
	}

	public String getFamilyName()  /* return this CPU family name */
	{
		return this.familyName;
	}

	public void setFamilyName(String familyName)  /* sets this CPU family name */
	{
		this.familyName = familyName;
	}

	public String getDenomination()  /* returns this CPU denomination */
	{
		return this.denomination;
	}

	public void setDenomination(String denomination)  /* sets this CPU denomination */
	{
		this.denomination = denomination;
	}
	
	public boolean outperforms(CPU cpuToCompareWith, boolean multithreadedProgramExecution)  /* returns true if this CPU outperforms the one passed in as a
	parameter for the specified type of program, and false otherwise */
	{
		if ((this.familyName + " " + this.denomination).equals("core i9"))  /* if this CPU is a core i9 */
		{
			if (!(cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i9"))  /* if the passed in CPU is different from a core i9 */
			{
				return true;  /* this CPU takes over */
			}
			else if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i9"))  /* if both CPUs are core i9 */
			{
				if (multithreadedProgramExecution)  /* if there are to be used to execute a multithreaded program, the CPU with the highest number of cores takes over */
				{
					if (this.numberOfCores > cpuToCompareWith.numberOfCores)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else  /* if there are to be used to execute a single threaded program, the CPU with the highest clock rate takes over */
				{
					if (this.clockRateInHz > cpuToCompareWith.clockRateInHz)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		else if ((this.familyName + " " + this.denomination).equals("core i7"))  /* if this CPU is a core i7 */
		{
			if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i9"))  /* if the passed in CPU is a core i9 */
			{
				return false;  /* the passed in CPU takes over */
			}
			else if (!(cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i7"))  /* if the passed in CPU is neither a core i9 nor a core i7 */
			{
				return true;  /* this CPU takes over */
			}
			else if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i7"))  /* if both CPUs are core i7 */
			{
				if (multithreadedProgramExecution)  /* if there are to be used to execute a multithreaded program, the CPU with the highest number of cores takes over */
				{
					if (this.numberOfCores > cpuToCompareWith.numberOfCores)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else  /* if there are to be used to execute a single threaded program, the CPU with the highest clock rate takes over */
				{
					if (this.clockRateInHz > cpuToCompareWith.clockRateInHz)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		else if ((this.familyName + " " + this.denomination).equals("core i5"))  /* if this CPU is a core i5 */
		{
			if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i9") 
					|| (cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i7"))  /* if the passed in CPU is a core i7 or core i9, it takes over */
			{
				return false;
			}
			else if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i3"))  /* if the passed in CPU is a core i3, this CPU takes over */
			{
				return true;
			}
			else if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i5"))  /* if both CPUs are core i5 */
			{
				if (multithreadedProgramExecution)  /* if there are to be used to execute a multithreaded program, the CPU with the highest number of cores takes over */
				{
					if (this.numberOfCores > cpuToCompareWith.numberOfCores)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else  /* if there are to be used to execute a single threaded program, the CPU with the highest clock rate takes over */
				{
					if (this.clockRateInHz > cpuToCompareWith.clockRateInHz)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		else if ((this.familyName + " " + this.denomination).equals("core i3"))  /* if this CPU is a core i3 */
		{
			if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i9") 
					|| (cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i7") 
					|| (cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i5"))  /* any passed in CPU that is not core i3 takes over */
			{
				return false;
			}
			else if ((cpuToCompareWith.familyName + " " + cpuToCompareWith.denomination).equals("core i3"))  /* if both CPUs are core i3 */
			{
				if (multithreadedProgramExecution)  /* if there are to be used to execute a multithreaded program, the CPU with the highest number of cores takes over */
				{
					if (this.numberOfCores > cpuToCompareWith.numberOfCores)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else  /* if there are to be used to execute a single threaded program, the CPU with the highest clock rate takes over */
				{
					if (this.clockRateInHz > cpuToCompareWith.clockRateInHz)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isEquivalent(CPU cpuToCompareWith)  /* returns true if the compared CPUs are equivalent in terms of performance and false otherwise */
	{
		if (this.familyName.equals(cpuToCompareWith.familyName) && this.denomination.equals(cpuToCompareWith.denomination) 
				&& this.numberOfCores == cpuToCompareWith.numberOfCores && this.clockRateInHz == cpuToCompareWith.clockRateInHz)
		{
			return true;
		}
		
		return false;
	}
}
