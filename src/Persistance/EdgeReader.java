package Persistance;

import Kochfractal.Edge;
import javafx.scene.paint.Color;
import timeutil.TimeStamp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Max Meijer on 10/05/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class EdgeReader {
    private ArrayList<Edge> edges = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(EdgeReader.class.getName());

    public void readFromTextFile(boolean useBuffer, File file) {
        TimeStamp ts = new TimeStamp();
        edges.clear();
        Scanner c = null;
        if (!useBuffer) {
            try {
                c = new Scanner(file);
                ts.setBegin("Start reading from text without buffer");
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        } else {
            try {
                c = new Scanner(new BufferedReader(new FileReader(file)));
                ts.setBegin("Start reading from text with buffer");
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        try {
            assert c != null;
            while (c.hasNextLine()) {
                String[] result = c.next().split(",");
//                for (String s : result) {
//                    System.out.println(s);
//                }
                Edge e = new Edge(Double.parseDouble(result[0]), Double.parseDouble(result[1]), Double.parseDouble(result[2]), Double.parseDouble(result[3]), Color.web(result[4]));
                edges.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }
        ts.setEnd("Finished reading text file");
        System.out.println(ts.toString());
        System.out.println(edges.size() + " edges found!");
    }

    public void readFromBinaryFile(boolean useBuffer, File file) {
        TimeStamp ts = new TimeStamp();
        edges.clear();
        ObjectInputStream stream = null;
        ArrayList<EdgeData> edgeData = new ArrayList<>();

        if(useBuffer) {
            try {
                stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
                ts.setBegin("Start reading from binary without buffer");
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        } else {
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                ts.setBegin("Start reading from binary without buffer");
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        if(stream != null) {
            try {
                edgeData = (ArrayList<EdgeData>) stream.readObject();
                ts.setEnd("Finished reading binary file");
                System.out.println(ts.toString());
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.severe(e.getMessage());
            }

            for(EdgeData ed :edgeData) {
                Edge e = new Edge(ed);
                edges.add(e);
            }
        }
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
