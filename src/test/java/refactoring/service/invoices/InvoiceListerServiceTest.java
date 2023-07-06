package refactoring.service.invoices;

import refactoring.FinConstants;
import refactoring.controller.ListInvoicesController;
import refactoring.dto.InvoiceData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceListerServiceTest {

/*
   functional requirements:
   + for all invoices return basic information including:
   externalId, invoiceType, commissionAmount, status, invoiceDate
   + for invoices from brazil partner return extra info including:
   rpsNumber, prefeituraUrl
   + return indicator if invoice is allowed to be paid by Adyen (we allow to be paid by Adyen in all countries but Brazil)
   - for all invoice types commission amount is positive, for credit note - negative
   - for all invoices from non-europe partners return isAdyenPayment = false, otherwise return true
   - partners can fetch invoices only for the assets they currently own
   - B.com staff can fetch invoices for the current owner but also can fetch all invoices for an asset disregarding the ownership
   - invoices may come from core billing and nbe
 */

    @Test
    void getInvoicesFromNonBrazil() {
        List<InvoiceData> invoices = getListInvoicesController().getInvoicesList(DebtorFactory.getNonBrazilDebtorId(), false);
        assertNotNull(invoices);
        assertEquals(getExpectedNonBrazilInvoices().size(), invoices.size());
        for(int i=0; i<invoices.size(); i++) {
            assertEquals(getExpectedNonBrazilInvoices().get(i), invoices.get(i));
        }
    }

    @Test
    void getInvoicesFromBrazil() {
        List<InvoiceData> invoices = getListInvoicesController().getInvoicesList(DebtorFactory.getBrazilDebtorId(), false);
        assertNotNull(invoices);
        assertEquals(getExpectedBrazilInvoices().size(), invoices.size());
        for(InvoiceData invoice: getExpectedBrazilInvoices()) {
            assertTrue(invoices.stream().anyMatch(i -> i.equals(invoice)));
        }
    }

    @Test
    void adyenAllowedInAllCountriesButBrazil() {
        List<InvoiceData> invoices = getListInvoicesController().getInvoicesList(DebtorFactory.getBrazilDebtorId(), false);
        assertNotNull(invoices);
        for(InvoiceData invoice: invoices) {
            assertFalse(invoice.isAdyenAllowed());
        }

        List<InvoiceData> invoices2 = getListInvoicesController().getInvoicesList(DebtorFactory.getNonBrazilDebtorId(), false);
        assertNotNull(invoices2);
        for(InvoiceData invoice: invoices2) {
            assertTrue(invoice.isAdyenAllowed());
        }
    }

    public static ListInvoicesController getListInvoicesController() {
        InvoiceListerService invoiceListerService = new InvoiceListerService(
                DebtorFactory.getDebtorService(), CoreBillingFactory.getFinanceInvoiceService(),
                NbeFactory.getBillingEngineClient(), CoreBillingFactory.getBrazilInvoiceService());
        return new ListInvoicesController(invoiceListerService);
    }

    private List<InvoiceData> getExpectedNonBrazilInvoices() {
        InvoiceData invoiceData1 = new InvoiceData();
        invoiceData1.setAssetId(DebtorFactory.getNonBrazilDebtorId());
        invoiceData1.setExternalId("20230103");
        invoiceData1.setInvoiceType("invoice");
        invoiceData1.setCommissionAmount(BigDecimal.valueOf(231.03));
        invoiceData1.setInvoiceType("reservation_statement");
        invoiceData1.setInvoiceDate(LocalDate.of(2023, 1, 3));
        invoiceData1.setAdyenAllowed(true);
        invoiceData1.setCompany(FinConstants.COMPANY_BOOKING_BV);
        InvoiceData invoiceData2 = new InvoiceData();
        invoiceData2.setAssetId(DebtorFactory.getNonBrazilDebtorId());
        invoiceData2.setExternalId("20230203");
        invoiceData2.setInvoiceType("invoice");
        invoiceData2.setCommissionAmount(BigDecimal.valueOf(232.03));
        invoiceData2.setInvoiceType("reservation_statement");
        invoiceData2.setInvoiceDate(LocalDate.of(2023, 2, 3));
        invoiceData2.setAdyenAllowed(true);
        invoiceData2.setCompany(FinConstants.COMPANY_BOOKING_BV);
        InvoiceData invoiceData3 = new InvoiceData();
        invoiceData3.setAssetId(DebtorFactory.getNonBrazilDebtorId());
        invoiceData3.setExternalId("20230303");
        invoiceData3.setInvoiceType("invoice");
        invoiceData3.setCommissionAmount(BigDecimal.valueOf(233.03));
        invoiceData3.setInvoiceType("reservation_statement");
        invoiceData3.setInvoiceDate(LocalDate.of(2023, 3, 3));
        invoiceData3.setAdyenAllowed(true);
        invoiceData3.setCompany(FinConstants.COMPANY_BOOKING_BV);
        InvoiceData invoiceData4 = new InvoiceData();
        invoiceData4.setAssetId(DebtorFactory.getNonBrazilDebtorId());
        invoiceData4.setExternalId("20230403");
        invoiceData4.setInvoiceType("invoice");
        invoiceData4.setCommissionAmount(BigDecimal.valueOf(234.03));
        invoiceData4.setInvoiceType("reservation_statement");
        invoiceData4.setInvoiceDate(LocalDate.of(2023, 4, 3));
        invoiceData4.setAdyenAllowed(true);
        invoiceData4.setCompany(FinConstants.COMPANY_BOOKING_BV);
        /*
        InvoiceData invoiceData1 = InvoiceData.builder()
                .assetId(DebtorFactory.getNonBrazilDebtorId())
                .externalId("20230103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(231.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .adyenAllowed(true)
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceData invoiceData2 = InvoiceData.builder()
                .assetId(DebtorFactory.getNonBrazilDebtorId())
                .externalId("20230203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(232.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .adyenAllowed(true)
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceData invoiceData3 = InvoiceData.builder()
                .assetId(DebtorFactory.getNonBrazilDebtorId())
                .externalId("20230303")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(233.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 3, 3))
                .adyenAllowed(true)
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceData invoiceData4 = InvoiceData.builder()
                .assetId(DebtorFactory.getNonBrazilDebtorId())
                .externalId("20230403")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(234.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 4, 3))
                .adyenAllowed(true)
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
         */
        return List.of(invoiceData1, invoiceData2, invoiceData3, invoiceData4);
    }

    private List<InvoiceData> getExpectedBrazilInvoices() {
        InvoiceData invoiceData1 = new InvoiceData();
        invoiceData1.setAssetId(DebtorFactory.getBrazilDebtorId());
        invoiceData1.setExternalId("20220103");
        invoiceData1.setInvoiceType("invoice");
        invoiceData1.setCommissionAmount(BigDecimal.valueOf(221.03));
        invoiceData1.setInvoiceType("reservation_statement");
        invoiceData1.setInvoiceDate(LocalDate.of(2022, 1, 3));
        invoiceData1.setRpsNumber(9999L);
        invoiceData1.setPrefeituraUrl("http://www.prefeitura.com/20220103");
        invoiceData1.setAdyenAllowed(false);
        invoiceData1.setCompany(FinConstants.COMPANY_BOOKING_LTDA);
        InvoiceData invoiceData2 = new InvoiceData();
        invoiceData2.setAssetId(DebtorFactory.getBrazilDebtorId());
        invoiceData2.setExternalId("20220203");
        invoiceData2.setInvoiceType("invoice");
        invoiceData2.setCommissionAmount(BigDecimal.valueOf(222.03));
        invoiceData2.setInvoiceType("reservation_statement");
        invoiceData2.setInvoiceDate(LocalDate.of(2022, 2, 3));
        invoiceData2.setRpsNumber(8888L);
        invoiceData2.setPrefeituraUrl("http://www.prefeitura.com/20220203");
        invoiceData2.setAdyenAllowed(false);
        invoiceData2.setCompany(FinConstants.COMPANY_BOOKING_LTDA);
        /*
        InvoiceData invoiceData1 = InvoiceData.builder()
                .assetId(DebtorFactory.getBrazilDebtorId())
                .externalId("20220103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(221.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 1, 3))
                .rpsNumber(9999L)
                .prefeituraUrl("http://www.prefeitura.com/20220103")
                .adyenAllowed(false)
                .company(FinConstants.COMPANY_BOOKING_LTDA)
                .build();
        InvoiceData invoiceData2 = InvoiceData.builder()
                .assetId(DebtorFactory.getBrazilDebtorId())
                .externalId("20220203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(222.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .rpsNumber(8888L)
                .prefeituraUrl("http://www.prefeitura.com/20220203")
                .adyenAllowed(false)
                .company(FinConstants.COMPANY_BOOKING_LTDA)
                .build();*/
        return List.of(invoiceData1, invoiceData2);
    }

}
