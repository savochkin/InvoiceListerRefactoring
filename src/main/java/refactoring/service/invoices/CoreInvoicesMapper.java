package refactoring.service.invoices;

import refactoring.domain.fin.FinanceInvoice;
import refactoring.dto.InvoiceData;

import java.util.List;

public class CoreInvoicesMapper {

    public static List<InvoiceData> mapCoreInvoices(List<FinanceInvoice> invoices) {
        return invoices.stream().map(i -> mapCoreInvoice(i)).toList();
    }

    private static InvoiceData mapCoreInvoice(FinanceInvoice i) {
        return InvoiceData.builder()
                .assetId(i.getHotelId())  // note: attribute name conversion
                .invoiceType(i.getType()) // // note: attribute name conversion
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getInvoiceId().toString())  // note: type  and attr name conversion
                .commissionAmount(i.getCommission()) // note: attribute name conversion
                .build();
    }
}
