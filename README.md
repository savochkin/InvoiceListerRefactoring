# InvoiceListerRefactoring
We have two implementation of the same functionality of retrieving invoices. The original one (master) is using anemic domain model and procedural style and another one (see esavochkin/refactoring1 branch) 
is using a rich model (that means that domain object encapsulate not only data but also the behaviour). 

We take the original implementation as a starting point and refactor to the rich model gradually using refactoring techniques (see Refactoring book by M.Fowler etc).

Main problems / bad smells in the original implementation (see master):
1. We are using mutable object for implementation - InvoiceData. This is a "Mutable Data" bad smell
(see https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Mutable%20Data).
We are passing the invoices to a service which sets some fields in it (e.g. see brazilInvoiceService.setInvoiceBrazilFields or invoice.setAdyenAllowed).
This may lead to unexpected and tricky bugs as we may update some fields in one place
and potentially break in some other place as there some other data was expected.
We need to remember that we should get invoices only through the InvoiceListerService.getInvoicesList. But what if
later somebody mutates some data in invoices for some reason? What if we want to guard the invariants/business rules?
Suggestion: Make InvoiceData immutable and use builders for creation as they are more clear.
2. Inconsistent and misleading naming of classes. Why do we have ListInvoicesController but InvoiceListerService? Why the domain object for Core Billing 
is named FinanceInvoice but the general dto is called InvoiceData (which is almost the same)? Same for naming of the services...
3. Package organization which is confusing and also leading to high coupling. Why do we have "repository.fin" package and "service.repo.fin" package? 
On the top level of the package hierarchy we see technical aspects - controller/service/dto/repository. That means that if we have several features than
those packages will contain a classes from different features (e.g. invoiceslister feature and residencystatus feature). 
Even if those feature are independent - the packaging structure will make them dependent.
Suggestion: We should seggregate classes related to different features (e.g. we should have top level packages "invoicelister" and "residencystatus" first, technical packages
may come at lower levels if needed).
4. We use services classes that act as a dumb proxy doing nothing special (see FinanceInvoiceBrazilService, DebtorService in our example). 
Those service do not anything of value just increasing the codebase.
Suggestion: Remove all such services and use repositories directly instead.
5. "Main" operation InvoiceListerService.getInvoicesList is a very long function with a lot of if statements in it. This makes it difficult to understand and
maintain. This method should be heavily refactored. We should use "Extract function"
6. We have special logic for Brazil - for this country several additional properties should be set and returned (for core we need to fetch rps+prefeitura, for nbe we need to caculate company 
and also we need to disable adyen for Brazil). This logic is now spread throughout the getInvoiceLister operation
and we use if statement for that several times. This is a this is "Repeated Switches" bad smell 
(https://learning.oreilly.com/library/view/refactoring-improving-the/9780134757681/ch03.xhtml#:-:text=Repeated%20Switches) as this logic of determining the Brazil 
debtor is present in several places in the codebase. What if we need to change something in this logic? Then we would need to browse through the entire codebase to 
check if we took into account all checks. 
Suggestion: we need to extract the "if debtor is Brazil" check into a function and, since it operates only on attributes of the debtor - we can move it to the debtor class.
7. Special logic for Brazil is an edge case. Having it implemented inside getInvoicesList operation via multiple ifs makes it more difficult to understand the main logic
of retrieving invoices. It is better to move the Brazil logic into subclasses so that it would be easier to grasp the main scenario, but an interested reader could always
dive into subclasses to understand Brazil implementation.
Suggestion: Encapsulate Brazil logic into subclasses and use polymorphism.

So the suggested refactoring includs (see MR):
1. InvoiceData made immutable.
2. Names of the classes made more consistent. Also Core billing related classes renamed from "Finance*" to "Core*" for better understanding.
3. Created package for the feature "invoicelister" with subpackages "api" - for UI interface, "corebilling/billingengine" - for core/nbe related classes, "domain" - for domain objects.
4. Proxy services like FinanceInvoiceBrazilService, DebtorService are removed. Using corresponding repositories instead directly.
5. getInvoicesList is now 4 lines long and moved to the Debtor class
6. If statement are removed by using polymorphism
7. Brazil logic is encapsulated in Brazil related subclasses. The main invoice retrieving logic is now crystal clear.
