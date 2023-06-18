package refactoring.service.repo.fin;

import refactoring.domain.fin.FinanceInvoice;

import java.time.LocalDate;
import java.util.List;

public interface FinanceInvoiceService {
    public List<FinanceInvoice> getInvoicesForHotel(Long hotelId);
}
