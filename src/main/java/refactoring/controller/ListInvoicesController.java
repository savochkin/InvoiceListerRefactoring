package refactoring.controller;

import lombok.AllArgsConstructor;
import refactoring.dto.InvoiceData;
import refactoring.service.invoices.InvoiceListerService;

import java.util.List;

// Code smell: The controller is called ListInvoiceController, while service is named InvoiceListerService. Naming is very confusing...
// Why names are so similar? why they are not still fully consistent?
@AllArgsConstructor
public class ListInvoicesController {
    InvoiceListerService invoiceListerService;
    // Code smell: We are using domain object as Dto here. As external clients depend on the Dto this will make it
    // difficult to change. That means that even if we would want to improve the internal structure of our application
    // this would be impossible to do without breaking compatibility for external clients.
    // suggestion: we should distinguish Dto's used for external communication and domain objects used for internal implementation
    public List<InvoiceData> getInvoicesList(Long assetId, Boolean includeCOO) {
        return invoiceListerService.getInvoicesList(assetId, includeCOO);
    }
}
