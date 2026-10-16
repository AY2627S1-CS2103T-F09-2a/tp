# shizhen-fix: limited rollout

This independent fixer runs after a successful `shizhen-review` handoff. It uses the existing encrypted `LLM_API_KEY` with SoCLaaS `x-test-1`. Only PRs authored by GitHub user ID `200508238` (`shiverin`) are eligible; the same restriction applies to manual runs, candidate preparation, and publication. Other authors still receive reviews but no automatic fixes.

The original PR is never automatically edited, closed, approved, or merged. A separate draft proposes bounded exact replacements in existing changed Java files and the User/Developer Guides. It skips unsupported or unsubstantiated repairs, does not create new files, and does not edit workflows or build settings. Tests run inside a disposable container without repository credentials or API keys.

For same-repository PRs, the fix PR targets the original branch. For fork PRs, the repository token cannot edit the fork, so a replacement draft targets the original base branch and contains the original changes plus repairs. A human chooses which alternative to continue. One attempt is reserved per original commit, and accepted automatic fix commits stop further automatic repairs on that PR.

The proposal, test, and publication jobs are separate. A failed test job is reported in the draft rather than presented as a clean result. Live gateway repair and publication have not been verified end to end. The current repository setting disables Actions-created PRs; enabling that setting and merging the setup workflows are required before activation. No repository settings or secrets are changed by this PR. The original `ai-review.yml` is untouched.

Run the local boundary tests:

```bash
python3 -m unittest discover -s .github/shizhen-fix -p 'test_fix.py' -v
```

The reusable package, installer, diagrams, architecture, and setup instructions are published at [shiverin/shizhen-review-fix](https://github.com/shiverin/shizhen-review-fix).
