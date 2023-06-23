package refactoring.debtor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.invoice.BrazilInvoiceData;
import refactoring.invoice.InvoiceData;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper=true)
@Data
@SuperBuilder
public class BrazilDebtor extends Debtor {
    protected List<InvoiceData> getInvoicesFromNbe(Boolean includeCOO) {
        return getBillingEngineClient().getInvoices(this.getDebtorId()).stream()
                .map(BrazilInvoiceData::fromNewBillingInvoice).toList();
    }

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
