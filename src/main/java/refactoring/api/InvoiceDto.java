package refactoring.api;

import lombok.Builder;
import lombok.Data;
import refactoring.invoice.BrazilInvoiceData;
import refactoring.invoice.InvoiceData;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
public class InvoiceDto {
    private final Long assetId;
    private final String displayId;
    private final String externalId;
    private final String invoiceType;
    private final BigDecimal commissionAmount;
    private final LocalDate invoiceDate;
    private final Long rpsNumber;
    private final String prefeituraUrl;

    // note: Dto knows about the domain object not vice versa.
    // domain objects should not know anything about the outside world
    public static InvoiceDto fromInvoiceData(InvoiceData invoice) {
        if (invoice instanceof BrazilInvoiceData) {
            return builder()
                    .assetId(invoice.getAssetId())
                    .displayId(invoice.getDisplayId())
                    .externalId(invoice.getExternalId())
                    .invoiceType(invoice.getInvoiceType())
                    .commissionAmount(invoice.getCommissionAmount())
                    .invoiceDate(invoice.getInvoiceDate())
                    .prefeituraUrl(((BrazilInvoiceData)invoice).getPrefeituraUrl())
                    .rpsNumber(((BrazilInvoiceData)invoice).getRpsNumber())
                    .build();
        } else {
            return builder()
                    .assetId(invoice.getAssetId())
                    .displayId(invoice.getDisplayId())
                    .externalId(invoice.getExternalId())
                    .invoiceType(invoice.getInvoiceType())
                    .commissionAmount(invoice.getCommissionAmount())
                    .invoiceDate(invoice.getInvoiceDate())
                    .build();
        }
    }
}
