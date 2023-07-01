package refactoring.corebilling;

import refactoring.corebilling.FinanceInvoice;

import java.util.List;

public interface FinanceInvoiceService {
    public List<FinanceInvoice> getInvoicesForHotel(Long hotelId);
}
