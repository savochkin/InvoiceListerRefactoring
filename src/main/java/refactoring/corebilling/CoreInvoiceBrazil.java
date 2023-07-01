package refactoring.corebilling;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreInvoiceBrazil {
    private final Long rpsNumber;
    private final Long invoiceId;
    private final String prefeituraUrl;
}
