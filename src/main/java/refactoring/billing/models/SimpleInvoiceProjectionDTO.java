package refactoring.billing.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class SimpleInvoiceProjectionDTO {
    private final String externalId;
    private final BigDecimal settlementInvoiceAmount;
    private final String invoiceType;
    private final LocalDate invoiceDate;
    // for now I do not want to bother with business entities
    private final Long assetId;
    private final String paidState;
    private final Long extraInfo_rpsNumber;
    private final String extraInfo_prefeituraUrl;
}
