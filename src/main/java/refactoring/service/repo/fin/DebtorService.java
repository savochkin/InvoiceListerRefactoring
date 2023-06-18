package refactoring.service.repo.fin;

import lombok.AllArgsConstructor;
import refactoring.domain.fin.Debtor;
import refactoring.repository.fin.DebtorRepository;

@AllArgsConstructor
public class DebtorService {
    private final DebtorRepository debtorRepository;

    public Debtor getDebtorById(Long debtorId) {
        return debtorRepository.getDebtorById(debtorId);
    }
}
