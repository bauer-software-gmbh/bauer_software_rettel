package de.bauersoft.views.address;

import java.util.Collection;
import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.combobox.ComboBox.ItemFilter;
import com.vaadin.flow.component.combobox.ComboBoxBase.CustomValueSetEvent;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.combobox.dataview.ComboBoxDataView;
import com.vaadin.flow.component.combobox.dataview.ComboBoxLazyDataView;
import com.vaadin.flow.component.combobox.dataview.ComboBoxListDataView;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.shared.HasClientValidation.ClientValidatedEvent;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.data.binder.ValidationStatusChangeListener;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.BackEndDataProvider;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.InMemoryDataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.dom.ClassList;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import de.bauersoft.data.entities.Address;
import de.bauersoft.data.repositories.address.AddressRepository;
import de.bauersoft.services.AddressService;
import de.bauersoft.views.DialogState;

public class AddressComboBox extends CustomField<Address>{

	private ComboBox<Address> addressComboBox = new ComboBox<Address>();
	public AddressComboBox(AddressRepository addressRepository,AddressService addressService) {
		addressComboBox.setRequired(true);
		addressComboBox.setItemLabelGenerator(address -> address.getStreet() + " " + address.getHouseNumber() + " "
				+ address.getPostalCode() + " " + address.getCity());
		addressComboBox.setItems(addressRepository.findAll());
		addressComboBox.setWidth("calc(100% - 45px)");
		Button newAddressButton = new Button();
		newAddressButton.setIcon(LineAwesomeIcon.PLUS_CIRCLE_SOLID.create());
		newAddressButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
		newAddressButton.getStyle().setTop("4px");
		newAddressButton.getStyle().setLeft("4px");
		newAddressButton.addClickListener(event -> new AddressDialog(addressService, null, new Address(), DialogState.NEW)
				.addOpenedChangeListener(openChangeEvent -> addressComboBox.setItems(addressRepository.findAll())));
		this.add(addressComboBox,newAddressButton);
	}
	@Override
	protected Address generateModelValue() {
		return addressComboBox.getValue();
	}

	@Override
	protected void setPresentationValue(Address newPresentationValue) {
		addressComboBox.setValue(newPresentationValue);		
	}
	public void setPrefixComponent(Component component) {
		addressComboBox.setPrefixComponent(component);
	}
	public boolean isClearButtonVisible() {
		return addressComboBox.isClearButtonVisible();
	}
	public boolean isAutoOpen() {
		return addressComboBox.isAutoOpen();
	}
	public String getAllowedCharPattern() {
		return addressComboBox.getAllowedCharPattern();
	}
	public void addThemeName(String themeName) {
		addressComboBox.addThemeName(themeName);
	}
	public void setErrorMessage(String errorMessage) {
		addressComboBox.setErrorMessage(errorMessage);
	}

	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		addressComboBox.setRequiredIndicatorVisible(requiredIndicatorVisible);
	}
	public void setTabIndex(int tabIndex) {
		addressComboBox.setTabIndex(tabIndex);
	}
	public void addThemeVariants(ComboBoxVariant... variants) {
		addressComboBox.addThemeVariants(variants);
	}
	public Validator<Address> getDefaultValidator() {
		return addressComboBox.getDefaultValidator();
	}
	public void setPlaceholder(String placeholder) {
		addressComboBox.setPlaceholder(placeholder);
	}
	public Registration addClientValidatedEventListener(ComponentEventListener<ClientValidatedEvent> listener) {
		return addressComboBox.addClientValidatedEventListener(listener);
	}
	public void addClassName(String className) {
		addressComboBox.addClassName(className);
	}
	public void setAutoOpen(boolean autoOpen) {
		addressComboBox.setAutoOpen(autoOpen);
	}
	public boolean isRequiredIndicatorVisible() {
		return addressComboBox.isRequiredIndicatorVisible();
	}
	public boolean removeThemeName(String themeName) {
		return addressComboBox.removeThemeName(themeName);
	}
	public void setClearButtonVisible(boolean clearButtonVisible) {
		addressComboBox.setClearButtonVisible(clearButtonVisible);
	}
	public String getHelperText() {
		return addressComboBox.getHelperText();
	}
	public void setReadOnly(boolean readOnly) {
		addressComboBox.setReadOnly(readOnly);
	}
	public String getErrorMessage() {
		return addressComboBox.getErrorMessage();
	}
	public void setLabel(String label) {
		addressComboBox.setLabel(label);
	}
	public Tooltip setTooltipText(String text) {
		return addressComboBox.setTooltipText(text);
	}
	public Component getPrefixComponent() {
		return addressComboBox.getPrefixComponent();
	}
	public void setAllowedCharPattern(String pattern) {
		addressComboBox.setAllowedCharPattern(pattern);
	}
	public boolean removeClassName(String className) {
		return addressComboBox.removeClassName(className);
	}
	public void removeThemeVariants(ComboBoxVariant... variants) {
		addressComboBox.removeThemeVariants(variants);
	}
	public boolean isReadOnly() {
		return addressComboBox.isReadOnly();
	}
	public String getPlaceholder() {
		return addressComboBox.getPlaceholder();
	}
	public void setHelperText(String helperText) {
		addressComboBox.setHelperText(helperText);
	}
	public Registration addValidationStatusChangeListener(ValidationStatusChangeListener<Address> listener) {
		return addressComboBox.addValidationStatusChangeListener(listener);
	}
	public void setInvalid(boolean invalid) {
		addressComboBox.setInvalid(invalid);
	}
	public void setThemeName(String themeName) {
		addressComboBox.setThemeName(themeName);
	}
	public Tooltip getTooltip() {
		return addressComboBox.getTooltip();
	}
	public void setClassName(String className) {
		addressComboBox.setClassName(className);
	}
	public ComboBoxListDataView<Address> setItems(Address... items) {
		return addressComboBox.setItems(items);
	}
	public String getThemeName() {
		return addressComboBox.getThemeName();
	}
	public boolean isInvalid() {
		return addressComboBox.isInvalid();
	}
	public void setHelperComponent(Component component) {
		addressComboBox.setHelperComponent(component);
	}
	public String getClassName() {
		return addressComboBox.getClassName();
	}
	public ThemeList getThemeNames() {
		return addressComboBox.getThemeNames();
	}
	public int getTabIndex() {
		return addressComboBox.getTabIndex();
	}
	public void setEnabled(boolean enabled) {
		addressComboBox.setEnabled(enabled);
	}
	public ClassList getClassNames() {
		return addressComboBox.getClassNames();
	}
	public void setThemeName(String themeName, boolean set) {
		addressComboBox.setThemeName(themeName, set);
	}
	public Component getHelperComponent() {
		return addressComboBox.getHelperComponent();
	}
	public boolean isEnabled() {
		return addressComboBox.isEnabled();
	}
	public void setClassName(String className, boolean set) {
		addressComboBox.setClassName(className, set);
	}
	public void addThemeNames(String... themeNames) {
		addressComboBox.addThemeNames(themeNames);
	}
	public void focus() {
		addressComboBox.focus();
	}
	public void removeThemeNames(String... themeNames) {
		addressComboBox.removeThemeNames(themeNames);
	}
	public Style getStyle() {
		return addressComboBox.getStyle();
	}
	public void addClassNames(String... classNames) {
		addressComboBox.addClassNames(classNames);
	}
	public Optional<Address> getOptionalValue() {
		return addressComboBox.getOptionalValue();
	}
	public void blur() {
		addressComboBox.blur();
	}
	public ShortcutRegistration addFocusShortcut(Key key, KeyModifier... keyModifiers) {
		return addressComboBox.addFocusShortcut(key, keyModifiers);
	}
	public void clear() {
		addressComboBox.clear();
	}
	public void removeClassNames(String... classNames) {
		addressComboBox.removeClassNames(classNames);
	}

	public boolean isAutofocus() {
		return addressComboBox.isAutofocus();
	}
	public void setAutofocus(boolean autofocus) {
		addressComboBox.setAutofocus(autofocus);
	}
	public int getPageSize() {
		return addressComboBox.getPageSize();
	}
	public boolean isEmpty() {
		return addressComboBox.isEmpty();
	}
	public void setPattern(String pattern) {
		addressComboBox.setPattern(pattern);
	}
	public Address getValue() {
		return addressComboBox.getValue();
	}
	public void setPageSize(int pageSize) {
		addressComboBox.setPageSize(pageSize);
	}
	public boolean isOpened() {
		return addressComboBox.isOpened();
	}
	public void setOpened(boolean opened) {
		addressComboBox.setOpened(opened);
	}
	public boolean isAllowCustomValue() {
		return addressComboBox.isAllowCustomValue();
	}
	public Address getEmptyValue() {
		return addressComboBox.getEmptyValue();
	}
	public void setAllowCustomValue(boolean allowCustomValue) {
		addressComboBox.setAllowCustomValue(allowCustomValue);
	}
	public boolean isRequired() {
		return addressComboBox.isRequired();
	}
	public void setRequired(boolean required) {
		addressComboBox.setRequired(required);
	}
	public Optional<String> getAriaLabel() {
		return addressComboBox.getAriaLabel();
	}
	public Optional<String> getAriaLabelledBy() {
		return addressComboBox.getAriaLabelledBy();
	}
	public void setItemLabelGenerator(ItemLabelGenerator<Address> itemLabelGenerator) {
		addressComboBox.setItemLabelGenerator(itemLabelGenerator);
	}
	public void setRenderer(Renderer<Address> renderer) {
		addressComboBox.setRenderer(renderer);
	}
	public void setValue(Address value) {
		addressComboBox.setValue(value);
	}
	public Registration addCustomValueSetListener(
			ComponentEventListener<CustomValueSetEvent<ComboBox<Address>>> listener) {
		return addressComboBox.addCustomValueSetListener(listener);
	}
	public void setVisible(boolean visible) {
		addressComboBox.setVisible(visible);
	}
	public ComboBoxListDataView<Address> getListDataView() {
		return addressComboBox.getListDataView();
	}
	public boolean isVisible() {
		return addressComboBox.isVisible();
	}
	public void onEnabledStateChanged(boolean enabled) {
		addressComboBox.onEnabledStateChanged(enabled);
	}
	public ComboBoxListDataView<Address> setItems(Collection<Address> items) {
		return addressComboBox.setItems(items);
	}
	public ComboBoxListDataView<Address> setItems(ItemFilter<Address> itemFilter, Collection<Address> items) {
		return addressComboBox.setItems(itemFilter, items);
	}
	public ComboBoxListDataView<Address> setItems(ItemFilter<Address> itemFilter, Address... items) {
		return addressComboBox.setItems(itemFilter, items);
	}
	public ComboBoxListDataView<Address> setItems(ItemFilter<Address> itemFilter,
			ListDataProvider<Address> listDataProvider) {
		return addressComboBox.setItems(itemFilter, listDataProvider);
	}
	public ComboBoxListDataView<Address> setItems(ListDataProvider<Address> dataProvider) {
		return addressComboBox.setItems(dataProvider);
	}
	public ComboBoxLazyDataView<Address> getLazyDataView() {
		return addressComboBox.getLazyDataView();
	}
	public void scrollIntoView() {
		addressComboBox.scrollIntoView();
	}
	public void scrollIntoView(ScrollOptions scrollOptions) {
		addressComboBox.scrollIntoView(scrollOptions);
	}
	public <T> T findAncestor(Class<T> componentType) {
		return addressComboBox.findAncestor(componentType);
	}
	public <C> ComboBoxLazyDataView<Address> setItemsWithFilterConverter(FetchCallback<Address, C> fetchCallback,
			SerializableFunction<String, C> filterConverter) {
		return addressComboBox.setItemsWithFilterConverter(fetchCallback, filterConverter);
	}
	public <C> ComboBoxLazyDataView<Address> setItemsWithFilterConverter(FetchCallback<Address, C> fetchCallback,
			CountCallback<Address, C> countCallback, SerializableFunction<String, C> filterConverter) {
		return addressComboBox.setItemsWithFilterConverter(fetchCallback, countCallback, filterConverter);
	}
	public ComboBoxLazyDataView<Address> setItems(FetchCallback<Address, String> fetchCallback) {
		return addressComboBox.setItems(fetchCallback);
	}
	public ComboBoxLazyDataView<Address> setItems(FetchCallback<Address, String> fetchCallback,
			CountCallback<Address, String> countCallback) {
		return addressComboBox.setItems(fetchCallback, countCallback);
	}
	public ComboBoxLazyDataView<Address> setItems(BackEndDataProvider<Address, String> dataProvider) {
		return addressComboBox.setItems(dataProvider);
	}
	public ComboBoxDataView<Address> getGenericDataView() {
		return addressComboBox.getGenericDataView();
	}
	public ComboBoxDataView<Address> setItems(DataProvider<Address, String> dataProvider) {
		return addressComboBox.setItems(dataProvider);
	}
	@Deprecated
	public ComboBoxDataView<Address> setItems(InMemoryDataProvider<Address> dataProvider) {
		return addressComboBox.setItems(dataProvider);
	}
	public ComboBoxDataView<Address> setItems(InMemoryDataProvider<Address> inMemoryDataProvider,
			SerializableFunction<String, SerializablePredicate<Address>> filterConverter) {
		return addressComboBox.setItems(inMemoryDataProvider, filterConverter);
	}
	public <C> void setDataProvider(DataProvider<Address, C> dataProvider,
			SerializableFunction<String, C> filterConverter) {
		addressComboBox.setDataProvider(dataProvider, filterConverter);
	}
	public void setDataProvider(FetchItemsCallback<Address> fetchItems,
			SerializableFunction<String, Integer> sizeCallback) {
		addressComboBox.setDataProvider(fetchItems, sizeCallback);
	}
	public DataProvider<Address, ?> getDataProvider() {
		return addressComboBox.getDataProvider();
	}
	public void setManualValidation(boolean enabled) {
		addressComboBox.setManualValidation(enabled);
	}

}
