package refactoring.repository.fin;

import refactoring.domain.fin.FinanceInvoiceBrazil;

import java.util.List;

public interface FinanceInvoiceBrazilRepository {
    List<FinanceInvoiceBrazil> getByInvoiceIds();
}
