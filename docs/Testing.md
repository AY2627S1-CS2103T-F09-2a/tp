---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

### Running the JavaFX UI tests

Tests that open real JavaFX windows (in `src/test/java/seedu/address/ui`, such as `DeleteConfirmationDialogTest` and
`PersonListPanelTest`) are marked `@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")`.
**Both methods above skip these tests unless the environment variable `POCO_UI_TESTS` is `true`**, and a skipped test
still lets the run pass. They also need a graphical session, such as a normal desktop login or `xvfb-run` on Linux.

* Mac/Linux: `POCO_UI_TESTS=true ./gradlew clean test`
* Windows (PowerShell): `$env:POCO_UI_TESTS="true"; .\gradlew clean test`
* Windows (Command Prompt): `set "POCO_UI_TESTS=true" && gradlew clean test` (the quotes stop a space before `&&` from becoming part of the value)
* IntelliJ: in the run configuration, add `POCO_UI_TESTS=true` under *Environment variables*.

To confirm that they ran, open `build/reports/tests/test/index.html` and check that the UI test classes show no
skipped tests. The [Developer Guide](DeveloperGuide.html#delete-confirmation) describes what the deletion UI tests cover.

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
