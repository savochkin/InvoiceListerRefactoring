package refactoring.domain.fin;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class Debtor {
    final private Long debtorId;
    final private String contractedBy;
}
