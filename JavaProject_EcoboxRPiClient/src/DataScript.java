/***
 * 
 * 
 * 
 * 
 * 
 *******************************************************************************************************************************************
 *                                                                                                                                         *
 *     /\    DISCLAIMER     UGLY, UN-OPTIMIZED, "ALPHA-PROTOTYPING" CODE                                                                   *
 *    /  \   DISCLAIMER     DO NOT READ FURTHER UNTIL YOU HAVE FOUND A CURE FOR EYE CANCER                                                 *
 *   / !! \  DISCLAIMER     #KAPPA                                                                                                         *
 *  /______\ DISCLAIMER     Seriously though. Don't judge, this was written in a rush and will be improved, revised, and refactored soon.  *
 *                                                                                                                                         *
 *******************************************************************************************************************************************
 *
 *
 *
 *
 * (I'll only warn you once)
 ***/


public abstract class DataScript implements DataRetriever, Comparable<DataScript>  {

	public final String NAME;	
	public boolean currentlyStreaming = false;
	public final PROTOCOL normalProtocol;
	public final PROTOCOL streamProtocol;
	public final long NORMAL_INTERVAL;
	public final Integer priority;
	
	long time = 0;
	long timesSent = 0;
	
	public boolean update(long runningSince){
		if(runningSince/NORMAL_INTERVAL == timesSent){
			timesSent++;
			return true;
		}
		return false;
	}
	
	public DataScript(String name, long interval, PROTOCOL nP, PROTOCOL sP, int priority){
		this.NAME = name;
		this.NORMAL_INTERVAL = interval;
		this.normalProtocol = nP;
		this.streamProtocol = sP;
		this.priority = priority;
	}
	
	
	@Override
    public int compareTo(DataScript other){
		return priority.compareTo(other.priority);
    }
	
}

interface DataRetriever {
	public Object retrieveData();
}