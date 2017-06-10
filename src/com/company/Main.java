package com.company;

import Persistance.EdgeWriter;
import Kochfractal.KochFractal;
import timeutil.TimeStamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        final Logger LOGGER = Logger.getLogger(Main.class.getName());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter kochfractal level:");
        int level = 1;

        try{
            level = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.err.println("Invalid Format!");
        }catch(IOException e) {
            LOGGER.severe(e.getMessage());
        }

        KochFractal kf = new KochFractal();
        kf.setLevel(level);

        EdgeWriter ew = new EdgeWriter();
        kf.addObserver(ew);

        kf.generateBottomEdge();
        kf.generateLeftEdge();
        kf.generateRightEdge();

        TimeStamp ts = new TimeStamp();
        ts.setBegin("Start mapped");
        ew.writeToMappedFile("export\\mapped.dat");
        ts.setEndBegin("Finished mapped, start bin");

        //ew.writeToBinaryFile(false, "binary_8.ser");
        ts.setEnd("Finished bin");
        System.out.println(ts.toString());

        //ew.writeToTextFile(true, "export\\Text_test.txt");
        System.out.println("Finished");
    }
}
