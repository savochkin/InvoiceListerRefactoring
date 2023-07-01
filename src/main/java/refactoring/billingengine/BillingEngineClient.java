package refactoring.billingengine;

import java.util.List;

public interface BillingEngineClient {
    List<SimpleInvoiceProjectionDTO> getInvoices(Long id);
}
