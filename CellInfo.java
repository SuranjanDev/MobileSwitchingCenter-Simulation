package mobileswitchingcenter;

/**
 * Class to store all information related to clusters and channels.
 * @author Suranjan
 * @version 1.0
 */
public class CellInfo {
    private int cellNo;// To store the cell number for a current cluster and channel.
    private int SNR;// To store the current value of SIR.(I named it SNR here).
    private boolean occupied;// To check if the current channel for that particular cluster is occupied
    /**
     * @param cn    cell number
     * @param snr   SIR value
     * @param occ   if the cluster is occupied
     */
    public CellInfo(int cn,int snr,boolean occ){
        this.cellNo=cn;
        this.SNR=snr;
        this.occupied=occ;
    }
    public void setCellNo(int cn){
        this.cellNo=cn;
    }
    public void setSNR(int snr){
        this.SNR=snr;
    }
    public void setOccupied(boolean oc){
        this.occupied=oc;
    }
    public int getCellNo(){
        return this.cellNo;
    }
    public int getSNR(){
        return this.SNR;
    }
    public boolean getOccupied(){
        return this.occupied;
    }
}
