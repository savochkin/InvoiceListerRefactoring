package refactoring.invoicelister.domain;

import lombok.Builder;
import lombok.Data;
import refactoring.billingengine.SimpleInvoiceProjectionDTO;
import refactoring.corebilling.CoreInvoice;
import refactoring.corebilling.CoreInvoiceBrazil;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InvoiceData {
    private final Long assetId;
    // this is what we display on invoices pages as "Invoice Number": brazil? rpsNumber : externalId
    private final String displayId;
    private final String externalId;
    private final String invoiceType;
    private final BigDecimal commissionAmount;
    private final LocalDate invoiceDate;
    private final int company;
    private final Long rpsNumber;
    // for brazil the download link should point to prefeituraUrl
    private final String prefeituraUrl;

    public static InvoiceData fromFinanceInvoice(InvoiceData invoice, CoreInvoiceBrazil brazil) {
        return builder()
                .assetId(invoice.getAssetId())
                .displayId(invoice.getDisplayId())
                .externalId(invoice.getExternalId())
                .invoiceType(invoice.getInvoiceType())
                .commissionAmount(invoice.getCommissionAmount())
                .invoiceDate(invoice.getInvoiceDate())
                .company(invoice.company)
                .rpsNumber(brazil == null? null : brazil.getRpsNumber())
                .prefeituraUrl(brazil == null? null : brazil.getPrefeituraUrl())
                .build();
    }

    public static InvoiceData fromFinanceInvoice(CoreInvoice invoice) {
        return builder()
                .assetId(invoice.getHotelId())
                .externalId(invoice.getInvoiceId().toString())
                .invoiceType(invoice.getType())
                .commissionAmount(invoice.getCommission())
                .invoiceDate(invoice.getInvoiceDate())
                .company(invoice.getCompany())
                .build();
    }

    public static InvoiceData fromNbeInvoice(SimpleInvoiceProjectionDTO invoice, int company) {
        return builder()
                .assetId(invoice.getAssetId())
                .externalId(invoice.getExternalId())
                .invoiceType(invoice.getInvoiceType())
                .commissionAmount(invoice.getSettlementInvoiceAmount())
                .invoiceDate(invoice.getInvoiceDate())
                .rpsNumber(invoice.getExtraInfo_rpsNumber())
                .prefeituraUrl(invoice.getExtraInfo_prefeituraUrl())
                .company(company)
                .build();
    }
}
