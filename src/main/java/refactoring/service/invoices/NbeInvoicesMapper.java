package refactoring.service.invoices;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.dto.InvoiceData;

import java.util.List;

public class NbeInvoicesMapper {
    public static List<InvoiceData> mapNbeInvoices(List<SimpleInvoiceProjectionDTO> invoices) {
        return invoices.stream().map(i -> mapNbeInvoice(i)).toList();
    }

    private static InvoiceData mapNbeInvoice(SimpleInvoiceProjectionDTO i) {
        /*
        Code smell: Mutable Data. If we make InvoiceData mutable - it makes our code fragile as
        it is very difficult to control invariants or business rules over invoice data.
        better to make InvoiceData immutable and use Builder pattern instead as it makes mapping
        much more clear:

        return InvoiceData.builder()
                .assetId(i.getAssetId())
                .invoiceType(i.getInvoiceType())
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getExternalId())
                .commissionAmount(i.getSettlementInvoiceAmount())
                .rpsNumber(i.getExtraInfo_rpsNumber()) // note: rpsNumber is provided by NBE
                .prefeituraUrl(i.getExtraInfo_prefeituraUrl()) // note: prefeituraUrl is provided by NBE
                .build();
         */
        InvoiceData invoice = new InvoiceData();
        invoice.setAssetId(i.getAssetId());
        invoice.setInvoiceType(i.getInvoiceType());
        invoice.setInvoiceDate(i.getInvoiceDate());
        invoice.setExternalId(i.getExternalId());
        invoice.setCommissionAmount(i.getSettlementInvoiceAmount());
        invoice.setRpsNumber(i.getExtraInfo_rpsNumber());
        invoice.setPrefeituraUrl(i.getExtraInfo_prefeituraUrl());
        return invoice;
    }
}
