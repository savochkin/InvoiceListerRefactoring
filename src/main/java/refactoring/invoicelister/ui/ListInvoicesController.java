package refactoring.invoicelister.ui;

import lombok.AllArgsConstructor;
import refactoring.invoicelister.domain.Debtor;
import refactoring.invoicelister.domain.DebtorFactory;

@AllArgsConstructor
public class ListInvoicesController {
    DebtorFactory debtorFactory;
    public GetInvoiceListResponse getInvoicesList(Long assetId, Boolean includeCOO) {
        Debtor debtor = debtorFactory.getById(assetId);
        return GetInvoiceListResponse.builder()
                .isAdyenAllowed(debtor.isAdyenAllowed())
                .invoices(debtor.getInvoices(includeCOO).stream().map(InvoiceDto::fromInvoiceData).toList())
                .build();
    }
}
