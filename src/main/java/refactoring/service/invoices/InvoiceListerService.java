package refactoring.service.invoices;

import lombok.AllArgsConstructor;
import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.domain.fin.Debtor;
import refactoring.domain.fin.FinanceInvoice;
import refactoring.dto.InvoiceData;
import refactoring.service.billingengine.BillingEngineClient;
import refactoring.service.invoices.brazil.BrazilInvoiceService;
import refactoring.service.repo.fin.DebtorService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class InvoiceListerService {
    private DebtorService debtorService;
    private FinanceInvoiceService financeInvoiceService;
    private BillingEngineClient billingEngineClient;
    private BrazilInvoiceService brazilInvoiceService;

    public List<InvoiceData> getInvoicesList(Long assetId, Boolean includeCOO) {
        Debtor debtor = debtorService.getDebtorById(assetId);
        List<InvoiceData> invoices = new ArrayList<>();
        invoices.addAll(getInvoicesFromCore(debtor, includeCOO));
        invoices.addAll(getInvoicesFromNbe(debtor, includeCOO));
        return invoices;
    }

    private List<InvoiceData> getInvoicesFromCore(Debtor debtor, Boolean includeCOO) {
        // filter invoices in core billing using debtor's start date
        List<FinanceInvoice> financeInvoices = financeInvoiceService.getInvoicesForHotel(debtor.getDebtorId());
        List<InvoiceData> invoices = CoreInvoicesMapper.mapCoreInvoices(financeInvoices);

        if (debtor.isContractedByBrazil()) {
            brazilInvoiceService.setInvoiceBrazilFields(invoices);
        }
        return invoices;
    }

    private List<InvoiceData> getInvoicesFromNbe(Debtor debtor, Boolean includeCOO) {
        // filter invoices in core billing using debtor's start date
        // TODO: dislike the mapper below
        List<SimpleInvoiceProjectionDTO> nbeInvoices = billingEngineClient.getInvoices(debtor.getDebtorId());
        return NbeInvoicesMapper.mapNbeInvoices(nbeInvoices);
    }


}
