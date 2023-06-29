package refactoring.domain.fin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FinanceInvoice {
    final private Long invoiceId;
    final private LocalDate invoiceDate;
    final private Long hotelId;
    final private BigDecimal commission;
    final private String paidState;
    final private String type;
    final private int company;
}
