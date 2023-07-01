package refactoring.corebilling;

import java.util.List;

public interface CoreInvoiceBrazilRepository {
    List<CoreInvoiceBrazil> getByInvoiceIds();
}
