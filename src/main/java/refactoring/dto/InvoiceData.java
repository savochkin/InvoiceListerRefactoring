package refactoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import refactoring.domain.fin.FinanceInvoiceBrazil;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class InvoiceData {
    private Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private String displayId;
    private String externalId;
    private String invoiceType;
    private BigDecimal commissionAmount;
    private LocalDate invoiceDate;
    private int company;
    // indicator if this invoice is allowed to be payed online via Adyen
    private boolean isAdyenAllowed;
    // Brazil specific field
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    // TODO: this property is not final, because we are setting it while enriching Core invoices
    private String prefeituraUrl;

    public static InvoiceData fromFinanceInvoice(InvoiceData invoice, FinanceInvoiceBrazil brazil) {
        InvoiceData result = new InvoiceData();
        result.setAssetId(invoice.getAssetId());
        result.setDisplayId(invoice.getDisplayId());
        result.setExternalId(invoice.getExternalId());
        result.setInvoiceType(invoice.getInvoiceType());
        result.setCommissionAmount(invoice.getCommissionAmount());
        result.setInvoiceDate(invoice.getInvoiceDate());
        result.setCompany(invoice.company);
        if (brazil != null) {
            result.setRpsNumber(brazil.getRpsNumber());
            result.setPrefeituraUrl(brazil.getPrefeituraUrl());
        }
        return result;
    }
 }
