package mobileswitchingcenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Suranjan
 */
public class MobileSwitchingCenter {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        simulate MSC=new simulate();
        MSC.initialize();   // Initialize the class members 
        MSC.start();// Start the simulation
    }
    
}
