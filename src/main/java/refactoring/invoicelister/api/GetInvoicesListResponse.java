package refactoring.invoicelister.api;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/*
    Data Transfer Object (DTO) that is used to carry data for the external (REST or GraphQL) API.
    We are distinguishing DTO from the domain model InvoiceData object
    What is the difference?

    Domain model is design to allow for the normalization, flexibility and maintenance

    Dto is design just to carry data for a particular scenario, e.g.:
    Debtor and InvoiceData are different domain objects
    Debtor may contain isAdyenEnabled property, while InvoiceData should contain only the data related to the invoice

    but for GetInvoiceList endpoint we may choose to pack information about the debtor and the invoice in one
    Dto just to return all the data for invoice page from one service call
 */
@Getter
@Builder
public class GetInvoicesListResponse {
    // note: this is a property of the debtor, not the invoice
    // but we may choose to send this information in the same Dto
    // to save on service calls
    private final boolean isAdyenEnabled;
    List<InvoiceDto> invoices;
}
