package com.synapseevent.service;

import com.synapseevent.entities.*;
import java.time.LocalDate;
import java.util.List;

public class PricingService {
    
    private TaxRateService taxRateService;
    private ServiceFeeRateService serviceFeeRateService;
    private PricingRulesService pricingRulesService;
    private CouponService couponService;

    public PricingService() {
        this.taxRateService = new TaxRateService();
        this.serviceFeeRateService = new ServiceFeeRateService();
        this.pricingRulesService = new PricingRulesService();
        this.couponService = new CouponService();
    }

    /**
     * Calculate complete pricing for an event
     * Formula: Total = venue_base_fee (+ per-person) + Σ(activity.price_per_person × headcount) + services + taxes + service_fee
     */
    public PricingCalculation calculateEventPrice(EventPricingRequest request) {
        PricingCalculation calculation = new PricingCalculation();

        // 1. Calculate venue fees
        Double venueBaseFee = calculateVenueBaseFee(request);
        Double venuePerPersonFee = calculateVenuePerPersonFee(request);
        calculation.setVenueBaseFee(venueBaseFee);
        calculation.setVenuePerPersonFee(venuePerPersonFee);

        // 2. Calculate activities total
        Double activitiesTotal = calculateActivitiesTotal(request);
        calculation.setActivitiesTotal(activitiesTotal);

        // 3. Calculate services total
        Double servicesTotal = request.getAdditionalServices() != null ? request.getAdditionalServices() : 0.0;
        calculation.setServicesTotal(servicesTotal);

        // 4. Calculate subtotal
        Double subtotal = venueBaseFee + (venuePerPersonFee * request.getHeadcount()) + 
                         activitiesTotal + servicesTotal;
        calculation.setSubtotal(subtotal);

        // 5. Apply discounts and pricing rules
        Double discountAmount = applyDiscountsAndRules(subtotal, request, calculation);
        Double discountedSubtotal = subtotal - discountAmount;
        calculation.setDiscountAmount(discountAmount);

        // 6. Calculate taxes
        Double taxAmount = calculateTaxes(discountedSubtotal, request, calculation);
        calculation.setTaxAmount(taxAmount);

        // 7. Calculate service fees
        Double serviceFeeAmount = calculateServiceFees(discountedSubtotal, request, calculation);
        calculation.setServiceFeeAmount(serviceFeeAmount);

        // 8. Calculate final total
        Double total = discountedSubtotal + taxAmount + serviceFeeAmount;
        calculation.setTotal(total);

        return calculation;
    }

    private Double calculateVenueBaseFee(EventPricingRequest request) {
        if (request.getVenue() == null) return 0.0;
        
        // Base venue fee logic - could be from venue entity or pricing rules
        Double baseFee = request.getVenue().getBaseFee() != null ? 
                         request.getVenue().getBaseFee() : 0.0;
        
        // Apply venue-specific discounts
        List<PricingRules> venueRules = pricingRulesService.findByEventTypeAndRuleType(
            request.getEventType(), "VENUE_BASE");
        
        for (PricingRules rule : venueRules) {
            if (rule.appliesToEvent(request.getEventType())) {
                baseFee -= rule.calculateDiscount(baseFee);
            }
        }
        
        return Math.max(0.0, baseFee);
    }

    private Double calculateVenuePerPersonFee(EventPricingRequest request) {
        if (request.getVenue() == null) return 0.0;
        
        Double perPersonFee = request.getVenue().getPerPersonFee() != null ? 
                             request.getVenue().getPerPersonFee() : 0.0;
        
        // Apply volume discounts based on headcount
        List<PricingRules> volumeRules = pricingRulesService.findByEventTypeAndRuleType(
            request.getEventType(), "GROUP_SIZE");
        
        for (PricingRules rule : volumeRules) {
            if (rule.appliesToGroupSize(request.getHeadcount())) {
                perPersonFee -= rule.calculateDiscount(perPersonFee);
            }
        }
        
        return Math.max(0.0, perPersonFee);
    }

    private Double calculateActivitiesTotal(EventPricingRequest request) {
        if (request.getActivities() == null) return 0.0;
        
        Double activitiesTotal = 0.0;
        for (TeamBuildingActivity activity : request.getActivities()) {
            Double activityPrice = activity.getPricePerPerson() != null ? 
                                 activity.getPricePerPerson() : 0.0;
            activitiesTotal += activityPrice * request.getHeadcount();
        }
        
        return activitiesTotal;
    }

    private Double applyDiscountsAndRules(Double subtotal, EventPricingRequest request, 
                                      PricingCalculation calculation) {
        Double totalDiscount = 0.0;

        // 1. Apply coupon if provided
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            Coupon coupon = couponService.findByCode(request.getCouponCode());
            if (coupon != null && coupon.canBeUsed(subtotal)) {
                Double couponDiscount = calculateCouponDiscount(coupon, subtotal);
                totalDiscount += couponDiscount;
                calculation.getAppliedDiscounts().add(
                    new PricingCalculation.AppliedDiscount(
                        coupon.getCode(), coupon.getType(), couponDiscount, 
                        "Coupon: " + coupon.getDescription()
                    )
                );
            }
        }

        // 2. Apply early bird discount
        if (request.getBookingDate() != null && request.getEventDate() != null) {
            List<PricingRules> earlyBirdRules = pricingRulesService.findByEventTypeAndRuleType(
                request.getEventType(), "EARLY_BIRD");
            
            for (PricingRules rule : earlyBirdRules) {
                if (rule.appliesToBookingDate(request.getBookingDate(), request.getEventDate())) {
                    Double earlyBirdDiscount = rule.calculateDiscount(subtotal);
                    totalDiscount += earlyBirdDiscount;
                    calculation.getAppliedDiscounts().add(
                        new PricingCalculation.AppliedDiscount(
                            "Early Bird", rule.getDiscountType(), earlyBirdDiscount,
                            "Early booking discount"
                        )
                    );
                }
            }
        }

        // 3. Apply off-peak discount
        if (request.getEventDate() != null) {
            List<PricingRules> offPeakRules = pricingRulesService.findByEventTypeAndRuleType(
                request.getEventType(), "OFF_PEAK");
            
            for (PricingRules rule : offPeakRules) {
                if (rule.appliesToDayOfWeek(request.getEventDate())) {
                    Double offPeakDiscount = rule.calculateDiscount(subtotal);
                    totalDiscount += offPeakDiscount;
                    calculation.getAppliedDiscounts().add(
                        new PricingCalculation.AppliedDiscount(
                            "Off-Peak", rule.getDiscountType(), offPeakDiscount,
                            "Off-peak day discount"
                        )
                    );
                }
            }
        }

        return totalDiscount;
    }

    private Double calculateCouponDiscount(Coupon coupon, Double subtotal) {
        if ("PERCENT".equalsIgnoreCase(coupon.getType())) {
            return subtotal * (coupon.getValue() / 100.0);
        } else if ("FIXED".equalsIgnoreCase(coupon.getType())) {
            return Math.min(coupon.getValue(), subtotal);
        }
        return 0.0;
    }

    private Double calculateTaxes(Double taxableAmount, EventPricingRequest request,
                                  PricingCalculation calculation) {
        List<TaxRate> applicableTaxes = taxRateService.findByEventType(request.getEventType());
        Double totalTax = 0.0;

        for (TaxRate tax : applicableTaxes) {
            if (tax.isValid() && tax.appliesToEventType(request.getEventType())) {
                Double taxAmount = tax.calculateTax(taxableAmount);
                totalTax += taxAmount;
                calculation.getAppliedTaxes().add(
                    new PricingCalculation.AppliedTax(tax.getName(), tax.getRate(), taxAmount)
                );
            }
        }

        return totalTax;
    }

    private Double calculateServiceFees(Double baseAmount, EventPricingRequest request,
                                        PricingCalculation calculation) {
        List<ServiceFeeRate> applicableFees = serviceFeeRateService.findByEventType(request.getEventType());
        Double totalFee = 0.0;

        for (ServiceFeeRate fee : applicableFees) {
            if (fee.isValid() && fee.appliesToEventType(request.getEventType())) {
                Double feeAmount = fee.calculateServiceFee(baseAmount);
                totalFee += feeAmount;
                if (calculation.getAppliedServiceFee() == null) {
                    calculation.setAppliedServiceFee(
                        new PricingCalculation.AppliedServiceFee(fee.getName(), fee.getRate(), feeAmount)
                    );
                }
            }
        }

        return totalFee;
    }

    /**
     * Generate payment schedule for staged payments
     */
    public List<PaymentSchedule> generatePaymentSchedule(Long bookingId, Double totalAmount, 
                                                    PaymentScheduleRequest request) {
        List<PaymentSchedule> schedule = new java.util.ArrayList<>();

        if (request.getDepositPercentage() != null && request.getDepositPercentage() > 0) {
            // Create deposit payment
            PaymentSchedule deposit = PaymentSchedule.createDepositPayment(
                bookingId, totalAmount, request.getDepositPercentage(), request.getEventDate()
            );
            schedule.add(deposit);
        }

        if (request.getInstallmentCount() != null && request.getInstallmentCount() > 1) {
            // Create installment payments
            Double remainingAmount = totalAmount - (totalAmount * request.getDepositPercentage() / 100.0);
            Double installmentAmount = remainingAmount / (request.getInstallmentCount() - 1);
            
            LocalDate installmentDate = LocalDate.now().plusMonths(1);
            for (int i = 2; i <= request.getInstallmentCount(); i++) {
                PaymentSchedule installment = PaymentSchedule.createInstallmentPayment(
                    bookingId, i, installmentAmount, installmentDate
                );
                schedule.add(installment);
                installmentDate = installmentDate.plusMonths(1);
            }
        } else {
            // Single final payment
            Double depositAmount = request.getDepositPercentage() != null ? 
                               totalAmount * (request.getDepositPercentage() / 100.0) : 0.0;
            PaymentSchedule finalPayment = PaymentSchedule.createFinalPayment(
                bookingId, totalAmount, depositAmount, request.getEventDate()
            );
            schedule.add(finalPayment);
        }

        return schedule;
    }
}
