package refactoring.service.invoices;

import refactoring.domain.fin.FinanceInvoice;
import refactoring.dto.InvoiceData;

import java.util.List;

public class CoreInvoicesMapper {

    public static List<InvoiceData> mapCoreInvoices(List<FinanceInvoice> invoices) {
        return invoices.stream().map(i -> mapCoreInvoice(i)).toList();
    }

    public static InvoiceData mapCoreInvoice(FinanceInvoice i) {
        /*
        Code smell: Mutable Data. If we make InvoiceData mutable - it makes our code fragile as
        it is very difficult to control invariants or business rules over invoice data.
        better to make InvoiceData immutable and use Builder pattern instead as it makes mapping
        much more clear:

        return InvoiceData.builder()
                .assetId(i.getHotelId())  // note: attribute name conversion
                .invoiceType(i.getType()) // // note: attribute name conversion
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getInvoiceId().toString())  // note: type  and attr name conversion
                .commissionAmount(i.getCommission()) // note: attribute name conversion
                .company(i.getCompany())
                .build();
         */
        InvoiceData invoice = new InvoiceData();
        invoice.setAssetId(i.getHotelId());
        invoice.setInvoiceType(i.getType());
        invoice.setInvoiceDate(i.getInvoiceDate());
        invoice.setExternalId(i.getInvoiceId().toString());
        invoice.setCommissionAmount(i.getCommission());
        invoice.setCompany(i.getCompany());
        return invoice;
    }
}
