# v1.4 fee features: integration handoff

This fragment documents shiverin's issues #109 and #110 for the shared-guide owner to incorporate through #102. It does not claim that the complete v1.4 release is published.

## All-student total: `feesummary`

Run `feesummary` without arguments. It reports the number of students with a fee and their total across **all stored students**, even when the displayed list is filtered. It preserves the filter, selection and saved data. Extra arguments are rejected.

Example: two students owing S$180.00 and S$75.50 produce `2 student(s) owe a total of S$255.50.` No fees produces `0 student(s) owe a total of S$0.00.` The total can exceed the per-student maximum.

## Record full payment: `paid INDEX`

Use a positive index from the currently displayed list. For example, run `fees`, check the visible student, then run `paid 1`. The full outstanding amount is removed and feedback is `Marked outstanding fee as paid for NAME: S$AMOUNT`.

The command preserves other fields and the active filter. A student disappears from a `fees` result after payment; remaining indexes update. In a name search, the student remains visible if they still match. The change survives restart if the normal save succeeds. Save errors are reported and mean persistence is not confirmed.

A student with no fee is rejected with `This student has no outstanding fee to mark as paid: NAME`. Missing, signed, zero, leading-zero, malformed and out-of-range indexes are rejected. Rejected commands do not change data or the list. `setoutstanding INDEX 0` remains available and allows clearing an already-empty fee.

## Implementation and diagram

`FeeSummaryCommand` sums immutable `OutstandingFee.value` amounts using `BigDecimal`, reading the complete address book rather than the filtered list. Query commands opt out of saving through `shouldSaveAddressBook()`.

`PaidCommand` validates the displayed index and requires a fee before creating an immutable replacement student with only the fee removed. The existing model updates the active filter. `LogicManager` saves successful mutations; an I/O failure is reported while the in-memory change may remain.

![Full-payment command sequence](../images/PaidSequenceDiagram.png)

## Manual acceptance checks

1. In an empty test book, add `add n/Alpha Student g/Mei Tan gp/91111111 f/180` and `add n/Beta Student g/Anil Kumar gp/92222222 f/75.50`.
1. Run `find Beta`, then `feesummary`. Expect `2 student(s) owe a total of S$255.50.` Beta remains the only displayed student.
1. Run `paid 1`. Expect S$75.50 removed from Beta; Beta remains visible. Alpha remains unchanged.
1. Run `fees`; expect only Alpha at index 1. Run `paid 1`; expect an empty fees list.
1. Run `feesummary`; expect `0 student(s) owe a total of S$0.00.`
1. Run `list`, then `paid 2` again; expect a no-fee error. Try `paid 0`, `paid +1`, `paid 01`, `paid 1 2`, and `feesummary extra`; expect errors without mutation.
1. Restart and confirm both fees remain absent and all guardian/contact/tag data remain intact.

The per-student fee range remains S$0.01–S$99999.99. The earlier provisional assigned-feature draft proposed a different limit; the shipped v1.3 model remains authoritative for this increment. No schema migration is required.
