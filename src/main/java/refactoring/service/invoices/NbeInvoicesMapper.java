package refactoring.service.invoices;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.dto.InvoiceData;

import java.util.List;

public class NbeInvoicesMapper {
    public static List<InvoiceData> mapNbeInvoices(List<SimpleInvoiceProjectionDTO> invoices) {
        return invoices.stream().map(i -> mapNbeInvoice(i)).toList();
    }

    private static InvoiceData mapNbeInvoice(SimpleInvoiceProjectionDTO i) {
        return InvoiceData.builder()
                .assetId(i.getAssetId())
                .invoiceType(i.getInvoiceType())
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getExternalId())
                .commissionAmount(i.getSettlementInvoiceAmount())
                .rpsNumber(i.getExtraInfo_rpsNumber()) // note: rpsNumber is provided by NBE
                .prefeituraUrl(i.getExtraInfo_prefeituraUrl()) // note: prefeituraUrl is provided by NBE
                .build();
    }
}
