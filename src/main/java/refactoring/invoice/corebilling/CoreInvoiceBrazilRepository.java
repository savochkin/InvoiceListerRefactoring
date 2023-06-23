package refactoring.invoice.corebilling;

import java.util.List;

// NOTE: Renamed Finance* to Core* as it is much more consistent with the terminology we use (CoreBilling vs NewBillingEngine).
// Prefix "Finance" is too generic and may confuse the reader
public interface CoreInvoiceBrazilRepository {
    List<CoreInvoiceBrazil> getByInvoiceIds();
}
