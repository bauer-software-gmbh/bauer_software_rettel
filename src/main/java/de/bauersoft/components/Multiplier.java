package de.bauersoft.components;

import de.bauersoft.data.entities.field.FieldMultiplier;
import de.bauersoft.data.entities.field.FieldMultiplierKey;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.data.repositories.institutionMultiplier.InstitutionMultiplierRepository;

import java.util.Optional;

public class Multiplier
{
    public static final int defaultMultiplier = 1;

    private final InstitutionMultiplierRepository institutionMultiplierRepository;
    private final FieldMultiplierRepository fieldMultiplierRepository;

    public Multiplier(InstitutionMultiplierRepository institutionMultiplierRepository, FieldMultiplierRepository fieldMultiplierRepository)
    {
        this.institutionMultiplierRepository = institutionMultiplierRepository;
        this.fieldMultiplierRepository = fieldMultiplierRepository;
    }
//
//    public boolean isLocal(long institutionId, long fieldId, long courseId)
//    {
//        Boolean isLocal = false//institutionMultiplierRepository.isLocal(institutionId, fieldId, courseId);
//        return (isLocal != null && isLocal);
//    }
//
//    public double getGlobalMultiplier(long fieldId, long courseId, double def)
//    {
//        Optional<FieldMultiplier> fieldMultiplierOpt = fieldMultiplierRepository
//                .findById
//                        (
//                                new FieldMultiplierKey(fieldId, courseId)
//                        );
//
//        return fieldMultiplierOpt.map(FieldMultiplier::getMultiplier).orElse(def);
//    }
//
//    public double getLocalMultiplier(long institutionId, long fieldId, long courseId, double def)
//    {
//        Optional<InstitutionMultiplier> instMultiplierOpt = institutionMultiplierRepository
//                .findById
//                        (
//                                new InstitutionMultiplierKey(institutionId, fieldId, courseId)
//                        );
//
//        return instMultiplierOpt.map(institutionMultiplier ->
//        {
//            return (institutionMultiplier.isLocal()) ? institutionMultiplier.getMultiplier() : def;
//
//        }).orElse(def);
//    }
//
//    public double getMultiplier(long institutionId, long fieldId, long courseId, double def)
//    {
//        Optional<FieldMultiplier> fieldMultiplierOpt = fieldMultiplierRepository
//                .findById
//                        (
//                                new FieldMultiplierKey(fieldId, courseId)
//                        );
//
//        Optional<InstitutionMultiplier> instMultiplierOpt = institutionMultiplierRepository
//                .findById
//                        (
//                                new InstitutionMultiplierKey(institutionId, fieldId, courseId)
//                        );
//
//        return instMultiplierOpt.map(InstitutionMultiplier::getMultiplier)
//                .orElse(fieldMultiplierOpt.map(FieldMultiplier::getMultiplier)
//                        .orElse(def));
//    }
//
//
//
//
//    public static boolean isLocal(InstitutionMultiplierRepository institutionMultiplierRepository, long institutionId, long fieldId, long courseId)
//    {
//        Boolean isLocal = institutionMultiplierRepository.isLocal(institutionId, fieldId, courseId);
//        return (isLocal != null && isLocal);
//    }
//
//    public static double getGlobalMultiplier(FieldMultiplierRepository fieldMultiplierRepository, long fieldId, long courseId)
//    {
//        Optional<FieldMultiplier> fieldMultiplierOpt = fieldMultiplierRepository
//                .findById
//                        (
//                                new FieldMultiplierKey(fieldId, courseId)
//                        );
//
//        return fieldMultiplierOpt.map(FieldMultiplier::getMultiplier).orElse(1d);
//    }
//
//    public static double getLocalMultiplier(InstitutionMultiplierRepository institutionMultiplierRepository, long institutionId, long fieldId, long courseId)
//    {
//        Optional<InstitutionMultiplier> instMultiplierOpt = institutionMultiplierRepository
//                .findById
//                        (
//                                new InstitutionMultiplierKey(institutionId, fieldId, courseId)
//                        );
//
//        return instMultiplierOpt.map(institutionMultiplier ->
//        {
//            return (institutionMultiplier.isLocal()) ? institutionMultiplier.getMultiplier() : 1d;
//
//        }).orElse(1d);
//    }
//
//    public static double getMultiplier(FieldMultiplierRepository fieldMultiplierRepository, InstitutionMultiplierRepository institutionMultiplierRepository, long institutionId, long fieldId, long courseId)
//    {
//        Optional<FieldMultiplier> fieldMultiplierOpt = fieldMultiplierRepository
//                .findById
//                        (
//                                new FieldMultiplierKey(fieldId, courseId)
//                        );
//
//        Optional<InstitutionMultiplier> instMultiplierOpt = institutionMultiplierRepository
//                .findById
//                        (
//                                new InstitutionMultiplierKey(institutionId, fieldId, courseId)
//                        );
//
//        return instMultiplierOpt.map(InstitutionMultiplier::getMultiplier)
//                .orElse(fieldMultiplierOpt.map(FieldMultiplier::getMultiplier)
//                        .orElse(1d));
//    }
}
