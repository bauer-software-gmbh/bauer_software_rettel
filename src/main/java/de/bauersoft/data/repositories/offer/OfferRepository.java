package de.bauersoft.data.repositories.offer;

import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.offer.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer>
{
    Optional<Offer> findByLocalDateAndField(LocalDate date, Field value);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM offer_menu WHERE offer_id = :offerId AND menu_id = :menuId", nativeQuery = true)
    void deleteByIdAndMenusId(@Param("offerId") Long offerId, @Param("menuId") Long menuId);

    boolean existsByMenusId(Long menuId);

    boolean existsByField(Field field);
}
