package refactoring.debtor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.invoice.BrazilInvoiceData;
import refactoring.invoice.InvoiceData;
import refactoring.invoice.corebilling.CoreInvoiceBrazilRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    Note: Encapsulates all the Brazil specific logic and dependencies.
    This allows a reader to not go into very detail until he really needs it.
 */
@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
public class BrazilDebtor extends Debtor {
    private final CoreInvoiceBrazilRepository coreInvoiceBrazilRepository;
    @Override
    protected List<InvoiceData> getInvoicesFromNbe(Boolean includeCOO) {
        return getBillingEngineClient().getInvoices(this.getDebtorId()).stream()
                .map(BrazilInvoiceData::fromNewBillingInvoice).toList();
    }

    @Override
    protected List<InvoiceData> getInvoicesFromCore(Boolean includeCOO) {
        return getBrazilianInvoices(getCoreInvoices());
    }

    private List<InvoiceData> getBrazilianInvoices(List<InvoiceData> invoices) {
        Map<String, InvoiceData> invoiceById = invoices.stream().collect(Collectors.toMap(InvoiceData::getExternalId, Function.identity()));
        return getCoreInvoiceBrazilRepository().getByInvoiceIds().stream()
                .map(b -> (InvoiceData) BrazilInvoiceData.fromInvoiceData(invoiceById.get(String.valueOf(b.getInvoiceId())), b))
                .toList();
    }

    private List<InvoiceData> getCoreInvoices() {
        return getCoreInvoiceRepository().getInvoicesForHotel(this.getDebtorId()).stream()
                .map(InvoiceData::fromCoreBillingInvoice).toList();
    }
}
