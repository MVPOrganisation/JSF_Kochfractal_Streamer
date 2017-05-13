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
        Scanner c = null;
        if (!useBuffer) {
            try {
                c = new Scanner(file);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        } else {
            try {
                c = new Scanner(new BufferedReader(new FileReader(file)));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        try {
            while (c.hasNextLine()) {
                String[] result = c.next().split(",");
                for (String s : result) {
                    System.out.println(s);
                }
                Edge e = new Edge(Double.parseDouble(result[0]), Double.parseDouble(result[1]), Double.parseDouble(result[2]), Double.parseDouble(result[3]), Color.web(result[4]));
                edges.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }
        System.out.println(edges.size() + " edges found!");
    }

    public void readFromBinaryFile(boolean useBuffer, File file) {
        ObjectInputStream stream = null;

        if(useBuffer) {
            try {
                stream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        } else {
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        if(stream != null) {
            try {
                edges.add((Edge) stream.readObject());
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
