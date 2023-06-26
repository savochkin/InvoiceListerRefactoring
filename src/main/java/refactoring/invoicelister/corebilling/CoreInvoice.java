package refactoring.invoicelister.corebilling;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// NOTE: Renamed Finance* to Core* as it is much more consistent with the terminology we use (CoreBilling vs NewBillingEngine).
// Prefix "Finance" is too generic and may confuse the reader
@Data
@Builder
public class CoreInvoice {
    private final Long invoiceId;
    private final LocalDate invoiceDate;
    private final Long hotelId;
    private final BigDecimal commission;
    private final String paidState;
    private final String type;
}
