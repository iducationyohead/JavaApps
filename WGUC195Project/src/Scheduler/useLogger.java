package Scheduler;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Jeremy H
 */
public class useLogger {
  
    private static FileHandler fh = null;
    private static final Logger LOG = Logger.getLogger(useLogger.class.getName());
 
    public static void startLog() {
        SimpleFormatter sf;

        try {
            fh = new FileHandler("AppUse-Log.%u.%g.txt", 1024 * 1024, 15, true);
        } catch (IOException ex) {
            Logger.getLogger(useLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(useLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger log = Logger.getLogger("");
        sf = new SimpleFormatter();
        fh.setFormatter(sf);
        log.addHandler(fh);
        log.setLevel(Level.INFO);

       
    }
}
