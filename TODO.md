## Approach Taken

Because this exercise is time-boxed (about 1-2 hours), I focused on finishing the core business path end-to-end and making it reliable enough to reason about.

What I completed:

- added support for both settlement transaction types (`Purchase Cash Settlement`, `Sale Cash Settlement`)
- mapped settlement rows into the `TradeSettlement` model
- implemented settlement reconciliation HTTP call
- refactored reconciliation HTTP logic into one shared flow (so contribution and settlement follow the same behavior)
- improved processing flow so parse issues are surfaced clearly
- updated end-to-end test coverage to verify the expected 2 contributions + 3 settlements from the sample file

## Key Considerations

- **Time vs. completeness:** I prioritized working settlement support and safer flow over full spec hardening.
- **Correctness of mapping:** Main risk was incorrect field mapping, so I focused on explicit mapping and E2E verification.
- **Consistency:** Centralizing HTTP behavior reduces bugs caused by duplicated logic.

## Tradeoffs Made

- I did **not** implement full field-spec validation (all size constraints, decimal format rules, and strict per-field checks).
- Current parsing is good enough for known-good files, but not yet a full compliance validator for every malformed input case.
- Processing is fail-fast today: one bad line stops the run.

## Further Improvements

- Add full field-level validation based on the file spec:
  - max lengths for text fields
  - precision/scale checks for `QUANTITY`, `RATE`, `AMOUNT`
  - stricter transaction-type rules
- Add targeted unit tests for invalid input paths.
- Improve reconciliation resiliency with retry/backoff and better operational visibility (metrics/logging).
- Decide whether batch behavior should remain fail-fast or support dead-letter + continue.
- Migrate from `Date`/`SimpleDateFormat` to `LocalDate`/`DateTimeFormatter` for safer date handling.

## Open Questions

- Should full field validation be part of this task, or follow in a second pass?
- Is fail-fast the desired production behavior, or should valid rows continue while bad rows are isolated?
- Is `JOB` guaranteed numeric in all upstream data, or should it be treated as a string field?
