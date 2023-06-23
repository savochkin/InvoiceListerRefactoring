package refactoring.invoice.corebilling;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// NOTE: Renamed Finance* to Core* as it is much more consistent with the terminology we use (CoreBilling vs NewBillingEngine).
// Prefix "Finance" is too generic and may confuse the reader
@Data
@Builder
public class CoreInvoice {
    final private Long invoiceId;
    final private LocalDate invoiceDate;
    final private Long hotelId;
    final private BigDecimal commission;
    final private String paidState;
    final private String type;
}
