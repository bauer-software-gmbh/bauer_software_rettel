package de.bauersoft.mobile.model.DTO;

import de.bauersoft.data.entities.order.OrderData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDataDTO {
    private String pattern;
    private String description;
    private String menuName;
    private int amount;

    public OrderDataDTO(OrderData data) {
        this.pattern = (data.getVariant() != null && data.getVariant().getPattern() != null)
                ? data.getVariant().getPattern().getName()
                : "Unbekannt";

        this.description = (data.getVariant() != null && data.getVariant().getDescription() != null)
                ? data.getVariant().getDescription()
                : "Keine Beschreibung";

        this.menuName = (data.getVariant() != null && data.getVariant().getMenu() != null)
                ? data.getVariant().getMenu().getName()
                : "Kein Menü";

        this.amount = data.getAmount() != null ? data.getAmount() : 0;
    }

    public OrderDataDTO() {}
}
