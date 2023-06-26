package refactoring.debtor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import refactoring.billingengine.BillingEngineClient;
import refactoring.invoice.InvoiceData;
import refactoring.invoice.corebilling.CoreInvoiceBrazilRepository;
import refactoring.invoice.corebilling.CoreInvoiceRepository;

import java.util.List;
import java.util.stream.Stream;

@Data
@SuperBuilder
@AllArgsConstructor
public class Debtor {
    private final Long debtorId;
    private final String contractedBy;
    private final CoreInvoiceRepository coreInvoiceRepository;
    private final BillingEngineClient billingEngineClient;
    private final CoreInvoiceBrazilRepository coreInvoiceBrazilRepository;

    // if we can pay online via Adyen - this is a property of the debtor, not invoice
    // for the sake of example assuming that Adyen is allowed everywhere but Brazil
    public boolean isAdyenEnabled() {
        return !isContractedByBrazil();
    }

    public boolean isContractedByBrazil() {return "CONTRACTED_BY_BRAZIL".equals(contractedBy);}

    // NOTE: logic is very straightforward as all specific parts (e.g. related to Brazil) are moved to a subclasses
    // No if statements related to Brazil specifics as we hid the brazil logic in a subclass (see BrazilDebtor and BrazilInvoiceData).
    // this allows to quickly grasp the main flow without getting overwhelmed with edge cases
    // if reader wants to go into specific edge cases - she can dive into subclasses. This still should be easier
    // than to navigate procedural code with a lot of ifs
    public List<InvoiceData> getInvoices(Boolean includeCOO) {
        return Stream.concat(
                getInvoicesFromCore(includeCOO).stream(),
                getInvoicesFromNbe(includeCOO).stream())
                .toList();
    }

    protected List<InvoiceData> getInvoicesFromCore(Boolean includeCOO) {
        return coreInvoiceRepository.getInvoicesForHotel(this.getDebtorId()).stream()
                .map(InvoiceData::fromCoreBillingInvoice).toList();
    }

    protected List<InvoiceData> getInvoicesFromNbe(Boolean includeCOO) {
        return billingEngineClient.getInvoices(this.getDebtorId()).stream()
                .map(InvoiceData::fromNewBillingInvoice).toList();
    }
}
