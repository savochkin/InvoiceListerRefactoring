package refactoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InvoiceData {
    final private Long assetId;
    final private Long externalId;
    final private String invoiceType;
    final private BigDecimal commissionAmount;
    final private LocalDate invoiceDate;
    final private Long rpsNumber;
    final private BigDecimal brlLiquido;
    final private boolean hasBoleto;
}
