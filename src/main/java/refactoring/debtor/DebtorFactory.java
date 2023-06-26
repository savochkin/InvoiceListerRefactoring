package refactoring.debtor;

import lombok.AllArgsConstructor;
import refactoring.billingengine.BillingEngineClient;
import refactoring.invoice.corebilling.CoreInvoiceBrazilRepository;
import refactoring.invoice.corebilling.CoreInvoiceRepository;

/*
 * NOTE: Removed all the services and used repositories directly instead
 * if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
 * services should be used to implement some important domain logic only if the logic does not fit
 * into any entity/VO/Repository
 *
 * Reorganized the packages so that they were more loosely coupled and cohesive,
 * e.g. all debtor related domain classes should go to "debtor" package
 * no controller/dto/services/repo like packages as they make all the packages tightly coupled
 *
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
                    .build();
        }
    }
}
