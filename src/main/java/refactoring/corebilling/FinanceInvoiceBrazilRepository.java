package refactoring.corebilling;

import refactoring.corebilling.FinanceInvoiceBrazil;

import java.util.List;

public interface FinanceInvoiceBrazilRepository {
    List<FinanceInvoiceBrazil> getByInvoiceIds();
}
