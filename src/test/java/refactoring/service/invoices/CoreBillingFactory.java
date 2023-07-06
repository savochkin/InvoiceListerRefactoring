package refactoring.service.invoices;

import refactoring.FinConstants;
import refactoring.domain.fin.FinanceInvoice;
import refactoring.domain.fin.FinanceInvoiceBrazil;
import refactoring.repository.fin.FinanceInvoiceBrazilRepository;
import refactoring.service.invoices.brazil.BrazilInvoiceService;
import refactoring.service.repo.fin.FinanceInvoiceBrazilService;
import refactoring.service.repo.fin.FinanceInvoiceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoreBillingFactory {
    static List<FinanceInvoice> getBrazilInvoices() {
        FinanceInvoice financeInvoice1 =  FinanceInvoice.builder()
                .invoiceId(20220203L)
                .invoiceDate(LocalDate.of(2022, 2, 3))
                .hotelId(DebtorFactory.getBrazilDebtorId())
                .commission(BigDecimal.valueOf(222.03))
                .paidState("paid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_LTDA)
                .build();
        return List.of(financeInvoice1);
    }

    public static List<FinanceInvoice> getNonBrazilInvoices() {
        FinanceInvoice financeInvoice1 =  FinanceInvoice.builder()
                .invoiceId(20230103L)
                .invoiceDate(LocalDate.of(2023, 1, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(231.03))
                .paidState("paid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        FinanceInvoice financeInvoice2 =  FinanceInvoice.builder()
                .invoiceId(20230203L)
                .invoiceDate(LocalDate.of(2023, 2, 3))
                .hotelId(111L)
                .commission(BigDecimal.valueOf(232.03))
                .paidState("unpaid")
                .type("reservation_statement")
                .company(FinConstants.COMPANY_BOOKING_BV)
                .build();
        return List.of(financeInvoice1, financeInvoice2);
    }

    public static List<FinanceInvoiceBrazil> getBrazilInvoicesExtraInfo() {
        FinanceInvoiceBrazil brazil1 = FinanceInvoiceBrazil.builder()
                .invoiceId(20220203L)
                .rpsNumber(8888L)
                .prefeituraUrl("http://www.prefeitura.com/20220203")
                .build();
        return List.of(brazil1);
    }

    public static FinanceInvoiceService getFinanceInvoiceService() {
        FinanceInvoiceService financeInvoiceService = mock(FinanceInvoiceService.class);
        when(financeInvoiceService.getInvoicesForHotel(DebtorFactory.getNonBrazilDebtorId())).thenReturn(getNonBrazilInvoices());
        when(financeInvoiceService.getInvoicesForHotel(DebtorFactory.getBrazilDebtorId())).thenReturn(getBrazilInvoices());
        return financeInvoiceService;
    }

    public static BrazilInvoiceService getBrazilInvoiceService() {
        FinanceInvoiceBrazilRepository financeInvoiceBrazilRepository = mock(FinanceInvoiceBrazilRepository.class);
        when(financeInvoiceBrazilRepository.getByInvoiceIds()).thenReturn(getBrazilInvoicesExtraInfo());

        FinanceInvoiceBrazilService financeInvoiceBrazilService = new FinanceInvoiceBrazilService(financeInvoiceBrazilRepository);
        BrazilInvoiceService brazilInvoiceService = new BrazilInvoiceService(financeInvoiceBrazilService);
        return brazilInvoiceService;
    }
}
