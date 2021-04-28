package com.mygdx.game.Controllers;

import com.fazecast.jSerialComm.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Arduino {
    public static int chosenPort;
    private ArduinoConnection ard;


    public Arduino(){
        SerialPort ports[] = SerialPort.getCommPorts();

        System.out.println("Selecteer een poort: ");
        for (SerialPort port : ports){
            System.out.println(port.getSystemPortName());
        }

        Scanner s = new Scanner(System.in);
        int chosenPort = s.nextInt();

        SerialPort port = ports[chosenPort - 1];

        this.ard = new ArduinoConnection(port);
        this.ard.start();
        }

    public boolean is_pressed(String button){
        try {
            return ard.result.contains(button);
        }catch (NullPointerException e){
            return false;
        }



    }

}

class ArduinoConnection extends Thread{

    public boolean connected = true;
    public String result = null;
    public SerialPort port;

    public ArduinoConnection(SerialPort port){
        this.port = port;

        if(port.openPort()){
            System.out.println("de poort is geopend");
        } else {
            System.out.println("poort niet geopend");
            return;
        }

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);



    }

    @Override
    public void run() {
        super.run();

        do{
                Scanner data = new Scanner(port.getInputStream());
                while(data.hasNextLine()) {
                    result = data.nextLine();
                    System.out.println(result);

                }

        }while(connected);

    }


    public void disconnect(){
        connected = false;

    }
}

