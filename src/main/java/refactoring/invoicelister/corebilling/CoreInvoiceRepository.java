package refactoring.invoicelister.corebilling;

import java.util.List;

// NOTE: Renamed Finance* to Core* as it is much more consistent with the terminology we use (CoreBilling vs NewBillingEngine).
// Prefix "Finance" is too generic and may confuse the reader
// NOTE: Removed CoreInvoiceService and used CoreInvoiceRepository instead. Do not see any value at having a service here.
public interface CoreInvoiceRepository {
    public List<CoreInvoice> getInvoicesForHotel(Long hotelId);
}
