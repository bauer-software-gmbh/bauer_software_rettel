package de.bauersoft.views.order;

import de.bauersoft.data.entities.order.OrderAllergen;

public class Beispiel
{
    public void test()
    {
        TempOrderEventManager tempOrderEvent = new TempOrderEventManager();
        tempOrderEvent.withOrderAllergenChangeListener(event ->
        {
            OrderAllergen orderAllergen = event.orderAllergen();
            int oldAmount = event.oldAmount();
            int newAmount = event.newAmount();

            //Dein code wenn das event getriggert wird
        });

        tempOrderEvent.withOrderAllergenChangeListener(event ->
        {
            OrderAllergen orderAllergen = event.orderAllergen();
            int oldAmount = event.oldAmount();
            int newAmount = event.newAmount();

            //Dein code wenn das event getriggert wird
        });
    }
}
