package refactoring.service.billingengine;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;

import java.util.List;

public interface BillingEngineClient {
    List<SimpleInvoiceProjectionDTO> getInvoices(Long id);
}
