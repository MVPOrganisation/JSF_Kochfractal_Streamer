package Persistance;

import Kochfractal.Edge;

import java.io.*;
import java.util.ArrayList;
import java.util.Observer;
import java.util.logging.Logger;

/**
 * Created by Max Meijer on 10/05/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class EdgeWriter implements Observer {

    private ArrayList<Edge> edges = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(EdgeWriter.class.getName());

    public EdgeWriter() {
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        edges.add((Edge) arg);
    }

    public void writeToTextFile(Edge edge, Boolean useBuffer, String file) {
        if(!useBuffer) {
            try (PrintStream fw = new PrintStream(file)) {
                for (Edge e : edges) {
                    fw.println(e.toString());
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } else {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                for (Edge e : edges) {
                    out.println(e.toString());
                }
            }catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    public void writeToBinaryFile(boolean useBuffer, String file) {
        if(!useBuffer) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                for (Edge e : edges) {
                    EdgeData ed = new EdgeData(e);
                    oos.writeObject(ed);
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream (new FileOutputStream(file)))) {
                for (Edge e : edges) {
                    EdgeData ed = new EdgeData(e);
                    oos.writeObject(ed);
                }
            }catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }
}
