package refactoring.controller;

import lombok.AllArgsConstructor;
import refactoring.domain.fin.Debtor;
import refactoring.domain.fin.FinanceInvoice;
import refactoring.dto.InvoiceData;
import refactoring.service.invoices.CoreInvoicesMapper;
import refactoring.service.repo.fin.DebtorService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ListInvoicesController {
    private DebtorService debtorService;
    private FinanceInvoiceService financeInvoiceService;

    public List<InvoiceData> getInvoicesList(Long assetId, String assetType, Boolean includeCOO) {
        Debtor debtor = debtorService.getDebtorById(assetId);

        return getInvoicesFromCore(debtor, includeCOO);
        //List<InvoiceData> invoices = coreInvoiceListerService.getInvoices(debtor, includeCOO);
        //invoices.addAll(nbeInvoiceListerService.getInvoices(debtor, assetType, includeCOO));
        //processCoreAndNbe(debtor, assetId, invoices);

        /*List<InvoiceData> invoices = new ArrayList<>();
        invoices.add(InvoiceData.builder()
                .assetId(111L)
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(10.0))
                .isAdyenPayment(true)
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .build());
        return invoices;*/
    }

    private List<InvoiceData> getInvoicesFromCore(Debtor debtor, Boolean includeCOO) {
        // filter invoices in core billing using debtor's start date
        List<FinanceInvoice> financeInvoices = financeInvoiceService.getInvoicesForHotel(debtor.getDebtorId());
        List<InvoiceData> coreInvoices = CoreInvoicesMapper.mapCoreInvoices(financeInvoices);
        return coreInvoices;
    }


}
