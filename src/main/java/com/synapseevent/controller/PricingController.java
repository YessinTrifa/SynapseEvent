package com.synapseevent.controller;

import com.synapseevent.entities.*;
import com.synapseevent.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.List;

public class PricingController {
    
    // Service instances
    private PricingService pricingService;
    private CouponService couponService;
    private PricingRulesService pricingRulesService;
    private PaymentScheduleService paymentScheduleService;
    private TaxRateService taxRateService;
    private ServiceFeeRateService serviceFeeRateService;
    
    // UI Components for Coupons
    @FXML private TableView<Coupon> couponTable;
    @FXML private TableColumn<Coupon, String> couponCodeColumn;
    @FXML private TableColumn<Coupon, String> couponTypeColumn;
    @FXML private TableColumn<Coupon, Double> couponValueColumn;
    @FXML private TableColumn<Coupon, String> couponStatusColumn;
    
    // UI Components for Pricing Rules
    @FXML private TableView<PricingRules> pricingRulesTable;
    @FXML private TableColumn<PricingRules, String> ruleTypeColumn;
    @FXML private TableColumn<PricingRules, String> ruleConditionColumn;
    @FXML private TableColumn<PricingRules, Double> ruleDiscountColumn;
    
    // UI Components for Tax Rates
    @FXML private TableView<TaxRate> taxRateTable;
    @FXML private TableColumn<TaxRate, String> taxNameColumn;
    @FXML private TableColumn<TaxRate, Double> taxRateColumn;
    
    // UI Components for Service Fees
    @FXML private TableView<ServiceFeeRate> serviceFeeTable;
    @FXML private TableColumn<ServiceFeeRate, String> feeNameColumn;
    @FXML private TableColumn<ServiceFeeRate, Double> feeRateColumn;
    
    // Form components
    @FXML private TextField couponCodeField;
    @FXML private ComboBox<String> couponTypeComboBox;
    @FXML private TextField couponValueField;
    @FXML private DatePicker couponStartDate;
    @FXML private DatePicker couponEndDate;
    @FXML private TextField couponUsageLimitField;
    @FXML private TextField couponMinSpendField;
    @FXML private TextArea couponDescriptionField;
    @FXML private CheckBox couponActiveCheckBox;
    
    @FXML private ComboBox<String> ruleTypeComboBox;
    @FXML private ComboBox<String> ruleConditionComboBox;
    @FXML private TextField ruleConditionValueField;
    @FXML private ComboBox<String> ruleDiscountTypeComboBox;
    @FXML private TextField ruleDiscountValueField;
    @FXML private TextArea ruleDescriptionField;
    @FXML private CheckBox ruleActiveCheckBox;
    
    @FXML private TextField taxNameField;
    @FXML private TextField taxRateField;
    @FXML private ComboBox<String> taxTypeComboBox;
    @FXML private ComboBox<String> taxApplicabilityComboBox;
    @FXML private TextArea taxDescriptionField;
    @FXML private CheckBox taxActiveCheckBox;
    
    @FXML private TextField feeNameField;
    @FXML private TextField feeRateField;
    @FXML private ComboBox<String> feeTypeComboBox;
    @FXML private ComboBox<String> feeApplicabilityComboBox;
    @FXML private TextField feeMinAmountField;
    @FXML private TextField feeMaxAmountField;
    @FXML private TextArea feeDescriptionField;
    @FXML private CheckBox feeActiveCheckBox;
    
    // Pricing calculator components
    @FXML private ComboBox<String> eventTypeComboBox;
    @FXML private TextField headcountField;
    @FXML private TextField totalAmountField;
    @FXML private TextField depositPercentageField;
    @FXML private TextField installmentCountField;
    @FXML private TextArea pricingResultArea;
    
    public PricingController() {
        this.pricingService = new PricingService();
        this.couponService = new CouponService();
        this.pricingRulesService = new PricingRulesService();
        this.paymentScheduleService = new PaymentScheduleService();
        this.taxRateService = new TaxRateService();
        this.serviceFeeRateService = new ServiceFeeRateService();
    }
    
    @FXML
    public void initialize() {
        setupCouponTable();
        setupPricingRulesTable();
        setupTaxRateTable();
        setupServiceFeeTable();
        setupFormComponents();
        loadData();
    }
    
    private void setupCouponTable() {
        couponCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        couponTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        couponValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        couponStatusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isValid() ? "Active" : "Inactive"
            )
        );
    }
    
    private void setupPricingRulesTable() {
        ruleTypeColumn.setCellValueFactory(new PropertyValueFactory<>("ruleType"));
        ruleConditionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getConditionType() + ": " + cellData.getValue().getConditionValue()
            )
        );
        ruleDiscountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDiscountValue() + " " + cellData.getValue().getDiscountType()
            )
        );
    }
    
    private void setupTaxRateTable() {
        taxNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taxRateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRate() + "%"
            )
        );
    }
    
    private void setupServiceFeeTable() {
        feeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        feeRateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRate() + "%"
            )
        );
    }
    
    private void setupFormComponents() {
        // Setup combo boxes
        couponTypeComboBox.setItems(FXCollections.observableArrayList("PERCENT", "FIXED"));
        ruleTypeComboBox.setItems(FXCollections.observableArrayList("VOLUME", "EARLY_BIRD", "OFF_PEAK", "GROUP_SIZE", "VENUE_BASE"));
        ruleConditionComboBox.setItems(FXCollections.observableArrayList("MIN_PEOPLE", "EXACT_PEOPLE", "RANGE_MIN", "DAYS_BEFORE_EVENT", "DAY_OF_WEEK"));
        ruleDiscountTypeComboBox.setItems(FXCollections.observableArrayList("PERCENT", "FIXED_AMOUNT"));
        taxTypeComboBox.setItems(FXCollections.observableArrayList("PERCENTAGE", "FIXED_AMOUNT"));
        taxApplicabilityComboBox.setItems(FXCollections.observableArrayList("ALL", "VENUE", "ACTIVITY", "SERVICE"));
        feeTypeComboBox.setItems(FXCollections.observableArrayList("PERCENTAGE", "FIXED_AMOUNT"));
        feeApplicabilityComboBox.setItems(FXCollections.observableArrayList("ALL", "VENUE_ONLY", "ACTIVITY_ONLY"));
        eventTypeComboBox.setItems(FXCollections.observableArrayList("TeamBuilding", "Anniversary", "Formation", "Paddle", "Partying"));
        
        // Set default values
        couponTypeComboBox.setValue("PERCENT");
        ruleTypeComboBox.setValue("GROUP_SIZE");
        ruleConditionComboBox.setValue("MIN_PEOPLE");
        ruleDiscountTypeComboBox.setValue("PERCENT");
        taxTypeComboBox.setValue("PERCENTAGE");
        taxApplicabilityComboBox.setValue("ALL");
        feeTypeComboBox.setValue("PERCENTAGE");
        feeApplicabilityComboBox.setValue("ALL");
        eventTypeComboBox.setValue("TeamBuilding");
        couponActiveCheckBox.setSelected(true);
        ruleActiveCheckBox.setSelected(true);
        taxActiveCheckBox.setSelected(true);
        feeActiveCheckBox.setSelected(true);
    }
    
    private void loadData() {
        try {
            couponTable.setItems(FXCollections.observableArrayList(couponService.readAll()));
            pricingRulesTable.setItems(FXCollections.observableArrayList(pricingRulesService.readAll()));
            if (taxRateService != null) {
                taxRateTable.setItems(FXCollections.observableArrayList(taxRateService.readAll()));
            }
            if (serviceFeeRateService != null) {
                serviceFeeTable.setItems(FXCollections.observableArrayList(serviceFeeRateService.readAll()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Coupon Management
    @FXML
    private void addCoupon() {
        try {
            Coupon coupon = new Coupon();
            coupon.setCode(couponCodeField.getText());
            coupon.setType(couponTypeComboBox.getValue());
            coupon.setValue(Double.parseDouble(couponValueField.getText()));
            coupon.setStartDate(couponStartDate.getValue());
            coupon.setEndDate(couponEndDate.getValue());
            coupon.setUsageLimit(Integer.parseInt(couponUsageLimitField.getText()));
            coupon.setMinSpend(Double.parseDouble(couponMinSpendField.getText()));
            coupon.setDescription(couponDescriptionField.getText());
            coupon.setIsActive(couponActiveCheckBox.isSelected());
            
            if (couponService.ajouter(coupon)) {
                showAlert("Success", "Coupon added successfully!");
                clearCouponForm();
                loadData();
            } else {
                showAlert("Error", "Failed to add coupon.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }
    
    @FXML
    private void updateCoupon() {
        Coupon selected = couponTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateCouponForm(selected);
        }
    }
    
    @FXML
    private void saveCoupon() {
        addCoupon();
    }
    
    @FXML
    private void deleteCoupon() {
        Coupon selected = couponTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Delete Coupon", "Are you sure you want to delete this coupon?")) {
                if (couponService.supprimer(selected)) {
                    showAlert("Success", "Coupon deleted successfully!");
                    loadData();
                } else {
                    showAlert("Error", "Failed to delete coupon.");
                }
            }
        }
    }
    
    // Pricing Rules Management
    @FXML
    private void addPricingRule() {
        try {
            PricingRules rule = new PricingRules();
            rule.setEventType("ALL"); // Can be made configurable
            rule.setRuleType(ruleTypeComboBox.getValue());
            rule.setConditionType(ruleConditionComboBox.getValue());
            rule.setConditionValue(Double.parseDouble(ruleConditionValueField.getText()));
            rule.setDiscountType(ruleDiscountTypeComboBox.getValue());
            rule.setDiscountValue(Double.parseDouble(ruleDiscountValueField.getText()));
            rule.setDescription(ruleDescriptionField.getText());
            rule.setIsActive(ruleActiveCheckBox.isSelected());
            
            if (pricingRulesService.ajouter(rule)) {
                showAlert("Success", "Pricing rule added successfully!");
                clearRuleForm();
                loadData();
            } else {
                showAlert("Error", "Failed to add pricing rule.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }
    
    @FXML
    private void updatePricingRule() {
        PricingRules selected = pricingRulesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateRuleForm(selected);
        }
    }
    
    @FXML
    private void deletePricingRule() {
        PricingRules selected = pricingRulesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Delete Pricing Rule", "Are you sure you want to delete this pricing rule?")) {
                if (pricingRulesService.supprimer(selected)) {
                    showAlert("Success", "Pricing rule deleted successfully!");
                    loadData();
                } else {
                    showAlert("Error", "Failed to delete pricing rule.");
                }
            }
        }
    }
    
    // Tax Rate Management
    @FXML
    private void addTaxRate() {
        try {
            TaxRate taxRate = new TaxRate();
            taxRate.setName(taxNameField.getText());
            taxRate.setRate(Double.parseDouble(taxRateField.getText()));
            taxRate.setType(taxTypeComboBox.getValue());
            taxRate.setApplicability(taxApplicabilityComboBox.getValue());
            taxRate.setDescription(taxDescriptionField.getText());
            taxRate.setIsActive(taxActiveCheckBox.isSelected());
            
            if (taxRateService != null && taxRateService.ajouter(taxRate)) {
                showAlert("Success", "Tax rate added successfully!");
                clearTaxForm();
                loadData();
            } else {
                showAlert("Error", "Failed to add tax rate.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }
    
    // Service Fee Management
    @FXML
    private void addServiceFee() {
        try {
            ServiceFeeRate feeRate = new ServiceFeeRate();
            feeRate.setName(feeNameField.getText());
            feeRate.setRate(Double.parseDouble(feeRateField.getText()));
            feeRate.setType(feeTypeComboBox.getValue());
            feeRate.setApplicability(feeApplicabilityComboBox.getValue());
            feeRate.setMinAmount(feeMinAmountField.getText().isEmpty() ? null : Double.parseDouble(feeMinAmountField.getText()));
            feeRate.setMaxAmount(feeMaxAmountField.getText().isEmpty() ? null : Double.parseDouble(feeMaxAmountField.getText()));
            feeRate.setDescription(feeDescriptionField.getText());
            feeRate.setIsActive(feeActiveCheckBox.isSelected());
            
            if (serviceFeeRateService != null && serviceFeeRateService.ajouter(feeRate)) {
                showAlert("Success", "Service fee added successfully!");
                clearFeeForm();
                loadData();
            } else {
                showAlert("Error", "Failed to add service fee.");
            }
        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }
    
    // Pricing Calculator
    @FXML
    private void calculatePricing() {
        try {
            // Create a sample pricing request (in real implementation, this would come from actual event data)
            EventPricingRequest request = new EventPricingRequest();
            request.setEventType(eventTypeComboBox.getValue());
            request.setHeadcount(Integer.parseInt(headcountField.getText()));
            request.setBookingDate(LocalDate.now());
            request.setEventDate(LocalDate.now().plusWeeks(2));
            request.setCouponCode(couponCodeField.getText());
            
            // Calculate pricing
            PricingCalculation calculation = pricingService.calculateEventPrice(request);
            
            // Display results
            StringBuilder result = new StringBuilder();
            result.append("=== PRICING CALCULATION ===\n\n");
            result.append("Event Type: ").append(request.getEventType()).append("\n");
            result.append("Headcount: ").append(request.getHeadcount()).append("\n\n");
            result.append("Venue Base Fee: $").append(String.format("%.2f", calculation.getVenueBaseFee())).append("\n");
            result.append("Venue Per Person: $").append(String.format("%.2f", calculation.getVenuePerPersonFee())).append("\n");
            result.append("Activities Total: $").append(String.format("%.2f", calculation.getActivitiesTotal())).append("\n");
            result.append("Services Total: $").append(String.format("%.2f", calculation.getServicesTotal())).append("\n");
            result.append("Subtotal: $").append(String.format("%.2f", calculation.getSubtotal())).append("\n\n");
            
            if (calculation.getDiscountAmount() > 0) {
                result.append("DISCOUNTS APPLIED:\n");
                for (PricingCalculation.AppliedDiscount discount : calculation.getAppliedDiscounts()) {
                    result.append("- ").append(discount.getName()).append(": -$").append(String.format("%.2f", discount.getAmount())).append("\n");
                }
                result.append("Total Discount: -$").append(String.format("%.2f", calculation.getDiscountAmount())).append("\n\n");
            }
            
            result.append("Tax Amount: $").append(String.format("%.2f", calculation.getTaxAmount())).append("\n");
            result.append("Service Fee: $").append(String.format("%.2f", calculation.getServiceFeeAmount())).append("\n");
            result.append("========================\n");
            result.append("TOTAL: $").append(String.format("%.2f", calculation.getTotal())).append("\n");
            
            pricingResultArea.setText(result.toString());
            
        } catch (Exception e) {
            showAlert("Error", "Calculation error: " + e.getMessage());
        }
    }
    
    // Helper methods
    private void clearCouponForm() {
        couponCodeField.clear();
        couponValueField.clear();
        couponUsageLimitField.clear();
        couponMinSpendField.clear();
        couponDescriptionField.clear();
        couponStartDate.setValue(null);
        couponEndDate.setValue(null);
    }
    
    private void clearRuleForm() {
        ruleConditionValueField.clear();
        ruleDiscountValueField.clear();
        ruleDescriptionField.clear();
    }
    
    private void clearTaxForm() {
        taxNameField.clear();
        taxRateField.clear();
        taxDescriptionField.clear();
    }
    
    private void clearFeeForm() {
        feeNameField.clear();
        feeRateField.clear();
        feeMinAmountField.clear();
        feeMaxAmountField.clear();
        feeDescriptionField.clear();
    }
    
    private void populateCouponForm(Coupon coupon) {
        couponCodeField.setText(coupon.getCode());
        couponTypeComboBox.setValue(coupon.getType());
        couponValueField.setText(coupon.getValue().toString());
        couponStartDate.setValue(coupon.getStartDate());
        couponEndDate.setValue(coupon.getEndDate());
        couponUsageLimitField.setText(coupon.getUsageLimit() != null ? coupon.getUsageLimit().toString() : "");
        couponMinSpendField.setText(coupon.getMinSpend() != null ? coupon.getMinSpend().toString() : "");
        couponDescriptionField.setText(coupon.getDescription());
        couponActiveCheckBox.setSelected(coupon.getIsActive());
    }
    
    private void populateRuleForm(PricingRules rule) {
        ruleTypeComboBox.setValue(rule.getRuleType());
        ruleConditionComboBox.setValue(rule.getConditionType());
        ruleConditionValueField.setText(rule.getConditionValue().toString());
        ruleDiscountTypeComboBox.setValue(rule.getDiscountType());
        ruleDiscountValueField.setText(rule.getDiscountValue().toString());
        ruleDescriptionField.setText(rule.getDescription());
        ruleActiveCheckBox.setSelected(rule.getIsActive());
    }
    
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
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
