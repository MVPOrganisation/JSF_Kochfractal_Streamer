package Persistance;

import Kochfractal.Edge;
import timeutil.TimeStamp;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.xml.crypto.dsig.CanonicalizationMethod.EXCLUSIVE;

/**
 * Created by Max Meijer on 10/05/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class EdgeWriter implements Observer {

    private ArrayList<Edge> edges = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(EdgeWriter.class.getName());
    private int fileSize = 20971520; //20MB
    private final int EDGE_BYTE_SIZE = 8*7;

    @Override
    public void update(java.util.Observable o, Object arg) {
        edges.add((Edge) arg);
    }

    public void writeToTextFile(Boolean useBuffer, String file) {
        File oldFile = new File(file);
        if (!useBuffer) {
            try (PrintStream fw = new PrintStream(oldFile)) {
                for (Edge e : edges) {
                    fw.println(e.toString());
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } else {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(oldFile)))) {
                for (Edge e : edges) {
                    out.println(e.toString());
                }
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        int lastDot = file.lastIndexOf('.');
        String newFileName = file.substring(0, lastDot) + "_completed" + file.substring(lastDot);

        File newFile = new File(newFileName);
        oldFile.renameTo(newFile);
    }

    public void writeToBinaryFile(boolean useBuffer, String file) {
        ArrayList<EdgeData> edgeDataList = createEdgeDataList();

        if (!useBuffer) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(edgeDataList);
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        } else {
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
                for (Edge e : edges) {
                    EdgeData ed = new EdgeData(e);
                    edgeDataList.add(ed);
                }
                oos.writeObject(edgeDataList);
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    public void writeToMappedFile(String fileName) {
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Write Mapped");
        try
        {
            RandomAccessFile memoryMappedFile = new RandomAccessFile(fileName, "rw");
            MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 4);
            FileLock exclusiveLock = null;

            final int EDGECOUNT = edges.size();
            System.out.println("Number of Edges: " + EDGECOUNT);
            out.putInt(EDGECOUNT);

            for (int i = 0; i <  EDGECOUNT; i++) {
                int writePos = 8 + EDGE_BYTE_SIZE*i;
                System.out.format("Writing to position: %1$s\n", writePos);
                out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, writePos, EDGE_BYTE_SIZE);
                exclusiveLock = memoryMappedFile.getChannel().lock(writePos, EDGE_BYTE_SIZE, false);

                Edge e = edges.get(i);
                EdgeData eD = new EdgeData(e);
                out.putDouble(eD.X1);
                out.putDouble(eD.Y1);
                out.putDouble(eD.X2);
                out.putDouble(eD.Y2);
                out.putDouble(eD.Red);
                out.putDouble(eD.Green);
                out.putDouble(eD.Blue);

                exclusiveLock.release();
                out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 4, 4);
                exclusiveLock = memoryMappedFile.getChannel().lock(4, 4, true);
                out.putInt(i);
                exclusiveLock.release();
            }
            memoryMappedFile.getChannel().close();
            memoryMappedFile.close();



        }
        catch (Exception ex)
        {
            Logger.getLogger(EdgeWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        timeStamp.setEnd("Stop - Write Mapped");
        System.out.println(timeStamp.toString());


    }

    private ArrayList<EdgeData> createEdgeDataList() {
        ArrayList<EdgeData> edgeDataList = new ArrayList<>();
        for (Edge e : edges) {
            edgeDataList.add(new EdgeData(e));
        }

        return edgeDataList;
    }
}
