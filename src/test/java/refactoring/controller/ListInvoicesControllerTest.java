package refactoring.controller;

import refactoring.billing.models.SimpleInvoiceProjectionDTO;
import refactoring.domain.fin.Debtor;
import refactoring.domain.fin.FinanceInvoice;
import refactoring.dto.InvoiceData;
import org.junit.jupiter.api.Test;
import refactoring.repository.fin.DebtorRepository;
import refactoring.service.billingengine.BillingEngineClient;
import refactoring.service.repo.fin.DebtorService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ListInvoicesControllerTest {
/*
   functional requirements:
   - for all invoices return basic information including:
   externalId, invoiceType, commissionAmount, status, invoiceDate
   - for invoices from brazil partner return extra info including:
   rpsNumber, prefeituraUrl, hasBoleto
   - for all invoice types commission amount is positive, for credit note - negative
   - for all invoices from non-europe partners return isAdyenPayment = false, otherwise return true
   - partners can fetch invoices only for the assets they currently own
   - B.com staff can fetch invoics for the current owner but also can fetch all invoices for an asset disregarding the ownership
   - invoices may come from core billing and nbe
 */
    @Test
    void getInvoiceBasicInformation() {
        DebtorRepository debtorRepository = mock(DebtorRepository.class);
        when(debtorRepository.getDebtorById(111L)).thenReturn(getTestDebtor());
        DebtorService debtorService = new DebtorService(debtorRepository);

        FinanceInvoiceService financeInvoiceService = mock(FinanceInvoiceService.class);
        when(financeInvoiceService.getInvoicesForHotel(111L)).thenReturn(getTestFinanceInvoices());

        BillingEngineClient billingEngineClient = mock(BillingEngineClient.class);
        when(billingEngineClient.getInvoices(111L)).thenReturn(getNbeInvoices());

        ListInvoicesController sut = new ListInvoicesController(debtorService, financeInvoiceService, billingEngineClient);
        List<InvoiceData> invoices = sut.getInvoicesList(111L, "ACCOMODATION", false);
        assertNotNull(invoices);
        assertEquals(4, invoices.size());
        assertEquals(getTestInvoices().get(0), invoices.get(0));
        assertEquals(getTestInvoices().get(1), invoices.get(1));
        assertEquals(getTestInvoices().get(2), invoices.get(2));
        assertEquals(getTestInvoices().get(3), invoices.get(3));
        verify(debtorRepository).getDebtorById(111L);
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
}
