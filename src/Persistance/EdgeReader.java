package Persistance;

import Kochfractal.Edge;
import javafx.scene.paint.Color;
import timeutil.TimeStamp;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        } catch (NoSuchElementException e) {
            LOGGER.severe(e.getMessage());
        } catch(Exception e) {
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

    public void readFromMappedFile(String fileName) {
        File file = new File(fileName);
        edges.clear();

        FileChannel fileChannel = null;
        MappedByteBuffer buffer = null;
        try {
            fileChannel = new RandomAccessFile(file, "r").getChannel();
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // the buffer now reads the file as if it were loaded in memory.
        assert buffer != null;

        //You can read the file from this buffer the way you like.
        while(buffer.hasRemaining())
        {
            Edge e = new Edge();
            e.X1 = buffer.getDouble();
            e.Y1 = buffer.getDouble();
            e.X2 = buffer.getDouble();
            e.Y2 = buffer.getDouble();
            double r = buffer.getDouble();
            double g = buffer.getDouble();
            double b = buffer.getDouble();
            e.setColor(r,g,b);
            edges.add(e);
        }

        System.out.println(edges.size() + " edges found!");
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
