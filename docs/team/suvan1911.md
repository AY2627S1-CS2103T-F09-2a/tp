---
layout: page
title: Suvan Handa's Project Portfolio Page
---

### Project: Poco Book

Poco Book is a desktop app that helps independent tutors manage their students, guardian contacts and outstanding
fees. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java.

Given below are my contributions to the project.

* **Code contributed**: [tP Code Dashboard](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=suvan1911&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

* **New feature: Outstanding-fee data model and persistence**
  ([#62](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/62),
  [#81](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/81)).
  * What it does: stores an optional outstanding fee alongside each student's required guardian contact details, and
    restores them when the tutor reopens Poco Book.
  * Highlights: implemented an immutable `OutstandingFee` value object using `BigDecimal`, enforcing the S$0.01 to
    S$99999.99 range and displaying amounts to two decimal places without floating-point rounding. Extended the
    student model and JSON storage while preserving the new fields through edits. Added model, storage and round-trip
    tests for missing or invalid data, boundary amounts and normalization.

* **New feature: Minimum outstanding-fee filtering**
  ([#137](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/137)).
  * What it does: `fees min/AMOUNT` shows students owing at least the specified amount; plain `fees` continues to
    show all students with outstanding fees.
  * Highlights: validates the threshold and rejects extra or duplicate arguments before changing the displayed list.
    Compares amounts inclusively with `BigDecimal`, preserves stored order, and uses the existing live filtered list so
    fee changes update the results. The query does not write to storage. Added parser, command and integration tests
    for boundaries, invalid inputs, list restoration and changing fees while filtered.

* **Enhancements to existing features**:
  * Recovered a window whose saved size is invalid or whose saved position is off-screen, while retaining reachable
    positions on any connected screen
    ([#259](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/259)).
  * Added a JavaFX regression test that scrolls a narrow details pane containing maximum-length fields to check that
    the final tags field is visible
    ([#260](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/260)).
  * Made the deletion confirmation fit the supported small-screen size
    ([#253](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/253)).

* **Documentation**:
  * Developer Guide: documented the `fees min/AMOUNT` parsing, inclusive filtering and read-only execution with a
    sequence diagram and its PlantUML source
    ([#145](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/145)). Clarified diagram call sequences and
    manual-test instructions, and separated all-student and filtered-list association labels in the Model diagram
    ([#251](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/251),
    [#261](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/261)).
  * User Guide: aligned the introduction with Poco Book, clarified setup, backup and command-input instructions, and
    documented the supported tag characters and single-instance constraint
    ([#49](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/49),
    [#251](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/251),
    [#255](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/255),
    [#256](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/256),
    [#257](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/257)).

* **Team-based tasks**:
  * Rebranded the project website for Poco Book and updated its navigation, repository and release links
    ([#49](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/49)); replaced the inherited AB3 help screenshot
    ([#258](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/258)).
  * Reworked the team's automated PR-review workflow to batch large diffs, report failures and support manual runs;
    tested the configuration against a representative large PR
    ([#94](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/94)).
