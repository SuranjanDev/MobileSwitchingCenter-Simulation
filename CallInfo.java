package mobileswitchingcenter;

/**
 * Class to store all details related to a call
 * @author Suranjan
 * @version 1.0
 */
public class CallInfo {
    private int time;   // Time of the call
    private int cell;   // Which cell the call has originated
    private int duration;   // duration of the call
    private int channel;    // Channel allocated for the call
    private boolean isDisconnect;   // If it's a call initiation or disconnect
    /**
     * @param time  // Time of the call
     * @param cell  // Which cell the call has originated
     * @param duration// duration of the call
     * @param isDiscon// If it's a call initiation or disconnect
     * @param chan // Channel allocated for the call
     */
    public CallInfo(int time,int cell,int duration, boolean isDiscon,int chan){
        this.time=time;
        this.cell=cell;
        this.duration=duration;
        this.isDisconnect=isDiscon;
        this.channel=chan;
    }
    public boolean getDisconnect(){
        return this.isDisconnect;
    }
    public int getTime(){
        return this.time;
    }
    public int getChannel(){
        return this.channel;
    }
    public int getCell(){
        return this.cell;
    }    
    public int getDuration(){
        return this.duration;
    }
    public void setChannel(int chan){
        this.channel=chan;
    }
}
