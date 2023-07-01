package refactoring.invoicelister.ui;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetInvoiceListResponse {
    // indicator if this invoice is allowed to be paid online via Adyen
    private final boolean isAdyenAllowed;
    private final List<InvoiceDto> invoices;
}
