package refactoring.invoicelister.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.corebilling.CoreInvoiceBrazil;
import refactoring.corebilling.CoreInvoiceBrazilService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
public class BrazilDebtor extends Debtor {
    private CoreInvoiceBrazilService coreInvoiceBrazilService;
    @Override
    protected List<InvoiceData> getCoreInvoices() {
        List<InvoiceData> invoices1 = getCoreInvoiceService().getInvoicesForHotel(this.getDebtorId())
                .stream().map(InvoiceData::fromFinanceInvoice).toList();
        return getBrazilInvoices(invoices1);
    }
    @Override
    protected List<InvoiceData> getNbeInvoices() {
        return getBillingEngineClient().getInvoices(this.getDebtorId())
                .stream().map(i->InvoiceData.fromNbeInvoice(i,FinConstants.COMPANY_BOOKING_LTDA)).toList();
    }
    private List<InvoiceData> getBrazilInvoices(List<InvoiceData> invoices) {
        Map<Long, CoreInvoiceBrazil> brazilInvoices = getBrazilInvoices();
        return invoices.stream().map(i -> InvoiceData.fromFinanceInvoice(i, brazilInvoices.get(Long.valueOf(i.getExternalId())))).toList();
    }
    private Map<Long, CoreInvoiceBrazil> getBrazilInvoices() {
        List<CoreInvoiceBrazil> brazilInvoices = getCoreInvoiceBrazilService().getByInvoiceIds();
        return brazilInvoices.stream().collect(Collectors.toMap(CoreInvoiceBrazil::getInvoiceId, Function.identity()));
    }
}
