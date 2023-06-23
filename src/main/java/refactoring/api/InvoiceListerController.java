package refactoring.api;

import lombok.AllArgsConstructor;
import refactoring.debtor.DebtorFactory;
import refactoring.invoice.InvoiceData;

import java.util.List;

@AllArgsConstructor
public class InvoiceListerController {
    private DebtorFactory debtorFactory;

    public List<InvoiceData> getInvoicesList(Long assetId, Boolean includeCOO) {
        return debtorFactory.getById(assetId).getInvoices(includeCOO);
    }
}
