package de.bauersoft.mobile.broadcaster;

import de.bauersoft.mobile.model.DTO.UserLocationDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class LocationBroadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();
    static List<Consumer<UserLocationDTO>> listeners = new CopyOnWriteArrayList<>();
    static List<Consumer<UserLocationDTO>> removalListeners = new CopyOnWriteArrayList<>();

    public static synchronized void register(Consumer<UserLocationDTO> listener) {
        listeners.add(listener);
    }

    public static synchronized void unregister(Consumer<UserLocationDTO> listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcastNewLocation(UserLocationDTO loc) {
        for (Consumer<UserLocationDTO> listener : listeners) {
            executor.execute(() -> listener.accept(loc));
        }
    }

    public static synchronized void registerRemovalListener(Consumer<UserLocationDTO> listener) {
        removalListeners.add(listener);
    }

    public static synchronized void unregisterRemovalListener(Consumer<UserLocationDTO> listener) {
        removalListeners.remove(listener);
    }

    public static synchronized void broadcastLocationRemoved(UserLocationDTO removedLocation) {
        for (Consumer<UserLocationDTO> listener : removalListeners) {
            executor.execute(() -> listener.accept(removedLocation));
        }
    }
}