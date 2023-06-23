package refactoring.debtor;

import lombok.AllArgsConstructor;
import refactoring.billingengine.BillingEngineClient;
import refactoring.invoice.corebilling.CoreInvoiceBrazilRepository;
import refactoring.invoice.corebilling.CoreInvoiceRepository;

/*
 * Real class is here: https://sourcegraph.booking.com/gitlab.booking.com/faas/finance-invoices/fintooling-services/-/blob/src/main/java/com/booking/faas/fintooling/service/repo/fin/DebtorService.java?L15:14&popover=pinned
 * TODO: remove all such services and use repositories directly instead
 * not sure what value they are adding...but they increase the code base and its complexity
 * if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
 * services should be used to implement some important domain logic only if the logic does not fit
 * into any entity/VO/Repository
 *
 * NOTE: Removed all the services and used repositories directly instead
 *
 * TODO: reorg package structure, services should go into the package by domain
 * what a strange name of the package!
 * all debtor related domain classes should go to "domain.debtor" package
 *
 * NOTE: Moved all debtor related domain classes to the "debtor" package
 */
@AllArgsConstructor
public class DebtorFactory {
    private final DebtorRepository debtorRepository;
    private CoreInvoiceRepository coreInvoiceRepository;
    private BillingEngineClient billingEngineClient;
    private CoreInvoiceBrazilRepository coreInvoiceBrazilRepository;

    /*
        if now is left in only one place - the factory method
        all other places in the code should not contain any ifs related to
        Brazil
    */
    public Debtor getById(Long debtorId) {
        Debtor debtor = debtorRepository.getDebtorById(debtorId);
        if (debtor.isContractedByBrazil()) {
            return BrazilDebtor.builder()
                    .debtorId(debtor.getDebtorId())
                    .contractedBy(debtor.getContractedBy())
                    .billingEngineClient(billingEngineClient)
                    .coreInvoiceRepository(coreInvoiceRepository)
                    .coreInvoiceBrazilRepository(coreInvoiceBrazilRepository)
                    .build();
        } else {
            return Debtor.builder()
                    .debtorId(debtor.getDebtorId())
                    .contractedBy(debtor.getContractedBy())
                    .billingEngineClient(billingEngineClient)
                    .coreInvoiceRepository(coreInvoiceRepository)
                    .coreInvoiceBrazilRepository(coreInvoiceBrazilRepository)
                    .build();
        }
    }
}
