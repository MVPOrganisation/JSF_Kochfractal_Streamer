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

        TimeStamp text = new TimeStamp();
        text.setBegin("Starting text no buffer");
        ew.writeToTextFile(null, false, "NoBuf.txt");
        text.setEndBegin("Finished text no buffer, starting text buffer");
        ew.writeToTextFile(null, true, "WithBuf.txt");
        text.setEnd("Finished text buffer");

        System.out.println(text);

        TimeStamp bin = new TimeStamp();
        bin.setBegin("Starting binary no buffer");
        ew.writeToBinaryFile(false, "NoBuf.ser");
        bin.setEndBegin("Finished binary no buffer, starting binary buffer");
        ew.writeToBinaryFile(true, "WithBuf.ser");
        bin.setEnd("Finished binary buffer");

        System.out.println(bin);

        System.out.println("Finished");
    }
}
