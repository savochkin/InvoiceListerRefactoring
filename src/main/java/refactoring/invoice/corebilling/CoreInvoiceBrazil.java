package refactoring.invoice.corebilling;

import lombok.Builder;
import lombok.Data;

// NOTE: Renamed Finance* to Core* as it is much more consistent with the terminology we use (CoreBilling vs NewBillingEngine).
// Prefix "Finance" is too generic and may confuse the reader
@Data
@Builder
public class CoreInvoiceBrazil {
    private final Long rpsNumber;
    private final Long invoiceId;
    private final String prefeituraUrl;
}
