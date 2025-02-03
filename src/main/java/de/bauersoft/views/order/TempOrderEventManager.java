package de.bauersoft.views.order;

import de.bauersoft.tools.listener.order.OrderAllergenChangeEvent;
import de.bauersoft.tools.listener.order.OrderDataChangeEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class TempOrderEventManager
{

    private final List<Consumer<OrderDataChangeEvent>> orderDataChangeListeners;
    private final List<Consumer<OrderAllergenChangeEvent>> orderAllergenChangeListeners;

    public TempOrderEventManager()
    {
        orderDataChangeListeners = new ArrayList<>();
        orderAllergenChangeListeners = new ArrayList<>();
    }

    public TempOrderEventManager withOrderDataChangeListener(Consumer<OrderDataChangeEvent> listener)
    {
        orderDataChangeListeners.add(listener);
        return this;
    }
    public TempOrderEventManager withOrderAllergenChangeListener(Consumer<OrderAllergenChangeEvent> listener)
    {
        orderAllergenChangeListeners.add(listener);
        return this;
    }

    public void fire()
    {
        OrderDataChangeEvent orderDataChangeEvent = new OrderDataChangeEvent(null, 0, 0);
        orderDataChangeListeners.forEach(listener -> listener.accept(orderDataChangeEvent));
    }

}
