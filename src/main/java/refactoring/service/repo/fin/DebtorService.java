package refactoring.service.repo.fin;

import lombok.AllArgsConstructor;
import refactoring.domain.fin.Debtor;
import refactoring.repository.fin.DebtorRepository;

/*
 * Real class is here: https://sourcegraph.booking.com/gitlab.booking.com/faas/finance-invoices/fintooling-services/-/blob/src/main/java/com/booking/faas/fintooling/service/repo/fin/DebtorService.java?L15:14&popover=pinned
 * TODO: remove all such services and use repositories directly instead
 * not sure what value they are adding...but they increase the code base and its complexity
 * if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
 * services should be used to implement some important domain logic only if the logic does not fit
 * into any entity/VO/Repository
 *
 * TODO: reorg package structure, services should go into the package by domain
 * what a strange name of the package!
 * all debtor related domain classes should go to "domain.debtor" package
 */
@AllArgsConstructor
public class DebtorService {
    private final DebtorRepository debtorRepository;

    public Debtor getDebtorById(Long debtorId) {
        return debtorRepository.getDebtorById(debtorId);
    }
}
