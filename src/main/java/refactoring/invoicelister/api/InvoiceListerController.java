package refactoring.invoicelister.api;

import lombok.AllArgsConstructor;
import refactoring.invoicelister.domain.Debtor;
import refactoring.invoicelister.domain.DebtorFactory;

@AllArgsConstructor
public class InvoiceListerController {
    private DebtorFactory debtorFactory;

    public GetInvoicesListResponse getInvoicesList(Long assetId, Boolean includeCOO) {
        Debtor debtor = debtorFactory.getById(assetId);
        // note: we are using Dto for this operation not the domain object
        // controller may use domain objects to perform the necessary operation
        // but at the end it assembles all the data and coverts it to the Dto
        // for transmission
        return GetInvoicesListResponse.builder()
                .isAdyenEnabled(debtor.isAdyenEnabled())
                .invoices(debtor.getInvoices(includeCOO).stream().map(InvoiceDto::fromInvoiceData).toList())
                .build();
    }
}
