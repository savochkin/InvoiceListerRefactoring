package refactoring.invoicelister.domain;

import lombok.AllArgsConstructor;
import refactoring.billingengine.BillingEngineClient;
import refactoring.corebilling.CoreInvoiceBrazilService;
import refactoring.corebilling.CoreInvoiceService;

/*
 * Real class is here: https://sourcegraph.booking.com/gitlab.booking.com/faas/finance-invoices/fintooling-services/-/blob/src/main/java/com/booking/faas/fintooling/service/repo/fin/DebtorService.java?L15:14&popover=pinned
 * Code smell: "Middle Man" bad smell. (see https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Middle%20Man)
 * not sure what value they are adding...but they increase the code base and its complexity
 * if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
 * services should be used to implement some important domain logic only if the logic does not fit
 * into any entity/VO/Repository
 * Suggestion: remove all such services and use repositories directly instead
 *
 * Code smell: Confusing package naming. We have "repository.fin" and "repo.fin". Why?
 * Suggestion: We need to break into modules/packages so that the intent of our application was clear and to improve loose coupling and high cohesision.
 * Better to separate UI layer, domain  layer and infrastructure layers.
 */
@AllArgsConstructor
public class DebtorFactory {
    private final DebtorRepository debtorRepository;
    private CoreInvoiceBrazilService coreInvoiceBrazilService;
    private CoreInvoiceService coreInvoiceService;
    private BillingEngineClient billingEngineClient;

    public Debtor getById(Long debtorId) {
        Debtor debtor = debtorRepository.getDebtorById(debtorId);
        if (debtor.isContractedByBrazil()) {
            return BrazilDebtor.builder()
                    .contractedBy(debtor.getContractedBy())
                    .debtorId(debtor.getDebtorId())
                    .billingEngineClient(billingEngineClient)
                    .coreInvoiceService(coreInvoiceService)
                    .coreInvoiceBrazilService(coreInvoiceBrazilService)
                    .build();
        } else {
            return Debtor.builder()
                    .contractedBy(debtor.getContractedBy())
                    .debtorId(debtor.getDebtorId())
                    .billingEngineClient(billingEngineClient)
                    .coreInvoiceService(coreInvoiceService)
                    .build();
        }
    }
}
