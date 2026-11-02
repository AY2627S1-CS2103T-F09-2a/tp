---
layout: page
title: Zhao Shizhen's Project Portfolio Page
---

### Project: Poco Book

Poco Book is a desktop app that helps independent tutors manage their students, guardian contacts and outstanding
fees. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=shiverin&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

* **New Feature**: Filtering students by outstanding fee (`fees`).
  ([#56](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/56), [#80](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/80))
  * What it does: lists every student who still owes money, so a tutor can see at a glance whom to remind.
  * Highlights: the filter stays live when fees change, so a student whose fee is cleared leaves the result at once.
    Every argument error names the exact problem instead of showing a generic message.

* **New Feature**: Setting, adding to and clearing an outstanding fee (`setoutstanding`).
  ([#80](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/80), [#232](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/232))
  * What it does: replaces a fee (`setoutstanding 1 150`), clears it (`setoutstanding 1 0`), or adds a new charge to
    the existing fee (`setoutstanding 1 +50`), so tutors do not have to work out running totals themselves.
  * Highlights: amounts are exact money values with at most two decimal places and a S$99999.99 cap. An addition
    that would exceed the cap is rejected with the refused amount and the total it would have produced, and nothing
    changes. Indexes refer to the displayed list, so the command works inside `find` and `fees` results.

* **New Feature**: Totalling outstanding fees (`feesummary`) and recording a full payment (`paid`).
  ([#115](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/115), [#116](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/116), [#133](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/133))
  * `feesummary` always counts every stored student, even while the list is filtered, and says so in its feedback.
  * `paid` clears a student's whole fee and reports how much was cleared.

* **New Feature**: Command-specific help (`help COMMAND`).
  ([#134](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/134))
  * Shows a single command's format and example in the result area without opening a window. Unknown command words
    list the valid ones.

* **Enhancements to existing features**:
  * Recovered from a corrupted `guiSettings` entry in `preferences.json` instead of crashing at startup
    ([#231](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/231)).
  * Accepted capitalisation-only edits of guardian names and tags
    ([#288](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/288)).
  * Fixed result-display sizing for wrapped rows and student-card layout
    ([#289](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/289), [#290](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/290)).
  * Made runtime files (data, preferences, logs) stay beside the JAR, wherever it is launched from, and enabled
    assertions for application runs ([#90](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/90), [#112](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/112)).
  * Aligned index parsing across commands with the MVP specification
    ([#78](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/78)).

* **Project management**:
  * Managed releases `v1.3`, `v1.4` and `v1.5` on GitHub, including packaging and smoke tests
    ([#95](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/95), [#146](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/146), [#291](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/291)).
  * Coordinated the milestones and wrote the weekly task issues, including the v1.6 task board
    ([#214](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/214)).
  * Reported over 30 `alpha-bug` issues during internal alpha testing, and further bugs during beta testing.

* **Documentation**:
  * User Guide:
    * Wrote the sections for `fees`, `setoutstanding`, `feesummary`, `paid` and `help COMMAND`.
    * Added the five-minute first-use walkthrough ([#284](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/284)).
  * Developer Guide:
    * Wrote the non-functional requirements and glossary ([#30](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/30), [#236](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/236)).
    * Wrote the acknowledgements ([#234](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/234)) and the
      undo/redo and archiving design proposals ([#235](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/235)).
    * Drew the `paid` sequence diagram ([#230](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/230)) and
      corrected the fee summary description ([#233](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/233)).
    * Wrote the fee-related implementation sections, manual tests and the planned enhancements appendix.

* **Community**:
  * Reviewed over 40 teammates' PRs, e.g.
    [#135](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/135), [#139](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/139),
    [#200](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/200), [#239](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/239),
    [#253](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/253), [#262](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/262).

* **Tools**:
  * Ran the JavaFX UI tests in CI ([#238](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/238)).
  * Set up automated PR review workflows ([#105](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/105), [#106](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/106)).
