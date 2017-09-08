package mobileswitchingcenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * @author Suranjan
 * @version 1.0
 */

public class simulate {

    private int noOfCluster = 3;    // Number of clusters
    private int clusterSize = 3;    // Cluster Size
    private int noOfChannels = 15;  // Number of channels

    private int cellDistance[][] = new int[clusterSize * noOfCluster][clusterSize * noOfCluster];// Cell Distance matrix
    private ArrayList<CallInfo> callRec = new ArrayList<CallInfo>();    //Event queue to store the call details. It is of type CallInfo.
    private CellInfo clusterDetail[][] = new CellInfo[noOfCluster][noOfChannels];   //2D matrix of type CellInfo to store the cluster and channel information
    private String output = "", comment = "";   // To display the final output
    private int newCall = 0, disconnectCall = 0, totalSIR = 0; // Variables to store the total no of call attempts, successful calls and total SIR for successful calls.
   /**
     * Method to initialize the call queue, Cluster information & Distance matrix from three different text files.
     * @throws FileNotFoundException 
     */
    public void initialize() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("input-low.txt"));
        String inp[] = new String[4];
        while (sc.hasNextLine()) {
            inp = sc.nextLine().split("\\s");
            /*
            Initialize the Call Queue: time,cell,duration, is it a call disconnect(false initially for all), channel allocated(-1 for all)
            */
            callRec.add(new CallInfo(Integer.parseInt(inp[1]), Integer.parseInt(inp[2]), Integer.parseInt(inp[3]), false, -1));
        }
        sc = new Scanner(new File("ClusterInfo.txt"));
        inp = new String[noOfChannels];
        for (int i = 0; i < 3; i++) {
            inp = sc.nextLine().split("\\s");
            for (int j = 0; j < noOfChannels; j++) {
                /*
                Initialize the ClusterDetail: cell no, SIR(35 initially for all), is occupied(initially false for all)
                */
                clusterDetail[i][j] = new CellInfo(Integer.parseInt(inp[j]), 35, false);
            }
        }
        inp = new String[noOfCluster * clusterSize];
        sc = new Scanner(new File("CellDistance.txt"));
        for (int i = 0; i < clusterSize * noOfCluster; i++) {
            inp = sc.nextLine().split("\\s");
            for (int j = 0; j < clusterSize * noOfCluster; j++) {
                //Initialize the distance matrix between cell i and j.
                cellDistance[i][j] = Integer.parseInt(inp[j]);
            }
        }
    }
     /**
     * The main method for the event driven simulation. It removes one event out of the queue and analyzes it.
     */
    public void start() {
        CallInfo call;  // To get the current call
        CallInfo newCallInfo;// To store the new call info.
        while (!callRec.isEmpty()) {    // Till the queue is empty
            call = callRec.get(0);  // Get the current event.
            if (checkState(call)) { //Check the state of the call to determine its type. If it's a call request, check if it can get a new channel.
                // Create a new call,if the call gets a new channel
                newCallInfo = new CallInfo(call.getTime() + call.getDuration(), call.getCell(), call.getDuration(), true, getChannel(call.getCell()));
                addQueue(newCallInfo);  // Add it to the event queue and sort it as per the time. 
            }
            updateState(call);//Update the state based on the call details.
            int sir = 0;    // To get the SIR a call has if it's successfully gets a channel.
            if (!call.getDisconnect()) {    // Call type should be a call request.
                for (int i = 0; i < noOfChannels; i++) {
                    // Get the current cluster and cell
                    if (clusterDetail[(callRec.get(0).getCell() - 1) / 3][i].getCellNo() == callRec.get(0).getCell() && (i == (callRec.get(0).getChannel() - 1))) {
                        sir = clusterDetail[(callRec.get(0).getCell() - 1) / 3][i].getSNR();// Get the SIR in that channel for that cell
                        totalSIR+=sir;// Add it to the total SIR
                        output += " SIR= " + sir +"dB"+'\n';//Display it in the output
                    }
                }
                output += comment + '\n';// Display the comment for the curernt call.
            }
            callRec.remove(0);// Remove the current call out of the queue.
        }
        double avgSIR=(double)totalSIR / (double)disconnectCall;// Average SIR
        System.out.print(output);
        System.out.println("Totals: "+disconnectCall+" calls accepted, "+(newCall-disconnectCall)+" calls rejected, "+((newCall-disconnectCall)*100/newCall)+"%GOS, Average SIR=" + new DecimalFormat("#.##").format(avgSIR));
    }
    /**
     * It checks the state of a call. It returns false if no channels can be allocated to a call request or when a call is disconnected.
     * @param call
     * @returns true if a channel can be successfully allocated to a call initiation request.
     */
    public boolean checkState(CallInfo call) {
        if (call.getDisconnect()) { // If the call is of disconnect type.
            output += "Disconnect: Number " + (++disconnectCall) + " Start time= " + call.getTime() + " End time= " + (call.getTime() + call.getDuration()) + " Cell= " + call.getCell() + " Duration= " + call.getDuration() + " Channel= " + call.getChannel() + '\n';
            return false;
        }
        int channel = getChannel(call.getCell());// Search for a channel for the call.
        if (channel == -1) {    // If there are no channels available. Return false, then the call will be dropped.
            output += "New Call: Number " + (++newCall) + " Time= " + call.getTime() + " Cell= " + call.getCell() + " Duration= " + call.getDuration() + " Rejected " + '\n';
            return false;
        } else {    // Set the channel to the call and return true at the end.
            call.setChannel(channel);
        }
        output += "New Call: Number " + (++newCall) + " Time= " + call.getTime() + " Cell=" + call.getCell() + " Duration= " + call.getDuration() + " Accepted " + " Channel= " + call.getChannel();
        return true;
    }
    /**
     * Given the cell number. This method returns the channel that has highest SIR or in case no channels can be allocated, it returns -1.
     * @param cellNo
     * @return channel number if available or -1.
     */
    public int getChannel(int cellNo) {
        int minSIR = 22;// Minimum SIR required
        int channel = -1;
        int cluster = (cellNo - 1) / 3; // Get the cluster number
        for (int i = 0; i < noOfChannels; i++) {
            // Look for the channel having highest SIR and which is free and can be allocated within that cell.
            if (clusterDetail[cluster][i].getSNR() > minSIR && !clusterDetail[cluster][i].getOccupied() && clusterDetail[cluster][i].getCellNo() == cellNo) {
                channel = i + 1;
                minSIR = clusterDetail[cluster][i].getSNR();    // Update the minSIR value.
            }
        }
        return channel;
    }
    /**
     * Updates the state of the cluster table and SIR value after every event.
     * @param call 
     */
    public void updateState(CallInfo call) {
        int cluster = (call.getCell() - 1) / 3;// get the cluster number
        int channel = call.getChannel();    // get the channel number
        if (call.getDisconnect()) { // If its a call disconnect. Free the channel.
            clusterDetail[cluster][channel - 1].setOccupied(false);
            setSir(call.getCell(), call.getChannel());// Update the SIR value
            return;
        }
        if (channel != -1) {// If a channel is allocated to a call. Mark it as occupied and set the SIR value.
            clusterDetail[cluster][channel - 1].setOccupied(true);
            setSir(call.getCell(), call.getChannel());
            comment = getReason(false, call.getCell(), call.getChannel());// to get the necessary comment about a call.
        } else {
            comment = getReason(true, call.getCell(), -1);//to get the necessary comment about a call.
        }
    }
    /**
     * This method adds a new call to the queue and then sorts it according to the time.
     * @param call 
     */
    public void addQueue(CallInfo call) {
        callRec.add(call);
        Collections.sort(callRec, (CallInfo s1, CallInfo s2) -> { 
            return s1.getTime() - s2.getTime(); // Sort the call queue as per time.
        });
    }
    /**
     * The setSir method computes the SIR for a particular channel in a cell.
     * @param cellNo
     * @param channel 
     */
    public void setSir(int cellNo, int channel) {
        int clusterNo = (cellNo - 1) / 3;   // Determine the cluster no. for a cell
        int sir;
        double numerator = Math.pow(1000, -4);// Numerator value for SIR
        double denominator = 0;//Initially the denominator is 0.
        for (int i = 0; i < clusterSize; i++) {
            int flag = -1;  // Determines channel in the co-channel cell is occupied 
            denominator = 0;
            for (int j = 0; j < clusterSize; j++) {
                if (clusterDetail[j][channel - 1].getOccupied() && (i != j)) { // If the co-channel is occupied, calculate the denominator value
                    flag = 0;
                    denominator += Math.pow(cellDistance[clusterDetail[j][channel - 1].getCellNo() - 1][clusterDetail[i][channel - 1].getCellNo() - 1], -4);
                }
            }
            if (flag == -1) {   // If all the co-channels are free
                clusterDetail[i][channel - 1].setSNR(35);
            } else {    // Convert the value of SIR to db using 10*log(SIR) formula.
                sir = (int) ((int) 10 * Math.log10(numerator / denominator));
                clusterDetail[i][channel - 1].setSNR(sir);
            }
        }
    }
    /**
     * This method gives the reason behind every call drop or if there is any interference for any on going call.
     * @param callDropped true if the call is dropped else false
     * @param cellNo
     * @param channelNo
     * @return 
     */
    public String getReason(boolean callDropped, int cellNo, int channelNo) {
        String out = "";
        int flag = -1, clusterNo = ((cellNo - 1) / 3); 
        if (callDropped) {// if the call is dropped
            for (int i = 0; i < noOfChannels; i++) {// Get the reason. If it's a low SIR or is the channel already occupied.
                if (clusterDetail[clusterNo][i].getOccupied() && cellNo == clusterDetail[clusterNo][i].getCellNo()) {
                    out += " " + (i + 1) + "/In Use";
                } else if (!clusterDetail[clusterNo][i].getOccupied() && cellNo == clusterDetail[clusterNo][i].getCellNo()) {
                    out += " " + (i + 1) + "/Low SIR="+clusterDetail[clusterNo][i].getSNR()+"dB";
                }
            }
        } else {    // If the call is succesfful, then get the interfearers if any.
            out = "Interferers: ";
            for (int i = 0; i < clusterSize; i++) {
                if (clusterNo != i) { //Get the list of interfearers along with the distance from the current cell.
                    if (clusterDetail[i][channelNo - 1].getOccupied()) {
                        out += "" + (clusterDetail[i][channelNo - 1].getCellNo()) + "/" + cellDistance[clusterDetail[i][channelNo - 1].getCellNo() - 1][clusterDetail[clusterNo][channelNo - 1].getCellNo() - 1];
                        flag = 0;
                    }
                }
            }
            if (flag == -1) {// If there are no interfearers
                out += " None";
            }
        }
        return out;
    } 
}
