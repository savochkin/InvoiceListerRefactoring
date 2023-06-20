package refactoring.service.repo.fin;

import lombok.AllArgsConstructor;
import refactoring.domain.fin.FinanceInvoiceBrazil;
import refactoring.repository.fin.FinanceInvoiceBrazilRepository;

import java.util.List;

/*
* Real class is here: src/main/java/com/booking/faas/fintooling/service/repo/fin/FinanceInvoiceBrazilService.java
* TODO: remove all such services and use repositories directly instead
* not sure what value they are adding...but they increase the code base and its complexity
* if we are following the DDD - suggestion is to use Entities, VO, Repositories as first class citizens
* services should be used to implement some important domain logic only if the logic does not fit
* into any entity/VO/Repository
*/
@AllArgsConstructor
public class FinanceInvoiceBrazilService {
    FinanceInvoiceBrazilRepository financeInvoiceBrazilRepository;
    public List<FinanceInvoiceBrazil> getByInvoiceIds() {
        return financeInvoiceBrazilRepository.getByInvoiceIds();
    }
}
