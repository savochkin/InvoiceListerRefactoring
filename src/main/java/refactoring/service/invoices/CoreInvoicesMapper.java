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
                .assetId(i.getHotelId())
                .invoiceType(i.getType())
                .invoiceDate(i.getInvoiceDate())
                .externalId(i.getInvoiceId())
                .commissionAmount(i.getCommission())
                .build();
    }
}
