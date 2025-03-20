package de.bauersoft.test;

import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.order.Order;
import de.bauersoft.data.entities.order.OrderAllergen;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.services.*;
import de.bauersoft.views.order.OrderView;
import org.aspectj.weaver.ast.Or;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class AutoInitializer
{

    @Bean
    public CommandLineRunner testInits(InstitutionService institutionService,
                                       InstitutionFieldsService institutionFieldsService,
                                       InstitutionAllergenService institutionAllergenService,
                                       InstitutionMultiplierService institutionMultiplierService,
                                       InstitutionPatternService institutionPatternService,
                                       FieldService fieldService,
                                       CourseService courseService,
                                       AllergenService allergenService,
                                       PatternService patternService,
                                       UnitService unitService,
                                       AdditiveService additiveService,
                                       OrderService orderService,
                                       OrderAllergenService orderAllergenService,
                                       OrderDataService orderDataService,
                                       VariantService variantService)
    {
        return args ->
        {
//            int totalUnits = 1000000;
//            int existingUnits = (int) unitService.count();
//
//            // Berechne, wie viele Units noch zu erstellen sind
//            int unitsToCreate = totalUnits - existingUnits;
//
//            // Verwende Parallel Stream, um Units parallel zu erstellen
//            IntStream.range(existingUnits, totalUnits)
//                    .parallel()
//                    .forEach(i -> {
//                        Additive unit = new Additive();
//                        unit.setName(UUID.randomUUID().toString());
//                        unit.setDescription(UUID.randomUUID().toString());
//
//                        additiveService.update(unit);
//                        System.out.println("Creating unit: " + i);
//                    });

            //new UnitCreator().createUnits(unitService);



            for(int i = courseService.findAll().size(); i < 5; i++)
            {
                Course course = new Course();
                course.setName(UUID.randomUUID().toString());

                courseService.update(course);
            }

            for(int i = allergenService.findAll().size(); i < 5; i++)
            {
                Allergen allergen = new Allergen();
                allergen.setName(UUID.randomUUID().toString());

                allergenService.update(allergen);
            }

            for(int i = institutionService.findAll().size(); i < 2; i++)
            {
                Institution institution = new Institution();
                institution.setName(UUID.randomUUID().toString());

                for(Field field : fieldService.findAll())
                {
                    InstitutionField institutionField = new InstitutionField();
                    institutionField.setInstitution(institution);
                    institutionField.setField(field);

                    institution.getInstitutionFields().add(institutionField);
                }

                institutionService.update(institution);

                institutionFieldsService.updateAll(institution.getInstitutionFields());
            }

//            for(long i = variantService.count(); i < 5; i++)
//            {
//                Variant variant = new Variant();
//                variant.setPattern(patternService.findAll().get(0));
//
//                variantService.update(variant);
//            }

//            Set<InstitutionAllergen> institutionAllergens = new HashSet<>();
//            for(int i = 0; i < 5; i++)
//            {
//                InstitutionAllergen institutionAllergen = new InstitutionAllergen();
//                institutionAllergen.setInstitutionField(institutionFieldsService.findAll().get(0));
//                institutionAllergen.setAllergens(allergenService.findAll().stream().collect(Collectors.toSet()));
//
//                institutionAllergens.add(institutionAllergen);
//            }


//            de.bauersoft.data.entities.order.Order order = new de.bauersoft.data.entities.order.Order();
//            order.setField(fieldService.findAll().get(0));
//            order.setInstitution(institutionService.findAll().get(0));
//            order.setOrderDate(LocalDate.now());
//
//            orderService.update(order);
//
//            Optional<de.bauersoft.data.entities.order.Order> orderOptional = orderService.findByOrderDateAndInstitutionAndField(LocalDate.now(), institutionService.findAll().get(0), fieldService.findAll().get(0));
//            if(orderOptional.isEmpty())
//                throw new RuntimeException("Order not found");

//            Order order = orderService.findAll().get(0);
//            System.out.println(order.toString());
//
//            List<OrderAllergen> orderAllergens = new ArrayList<>();
//            for(int i = 0; i < 5; i++)
//            {
//                OrderAllergen orderAllergen = new OrderAllergen();
//                orderAllergen.set_order(order);
//                orderAllergen.setAllergens(allergenService.findAll().stream().collect(Collectors.toSet()));
//
//                orderAllergens.add(orderAllergen);
//            }
//
//            System.out.println("id: " + order.getId());

//            orderAllergenService.updateAll(orderAllergens);


//            List<OrderAllergen> orderAllergens = new ArrayList<>(order.getOrderAllergens());
//            orderAllergens.remove(0);
//
//            order.setOrderAllergens(orderAllergens.stream().collect(Collectors.toSet()));
//            orderService.update(order);



            //InstitutionField institutionField = institutionFieldsService.findAll().get(0);

//            for(int i = 0; i < 5; i++)
//            {
//                InstitutionAllergen institutionAllergen = new InstitutionAllergen();
//                institutionAllergen.setInstitutionField(institutionField);
//                institutionAllergen.setAllergens(allergenService.findAll().stream().collect(Collectors.toSet()));
//
//                institutionAllergenService.update(institutionAllergen);
//            }

//            Allergen allergen = new Allergen();
//            allergen.setName("UwU");
//
//            allergenService.update(allergen);
//
//            for(InstitutionAllergen institutionAllergen : institutionAllergenService.findAll())
//            {
//                if(institutionAllergen.getAllergens().size() < 1) continue;
//
//                List<Allergen> allergens = institutionAllergen.getAllergens().stream().collect(Collectors.toList());
//                allergens.remove(0);
//
//                allergens.add(allergen);
//
//
//                institutionAllergen.setAllergens(allergens.stream().collect(Collectors.toSet()));
//
//
//                institutionAllergenService.update(institutionAllergen);
//            }

//            for(InstitutionField institutionField : institutionFieldsService.findAll())
//            {
//                for(Course course : courseService.findAll())
//                {
//                    InstitutionMultiplierKey key = new InstitutionMultiplierKey();
//                    key.setInstitutionFieldId(institutionField.getId());
//                    key.setCourseId(course.getId());
//
//                    InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
//                    institutionMultiplier.setId(key);
//                    institutionMultiplier.setInstitutionField(institutionField);
//                    institutionMultiplier.setCourse(course);
//                    institutionMultiplier.setMultiplier(1d);
//
//                    institutionField.getInstitutionMultipliers().add(institutionMultiplier);
//                }
//
//                institutionMultiplierService.updateAll(institutionField.getInstitutionMultipliers());
//            }
//
//            for(InstitutionField institutionField : institutionFieldsService.findAll())
//            {
//                for(Pattern pattern : patternService.findAll())
//                {
//                    InstitutionPatternKey key = new InstitutionPatternKey();
//                    key.setInstitutionFieldId(institutionField.getId());
//                    key.setPatternId(pattern.getId());
//
//                    InstitutionPattern institutionPattern = new InstitutionPattern();
//                    institutionPattern.setId(key);
//                    institutionPattern.setInstitutionField(institutionField);
//                    institutionPattern.setPattern(pattern);
//                    institutionPattern.setAmount(20);
//
//                    institutionField.getInstitutionPatterns().add(institutionPattern);
//                }
//
//                institutionPatternService.updateAll(institutionField.getInstitutionPatterns());
//            }

//            for(InstitutionField institutionField : institutionFieldsService.findAll())
//            {
//                for(Allergen allergen : allergenService.findAll())
//                {
//                    InstitutionAllergenKey key = new InstitutionAllergenKey();
//                    key.setInstitutionFieldId(institutionField.getId());
//                    key.setAllergenId(allergen.getId());
//
//                    InstitutionAllergen institutionAllergen = new InstitutionAllergen();
//                    institutionAllergen.setId(key);
//                    institutionAllergen.setInstitutionField(institutionField);
//                    institutionAllergen.setAllergen(allergen);
//
//                    institutionField.getInstitutionAllergens().add(institutionAllergen);
//                }
//
//                institutionAllergenService.updateAll(institutionField.getInstitutionAllergens());
//            }

        };
    }

/*
    @Bean
    @org.springframework.core.annotation.Order(2)
    public CommandLineRunner initializeDefaults(UnitService unitService,
                                                IngredientService ingredientService,
                                                AllergenService allergenService,
                                                AdditiveService additiveService,
                                                MenuService menuService,
                                                FieldService fieldService,
                                                OfferService offerService,
//                                                OfferMenuService offerMenuService,
                                                OrderService orderService,
                                                OrderDataService orderDataService,
                                                VariantService variantService,
                                                InstitutionService institutionService,
                                                PatternService patternService,
                                                OrderAllergenService orderAllergenService,
                                                CourseService courseService,
                                                RecipeService recipeService,
                                                FormulationService formulationService,
                                                PatternRepository patternRepository,
                                                ComponentService componentService)
    {
        return args ->
        {
            List<String> recipePool = List.of("Schnitzel", "Pommes", "Salat", "Pizza", "Spaghetti");
            for(int i = recipeService.getRepository().findAll().size(); i < 5; i++)
            {
                Recipe recipe = Recipe
                        .builder()
                        .name(recipePool.get(i))
                        .build();

                recipeService.update(recipe);
            }

            List<String> coursePool = List.of("Vorspeise", "Hauptspeise", "Nachspeise", "Beilage", "Getränk");
            for(int i = courseService.getRepository().findAll().size(); i < 5; i++)
            {
                Course course = Course
                        .builder()
                        .name(coursePool.get(i))
                        .build();

                courseService.update(course);
            }

            for(int i = menuService.getRepository().findAll().size(); i < 5; i++)
            {
                Variant variant = Variant
                        .builder()
                        .pattern(patternService.getRepository().findAll().get(1))
                        .build();

                variantService.update(variant);

                Menu menu = Menu
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .variants(Set.of(variant))
                        .build();

                variant.setMenu(menu);

                menuService.update(menu);
                variantService.update(variant);
            }

            for(int i = institutionService.getRepository().findAll().size(); i < 5; i++)
            {
                Institution institution = Institution
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .build();

                institutionService.update(institution);
            }

            Set<Field> fields = new HashSet<>();
            for(int i = fieldService.getRepository().findAll().size(); i < 5; i++)
            {
                Field field = Field
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .build();

                fieldService.update(field);
                fields.add(field);
            }

            if(!unitService.getRepository().existsUntitByName("g"))
                unitService.update(new Unit("g", "gram"));

            for(int i = ingredientService.getRepository().findAll().size(); i < 5; i++)
            {
                Ingredient ingredient = Ingredient
                        .builder()
                        .unit(unitService.getRepository().findAll().get(0))
                        .name(UUID.randomUUID().toString())
                        .build();

                ingredientService.update(ingredient);
            }

            for(int i = allergenService.getRepository().findAll().size(); i < 5; i++)
            {
                Allergen allergen = Allergen
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .build();

                allergenService.update(allergen);
            }

            for(int i = additiveService.getRepository().findAll().size(); i < 5; i++)
            {
                Additive additive = Additive
                        .builder()
                        .description(UUID.randomUUID().toString())
                        .name(UUID.randomUUID().toString())
                        .build();

                additiveService.update(additive);
            }

            for(int i = offerService.getRepository().findAll().size(); i < 5; i++)
            {
                Offer offer = Offer
                        .builder()
                        .orderDate(LocalDate.now())
                        .field(fieldService.getRepository().findById(Integer.valueOf(i).longValue() + 1l).get())
                        .build();

                offerService.update(offer);

//                OfferMenuKey offerMenuKey = OfferMenuKey
//                        .builder()
//                        .offerId(offer.getId())
//                        .menuId(Integer.valueOf(i).longValue() + 1l)
//                        .build();
//
//                OfferMenu offerMenu = OfferMenu
//                        .builder()
//                        .id(offerMenuKey)
//                        .offer(offer)
//                        .menu(menuService.getRepository().findById(Integer.valueOf(i).longValue() + 1l).get())
//                        .build();
//
//                offerMenuService.update(offerMenu);
//                Offer offer = Offer
//                        .builder()
//                        .field(fieldService.getRepository().findById(Integer.valueOf(i).longValue() + 1l).get())
//                        .orderDate(Week.getDate(DayOfWeek.of(i+ 1), LocalDate.now()))
//                        .menus(menuService.getRepository().findAll().stream().collect(Collectors.toSet()))
//                        .build();
//
//                offerService.update(offer);
            }

//            for(int i = offerNService.getRepository().findAll().size(); i < 5; i++)
//            {
//                Offer offerN = Offer
//                        .builder()
//                        .orderDate(LocalDate.now())
//                        .field(fieldService.getRepository().findById(Integer.valueOf(i).longValue() + 1l).get())
//                        .menus(menuService.getRepository().findAll().stream().collect(Collectors.toSet()))
//                        .build();
//
//                offerNService.update(offerN);
//            }
//
//            offerNService.delete(1l);
//            menuService.delete(1l);

            for(int i = orderService.getRepository().findAll().size(); i < 5; i++)
            {
                Order order = Order
                        .builder()
                        .orderDate(LocalDate.now())
                        .field(fieldService.getRepository().findById(Integer.valueOf(i + 1).longValue()).get())
                        .institution(institutionService.getRepository().findById(Integer.valueOf(i + 1).longValue()).get())
                        .build();

                orderService.update(order);

                OrderDataKey key = OrderDataKey
                        .builder()
                        .orderId(order.getId())
                        .variantId(Integer.valueOf(i).longValue() + 1l)
                        .build();

                OrderData orderData = OrderData
                        .builder()
                        .id(key)
                        ._order(order)
                        .variant(variantService.getRepository().findById(Integer.valueOf(i + 1).longValue()).get())
                        .amount(new Random().nextInt(100))
                        .build();

                orderDataService.update(orderData);

                OrderAllergenKey orderAllergenKey = OrderAllergenKey
                        .builder()
                        .orderId(order.getId())
                        .allergenId(Integer.valueOf(i + 1).longValue())
                        .build();

                OrderAllergen orderAllergen = OrderAllergen
                        .builder()
                        .id(orderAllergenKey)
                        ._order(order)
                        .allergen(allergenService.getRepository().findById(Integer.valueOf(i + 1).longValue()).get())
                        .build();

                orderAllergenService.update(orderAllergen);
            }

//            for(int i = 0; i < 5; i++)
//            {
//                for(Course course : courseService.getRepository().findAll())
//                {
//                    for(Pattern pattern : patternRepository.findAll())
//                    {
//                        Recipe recipe = Recipe
//                                .builder()
//                                .name(UUID.randomUUID().toString())
//                                .patterns(Set.of(pattern))
//                                .build();
//
//                        recipeService.update(recipe);
//
//                        Component component = Component
//                                .builder()
//                                .course(course)
//                                .name(UUID.randomUUID().toString())
//                                .recipes(Set.of(recipe))
//                                .build();
//
//                        componentService.update(component);
//                    }
//                }
//            }
        };


    }*/

}
