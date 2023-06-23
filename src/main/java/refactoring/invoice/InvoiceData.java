package refactoring.invoice;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.invoice.corebilling.CoreInvoice;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@SuperBuilder
public class InvoiceData {
    private final Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private final String displayId;
    private final String externalId;
    private final String invoiceType;
    private final BigDecimal commissionAmount;
    private final LocalDate invoiceDate;

    // NOTE: Mappers are replaced by factory methods based on builders
    // this makes the mapping logic in one place
    public static InvoiceData fromCoreBillingInvoice(CoreInvoice invoice) {
        return InvoiceData.builder()
                .assetId(invoice.getHotelId())  // note: attribute name conversion
                .invoiceType(invoice.getType()) // // note: attribute name conversion
                .invoiceDate(invoice.getInvoiceDate())
                .externalId(invoice.getInvoiceId().toString())  // note: type  and attr name conversion
                .commissionAmount(invoice.getCommission()) // note: attribute name conversion
                .displayId(invoice.getInvoiceId().toString())
                .build();
    }

    public static InvoiceData fromNewBillingInvoice(SimpleInvoiceProjectionDTO i) {
        return builder()
                .assetId(i.getAssetId())
                .invoiceType(i.getInvoiceType())
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getExternalId())
                .commissionAmount(i.getSettlementInvoiceAmount())
                .displayId(i.getExternalId())
                .build();
    }
}
