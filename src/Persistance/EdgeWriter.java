package Persistance;

import Kochfractal.Edge;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.logging.Logger;

import static javax.xml.crypto.dsig.CanonicalizationMethod.EXCLUSIVE;

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

    public void writeToMappedFile(boolean useBuffer, String fileName, int level) {
        try {
            System.out.println("Start Writing");

            File file = new File(fileName);
            if (file.exists())
                file.delete();
            file.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel fc = raf.getChannel();

            FileLock lock;

            raf.writeInt(level);
            raf.writeBytes("\n");

            String line = "";
            for (Edge e : edges) {
                line = "";
                line += e.X1 + ";";
                line += e.Y1 + ";";
                line += e.X2 + ";";
                line += e.Y2 + ";";
                line += e.color.getRed() + ",";
                line += e.color.getGreen() + ",";
                line += e.color.getBlue() + "\n";

                lock = fc.lock(raf.length(), line.getBytes().length, false);
                raf.writeBytes(line);
                lock.release();
            }

            raf.close();
        } catch (Exception w) {
            w.printStackTrace();
        }


    }

    private ArrayList<EdgeData> createEdgeDataList() {
        ArrayList<EdgeData> edgeDataList = new ArrayList<>();
        for (Edge e : edges) {
            edgeDataList.add(new EdgeData(e));
        }

        return edgeDataList;
    }
}
