/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistance;

import Kochfractal.Edge;
import com.company.JSF31KochFractalFXReader;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by Max Meijer on 07/06/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class KochReaderWithFileLock implements Runnable {

    private static final String MAPPEDKOCHPATH = "C:\\Users\\Max\\Source\\Repos\\Semester 3\\JSF_Kochfractal_Streamer\\export\\mapped.dat";
    private static final int FILESIZE = 20971520; //20MB
    private final int EDGE_BYTE_SIZE = 8 * 7;
    private JSF31KochFractalFXReader reader;

    public KochReaderWithFileLock(JSF31KochFractalFXReader manager) {
        this.reader = manager;
    }

    @Override
    public void run() {
        FileLock lock = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(MAPPEDKOCHPATH, "rw");
            FileChannel ch = raf.getChannel();
            MappedByteBuffer in = ch.map(FileChannel.MapMode.READ_ONLY, 0, 4);
            // read the number of edges
            final int numberOfEdges = in.getInt();
            System.out.println("Number of Edges: " + numberOfEdges);

            for (int i = 0; i < numberOfEdges; i++) {
                in = ch.map(FileChannel.MapMode.READ_ONLY, 4, 4);
                lock = ch.lock(4, 4, true);
                while (i - 1 >= in.getInt()) {
                    in.clear();
                }
                lock.release();
                int readingPos = 8 + EDGE_BYTE_SIZE * i;
                System.out.format("Reading position: %1$s\n", readingPos);
                in = ch.map(FileChannel.MapMode.READ_ONLY, readingPos, EDGE_BYTE_SIZE);
                lock = ch.lock(readingPos, EDGE_BYTE_SIZE, false);
                Edge e = new Edge(in.getDouble(), in.getDouble(),
                        in.getDouble(), in.getDouble(),
                        in.getDouble(), in.getDouble(), in.getDouble());
                System.out.println(e.toString());
                this.reader.requestDrawEdge(e);
                lock.release();
            }
            ch.close();
            raf.close();
            this.reader.startWatching();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void deleteFile() {
        try {

            File file = new File(MAPPEDKOCHPATH);

            if (file.delete()) {
                System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
