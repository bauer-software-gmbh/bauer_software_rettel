package de.bauersoft.test;

import de.bauersoft.data.entities.role.Role;
import de.bauersoft.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Start
{
/*
    @Bean
    @Order(Integer.MAX_VALUE)
    public CommandLineRunner autoStart2(OfferService offerService, FieldRepository fieldRepository, OrderService orderService, OrderDataService orderDataService, OrderAllergenService orderAllergenService)
    {
        return args ->
        {

            for(Offer offer : offerService.getRepository().findAll())
            {
                System.out.println(offer.toString());
            }

//            for(de.bauersoft.data.entities.order.Order order : orderService.getRepository().findAll())
//            {
//                System.out.println(order.toString());
//            }
//
//            for(de.bauersoft.data.entities.order.OrderData orderData : orderDataService.getRepository().findAll())
//            {
//                System.out.println(orderData.toString());
//            }
//
//            for(de.bauersoft.data.entities.order.OrderAllergen orderAllergen : orderAllergenService.getRepository().findAll())
//            {
//                System.out.println(orderAllergen.toString());
//            }
        };
    }
*/
    @Bean
    public CommandLineRunner autoStart(UserService userService)
    {
        return args ->
        {
            if(userService.getRepository().existsByEmail("krall@bauer-soft.de"))
                return;

            userService.createUser("Milan", "Krall", "krall@bauer-soft.de", "1234", Role.values());
        };
    }

}
