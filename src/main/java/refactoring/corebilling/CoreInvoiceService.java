package refactoring.corebilling;

import java.util.List;

public interface CoreInvoiceService {
    public List<CoreInvoice> getInvoicesForHotel(Long hotelId);
}
