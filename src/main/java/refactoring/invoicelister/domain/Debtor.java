package refactoring.invoicelister.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.FinConstants;
import refactoring.billingengine.BillingEngineClient;
import refactoring.corebilling.CoreInvoiceService;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
public class Debtor {
    private final Long debtorId;
    private final String contractedBy;
    private final CoreInvoiceService coreInvoiceService;
    private final BillingEngineClient billingEngineClient;

    public List<InvoiceData> getInvoices(Boolean includeCOO) {
        List<InvoiceData> invoices = new ArrayList<>();
        invoices.addAll(getCoreInvoices());
        invoices.addAll(getNbeInvoices());
        return invoices;
    }

    protected List<InvoiceData> getNbeInvoices() {
        return billingEngineClient.getInvoices(this.getDebtorId())
                .stream().map(i->InvoiceData.fromNbeInvoice(i,FinConstants.COMPANY_BOOKING_BV)).toList();
    }

    protected List<InvoiceData> getCoreInvoices() {
        return coreInvoiceService.getInvoicesForHotel(this.getDebtorId())
                .stream().map(InvoiceData::fromFinanceInvoice).toList();
    }

    public boolean isAdyenAllowed() {
        return !isContractedByBrazil();
    }

    public boolean isContractedByBrazil() {
        return FinConstants.CONTRACTED_BY_BRAZIL.equals(getContractedBy());
    }

}
