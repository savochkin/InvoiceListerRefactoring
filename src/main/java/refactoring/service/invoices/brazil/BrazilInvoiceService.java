package refactoring.service.invoices.brazil;

import lombok.AllArgsConstructor;
import refactoring.domain.fin.FinanceInvoiceBrazil;
import refactoring.dto.InvoiceData;
import refactoring.service.repo.fin.FinanceInvoiceBrazilService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BrazilInvoiceService {
    FinanceInvoiceBrazilService financeInvoiceBrazilService;
    public void setInvoiceBrazilFields(List<InvoiceData> invoices) {
        Map<Long, FinanceInvoiceBrazil> brazilInvoices = getBrazilInvoices();
        for(var invoice: invoices) {
            FinanceInvoiceBrazil brazilInvoice = brazilInvoices.get(Long.valueOf(invoice.getExternalId()));
            if (brazilInvoice != null) {
                invoice.setRpsNumber(brazilInvoice.getRpsNumber());
                invoice.setPrefeituraUrl(brazilInvoice.getPrefeituraUrl());
            }
        }
    }

    private Map<Long, FinanceInvoiceBrazil> getBrazilInvoices() {
        List<FinanceInvoiceBrazil> brazilInvoices = financeInvoiceBrazilService.getByInvoiceIds();
        return brazilInvoices.stream().collect(Collectors.toMap(FinanceInvoiceBrazil::getInvoiceId, Function.identity()));
    }
}
