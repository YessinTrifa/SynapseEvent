package com.synapseevent.controller;

import com.synapseevent.entities.*;
import com.synapseevent.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;

public class PricingController {

    // ── Services ─────────────────────────────────────────────────────────────
    private final PricingService pricingService = new PricingService();
    private final CouponService couponService = new CouponService();
    private final PricingRulesService pricingRulesService = new PricingRulesService();
    private final TaxRateService taxRateService = new TaxRateService();
    private final ServiceFeeRateService serviceFeeRateService = new ServiceFeeRateService();

    // tracked editing IDs
    private Long editingCouponId = null;
    private Long editingRuleId = null;
    private Long editingTaxId = null;
    private Long editingFeeId = null;

    // ── COUPON TABLE ──────────────────────────────────────────────────────────
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponCodeColumn;
    @FXML private TableColumn<Coupon, String> couponTypeColumn;
    @FXML private TableColumn<Coupon, String> couponValueColumn;
    @FXML private TableColumn<Coupon, String> couponStartColumn;
    @FXML private TableColumn<Coupon, String> couponEndColumn;
    @FXML private TableColumn<Coupon, String> couponUsageColumn;
    @FXML private TableColumn<Coupon, String> couponStatusColumn;

    // ── PRICING RULES TABLE ───────────────────────────────────────────────────
    @FXML private TableView<PricingRules> pricingRulesTable;
    @FXML private TableColumn<PricingRules, String> ruleEventTypeColumn;
    @FXML private TableColumn<PricingRules, String> ruleTypeColumn;
    @FXML private TableColumn<PricingRules, String> ruleConditionColumn;
    @FXML private TableColumn<PricingRules, String> ruleDiscountColumn;
    @FXML private TableColumn<PricingRules, String> ruleActiveColumn;

    // ── TAX RATES TABLE ───────────────────────────────────────────────────────
    @FXML private TableView<TaxRate> taxRateTable;
    @FXML private TableColumn<TaxRate, String> taxNameColumn;
    @FXML private TableColumn<TaxRate, String> taxRateColumn;
    @FXML private TableColumn<TaxRate, String> taxTypeColumn;
    @FXML private TableColumn<TaxRate, String> taxApplicabilityColumn;
    @FXML private TableColumn<TaxRate, String> taxActiveColumn;

    // ── SERVICE FEE TABLE ─────────────────────────────────────────────────────
    @FXML private TableView<ServiceFeeRate> serviceFeeTable;
    @FXML private TableColumn<ServiceFeeRate, String> feeNameColumn;
    @FXML private TableColumn<ServiceFeeRate, String> feeRateColumn;
    @FXML private TableColumn<ServiceFeeRate, String> feeTypeColumn;
    @FXML private TableColumn<ServiceFeeRate, String> feeApplicabilityColumn;
    @FXML private TableColumn<ServiceFeeRate, String> feeActiveColumn;

    // ── PAYMENT SCHEDULE TABLE ────────────────────────────────────────────────
    @FXML private TableView<PaymentSchedule> paymentScheduleTable;
    @FXML private TableColumn<PaymentSchedule, String>  schedInstallmentColumn;
    @FXML private TableColumn<PaymentSchedule, String>  schedDescriptionColumn;
    @FXML private TableColumn<PaymentSchedule, String>  schedAmountColumn;
    @FXML private TableColumn<PaymentSchedule, String>  schedDueDateColumn;
    @FXML private TableColumn<PaymentSchedule, String>  schedDepositColumn;

    // ── COUPON FORM ───────────────────────────────────────────────────────────
    @FXML private TextField couponCodeField;
    @FXML private ComboBox<String> couponTypeComboBox;
    @FXML private TextField couponValueField;
    @FXML private DatePicker couponStartDate;
    @FXML private DatePicker couponEndDate;
    @FXML private TextField couponUsageLimitField;
    @FXML private TextField couponMinSpendField;
    @FXML private TextField couponApplicableField;
    @FXML private TextArea couponDescriptionField;
    @FXML private CheckBox couponActiveCheckBox;

    // ── PRICING RULE FORM ─────────────────────────────────────────────────────
    @FXML private ComboBox<String> ruleEventTypeComboBox;
    @FXML private ComboBox<String> ruleTypeComboBox;
    @FXML private ComboBox<String> ruleConditionComboBox;
    @FXML private TextField ruleConditionValueField;
    @FXML private ComboBox<String> ruleDiscountTypeComboBox;
    @FXML private TextField ruleDiscountValueField;
    @FXML private DatePicker ruleStartDate;
    @FXML private DatePicker ruleEndDate;
    @FXML private TextArea ruleDescriptionField;
    @FXML private CheckBox ruleActiveCheckBox;

    // ── TAX RATE FORM ─────────────────────────────────────────────────────────
    @FXML private TextField taxNameField;
    @FXML private TextField taxRateField;
    @FXML private ComboBox<String> taxTypeComboBox;
    @FXML private ComboBox<String> taxApplicabilityComboBox;
    @FXML private ComboBox<String> taxEventTypeComboBox;
    @FXML private TextArea taxDescriptionField;
    @FXML private CheckBox taxActiveCheckBox;

    // ── SERVICE FEE FORM ──────────────────────────────────────────────────────
    @FXML private TextField feeNameField;
    @FXML private TextField feeRateField;
    @FXML private ComboBox<String> feeTypeComboBox;
    @FXML private ComboBox<String> feeApplicabilityComboBox;
    @FXML private TextField feeMinAmountField;
    @FXML private TextField feeMaxAmountField;
    @FXML private TextArea feeDescriptionField;
    @FXML private CheckBox feeActiveCheckBox;

    // ── CALCULATOR FORM ───────────────────────────────────────────────────────
    @FXML private ComboBox<String> eventTypeComboBox;
    @FXML private TextField headcountField;
    @FXML private TextField venueBaseFeeField;
    @FXML private TextField venuePerPersonFeeField;
    @FXML private TextField activitiesTotalField;
    @FXML private TextField totalAmountField;
    @FXML private DatePicker calcEventDatePicker;
    @FXML private TextField calcCouponCodeField;
    @FXML private TextField depositPercentageField;
    @FXML private TextField installmentCountField;
    @FXML private TextArea pricingResultArea;

    // ── INIT ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupCouponTable();
        setupPricingRulesTable();
        setupTaxRateTable();
        setupServiceFeeTable();
        setupPaymentScheduleTable();
        setupFormCombos();
        setupTableSelectionListeners();
        loadAllData();
    }

    // ── TABLE SETUP ───────────────────────────────────────────────────────────

    private void setupCouponTable() {
        couponCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        couponTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        couponValueColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getValue() != null ? cd.getValue().getValue().toString() : "-"));
        couponStartColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getStartDate() != null ? cd.getValue().getStartDate().toString() : "-"));
        couponEndColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getEndDate() != null ? cd.getValue().getEndDate().toString() : "-"));
        couponUsageColumn.setCellValueFactory(cd -> {
            Coupon c = cd.getValue();
            String used = c.getUsedCount() != null ? c.getUsedCount().toString() : "0";
            String limit = c.getUsageLimit() != null ? c.getUsageLimit().toString() : "∞";
            return new SimpleStringProperty(used + " / " + limit);
        });
        couponStatusColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().isValid() ? "✅ Active" : "❌ Inactive"));
    }

    private void setupPricingRulesTable() {
        ruleEventTypeColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getEventType() != null ? cd.getValue().getEventType() : "ALL"));
        ruleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("ruleType"));
        ruleConditionColumn.setCellValueFactory(cd -> {
            PricingRules r = cd.getValue();
            return new SimpleStringProperty(r.getConditionType() + " ≥ " + r.getConditionValue());
        });
        ruleDiscountColumn.setCellValueFactory(cd -> {
            PricingRules r = cd.getValue();
            return new SimpleStringProperty(r.getDiscountValue() + " " + r.getDiscountType());
        });
        ruleActiveColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getIsActive()) ? "✅" : "❌"));
    }

    private void setupTaxRateTable() {
        taxNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taxRateColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getRate() + "%"));
        taxTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        taxApplicabilityColumn.setCellValueFactory(new PropertyValueFactory<>("applicability"));
        taxActiveColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getIsActive()) ? "✅" : "❌"));
    }

    private void setupServiceFeeTable() {
        feeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        feeRateColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getRate() + "%"));
        feeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        feeApplicabilityColumn.setCellValueFactory(new PropertyValueFactory<>("applicability"));
        feeActiveColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getIsActive()) ? "✅" : "❌"));
    }

    private void setupPaymentScheduleTable() {
        schedInstallmentColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getInstallmentNumber() != null
                ? "#" + cd.getValue().getInstallmentNumber() : "-"));
        schedDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        schedAmountColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getAmount() != null
                ? String.format("%.2f TND", cd.getValue().getAmount()) : "-"));
        schedDueDateColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getDueDate() != null
                ? cd.getValue().getDueDate().toString() : "-"));
        schedDepositColumn.setCellValueFactory(cd ->
            new SimpleStringProperty(Boolean.TRUE.equals(cd.getValue().getIsDeposit()) ? "✅ Deposit" : ""));
    }

    // ── COMBO BOX SETUP ───────────────────────────────────────────────────────

    private void setupFormCombos() {
        ObservableList<String> eventTypes = FXCollections.observableArrayList(
            "ALL", "TeamBuilding", "Anniversary", "Formation", "Paddle", "Partying");

        couponTypeComboBox.setItems(FXCollections.observableArrayList("PERCENT", "FIXED"));
        couponTypeComboBox.setValue("PERCENT");

        ruleEventTypeComboBox.setItems(eventTypes);
        ruleEventTypeComboBox.setValue("ALL");
        ruleTypeComboBox.setItems(FXCollections.observableArrayList(
            "VOLUME", "EARLY_BIRD", "OFF_PEAK", "GROUP_SIZE", "VENUE_BASE"));
        ruleTypeComboBox.setValue("GROUP_SIZE");
        ruleConditionComboBox.setItems(FXCollections.observableArrayList(
            "MIN_PEOPLE", "EXACT_PEOPLE", "RANGE_MIN", "DAYS_BEFORE_EVENT", "DAY_OF_WEEK"));
        ruleConditionComboBox.setValue("MIN_PEOPLE");
        ruleDiscountTypeComboBox.setItems(FXCollections.observableArrayList("PERCENT", "FIXED_AMOUNT"));
        ruleDiscountTypeComboBox.setValue("PERCENT");

        taxTypeComboBox.setItems(FXCollections.observableArrayList("PERCENTAGE", "FIXED_AMOUNT"));
        taxTypeComboBox.setValue("PERCENTAGE");
        taxApplicabilityComboBox.setItems(FXCollections.observableArrayList("ALL", "VENUE", "ACTIVITY", "SERVICE"));
        taxApplicabilityComboBox.setValue("ALL");
        taxEventTypeComboBox.setItems(eventTypes);
        taxEventTypeComboBox.setValue("ALL");

        feeTypeComboBox.setItems(FXCollections.observableArrayList("PERCENTAGE", "FIXED_AMOUNT"));
        feeTypeComboBox.setValue("PERCENTAGE");
        feeApplicabilityComboBox.setItems(FXCollections.observableArrayList("ALL", "VENUE_ONLY", "ACTIVITY_ONLY"));
        feeApplicabilityComboBox.setValue("ALL");

        eventTypeComboBox.setItems(FXCollections.observableArrayList(
            "TeamBuilding", "Anniversary", "Formation", "Paddle", "Partying"));
        eventTypeComboBox.setValue("TeamBuilding");
    }

    // ── SELECTION LISTENERS ───────────────────────────────────────────────────

    private void setupTableSelectionListeners() {
        couponTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateCouponForm(sel);
        });
        pricingRulesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateRuleForm(sel);
        });
        taxRateTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateTaxForm(sel);
        });
        serviceFeeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateFeeForm(sel);
        });
    }

    // ── LOAD DATA ─────────────────────────────────────────────────────────────

    private void loadAllData() {
        try {
            couponTable.setItems(FXCollections.observableArrayList(couponService.readAll()));
            pricingRulesTable.setItems(FXCollections.observableArrayList(pricingRulesService.readAll()));
            taxRateTable.setItems(FXCollections.observableArrayList(taxRateService.readAll()));
            serviceFeeTable.setItems(FXCollections.observableArrayList(serviceFeeRateService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // COUPON CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void addCoupon() {
        try {
            Coupon coupon = buildCouponFromForm();
            if (couponService.ajouter(coupon)) {
                showAlert("Success", "Coupon added successfully!");
                clearCouponFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to add coupon.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void saveCoupon() {
        if (editingCouponId == null) {
            showAlert("Info", "Select a coupon from the table first, or use 'Add New'.");
            return;
        }
        try {
            Coupon coupon = buildCouponFromForm();
            coupon.setId(editingCouponId);
            if (couponService.modifier(coupon)) {
                showAlert("Success", "Coupon updated successfully!");
                clearCouponFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to update coupon.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void deleteCoupon() {
        Coupon selected = couponTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Info", "Select a coupon to delete."); return; }
        if (showConfirmation("Delete Coupon", "Delete coupon \"" + selected.getCode() + "\"?")) {
            if (couponService.supprimer(selected)) {
                showAlert("Success", "Coupon deleted.");
                clearCouponFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to delete coupon.");
            }
        }
    }

    private Coupon buildCouponFromForm() {
        Coupon coupon = new Coupon();
        coupon.setCode(couponCodeField.getText().trim().toUpperCase());
        coupon.setType(couponTypeComboBox.getValue());
        coupon.setValue(Double.parseDouble(couponValueField.getText().trim()));
        coupon.setStartDate(couponStartDate.getValue());
        coupon.setEndDate(couponEndDate.getValue());
        String limText = couponUsageLimitField.getText().trim();
        coupon.setUsageLimit(limText.isEmpty() ? null : Integer.parseInt(limText));
        String minText = couponMinSpendField.getText().trim();
        coupon.setMinSpend(minText.isEmpty() ? null : Double.parseDouble(minText));
        String appText = couponApplicableField != null ? couponApplicableField.getText().trim() : "";
        coupon.setApplicableEventTypes(appText.isEmpty() ? null : appText);
        coupon.setDescription(couponDescriptionField.getText());
        coupon.setIsActive(couponActiveCheckBox.isSelected());
        return coupon;
    }

    private void populateCouponForm(Coupon c) {
        editingCouponId = c.getId();
        couponCodeField.setText(c.getCode());
        couponTypeComboBox.setValue(c.getType());
        couponValueField.setText(c.getValue() != null ? c.getValue().toString() : "");
        couponStartDate.setValue(c.getStartDate());
        couponEndDate.setValue(c.getEndDate());
        couponUsageLimitField.setText(c.getUsageLimit() != null ? c.getUsageLimit().toString() : "");
        couponMinSpendField.setText(c.getMinSpend() != null ? c.getMinSpend().toString() : "");
        if (couponApplicableField != null)
            couponApplicableField.setText(c.getApplicableEventTypes() != null ? c.getApplicableEventTypes() : "");
        couponDescriptionField.setText(c.getDescription() != null ? c.getDescription() : "");
        couponActiveCheckBox.setSelected(Boolean.TRUE.equals(c.getIsActive()));
    }

    @FXML
    private void clearCouponFormAction() {
        editingCouponId = null;
        couponCodeField.clear();
        couponValueField.clear();
        couponUsageLimitField.clear();
        couponMinSpendField.clear();
        if (couponApplicableField != null) couponApplicableField.clear();
        couponDescriptionField.clear();
        couponStartDate.setValue(null);
        couponEndDate.setValue(null);
        couponActiveCheckBox.setSelected(true);
        couponTable.getSelectionModel().clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRICING RULES CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void addPricingRule() {
        try {
            PricingRules rule = buildRuleFromForm();
            if (pricingRulesService.ajouter(rule)) {
                showAlert("Success", "Pricing rule added.");
                clearRuleFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to add rule.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void savePricingRule() {
        if (editingRuleId == null) {
            showAlert("Info", "Select a rule from the table first.");
            return;
        }
        try {
            PricingRules rule = buildRuleFromForm();
            rule.setId(editingRuleId);
            if (pricingRulesService.modifier(rule)) {
                showAlert("Success", "Rule updated.");
                clearRuleFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to update rule.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void deletePricingRule() {
        PricingRules selected = pricingRulesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Info", "Select a rule to delete."); return; }
        if (showConfirmation("Delete Rule", "Delete this pricing rule?")) {
            if (pricingRulesService.supprimer(selected)) {
                showAlert("Success", "Rule deleted.");
                clearRuleFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to delete rule.");
            }
        }
    }

    private PricingRules buildRuleFromForm() {
        PricingRules rule = new PricingRules();
        String et = ruleEventTypeComboBox.getValue();
        rule.setEventType("ALL".equals(et) ? null : et);
        rule.setRuleType(ruleTypeComboBox.getValue());
        rule.setConditionType(ruleConditionComboBox.getValue());
        rule.setConditionValue(Double.parseDouble(ruleConditionValueField.getText().trim()));
        rule.setDiscountType(ruleDiscountTypeComboBox.getValue());
        rule.setDiscountValue(Double.parseDouble(ruleDiscountValueField.getText().trim()));
        rule.setStartDate(ruleStartDate != null ? ruleStartDate.getValue() : null);
        rule.setEndDate(ruleEndDate != null ? ruleEndDate.getValue() : null);
        rule.setDescription(ruleDescriptionField.getText());
        rule.setIsActive(ruleActiveCheckBox.isSelected());
        return rule;
    }

    private void populateRuleForm(PricingRules r) {
        editingRuleId = r.getId();
        ruleEventTypeComboBox.setValue(r.getEventType() != null ? r.getEventType() : "ALL");
        ruleTypeComboBox.setValue(r.getRuleType());
        ruleConditionComboBox.setValue(r.getConditionType());
        ruleConditionValueField.setText(r.getConditionValue() != null ? r.getConditionValue().toString() : "");
        ruleDiscountTypeComboBox.setValue(r.getDiscountType());
        ruleDiscountValueField.setText(r.getDiscountValue() != null ? r.getDiscountValue().toString() : "");
        if (ruleStartDate != null) ruleStartDate.setValue(r.getStartDate());
        if (ruleEndDate != null) ruleEndDate.setValue(r.getEndDate());
        ruleDescriptionField.setText(r.getDescription() != null ? r.getDescription() : "");
        ruleActiveCheckBox.setSelected(Boolean.TRUE.equals(r.getIsActive()));
    }

    @FXML
    private void clearRuleFormAction() {
        editingRuleId = null;
        ruleConditionValueField.clear();
        ruleDiscountValueField.clear();
        ruleDescriptionField.clear();
        if (ruleStartDate != null) ruleStartDate.setValue(null);
        if (ruleEndDate != null) ruleEndDate.setValue(null);
        ruleActiveCheckBox.setSelected(true);
        pricingRulesTable.getSelectionModel().clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TAX RATE CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void addTaxRate() {
        try {
            TaxRate tax = buildTaxFromForm();
            if (taxRateService.ajouter(tax)) {
                showAlert("Success", "Tax rate added.");
                clearTaxFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to add tax rate.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void saveTaxRate() {
        if (editingTaxId == null) {
            showAlert("Info", "Select a tax rate from the table first.");
            return;
        }
        try {
            TaxRate tax = buildTaxFromForm();
            tax.setId(editingTaxId);
            if (taxRateService.modifier(tax)) {
                showAlert("Success", "Tax rate updated.");
                clearTaxFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to update tax rate.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void deleteTaxRate() {
        TaxRate selected = taxRateTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Info", "Select a tax rate to delete."); return; }
        if (showConfirmation("Delete Tax Rate", "Delete \"" + selected.getName() + "\"?")) {
            if (taxRateService.supprimer(selected)) {
                showAlert("Success", "Tax rate deleted.");
                clearTaxFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to delete tax rate.");
            }
        }
    }

    private TaxRate buildTaxFromForm() {
        TaxRate tax = new TaxRate();
        tax.setName(taxNameField.getText().trim());
        tax.setRate(Double.parseDouble(taxRateField.getText().trim()));
        tax.setType(taxTypeComboBox.getValue());
        tax.setApplicability(taxApplicabilityComboBox.getValue());
        String et = taxEventTypeComboBox != null ? taxEventTypeComboBox.getValue() : "ALL";
        tax.setEventType("ALL".equals(et) ? null : et);
        tax.setDescription(taxDescriptionField.getText());
        tax.setIsActive(taxActiveCheckBox.isSelected());
        return tax;
    }

    private void populateTaxForm(TaxRate t) {
        editingTaxId = t.getId();
        taxNameField.setText(t.getName() != null ? t.getName() : "");
        taxRateField.setText(t.getRate() != null ? t.getRate().toString() : "");
        taxTypeComboBox.setValue(t.getType());
        taxApplicabilityComboBox.setValue(t.getApplicability());
        if (taxEventTypeComboBox != null)
            taxEventTypeComboBox.setValue(t.getEventType() != null ? t.getEventType() : "ALL");
        taxDescriptionField.setText(t.getDescription() != null ? t.getDescription() : "");
        taxActiveCheckBox.setSelected(Boolean.TRUE.equals(t.getIsActive()));
    }

    @FXML
    private void clearTaxFormAction() {
        editingTaxId = null;
        taxNameField.clear();
        taxRateField.clear();
        taxDescriptionField.clear();
        taxActiveCheckBox.setSelected(true);
        taxRateTable.getSelectionModel().clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SERVICE FEE CRUD
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void addServiceFee() {
        try {
            ServiceFeeRate fee = buildFeeFromForm();
            if (serviceFeeRateService.ajouter(fee)) {
                showAlert("Success", "Service fee added.");
                clearFeeFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to add service fee.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void saveServiceFee() {
        if (editingFeeId == null) {
            showAlert("Info", "Select a service fee from the table first.");
            return;
        }
        try {
            ServiceFeeRate fee = buildFeeFromForm();
            fee.setId(editingFeeId);
            if (serviceFeeRateService.modifier(fee)) {
                showAlert("Success", "Service fee updated.");
                clearFeeFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to update service fee.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void deleteServiceFee() {
        ServiceFeeRate selected = serviceFeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Info", "Select a service fee to delete."); return; }
        if (showConfirmation("Delete Service Fee", "Delete \"" + selected.getName() + "\"?")) {
            if (serviceFeeRateService.supprimer(selected)) {
                showAlert("Success", "Service fee deleted.");
                clearFeeFormAction();
                loadAllData();
            } else {
                showAlert("Error", "Failed to delete service fee.");
            }
        }
    }

    private ServiceFeeRate buildFeeFromForm() {
        ServiceFeeRate fee = new ServiceFeeRate();
        fee.setName(feeNameField.getText().trim());
        fee.setRate(Double.parseDouble(feeRateField.getText().trim()));
        fee.setType(feeTypeComboBox.getValue());
        fee.setApplicability(feeApplicabilityComboBox.getValue());
        String minT = feeMinAmountField.getText().trim();
        String maxT = feeMaxAmountField.getText().trim();
        fee.setMinAmount(minT.isEmpty() ? null : Double.parseDouble(minT));
        fee.setMaxAmount(maxT.isEmpty() ? null : Double.parseDouble(maxT));
        fee.setDescription(feeDescriptionField.getText());
        fee.setIsActive(feeActiveCheckBox.isSelected());
        return fee;
    }

    private void populateFeeForm(ServiceFeeRate f) {
        editingFeeId = f.getId();
        feeNameField.setText(f.getName() != null ? f.getName() : "");
        feeRateField.setText(f.getRate() != null ? f.getRate().toString() : "");
        feeTypeComboBox.setValue(f.getType());
        feeApplicabilityComboBox.setValue(f.getApplicability());
        feeMinAmountField.setText(f.getMinAmount() != null ? f.getMinAmount().toString() : "");
        feeMaxAmountField.setText(f.getMaxAmount() != null ? f.getMaxAmount().toString() : "");
        feeDescriptionField.setText(f.getDescription() != null ? f.getDescription() : "");
        feeActiveCheckBox.setSelected(Boolean.TRUE.equals(f.getIsActive()));
    }

    @FXML
    private void clearFeeFormAction() {
        editingFeeId = null;
        feeNameField.clear();
        feeRateField.clear();
        feeMinAmountField.clear();
        feeMaxAmountField.clear();
        feeDescriptionField.clear();
        feeActiveCheckBox.setSelected(true);
        serviceFeeTable.getSelectionModel().clearSelection();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRICE CALCULATOR
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void calculatePricing() {
        try {
            String eventType = eventTypeComboBox.getValue();
            int headcount = headcountField.getText().isBlank() ? 1 : Integer.parseInt(headcountField.getText().trim());
            double venueBase = venueBaseFeeField.getText().isBlank() ? 0 : Double.parseDouble(venueBaseFeeField.getText().trim());
            double perPerson = venuePerPersonFeeField.getText().isBlank() ? 0 : Double.parseDouble(venuePerPersonFeeField.getText().trim());
            double activities = activitiesTotalField.getText().isBlank() ? 0 : Double.parseDouble(activitiesTotalField.getText().trim());
            double services = totalAmountField.getText().isBlank() ? 0 : Double.parseDouble(totalAmountField.getText().trim());
            LocalDate eventDate = calcEventDatePicker != null && calcEventDatePicker.getValue() != null
                ? calcEventDatePicker.getValue() : LocalDate.now().plusWeeks(4);
            String couponCode = calcCouponCodeField != null ? calcCouponCodeField.getText().trim() : "";

            // Build a venue-like request
            Venue venue = new Venue();
            venue.setBaseFee(venueBase);
            venue.setPerPersonFee(perPerson);

            EventPricingRequest request = new EventPricingRequest();
            request.setEventType(eventType);
            request.setHeadcount(headcount);
            request.setVenue(venue);
            request.setAdditionalServices(services);
            request.setBookingDate(LocalDate.now());
            request.setEventDate(eventDate);
            request.setCouponCode(couponCode.isEmpty() ? null : couponCode);

            // Override activities total by manually adjusting
            double extraActivities = activities;

            PricingCalculation calc = pricingService.calculateEventPrice(request);
            // Add manual activities total on top if entered separately
            if (extraActivities > 0 && calc.getActivitiesTotal() == 0) {
                calc.setActivitiesTotal(extraActivities);
                double newSubtotal = calc.getSubtotal() + extraActivities;
                calc.setSubtotal(newSubtotal);
                double newTotal = calc.getTotal() + extraActivities;
                calc.setTotal(newTotal);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════╗\n");
            sb.append("║      PRICING BREAKDOWN           ║\n");
            sb.append("╚══════════════════════════════════╝\n\n");
            sb.append(String.format("  Event Type    : %s%n", eventType));
            sb.append(String.format("  Headcount     : %d persons%n", headcount));
            sb.append(String.format("  Event Date    : %s%n%n", eventDate));
            sb.append("  ── Base Costs ──────────────────\n");
            sb.append(String.format("  Venue Base Fee : %8.2f TND%n", calc.getVenueBaseFee()));
            sb.append(String.format("  Per-Person Fee : %8.2f TND  (×%d)%n", calc.getVenuePerPersonFee(), headcount));
            sb.append(String.format("  Activities     : %8.2f TND%n", calc.getActivitiesTotal()));
            sb.append(String.format("  Extra Services : %8.2f TND%n", calc.getServicesTotal()));
            sb.append(String.format("  ─────────────────────────────────%n"));
            sb.append(String.format("  Subtotal       : %8.2f TND%n%n", calc.getSubtotal()));

            if (!calc.getAppliedDiscounts().isEmpty()) {
                sb.append("  ── Discounts Applied ───────────\n");
                for (PricingCalculation.AppliedDiscount d : calc.getAppliedDiscounts()) {
                    sb.append(String.format("  %-20s: -%6.2f TND%n", d.getName(), d.getAmount()));
                }
                sb.append(String.format("  Total Discount : -%7.2f TND%n%n", calc.getDiscountAmount()));
            }

            if (!calc.getAppliedTaxes().isEmpty()) {
                sb.append("  ── Taxes ───────────────────────\n");
                for (PricingCalculation.AppliedTax t : calc.getAppliedTaxes()) {
                    sb.append(String.format("  %-20s: %7.2f TND (%.1f%%)%n",
                        t.getName(), t.getAmount(), t.getRate()));
                }
                sb.append(String.format("  Total Tax      : %8.2f TND%n%n", calc.getTaxAmount()));
            }

            sb.append(String.format("  Service Fee    : %8.2f TND%n", calc.getServiceFeeAmount()));
            sb.append("  ═════════════════════════════════\n");
            sb.append(String.format("  TOTAL          : %8.2f TND%n", calc.getTotal()));

            pricingResultArea.setText(sb.toString());

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for fees and headcount.");
        } catch (Exception e) {
            showAlert("Error", "Calculation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void generateSchedule() {
        try {
            String totalText = pricingResultArea.getText();
            double total = 0.0;
            // Try to extract from result area, else parse totalAmountField
            for (String line : totalText.split("\n")) {
                if (line.contains("TOTAL")) {
                    String[] parts = line.trim().split("\\s+");
                    for (String p : parts) {
                        try { total = Double.parseDouble(p.replace("TND", "").trim()); break; } catch (Exception ignored) {}
                    }
                }
            }
            if (total <= 0) {
                showAlert("Info", "Run 'Calculate Price' first to determine the total.");
                return;
            }

            double depositPct = depositPercentageField.getText().isBlank()
                ? 30.0 : Double.parseDouble(depositPercentageField.getText().trim());
            int installments = installmentCountField.getText().isBlank()
                ? 2 : Integer.parseInt(installmentCountField.getText().trim());
            LocalDate eventDate = calcEventDatePicker != null && calcEventDatePicker.getValue() != null
                ? calcEventDatePicker.getValue() : LocalDate.now().plusMonths(3);

            PaymentScheduleRequest schedReq = new PaymentScheduleRequest();
            schedReq.setBookingId(-1L);
            schedReq.setTotalAmount(total);
            schedReq.setDepositPercentage(depositPct);
            schedReq.setInstallmentCount(installments);
            schedReq.setEventDate(eventDate);

            List<PaymentSchedule> schedule = pricingService.generatePaymentSchedule(-1L, total, schedReq);

            // Mark deposit
            if (!schedule.isEmpty()) {
                schedule.get(0).setIsDeposit(true);
            }

            paymentScheduleTable.setItems(FXCollections.observableArrayList(schedule));

        } catch (Exception e) {
            showAlert("Error", "Schedule generation error: " + e.getMessage());
        }
    }

    // ── UTILITIES ─────────────────────────────────────────────────────────────

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }
}
