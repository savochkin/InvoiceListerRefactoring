package refactoring.invoicelister.api;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.invoicelister.domain.Debtor;
import refactoring.invoicelister.corebilling.CoreInvoice;
import refactoring.invoicelister.corebilling.CoreInvoiceBrazil;
import org.junit.jupiter.api.Test;
import refactoring.invoicelister.domain.DebtorRepository;
import refactoring.invoicelister.corebilling.CoreInvoiceBrazilRepository;
import refactoring.invoicelister.billingengine.BillingEngineClient;
import refactoring.invoicelister.domain.DebtorFactory;
import refactoring.invoicelister.corebilling.CoreInvoiceRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceListerControllerTest {
    final private static long NON_BRAZIL_DEBTOR_ID = 111L;
    final private static long BRAZIL_DEBTOR_ID = 222L;
/*
   functional requirements:
   + for all invoices return basic information including:
   externalId, invoiceType, commissionAmount, status, invoiceDate
   + for invoices from brazil partner return extra info including:
   rpsNumber, prefeituraUrl
   + display id should be calculated differently for brazil and non brazil, as follows  brazil? rpsNumber : externalId
   + or all invoices from non-europe partners return isAdyenAllowed = false, otherwise return true

   not implemented so far:
   - for all invoice types commission amount is positive, for credit note - negative
   - partners can fetch invoices only for the assets they currently own
   - B.com staff can fetch invoices for the current owner but also can fetch all invoices for an asset disregarding the ownership
   - invoices may come from core billing and nbe
 */

    @Test
    void getInvoicesFromNonBrazil() {
        GetInvoicesListResponse response = getInvoiceListerController().getInvoicesList(NON_BRAZIL_DEBTOR_ID, false);
        assertNotNull(response.getInvoices());
        assertEquals(getExpectedNonBrazilInvoices().size(), response.getInvoices().size());
        for(int i=0; i<response.getInvoices().size(); i++) {
            assertEquals(getExpectedNonBrazilInvoices().get(i), response.getInvoices().get(i));
        }
    }

    @Test
    void getInvoicesFromBrazil() {
        GetInvoicesListResponse response = getInvoiceListerController().getInvoicesList(BRAZIL_DEBTOR_ID, false);
        assertNotNull(response.getInvoices());
        assertEquals(getExpectedBrazilInvoices().size(), response.getInvoices().size());
        for(InvoiceDto expected: getExpectedBrazilInvoices()) {
            assertTrue(response.getInvoices().stream().anyMatch(i -> i.equals(expected)));
        }
    }

    @Test
    void adyenAllowedInAllCountriesButBrazil() {
        GetInvoicesListResponse response = getInvoiceListerController().getInvoicesList(BRAZIL_DEBTOR_ID, false);
        assertFalse(response.isAdyenEnabled());

        GetInvoicesListResponse response2 =  getInvoiceListerController().getInvoicesList(NON_BRAZIL_DEBTOR_ID, false);
        assertTrue(response2.isAdyenEnabled());
    }


    private InvoiceListerController getInvoiceListerController() {
        InvoiceListerController sut = new InvoiceListerController(getDebtorService());
        return sut;
    }
    private BillingEngineClient getBillingEngineClient() {
        BillingEngineClient billingEngineClient = mock(BillingEngineClient.class);
        when(billingEngineClient.getInvoices(NON_BRAZIL_DEBTOR_ID)).thenReturn(getNbeInvoices());
        when(billingEngineClient.getInvoices(BRAZIL_DEBTOR_ID)).thenReturn(getNbeBrazilInvoices());
        return billingEngineClient;
    }

    private CoreInvoiceRepository getCoreInvoiceRepository() {
        CoreInvoiceRepository coreInvoiceRepository = mock(CoreInvoiceRepository.class);
        when(coreInvoiceRepository.getInvoicesForHotel(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestFinanceInvoices());
        when(coreInvoiceRepository.getInvoicesForHotel(BRAZIL_DEBTOR_ID)).thenReturn(getTestFinanceBrazilInvoices());
        return coreInvoiceRepository;
    }

    private DebtorFactory getDebtorService() {
        DebtorRepository debtorRepository = mock(DebtorRepository.class);
        when(debtorRepository.getDebtorById(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestDebtor());
        when(debtorRepository.getDebtorById(BRAZIL_DEBTOR_ID)).thenReturn(getBrazilDebtor());
        return new DebtorFactory(debtorRepository, getCoreInvoiceRepository(), getBillingEngineClient(), getCoreInvoiceBrazilRepository());
    }

    private CoreInvoiceBrazilRepository getCoreInvoiceBrazilRepository() {
        CoreInvoiceBrazilRepository coreInvoiceBrazilRepository = mock(CoreInvoiceBrazilRepository.class);
        when(coreInvoiceBrazilRepository.getByInvoiceIds()).thenReturn(getCoreInvoiceBrazilInvoices());
        return coreInvoiceBrazilRepository;
    }

    private List<CoreInvoiceBrazil> getCoreInvoiceBrazilInvoices() {
        CoreInvoiceBrazil brazil1 = CoreInvoiceBrazil.builder()
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

    private List<CoreInvoice> getTestFinanceInvoices() {
        CoreInvoice coreInvoice1 =  CoreInvoice.builder()
                .invoiceId(20230103L)
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(231.03))
                .paidState("paid")
                .type("reservation_statement")
                .build();
        CoreInvoice coreInvoice2 =  CoreInvoice.builder()
                .invoiceId(20230203L)
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(232.03))
                .paidState("unpaid")
                .type("reservation_statement")
                .build();
        return List.of(coreInvoice1, coreInvoice2);
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

    private List<InvoiceDto> getExpectedNonBrazilInvoices() {
        InvoiceDto invoiceData1 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(231.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .displayId("20230103")
                .build();
        InvoiceDto invoiceData2 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(232.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .displayId("20230203")
                .build();
        InvoiceDto invoiceData3 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230303")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(233.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 3, 3))
                .displayId("20230303")
                .build();
        InvoiceDto invoiceData4 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230403")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(234.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 4, 3))
                .displayId("20230403")
                .build();
        return List.of(invoiceData1, invoiceData2, invoiceData3, invoiceData4);
    }

    private List<InvoiceDto> getExpectedBrazilInvoices() {
        InvoiceDto invoiceData1 = InvoiceDto.builder()
                .assetId(BRAZIL_DEBTOR_ID)
                .externalId("20220103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(221.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 1, 3))
                .rpsNumber(9999L)
                .prefeituraUrl("http://www.prefeitura.com/20220103")
                .displayId(String.valueOf(9999L))
                .build();
        InvoiceDto invoiceData2 = InvoiceDto.builder()
                .assetId(BRAZIL_DEBTOR_ID)
                .externalId("20220203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(222.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .rpsNumber(8888L)
                .prefeituraUrl("http://www.prefeitura.com/20220203")
                .displayId(String.valueOf(8888L))
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

    private List<CoreInvoice> getTestFinanceBrazilInvoices() {
        CoreInvoice coreInvoice1 =  CoreInvoice.builder()
                .invoiceId(20220203L)
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .hotelId(BRAZIL_DEBTOR_ID)
                .commission(BigDecimal.valueOf(222.03))
                .paidState("paid")
                .type("reservation_statement")
                .build();
        return List.of(coreInvoice1);
    }
}
