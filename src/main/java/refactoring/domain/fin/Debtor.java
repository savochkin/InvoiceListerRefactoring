package refactoring.domain.fin;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import refactoring.FinConstants;

@Data
@Builder
public class Debtor {
    final private Long debtorId;
    final private String contractedBy;

    public boolean isContractedByBrazil() {
        return FinConstants.CONTRACTED_BY_BRAZIL.equals(getContractedBy());
    }
}
