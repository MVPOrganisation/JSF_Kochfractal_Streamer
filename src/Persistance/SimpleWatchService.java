package Persistance;

import com.company.JSF31KochFractalFX;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Max Meijer on 07/06/2017.
 * Fontys University of Applied Sciences, Eindhoven
 */
public class SimpleWatchService extends Thread {

    JSF31KochFractalFX reference = null;
    private AtomicBoolean stop = new AtomicBoolean(false);


    public boolean isStopped() { return stop.get(); }
    public void stopThread() { stop.set(true); }

    public void doOnChange() {
        // Do whatever action you want here
        System.out.println("Did me a change");
        reference.readfileFromWatcher();
    }

    public SimpleWatchService(JSF31KochFractalFX app) {
        this.reference = app;
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get("C:\\Users\\max1_\\Source\\Repos\\Semester 3\\JSF_Kochfractal_Streamer\\export");
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try { key = watcher.poll(1, TimeUnit.MILLISECONDS); }
                catch (InterruptedException e) { return; }
                if (key == null) { Thread.yield(); continue; }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    System.out.println(filename.toString());
                    System.out.println(kind);

                    if (filename.toString().equals("Text_test_completed.txt")) {
                        System.out.println("changed");
                        doOnChange();
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
