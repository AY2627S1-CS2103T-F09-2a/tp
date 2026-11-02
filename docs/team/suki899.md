---
layout: page
title: Maneerat Apicha's Project Portfolio Page
---

### Project: Poco Book

Poco Book is a desktop app that helps independent tutors manage their students, guardian contacts and outstanding
fees. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java.

Given below are my contributions to the project.

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=Suki899&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

* **New Feature**: Adding students with guardian contact and an optional fee (`add`).
  ([#63](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/63), [#79](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/79), [#190](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/190))
  * What it does: records a student with the guardian's name and phone (both required), plus the student's optional
    phone, email, address, tags and outstanding fee. A tutor can save everything they need from one enrolment message.
  * Highlights: replaced AB3's model of a contact with the student–guardian record the rest of the product builds on.
    Added the `GuardianName` and `GuardianPhone` value objects: guardian names are Unicode-normalised, limited to
    70 characters and compared ignoring case, and guardian phones must be 8 digits. A student is identified by name plus guardian phone, so siblings can share a
    guardian while true duplicates are still caught.

* **New Feature**: Editing guardian details and clearing optional fields (`edit`).
  ([#136](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/136), [#203](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/203), [#212](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/212), [#248](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/248))
  * What it does: updates guardian name and phone alongside the student fields, clears an optional phone, email or
    address with an empty prefix, and rejects an edit that changes nothing.
  * Highlights: changing the guardian phone can change a student's identity, so the duplicate check runs on the
    edited record. When a guardian phone changes, the feedback names any siblings still on the old number, so the
    tutor knows which other records to update. Fees are kept out of `edit` and point the user to `setoutstanding`.

* **Enhancements to existing features**:
  * Made validation errors name the failing field and every missing parameter, instead of AB3's generic usage text
    ([#199](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/199), [#210](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/210), [#211](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/211), [#209](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/209)).
  * Accepted names a tutor really meets, such as `Ravi S/O Raj`, and Singapore phones written with `+65`
    ([#200](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/200), [#247](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/247)).
  * Merged tags that differ only in case, and made `find` ignore punctuation at the edges of name words
    ([#208](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/208), [#207](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/207)).
  * Grew the result display to fit multi-line feedback, and refreshed the details pane after case-only edits
    ([#204](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/204), [#246](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/246)).
  * Fixed the v1.3 mismatches between the app and the User Guide found by testing the merged release
    ([#91](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/91)).

* **Project management**:
  * Set up the issue tracker at the start: wrote the user-story issues for the MVP features and the first
    implementation tasks ([#1](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/1)–[#29](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/29)), including the Week 6 MVP specification task ([#9](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/9)).
  * Updated the README and drew the product UI mock-up ([#40](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/40)).
  * Reported bugs found while testing teammates' features ([#192](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/192)–[#198](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/198), [#307](https://github.com/AY2627S1-CS2103T-F09-2a/tp/issues/307)).

* **Documentation**:
  * User Guide:
    * Wrote the `add` and `edit` sections, including the field limits and supported characters
      ([#79](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/79), [#201](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/201), [#202](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/202), [#203](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/203)).
    * Did the v1.6 accuracy pass against the v1.5 JAR ([#309](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/309)).
  * Developer Guide:
    * Wrote the "Editing guardian contact details" implementation section with its sequence diagram, and the
      matching manual tests ([#136](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/136), [#283](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/283)).

* **Community**:
  * Reviewed over 40 teammates' PRs, e.g.
    [#135](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/135), [#139](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/139), [#146](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/146), [#257](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/257), [#278](https://github.com/AY2627S1-CS2103T-F09-2a/tp/pull/278).
