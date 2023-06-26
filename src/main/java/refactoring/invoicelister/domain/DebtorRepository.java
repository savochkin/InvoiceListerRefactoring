package refactoring.invoicelister.domain;

public interface DebtorRepository {
    Debtor getDebtorById(Long debtorId);
}
