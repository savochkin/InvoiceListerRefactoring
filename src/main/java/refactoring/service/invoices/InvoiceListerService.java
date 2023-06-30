package refactoring.service.invoices;

import lombok.AllArgsConstructor;
import refactoring.FinConstants;
import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.domain.fin.Debtor;
import refactoring.dto.InvoiceData;
import refactoring.service.billingengine.BillingEngineClient;
import refactoring.service.invoices.brazil.BrazilInvoiceService;
import refactoring.service.repo.fin.DebtorService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class InvoiceListerService {
    private DebtorService debtorService;
    private FinanceInvoiceService financeInvoiceService;
    private BillingEngineClient billingEngineClient;
    private BrazilInvoiceService brazilInvoiceService;

    /*
     Code smell: Long function with many parameters which contains a lot of if statements which makes it fragile and difficult to understand.
     This is a main invoice lister operation which fetches invoices for a debtor.
     However, since we are using anemic model (e.g. Debtor is just a data holder which exposes all its data via getters and setters)
     in order to implement special processing for Brazil we need to use a lot of "if" statements
     throughout the code. In our example we have several if statements - one for setting rps/prefeitura for core invoices, one for setting company for nbe invoices
     and one for implementing isAdyenAllowed logic. In real life number of special cases grow making it very difficult to understand the code.

     Also, our procedural style make it necessary to pass a lot of parameters around
     (in this example we need to pass assetId to retrieve debtor).

     suggestion: Encapsulate the behaviour in a rich domain model (e.g. move getInvoices list to the Debtor class, encapsulate Brazil specifics
     in a Brazil subclasses). Debtor entity should encapsulate the complexity of the fetching invoices via Core and NBE
     this method should look like
     {
        return DebtorRepository.findById(assetId).getInvoices();
     }

     */
    public List<InvoiceData> getInvoicesList(Long assetId, Boolean includeCOO) {
        Debtor debtor = debtorService.getDebtorById(assetId);
        List<InvoiceData> invoices = new ArrayList<>();
        List<InvoiceData> invoices1 = financeInvoiceService.getInvoicesForHotel(debtor.getDebtorId())
                .stream().map(CoreInvoicesMapper::mapCoreInvoice).toList();

        /*
        For core billing invoices we need to fetch Brazil related data
        from the special Brazil table
        Code smell: The above comment is a bad smell. This adds a risk of getting comments
        out of sync with the code
        (see https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Comments)
        */
        /*
        Code smell: this is "Repeated Switches" bad smell
        (https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Repeated%20Switches)
        as this logic of determining the Brazil debtor is present in several places in the codebase
        suggestion: we need to extract this check into a function and, since it operates only on attributes
        of the debtor - we can move it to the debtor class
        */
        if (FinConstants.CONTRACTED_BY_BRAZIL.equals(debtor.getContractedBy())) {
            /*
            Code smell: This is a "Mutable Data" bad smell
            (see https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Mutable%20Data).
            We are passing the invoices to a service which sets some fields in it.
            This may lead to unexpected and tricky bugs as we may update some fields in one place
            and potentially break in some other place as there some other data was expected.
            Suggestion: We should use immutable objects whenever possible, everychange should result in a new copy of the data
            e.g. in this case we may intially have basic InvoiceData immutable object, but for Brazil we would need to
            create a new object BrazilInvoiceData.
             */
            brazilInvoiceService.setInvoiceBrazilFields(invoices1);
        }
        invoices.addAll(invoices1);
        List<SimpleInvoiceProjectionDTO> nbeInvoices = billingEngineClient.getInvoices(debtor.getDebtorId());
        List<InvoiceData> invoices2 = NbeInvoicesMapper.mapNbeInvoices(nbeInvoices);

        for(InvoiceData invoice1 : invoices2) {
            // Code smell: "Repeated Switches"
            int company = FinConstants.CONTRACTED_BY_BRAZIL.equals(debtor.getContractedBy())
                    ? FinConstants.COMPANY_BOOKING_LTDA : FinConstants.COMPANY_BOOKING_BV;
            invoice1.setCompany(company);
        }
        invoices.addAll(invoices2);

        for(InvoiceData invoice: invoices) {
            // Code smell: "Repeated Switches"
            if (FinConstants.CONTRACTED_BY_BRAZIL.equals(debtor.getContractedBy())) {
                // Requirement: We allow paying online through Adyen in all countries but Brazil
                // Code smell: if the business logic relies only on Debtor attributes - this property should be moved to the Debtor class along with
                // the logic. If it also takes into account invoices attributes - then it should be encapsulated in the invoices class. Exposing the
                // business logic in the service places produces risks of bugs... we need to remember that we need to obtain invoices
                // only through this operation. What if somebody fetches invoices directly? What if somebody mutates invoices for some reason?
                // Code smell: Mutable data (https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Mutable%20Data)
                // suggestion: Make InvoiceData immutable and encapsulate "AdyenAllowed" business logic to the Debtor class
                invoice.setAdyenAllowed(false);
            } else {
                invoice.setAdyenAllowed(true);
            }
        }
        return invoices;
    }


}
