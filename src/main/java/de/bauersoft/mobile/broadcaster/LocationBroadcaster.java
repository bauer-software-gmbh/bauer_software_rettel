package de.bauersoft.mobile.broadcaster;

import de.bauersoft.oldMap.TourLocationDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class LocationBroadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();
    static List<Consumer<TourLocationDTO>> listeners = new CopyOnWriteArrayList<>();
    static List<Consumer<TourLocationDTO>> removalListeners = new CopyOnWriteArrayList<>();

    public static synchronized void register(Consumer<TourLocationDTO> listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(Consumer<TourLocationDTO> listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcastNewLocation(TourLocationDTO loc) {
        for (Consumer<TourLocationDTO> listener : listeners) {
            executor.execute(() -> listener.accept(loc));
        }
    }

    public static synchronized void registerRemovalListener(Consumer<TourLocationDTO> listener) {
        removalListeners.add(listener);
    }

    public static synchronized void unregisterRemovalListener(Consumer<TourLocationDTO> listener) {
        removalListeners.remove(listener);
    }

    public static synchronized void broadcastLocationRemoved(TourLocationDTO removedLocation) {
        for (Consumer<TourLocationDTO> listener : removalListeners) {
            executor.execute(() -> listener.accept(removedLocation));
        }
    }
}