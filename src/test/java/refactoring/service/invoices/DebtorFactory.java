package refactoring.service.invoices;

import refactoring.domain.fin.Debtor;
import refactoring.repository.fin.DebtorRepository;
import refactoring.service.repo.fin.DebtorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DebtorFactory {
    final private static long NON_BRAZIL_DEBTOR_ID = 111L;
    final private static long BRAZIL_DEBTOR_ID = 222L;
    public static long getBrazilDebtorId() {
        return BRAZIL_DEBTOR_ID;
    }
    public static long getNonBrazilDebtorId() {
        return NON_BRAZIL_DEBTOR_ID;
    }

    public static Debtor getNonBrazilDebtor() {
        return Debtor.builder()
                .debtorId(111L)
                .contractedBy("NON-BRAZIL")
                .build();
    }

    public static Debtor getBrazilDebtor() {
        return Debtor.builder()
                .debtorId(getBrazilDebtorId())
                .contractedBy("CONTRACTED_BY_BRAZIL")
                .build();
    }

    public static DebtorService getDebtorService() {
        DebtorRepository debtorRepository = mock(DebtorRepository.class);
        when(debtorRepository.getDebtorById(getNonBrazilDebtorId())).thenReturn(getNonBrazilDebtor());
        when(debtorRepository.getDebtorById(getBrazilDebtorId())).thenReturn(getBrazilDebtor());
        return new DebtorService(debtorRepository);
    }
}
