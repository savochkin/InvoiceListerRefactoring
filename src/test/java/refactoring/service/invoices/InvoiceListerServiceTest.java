package refactoring.service.invoices;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.domain.fin.Debtor;
import refactoring.domain.fin.FinanceInvoice;
import refactoring.domain.fin.FinanceInvoiceBrazil;
import refactoring.dto.InvoiceData;
import org.junit.jupiter.api.Test;
import refactoring.repository.fin.DebtorRepository;
import refactoring.repository.fin.FinanceInvoiceBrazilRepository;
import refactoring.service.billingengine.BillingEngineClient;
import refactoring.service.invoices.brazil.BrazilInvoiceService;
import refactoring.service.repo.fin.DebtorService;
import refactoring.service.repo.fin.FinanceInvoiceBrazilService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceListerServiceTest {
    final private static long NON_BRAZIL_DEBTOR_ID = 111L;
    final private static long BRAZIL_DEBTOR_ID = 222L;
/*
   functional requirements:
   + for all invoices return basic information including:
   externalId, invoiceType, commissionAmount, status, invoiceDate
   + for invoices from brazil partner return extra info including:
   rpsNumber, prefeituraUrl
   - for all invoice types commission amount is positive, for credit note - negative
   - for all invoices from non-europe partners return isAdyenPayment = false, otherwise return true
   - partners can fetch invoices only for the assets they currently own
   - B.com staff can fetch invoices for the current owner but also can fetch all invoices for an asset disregarding the ownership
   - invoices may come from core billing and nbe
 */

    @Test
    void getInvoiceBasicInformation() {
        DebtorService debtorService = getDebtorService();
        BrazilInvoiceService brazilInvoiceService = getBrazilInvoiceService();
        FinanceInvoiceService financeInvoiceService = getFinanceInvoiceService();
        BillingEngineClient billingEngineClient = getBillingEngineClient();

        InvoiceListerService sut = new InvoiceListerService(debtorService, financeInvoiceService, billingEngineClient, brazilInvoiceService);
        List<InvoiceData> invoices = sut.getInvoicesList(NON_BRAZIL_DEBTOR_ID, false);
        assertNotNull(invoices);
        assertEquals(getTestInvoices().size(), invoices.size());
        for(int i=0; i<invoices.size(); i++) {
            assertEquals(getTestInvoices().get(i), invoices.get(i));
        }
    }

    @Test
    void getInvoicesFromBrazil() {
        DebtorService debtorService = getDebtorService();
        BrazilInvoiceService brazilInvoiceService = getBrazilInvoiceService();
        FinanceInvoiceService financeInvoiceService = getFinanceInvoiceService();
        BillingEngineClient billingEngineClient = getBillingEngineClient();

        InvoiceListerService sut = new InvoiceListerService(debtorService, financeInvoiceService, billingEngineClient, brazilInvoiceService);
        List<InvoiceData> invoices = sut.getInvoicesList(BRAZIL_DEBTOR_ID, false);
        assertNotNull(invoices);
        assertEquals(getTestBrazilInvoices().size(), invoices.size());
        for(InvoiceData invoice: getTestBrazilInvoices()) {
            assertTrue(invoices.stream().anyMatch(i -> i.equals(invoice)));
        }
    }

    private BillingEngineClient getBillingEngineClient() {
        BillingEngineClient billingEngineClient = mock(BillingEngineClient.class);
        when(billingEngineClient.getInvoices(NON_BRAZIL_DEBTOR_ID)).thenReturn(getNbeInvoices());
        when(billingEngineClient.getInvoices(BRAZIL_DEBTOR_ID)).thenReturn(getNbeBrazilInvoices());
        return billingEngineClient;
    }

    private FinanceInvoiceService getFinanceInvoiceService() {
        FinanceInvoiceService financeInvoiceService = mock(FinanceInvoiceService.class);
        when(financeInvoiceService.getInvoicesForHotel(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestFinanceInvoices());
        when(financeInvoiceService.getInvoicesForHotel(BRAZIL_DEBTOR_ID)).thenReturn(getTestFinanceBrazilInvoices());
        return financeInvoiceService;
    }

    private DebtorService getDebtorService() {
        DebtorRepository debtorRepository = mock(DebtorRepository.class);
        when(debtorRepository.getDebtorById(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestDebtor());
        when(debtorRepository.getDebtorById(BRAZIL_DEBTOR_ID)).thenReturn(getBrazilDebtor());
        return new DebtorService(debtorRepository);
    }

    private BrazilInvoiceService getBrazilInvoiceService() {
        FinanceInvoiceBrazilRepository financeInvoiceBrazilRepository = mock(FinanceInvoiceBrazilRepository.class);
        when(financeInvoiceBrazilRepository.getByInvoiceIds()).thenReturn(getFinanceInvoiceBrazilInvoices());

        FinanceInvoiceBrazilService financeInvoiceBrazilService = new FinanceInvoiceBrazilService(financeInvoiceBrazilRepository);
        BrazilInvoiceService brazilInvoiceService = new BrazilInvoiceService(financeInvoiceBrazilService);
        return brazilInvoiceService;
    }

    private List<FinanceInvoiceBrazil> getFinanceInvoiceBrazilInvoices() {
        FinanceInvoiceBrazil brazil1 = FinanceInvoiceBrazil.builder()
                .invoiceId(20220203L)
                .rpsNumber(8888L)
                .prefeituraUrl("http://www.prefeitura.com/20220203")
                .build();
        return List.of(brazil1);
    }

    private List<SimpleInvoiceProjectionDTO> getNbeInvoices() {
        SimpleInvoiceProjectionDTO invoice1 = SimpleInvoiceProjectionDTO.builder()
                .externalId("20230303")
                .invoiceDate(LocalDate.of(2023, 3, 3))
                .assetId(111L)
                .settlementInvoiceAmount(BigDecimal.valueOf(233.03))
                .paidState("paid")
                .invoiceType("reservation_statement")
                .build();
        SimpleInvoiceProjectionDTO invoice2 = SimpleInvoiceProjectionDTO.builder()
                .externalId("20230403")
                .invoiceDate(LocalDate.of(2023, 4, 3))
                .assetId(111L)
                .settlementInvoiceAmount(BigDecimal.valueOf(234.03))
                .paidState("paid")
                .invoiceType("reservation_statement")
                .build();
        return List.of(invoice1, invoice2);
    }

    private List<FinanceInvoice> getTestFinanceInvoices() {
        FinanceInvoice financeInvoice1 =  FinanceInvoice.builder()
                .invoiceId(20230103L)
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(231.03))
                .paidState("paid")
                .type("reservation_statement")
                .build();
        FinanceInvoice financeInvoice2 =  FinanceInvoice.builder()
                .invoiceId(20230203L)
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(232.03))
                .paidState("unpaid")
                .type("reservation_statement")
                .build();
        return List.of(financeInvoice1, financeInvoice2);
    }

    private Debtor getTestDebtor() {
        return Debtor.builder()
                .debtorId(111L)
                .contractedBy("NON-BRAZIL")
                .build();
    }

    private Debtor getBrazilDebtor() {
        return Debtor.builder()
                .debtorId(BRAZIL_DEBTOR_ID)
                .contractedBy("CONTRACTED_BY_BRAZIL")
                .build();
    }

    private List<InvoiceData> getTestInvoices() {
        InvoiceData invoiceData1 = InvoiceData.builder()
                .assetId(111L)
                .externalId("20230103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(231.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .build();
        InvoiceData invoiceData2 = InvoiceData.builder()
                .assetId(111L)
                .externalId("20230203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(232.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .build();
        InvoiceData invoiceData3 = InvoiceData.builder()
                .assetId(111L)
                .externalId("20230303")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(233.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 3, 3))
                .build();
        InvoiceData invoiceData4 = InvoiceData.builder()
                .assetId(111L)
                .externalId("20230403")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(234.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 4, 3))
                .build();
        return List.of(invoiceData1, invoiceData2, invoiceData3, invoiceData4);
    }

    private List<InvoiceData> getTestBrazilInvoices() {
        InvoiceData invoiceData1 = InvoiceData.builder()
                .assetId(BRAZIL_DEBTOR_ID)
                .externalId("20220103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(221.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 1, 3))
                .rpsNumber(9999L)
                .prefeituraUrl("http://www.prefeitura.com/20220103")
                .build();
        InvoiceData invoiceData2 = InvoiceData.builder()
                .assetId(BRAZIL_DEBTOR_ID)
                .externalId("20220203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(222.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .rpsNumber(8888L)
                .prefeituraUrl("http://www.prefeitura.com/20220203")
                .build();
        return List.of(invoiceData1, invoiceData2);
    }

    private List<SimpleInvoiceProjectionDTO> getNbeBrazilInvoices() {
        SimpleInvoiceProjectionDTO invoice1 = SimpleInvoiceProjectionDTO.builder()
                .externalId("20220103")
                .invoiceDate(LocalDate.of(2022, 1, 3))
                .assetId(BRAZIL_DEBTOR_ID)
                .settlementInvoiceAmount(BigDecimal.valueOf(221.03))
                .paidState("paid")
                .invoiceType("reservation_statement")
                .extraInfo_rpsNumber(9999L)
                .extraInfo_prefeituraUrl("http://www.prefeitura.com/20220103")
                .build();
        return List.of(invoice1);
    }

    private List<FinanceInvoice> getTestFinanceBrazilInvoices() {
        FinanceInvoice financeInvoice1 =  FinanceInvoice.builder()
                .invoiceId(20220203L)
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .hotelId(BRAZIL_DEBTOR_ID)
                .commission(BigDecimal.valueOf(222.03))
                .paidState("paid")
                .type("reservation_statement")
                .build();
        return List.of(financeInvoice1);
    }
}
