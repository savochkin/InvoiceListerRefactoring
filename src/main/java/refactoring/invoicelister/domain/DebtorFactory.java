package refactoring.invoicelister.domain;

import lombok.AllArgsConstructor;
import refactoring.invoicelister.billingengine.BillingEngineClient;
import refactoring.invoicelister.corebilling.CoreInvoiceBrazilRepository;
import refactoring.invoicelister.corebilling.CoreInvoiceRepository;
import refactoring.invoicelister.domain.brazil.BrazilDebtor;

/*
 * NOTE: Removed all the services and used repositories directly instead
 * if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
 * services should be used to implement some important domain logic only if the logic does not fit
 * into any entity/VO/Repository
 */
@AllArgsConstructor
public class DebtorFactory {
    private final DebtorRepository debtorRepository;
    private final CoreInvoiceRepository coreInvoiceRepository;
    private final BillingEngineClient billingEngineClient;
    private final CoreInvoiceBrazilRepository coreInvoiceBrazilRepository;

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
