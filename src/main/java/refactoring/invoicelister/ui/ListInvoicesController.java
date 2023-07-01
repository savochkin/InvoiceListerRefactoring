package refactoring.invoicelister.ui;

import lombok.AllArgsConstructor;
import refactoring.invoicelister.domain.DebtorFactory;
import refactoring.invoicelister.domain.InvoiceData;

import java.util.List;

@AllArgsConstructor
public class ListInvoicesController {
    DebtorFactory debtorFactory;
    // Code smell: We are using domain object as Dto here. As external clients depend on the Dto this will make it
    // difficult to change. That means that even if we would want to improve the internal structure of our application
    // this would be impossible to do without breaking compatibility for external clients.
    // suggestion: we should distinguish Dto's used for external communication and domain objects used for internal implementation
    public List<InvoiceData> getInvoicesList(Long assetId, Boolean includeCOO) {
        return debtorFactory.getById(assetId).getInvoices(includeCOO);
    }
}
