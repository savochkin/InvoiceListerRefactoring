package refactoring.service.invoices;

import refactoring.FinConstants;
import refactoring.billingengine.SimpleInvoiceProjectionDTO;
import refactoring.invoicelister.ui.GetInvoiceListResponse;
import refactoring.invoicelister.ui.InvoiceDto;
import refactoring.invoicelister.ui.ListInvoicesController;
import refactoring.invoicelister.domain.Debtor;
import refactoring.corebilling.CoreInvoice;
import refactoring.corebilling.CoreInvoiceBrazil;
import refactoring.invoicelister.domain.InvoiceData;
import org.junit.jupiter.api.Test;
import refactoring.invoicelister.domain.DebtorRepository;
import refactoring.corebilling.CoreInvoiceBrazilRepository;
import refactoring.billingengine.BillingEngineClient;
import refactoring.invoicelister.domain.DebtorFactory;
import refactoring.corebilling.CoreInvoiceBrazilService;
import refactoring.corebilling.CoreInvoiceService;

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
   + return indicator if invoice is allowed to be paid by Adyen (we allow to be paid by Adyen in all countries but Brazil)
   - for all invoice types commission amount is positive, for credit note - negative
   - for all invoices from non-europe partners return isAdyenPayment = false, otherwise return true
   - partners can fetch invoices only for the assets they currently own
   - B.com staff can fetch invoices for the current owner but also can fetch all invoices for an asset disregarding the ownership
   - invoices may come from core billing and nbe
 */

    @Test
    void getInvoicesFromNonBrazil() {
        GetInvoiceListResponse response = getListInvoicesController().getInvoicesList(NON_BRAZIL_DEBTOR_ID, false);
        assertNotNull(response.getInvoices());
        assertEquals(getTestInvoices().size(), response.getInvoices().size());
        for(int i=0; i<response.getInvoices().size(); i++) {
            assertEquals(getTestInvoices().get(i), response.getInvoices().get(i));
        }
    }

    @Test
    void getInvoicesFromBrazil() {
        GetInvoiceListResponse response = getListInvoicesController().getInvoicesList(BRAZIL_DEBTOR_ID, false);
        assertNotNull(response.getInvoices());
        assertEquals(getTestBrazilInvoices().size(), response.getInvoices().size());
        for(InvoiceDto invoice: getTestBrazilInvoices()) {
            assertTrue(response.getInvoices().stream().anyMatch(i -> i.equals(invoice)));
        }
    }

    @Test
    void adyenAllowedInAllCountriesButBrazil() {
        GetInvoiceListResponse response = getListInvoicesController().getInvoicesList(BRAZIL_DEBTOR_ID, false);
        assertFalse(response.isAdyenAllowed());

        GetInvoiceListResponse response2 = getListInvoicesController().getInvoicesList(NON_BRAZIL_DEBTOR_ID, false);
        assertTrue(response2.isAdyenAllowed());
    }


    private ListInvoicesController getListInvoicesController() {
        return new ListInvoicesController(getDebtorFactory());
    }

    private BillingEngineClient getBillingEngineClient() {
        BillingEngineClient billingEngineClient = mock(BillingEngineClient.class);
        when(billingEngineClient.getInvoices(NON_BRAZIL_DEBTOR_ID)).thenReturn(getNbeInvoices());
        when(billingEngineClient.getInvoices(BRAZIL_DEBTOR_ID)).thenReturn(getNbeBrazilInvoices());
        return billingEngineClient;
    }

    private CoreInvoiceService getCoreInvoiceService() {
        CoreInvoiceService coreInvoiceService = mock(CoreInvoiceService.class);
        when(coreInvoiceService.getInvoicesForHotel(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestCoreInvoices());
        when(coreInvoiceService.getInvoicesForHotel(BRAZIL_DEBTOR_ID)).thenReturn(getTestCoreBrazilInvoices());
        return coreInvoiceService;
    }

    private DebtorFactory getDebtorFactory() {
        DebtorRepository debtorRepository = mock(DebtorRepository.class);
        when(debtorRepository.getDebtorById(NON_BRAZIL_DEBTOR_ID)).thenReturn(getTestDebtor());
        when(debtorRepository.getDebtorById(BRAZIL_DEBTOR_ID)).thenReturn(getBrazilDebtor());
        return new DebtorFactory(debtorRepository, getCoreInvoiceBrazilService(), getCoreInvoiceService(), getBillingEngineClient());
    }

    private CoreInvoiceBrazilService getCoreInvoiceBrazilService() {
        CoreInvoiceBrazilRepository coreInvoiceBrazilRepository = mock(CoreInvoiceBrazilRepository.class);
        when(coreInvoiceBrazilRepository.getByInvoiceIds()).thenReturn(getCoreInvoiceBrazilInvoices());
        return new CoreInvoiceBrazilService(coreInvoiceBrazilRepository);
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

    private List<CoreInvoice> getTestCoreInvoices() {
        CoreInvoice coreInvoice1 =  CoreInvoice.builder()
                .invoiceId(20230103L)
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(231.03))
                .paidState("paid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        CoreInvoice coreInvoice2 =  CoreInvoice.builder()
                .invoiceId(20230203L)
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(232.03))
                .paidState("unpaid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_BV)
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

    private List<InvoiceDto> getTestInvoices() {
        InvoiceDto invoiceData1 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(231.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceDto invoiceData2 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230203")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(232.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceDto invoiceData3 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230303")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(233.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 3, 3))
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        InvoiceDto invoiceData4 = InvoiceDto.builder()
                .assetId(111L)
                .externalId("20230403")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(234.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2023, 4, 3))
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();

        return List.of(invoiceData1, invoiceData2, invoiceData3, invoiceData4);
    }

    private List<InvoiceDto> getTestBrazilInvoices() {
        InvoiceDto invoiceData1 = InvoiceDto.builder()
                .assetId(BRAZIL_DEBTOR_ID)
                .externalId("20220103")
                .invoiceType("invoice")
                .commissionAmount(BigDecimal.valueOf(221.03))
                .invoiceType("reservation_statement")
                .invoiceDate(LocalDate.of(2022, 1, 3))
                .rpsNumber(9999L)
                .prefeituraUrl("http://www.prefeitura.com/20220103")
                .company(FinConstants.COMPANY_BOOKING_LTDA)
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
                .company(FinConstants.COMPANY_BOOKING_LTDA)
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

    private List<CoreInvoice> getTestCoreBrazilInvoices() {
        CoreInvoice coreInvoice1 =  CoreInvoice.builder()
                .invoiceId(20220203L)
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .hotelId(BRAZIL_DEBTOR_ID)
                .commission(BigDecimal.valueOf(222.03))
                .paidState("paid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_LTDA)
                .build();
        return List.of(coreInvoice1);
    }
}
