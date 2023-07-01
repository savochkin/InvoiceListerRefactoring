package refactoring.invoicelister.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
@Builder
public class Debtor {
    final private Long debtorId;
    final private String contractedBy;

    private FinanceInvoiceService financeInvoiceService;
    private BillingEngineClient billingEngineClient;
    private FinanceInvoiceBrazilService financeInvoiceBrazilService;

    public List<InvoiceData> getInvoices(Boolean includeCOO) {
        List<InvoiceData> invoices = new ArrayList<>();
        List<InvoiceData> invoices1 = getCoreInvoices();
        invoices.addAll(invoices1);
        List<InvoiceData> invoices2 = getNbeInvoices();
        invoices.addAll(invoices2);
        return invoices.stream().map(i->InvoiceData.fromInvoiceDataAndAdyenAllowed(i, this.isAdyenAllowed())).toList();
    }

    private List<InvoiceData> getNbeInvoices() {
        List<InvoiceData> invoices = billingEngineClient.getInvoices(this.getDebtorId())
                .stream().map(InvoiceData::fromNbeInvoice).toList();

        int company = this.isContractedByBrazil()
                ? FinConstants.COMPANY_BOOKING_LTDA : FinConstants.COMPANY_BOOKING_BV;
        return invoices.stream().map(i -> InvoiceData.fromFinanceInvoiceAndCompany(i, company)).toList();
    }

    private List<InvoiceData> getCoreInvoices() {
        List<InvoiceData> invoices1 = financeInvoiceService.getInvoicesForHotel(this.getDebtorId())
                .stream().map(InvoiceData::fromFinanceInvoice).toList();

        /*
        For core billing invoices we need to fetch Brazil related data
        from the special Brazil table
        Code smell: The above comment is a bad smell. This adds a risk of getting comments
        out of sync with the code
        (see https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Comments)
        */
        /*
        Code smell: this is "Repeated Switches" bad smell
        (https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Repeated%20Switches)
        as this logic of determining the Brazil debtor is present in several places in the codebase
        suggestion: we need to extract this check into a function and, since it operates only on attributes
        of the debtor - we can move it to the debtor class
        */
        if (this.isContractedByBrazil()) {
            return getBrazilInvoices(invoices1);
        } else {
            return invoices1;
        }
    }

    public List<InvoiceData> getBrazilInvoices(List<InvoiceData> invoices) {
        Map<Long, FinanceInvoiceBrazil> brazilInvoices = getBrazilInvoices();
        return invoices.stream().map(i -> InvoiceData.fromFinanceInvoice(i, brazilInvoices.get(Long.valueOf(i.getExternalId())))).toList();
    }

    private Map<Long, FinanceInvoiceBrazil> getBrazilInvoices() {
        List<FinanceInvoiceBrazil> brazilInvoices = financeInvoiceBrazilService.getByInvoiceIds();
        return brazilInvoices.stream().collect(Collectors.toMap(FinanceInvoiceBrazil::getInvoiceId, Function.identity()));
    }

    public boolean isAdyenAllowed() {
        return !isContractedByBrazil();
    }

    public boolean isContractedByBrazil() {
        return FinConstants.CONTRACTED_BY_BRAZIL.equals(getContractedBy());
    }

    public static Debtor fromDebtor(Debtor debtor, BillingEngineClient billingEngineClient, FinanceInvoiceService financeInvoiceService, FinanceInvoiceBrazilService financeInvoiceBrazilService) {
        return builder()
                .contractedBy(debtor.getContractedBy())
                .debtorId(debtor.getDebtorId())
                .billingEngineClient(billingEngineClient)
                .financeInvoiceService(financeInvoiceService)
                .financeInvoiceBrazilService(financeInvoiceBrazilService)
                .build();
    }
}
