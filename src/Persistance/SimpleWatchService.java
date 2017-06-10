package Persistance;

import com.company.JSF31KochFractalFXReader;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Max Meijer on 07/06/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class SimpleWatchService extends Thread {

    JSF31KochFractalFXReader reference = null;
    private AtomicBoolean stop = new AtomicBoolean(false);


    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() {
        // Do whatever action you want here
        // System.out.println("Did me a change");
        reference.fileIsReader();
    }

    public SimpleWatchService(JSF31KochFractalFXReader app) {
        //System.out.println("Initialising watch service");
        this.reference = app;
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get("C:\\Users\\Max\\Source\\Repos\\Semester 3\\JSF_Kochfractal_Streamer\\export");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(25, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY ||
                            kind == java.nio.file.StandardWatchEventKinds.ENTRY_CREATE ) {
                        System.out.println("File is changed.");
                        doOnChange();
                        break;
                    }
                    boolean valid = key.reset();
                    if (!valid) { break; }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            // Log or rethrow the error
        }
    }
}
