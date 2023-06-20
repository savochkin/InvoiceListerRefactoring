package refactoring.domain.fin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinanceInvoiceBrazil {
    private final Long rpsNumber;
    private final Long invoiceId;
    private final String prefeituraUrl;
}
