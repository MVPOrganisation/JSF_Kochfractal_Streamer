package Persistance;

import Kochfractal.Edge;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
        ArrayList<EdgeData> edgeDataList = createEdgeDataList();

        if(!useBuffer) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(edgeDataList);
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream (new FileOutputStream(file)))) {
                for (Edge e : edges) {
                    EdgeData ed = new EdgeData(e);
                    edgeDataList.add(ed);
                }
                oos.writeObject(edgeDataList);
            }catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    public void writeToMappedFile(boolean useBuffer, String fileName) {
        // Create file object
        File file = new File(fileName);

        // Get file channel in readonly mode
        FileChannel fileChannel = null;
        MappedByteBuffer buffer = null;
        try {
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, (7*8)*edges.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write the content using put methods
        assert buffer != null;
        for(Edge e : edges){
            buffer.putDouble(e.X1);
            buffer.putDouble(e.Y1);
            buffer.putDouble(e.X2);
            buffer.putDouble(e.Y2);
            buffer.putDouble(e.color.getRed());
            buffer.putDouble(e.color.getGreen());
            buffer.putDouble(e.color.getBlue());
        }
    }

    private ArrayList<EdgeData> createEdgeDataList() {
        ArrayList<EdgeData> edgeDataList = new ArrayList<>();
        for (Edge e: edges) {
            edgeDataList.add(new EdgeData(e));
        }

        return edgeDataList;
    }
}
