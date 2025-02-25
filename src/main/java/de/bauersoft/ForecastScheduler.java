package de.bauersoft;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.data.entities.order.*;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.services.*;
import de.bauersoft.services.offer.OfferService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ForecastScheduler
{

    private final OrderService orderService;
    private final  OrderDataService orderDataService;
    private final OrderAllergenService orderAllergenService;
    private final InstitutionFieldsService institutionFieldsService;
    private final  InstitutionPatternService institutionPatternService;
    private final InstitutionAllergenService institutionAllergenService;
    private final  VariantService variantService;
    private final  OfferService offerService;
    private final FieldService fieldService;

    public ForecastScheduler(OrderService orderService, OrderDataService orderDataService, OrderAllergenService orderAllergenService, InstitutionFieldsService institutionFieldsService, InstitutionPatternService institutionPatternService, InstitutionAllergenService institutionAllergenService, VariantService variantService, OfferService offerService, FieldService fieldService)
    {
        this.orderService = orderService;
        this.orderDataService = orderDataService;
        this.orderAllergenService = orderAllergenService;
        this.institutionFieldsService = institutionFieldsService;
        this.institutionPatternService = institutionPatternService;
        this.institutionAllergenService = institutionAllergenService;
        this.variantService = variantService;
        this.offerService = offerService;
        this.fieldService = fieldService;
    }

    @Scheduled(cron = "0 1 0 * * *")
    public void loadForecast()
    {
        System.out.println("Forecast loaded");

        Map<Field, Map<Pattern, Variant>> todaysMenus = fieldService.findAll()
                .stream()
                .map(field -> offerService.findByLocalDateAndField(LocalDate.now(), field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(offer -> offer.getMenus() != null && offer.getMenus().size() >= 1)
                .collect(Collectors.toMap(
                        Offer::getField,
                        offer -> offer.getMenus()
                                .stream()
                                .filter(menu -> menu.getVariants() != null && menu.getVariants().size() >= 1)
                                .flatMap(menu -> menu.getVariants().stream())
                                .collect(Collectors.toMap(
                                        Variant::getPattern,
                                        variant -> variant
                                ))

                ));


        for(InstitutionField institutionField : institutionFieldsService.findAll())
        {
            if(institutionField.isClosed()) continue;

            Institution institution = institutionField.getInstitution();
            Field field = institutionField.getField();

            Map<Pattern, Variant> todaysVariants = todaysMenus.get(field);
            if(todaysVariants == null || todaysVariants.isEmpty()) continue;

            Order order = new Order();
            order.setInstitution(institution);
            order.setField(field);
            order.setLocalDate(LocalDate.now());

            orderService.update(order);

            for(InstitutionPattern institutionPattern : institutionField.getInstitutionPatterns())
            {
                Pattern pattern = institutionPattern.getPattern();
                Variant variant = todaysVariants.get(pattern);
                if(variant == null) continue;

                OrderData orderData = new OrderData();
                orderData.setId(new OrderDataKey(order.getId(), variant.getId()));
                orderData.set_order(order);
                orderData.setVariant(variant);

                orderData.setAmount(institutionPattern.getAmount());

                orderDataService.update(orderData);
            }

            for(InstitutionAllergen institutionAllergen : institutionField.getInstitutionAllergens())
            {
                Allergen allergen = institutionAllergen.getAllergen();

                OrderAllergen orderAllergen = new OrderAllergen();
                orderAllergen.setId(new OrderAllergenKey(order.getId(), allergen.getId()));
                orderAllergen.set_order(order);
                orderAllergen.setAllergen(allergen);

                orderAllergen.setAmount(institutionAllergen.getAmount());

                orderAllergenService.update(orderAllergen);
            }
        }
    }
}
