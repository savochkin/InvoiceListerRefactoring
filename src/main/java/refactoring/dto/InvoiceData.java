package refactoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class InvoiceData {
    private Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private String displayId;
    private String externalId;
    private String invoiceType;
    private BigDecimal commissionAmount;
    private LocalDate invoiceDate;
    private int company;
    // indicator if this invoice is allowed to be payed online via Adyen
    private boolean isAdyenAllowed;
    // Brazil specific field
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private String prefeituraUrl;

 }
