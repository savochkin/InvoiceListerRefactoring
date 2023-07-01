package refactoring.invoicelister.ui;

import lombok.Builder;
import lombok.Data;
import refactoring.invoicelister.domain.InvoiceData;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InvoiceDto {
    private final Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private final String displayId;
    private final String externalId;
    private final String invoiceType;
    private final BigDecimal commissionAmount;
    private final LocalDate invoiceDate;
    private final int company;
    // Brazil specific field
    private final Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    private final String prefeituraUrl;

    public static InvoiceDto fromInvoiceData(InvoiceData invoice) {
        return builder()
                .assetId(invoice.getAssetId())
                .displayId(invoice.getDisplayId())
                .externalId(invoice.getExternalId())
                .invoiceType(invoice.getInvoiceType())
                .commissionAmount(invoice.getCommissionAmount())
                .invoiceDate(invoice.getInvoiceDate())
                .company(invoice.getCompany())
                .rpsNumber(invoice.getRpsNumber())
                .prefeituraUrl(invoice.getPrefeituraUrl())
                .build();
    }
}
