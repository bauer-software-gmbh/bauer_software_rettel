package de.bauersoft.components.comboBox;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import de.bauersoft.data.entities.address.Address;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class AddressComboBox extends CustomField<Address>
{
    private final ComboBox<Address> addressComboBox;
    private final Button addButton;

    public AddressComboBox()
    {
        addressComboBox = new ComboBox<>();

        addButton = new Button(LineAwesomeIcon.PLUS_SOLID.create());

        this.add(addressComboBox, addButton);
    }

    @Override
    protected Address generateModelValue()
    {
        return null;
    }

    @Override
    protected void setPresentationValue(Address newPresentationValue)
    {

    }
}
