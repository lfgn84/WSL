package server;

import java.io.*;
import java.util.Properties;

public class MongoProcess implements Runnable {
    private Properties prop=null;
    private PrintWriter log=null;

    public MongoProcess(Properties prop){
        this.prop=prop;
        try {
            log = new PrintWriter(new File(prop.getProperty("WSL.MongoServer.Logfile")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startMongod()
        {
         //   System.out.println("Hello World");
            //String command = "git describe --abbrev=0 --tags";
            String command =prop.getProperty("WSL.mongopath")+ "mongod --bind_ip 127.0.0.1";
            try {
                Process p = Runtime.getRuntime().git exec(command);
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                String text = command + "\n";
              //  System.out.println(text);
                while ((line = input.readLine()) != null) {
                   // text += line;
                   // System.out.println("Line: " + line);
                    log.append("Line: ").append(line).append("\n");
                    log.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        this.startMongod();
    }
}
