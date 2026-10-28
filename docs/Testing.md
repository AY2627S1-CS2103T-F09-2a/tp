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

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

### Running the JavaFX UI tests

Both methods above skip the tests that need the JavaFX toolkit, such as `DeleteConfirmationDialogTest` and `PersonListPanelTest`, unless the environment variable `POCO_UI_TESTS` is exactly `true`. A skipped test does not fail, so a run without the variable can pass even though none of these tests ran. In the Gradle test report (`build/reports/tests/test/index.html`), they are counted as skipped.

These tests are the test classes annotated with `@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")`. To list them, search `src/test` for `POCO_UI_TESTS`. Annotate any new test that needs the JavaFX toolkit in the same way.

To run them:

1. Use a desktop session, because JavaFX needs a display and some of these tests open real windows. With the variable set but no display, such as over SSH on Linux, these tests fail, and the run may stop responding. If it does, stop it with Ctrl+C.
1. Set the variable and run the tests:
   * macOS or Linux: `POCO_UI_TESTS=true ./gradlew clean test`
   * Windows PowerShell: `$env:POCO_UI_TESTS = "true"`, then `.\gradlew.bat clean test`
   * Windows Command Prompt: `set "POCO_UI_TESTS=true"`, then `gradlew clean test`. Do not write `set POCO_UI_TESTS=true && gradlew clean test`: it stores `true ` with a trailing space, so the tests stay skipped.
   * IntelliJ: choose `Run` > `Edit Configurations...`, select the test configuration, and add `POCO_UI_TESTS=true` under `Environment variables`. To do this for every new test configuration, edit the template of that configuration type under `Edit configuration templates...`: JUnit, or Gradle if IntelliJ runs tests with Gradle (set in `Settings` > `Build, Execution, Deployment` > `Build Tools` > `Gradle` > `Run tests using`).
1. Leave any windows that open and close alone until the run ends.

To run one test class, add `--tests`, for example `POCO_UI_TESTS=true ./gradlew clean test --tests seedu.address.ui.PersonListPanelTest`.

Keep `clean` in the Gradle commands. It makes Gradle run every test again, even if the tests passed in an earlier run without the variable. In PowerShell and Command Prompt, the variable stays set until you close that window.

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
