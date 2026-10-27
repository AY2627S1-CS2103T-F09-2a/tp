#!/usr/bin/env python3
"""Check that the JavaFX UI tests really ran instead of being skipped.

The UI tests in ``src/test/java`` are annotated with
``@EnabledIfEnvironmentVariable(named = "POCO_UI_TESTS", matches = "true")``, so a
misconfigured job would pass while every one of them was skipped. This script reads
the counts from the JUnit XML reports and fails when any of those classes produced no
executed test, or when a test in them was skipped or failed.

Usage: python3 .github/verify-ui-test-results.py [results-directory]
"""

import glob
import os
import re
import sys

DEFAULT_RESULTS = os.path.join("build", "test-results", "test")
GATED_ANNOTATION = re.compile(
    r'EnabledIfEnvironmentVariable\s*\(\s*named\s*=\s*"POCO_UI_TESTS"\s*(?:,\s*matches\s*=\s*"true"\s*)?\)')
WHITESPACE = re.compile(r"\s+")
PACKAGE_PATTERN = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
# Only the opening <testsuite ...> tag is read: its attributes hold every count needed,
# so the report is never parsed as a whole document.
TESTS_START = re.compile(r"<testsuite\b([^>]*)>")
ATTRIBUTES = re.compile(r'([\w:.-]+)="([^"]*)"')
REPORTED_COUNTS = ("tests", "skipped", "failures", "errors")


def read_text(path):
    """Return the text of a file, or an empty string when it cannot be read."""
    try:
        with open(path, encoding="utf-8", errors="replace") as source:
            return source.read()
    except OSError as error:
        print(f"WARNING: cannot read {path}: {error}")
        return ""


def gated_classes(source_root):
    """Return {fully qualified class name: source path} for POCO_UI_TESTS-gated tests.

    The annotation is matched with its whitespace collapsed, so that a wrap across lines or
    a missing space around ``=`` still counts as a gate rather than silently dropping the
    class from the check.
    """
    classes = {}
    for path in glob.glob(os.path.join(source_root, "**", "*.java"), recursive=True):
        text = read_text(path)
        if not GATED_ANNOTATION.search(WHITESPACE.sub(" ", text)):
            continue
        package = PACKAGE_PATTERN.search(text)
        name = os.path.splitext(os.path.basename(path))[0]
        classes[f"{package.group(1)}.{name}" if package else name] = path
    return classes


def count(value):
    """Return a reported count, treating anything unreadable as zero."""
    try:
        return int(value)
    except (TypeError, ValueError):
        return 0


def read_report(path):
    """Return (class name, tests, skipped, failures) from one JUnit XML report."""
    text = read_text(path)
    opening = TESTS_START.search(text)
    if not opening:
        print(f"WARNING: skipping {path}: no <testsuite> start tag")
        return None
    attributes = dict(ATTRIBUTES.findall(opening.group(1)))
    if not all(name in attributes for name in REPORTED_COUNTS):
        print(f"WARNING: skipping {path}: the counts are incomplete")
        return None
    return (
        attributes.get("name", ""),
        count(attributes["tests"]),
        count(attributes["skipped"]),
        count(attributes["failures"]) + count(attributes["errors"]),
    )


def main():
    results_dir = sys.argv[1] if len(sys.argv) > 1 else DEFAULT_RESULTS
    gated = gated_classes(os.path.join("src", "test", "java"))
    if not gated:
        print("No POCO_UI_TESTS-gated test classes were found; refusing to report success.")
        return 1

    reports = {}
    for path in sorted(glob.glob(os.path.join(results_dir, "TEST-*.xml"))):
        report = read_report(path)
        if report is not None:
            reports[report[0]] = report[1:]
    if not reports:
        print(f"No usable JUnit XML reports in {results_dir}; the test task did not run.")
        return 1

    executed = 0
    problems = []
    for name in sorted(gated):
        tests, skipped, failures = reports.get(name, (0, 0, 0))
        if tests == 0:
            problems.append(f"{name}: no executed tests (missing from the reports or all skipped)")
            continue
        if skipped:
            problems.append(f"{name}: {skipped} of {tests} tests skipped")
        if failures:
            problems.append(f"{name}: {failures} of {tests} tests failed")
        executed += tests - skipped
        print(f"{name}: {tests - skipped} passed of {tests}")

    print(f"\nJavaFX UI tests executed: {executed} across {len(gated)} gated classes")
    if executed == 0:
        problems.append("no JavaFX UI test executed, so POCO_UI_TESTS was not in effect")

    for problem in problems:
        print(f"FAILED: {problem}")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
