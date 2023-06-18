package refactoring.billing.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SimpleInvoiceProjectionDTO {
    final private String externalId;
    final private BigDecimal settlementInvoiceAmount;
    final private String invoiceType;
    final private LocalDate invoiceDate;
    // for now I do not want to bother with business entities
    final private Long assetId;
    final private String paidState;
}
