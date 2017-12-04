/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fallintooblivion;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import static java.lang.Thread.sleep;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class WatchDir {
    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private boolean trace = false;
    public static ArrayList foldersToEncrypt = new ArrayList<String>();
    public static ReentrantLock foldersToEncryptLock = new ReentrantLock();
 
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    WatchDir(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        register(dir);
 
        // enable trace after initial registration
        this.trace = true;
    }
    
        private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }
        
        void processEvents() {
        for (;;) {
 
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                System.out.println("watchertake went wrong");
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
 
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();
 
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
 
                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
 
                // print out event
                System.out.println("");
                // System.out.format("%s: %s\n", event.kind().name(), child); // Original print
                System.out.format("Trashed: %s\n", child);
                System.out.print("FallIntoOblivion> ");
                foldersToEncryptLock.lock();
                try{
                    foldersToEncrypt.add(child.toString());
                    // System.out.println(foldersToEncrypt.toString());
                }   finally {
                    foldersToEncryptLock.unlock();
                }
            }
 
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
 
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
            System.out.println("watch process ended");
    }
}
