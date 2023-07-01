package refactoring.invoicelister.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import refactoring.FinConstants;
import refactoring.billingengine.BillingEngineClient;
import refactoring.corebilling.FinanceInvoiceBrazil;
import refactoring.corebilling.FinanceInvoiceBrazilService;
import refactoring.corebilling.FinanceInvoiceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
public class Debtor {
    private final Long debtorId;
    private final String contractedBy;
    private final FinanceInvoiceService financeInvoiceService;
    private final BillingEngineClient billingEngineClient;

    public List<InvoiceData> getInvoices(Boolean includeCOO) {
        List<InvoiceData> invoices = new ArrayList<>();
        invoices.addAll(getCoreInvoices());
        invoices.addAll(getNbeInvoices());
        return invoices.stream().map(i->InvoiceData.fromInvoiceDataAndAdyenAllowed(i, this.isAdyenAllowed())).toList();
    }

    protected List<InvoiceData> getNbeInvoices() {
        return billingEngineClient.getInvoices(this.getDebtorId())
                .stream().map(i->InvoiceData.fromNbeInvoice(i,FinConstants.COMPANY_BOOKING_BV)).toList();
    }

    protected List<InvoiceData> getCoreInvoices() {
        return financeInvoiceService.getInvoicesForHotel(this.getDebtorId())
                .stream().map(InvoiceData::fromFinanceInvoice).toList();
    }

    public boolean isAdyenAllowed() {
        return !isContractedByBrazil();
    }

    public boolean isContractedByBrazil() {
        return FinConstants.CONTRACTED_BY_BRAZIL.equals(getContractedBy());
    }

}
