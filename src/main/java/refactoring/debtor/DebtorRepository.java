package refactoring.debtor;

import refactoring.debtor.Debtor;

public interface DebtorRepository {
    Debtor getDebtorById(Long debtorId);
}
