package Persistance;

import Kochfractal.Edge;
import javafx.scene.paint.Color;

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
        edges.clear();
        try(Scanner c = new Scanner(file)) {
            while(c.hasNextLine()){
                String[] result = c.next().split(",");
                for (String s : result) {
                    System.out.println(s);
                }
                Edge e = new Edge(Double.parseDouble(result[0]), Double.parseDouble(result[1]),Double.parseDouble(result[2]),Double.parseDouble(result[3]), Color.web(result[4]));
                edges.add(e);
            }
        }catch(Exception e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        System.out.println(edges.size() + " edges found!");
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
