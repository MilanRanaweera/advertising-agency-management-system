package com.sliit.aams.quotation.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateQuotationRequest {
    private Long customerId;
    private List<Item> items;

    @Data
    public static class Item {
        private Long serviceId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
    }
}