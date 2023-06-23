package refactoring.invoice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.invoice.corebilling.CoreInvoiceBrazil;

@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
public class BrazilInvoiceData extends InvoiceData {
    // Brazil specific field
    // NOTE: all the fields are final as we removed setXXXFields methods
    private final Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    private final String prefeituraUrl;

    // NOTE: Mappers are replaced by factory methods based on builders
    // this makes the mapping logic in one place
    public static InvoiceData fromInvoiceData(InvoiceData invoice, CoreInvoiceBrazil brazil) {
        return builder()
                .assetId(invoice.getAssetId())  // note: attribute name conversion
                .invoiceType(invoice.getInvoiceType()) // // note: attribute name conversion
                .invoiceDate(invoice.getInvoiceDate())
                .externalId(invoice.getExternalId().toString())  // note: type  and attr name conversion
                .commissionAmount(invoice.getCommissionAmount()) // note: attribute name conversion
                .rpsNumber(brazil.getRpsNumber())
                .prefeituraUrl(brazil.getPrefeituraUrl())
                .displayId(String.valueOf(brazil.getRpsNumber())) // note: display id for brazil is coming from rps number
                .build();
    }

    public static InvoiceData fromNewBillingInvoice(SimpleInvoiceProjectionDTO i) {
        return builder()
                .assetId(i.getAssetId())
                .invoiceType(i.getInvoiceType())
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getExternalId())
                .commissionAmount(i.getSettlementInvoiceAmount())
                .rpsNumber(i.getExtraInfo_rpsNumber()) // note: rpsNumber is provided by NBE
                .prefeituraUrl(i.getExtraInfo_prefeituraUrl()) // note: prefeituraUrl is provided by NBE
                .displayId(String.valueOf(i.getExtraInfo_rpsNumber())) // note: display id for brazil is coming from rps number
                .build();
    }
}
