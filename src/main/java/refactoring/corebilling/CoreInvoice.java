package refactoring.corebilling;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CoreInvoice {
    private final Long invoiceId;
    private final LocalDate invoiceDate;
    private final Long hotelId;
    private final BigDecimal commission;
    private final String paidState;
    private final String type;
    private final int company;
}
