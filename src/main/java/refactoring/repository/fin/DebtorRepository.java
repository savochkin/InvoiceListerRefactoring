package refactoring.repository.fin;

import refactoring.domain.fin.Debtor;

public interface DebtorRepository {
    Debtor getDebtorById(Long debtorId);
}
