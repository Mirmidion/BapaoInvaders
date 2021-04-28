package com.mygdx.game.Controllers;

import com.fazecast.jSerialComm.*;
import java.util.Scanner;

public class Arduino {
    public static int chosenPort;

    public static void setup(){
        SerialPort ports[] = SerialPort.getCommPorts();

        System.out.println("Selecteer een poort: ");
        for (SerialPort port : ports){
            System.out.println(port.getSystemPortName());
        }

        Scanner s = new Scanner(System.in);
        chosenPort = s.nextInt();

        SerialPort port = ports[chosenPort - 1];


        if(port.openPort()){
            System.out.println("de poort is geopend");
        } else {
            System.out.println("poort niet geopend");
            return;
        }

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);


    }

    public static boolean buttonPressed(Scanner data){


        while(data.hasNextLine()){
            System.out.println(data.nextLine());
        }
    }


}

