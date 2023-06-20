package refactoring.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InvoiceData {
    private final Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private final String displayId;
    private final String externalId;
    private final String invoiceType;
    private final BigDecimal commissionAmount;
    private final LocalDate invoiceDate;
    // Brazil specific field
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private String prefeituraUrl;
 }
