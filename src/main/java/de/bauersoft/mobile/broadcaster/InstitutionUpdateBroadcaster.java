package de.bauersoft.mobile.broadcaster;

import de.bauersoft.data.entities.tour.tour.TourInstitution;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class InstitutionUpdateBroadcaster {
    private static final List<Consumer<TourInstitution>> listeners = new CopyOnWriteArrayList<>();

    public static void register(Consumer<TourInstitution> listener) {
        listeners.add(listener);
    }

    public static void unregister(Consumer<TourInstitution> listener) {
        listeners.remove(listener);
    }

    public static void broadcast(TourInstitution institution) {
        for (Consumer<TourInstitution> listener : listeners) {
            listener.accept(institution);
        }
    }
}
