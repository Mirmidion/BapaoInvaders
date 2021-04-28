package com.mygdx.game.Controllers.raspController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class RaspController {

    public String deviceInformation;

    private RaspConnection rasp;

    public RaspController(String raspberry_ip){
        deviceInformation = "raspberry pi controller at: "+raspberry_ip;
        rasp = new RaspConnection(raspberry_ip);
        rasp.start();

    }

    public boolean is_pressed(String button){
        try {
            return rasp.result.contains(button);
        }catch (NullPointerException e){
            return false;
        }
    }


}



class RaspConnection extends Thread{

    public boolean connected = true;
    public String result = null;
    public String raspberryIp;

    public RaspConnection(String raspberry_ip){
        raspberryIp = raspberry_ip;

    }

    @Override
    public void run() {
        super.run();

        do{

            try {
                URL url = new URL("http://"+raspberryIp+"/cgi-bin/controller.py");
                //Retrieving the contents of the specified page
                Scanner sc = new Scanner(url.openStream());
                //Instantiating the StringBuffer class to hold the result
                StringBuffer sb = new StringBuffer();
                while (sc.hasNext()) {
                    sb.append(sc.next());
                }
                //Retrieving the String from the String Buffer object
                result = sb.toString();

                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }while(connected);

    }


    public void disconnect(){
        connected = false;

    }
}
