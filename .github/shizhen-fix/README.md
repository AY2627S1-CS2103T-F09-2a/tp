# shizhen-fix: limited rollout

This workflow uses SoCLaaS `x-test-1` with the existing `LLM_API_KEY`. **Only PRs authored by shiverin (GitHub user ID `200508238`) are eligible**, including manual runs. The numeric author gate is checked before a model call, while preparing tests, and again before publishing. Other authors' PRs still receive reviews but never get a fix proposal from this workflow. The allowlisted identity and context limits are in `config.json`, loaded only from trusted automation code.

## Flow

1. `shizhen-review` snapshots the eligible original PR head, performs its review, and uploads structured findings only if that head is unchanged. The existing `ai-review.yml` is untouched.
1. A successful `shizhen-review` run triggers `shizhen-fix`. The fixer verifies the source workflow, repository, artifact, PR author, open/non-draft state, and reviewed commit.
1. The fixer reserves `shizhen-fix/pr-N-SHA` once per original commit before calling SoCLaaS. The model examines complete changed Java/guide files and decides which findings are substantiated. Unsupported or incorrect findings are skipped. No supported fixes means no new PR.
1. An independent job reconstructs the immutable source snapshot and applies validated exact replacements. Gradle `check coverage` runs in an unprivileged disposable container without credentials, runner files, the Docker socket, or shared caches. Internet access permits dependency downloads.
1. A third job reads the original proposal artifact, not the test container's modified files. It revalidates the author, current original PR head, file scope, modes, and original hashes, then creates a separate commit and **draft** PR. Test failures are reported in its description; nothing is automatically approved or merged.

For a same-repository source branch, the fix PR targets the original branch. Accepting it updates the original PR; rejecting it leaves that PR unchanged. For a fork branch, the repository's Actions token cannot write to that fork: the bot instead opens a **replacement draft PR against the original base branch**, containing the original changes plus fixes. Choose either the replacement or the original; do not merge both. The fork branch and original PR are never automatically changed or closed.

Generated branches are excluded from automatic fixes. PRs containing an accepted `Apply shizhen-fix` commit are also excluded to stop fix/review loops. Human merges or pushes can trigger the usual review/CI workflows again. Fix PRs created with `GITHUB_TOKEN` may require approval for further PR-triggered workflows; this workflow's own isolated checks run before publication regardless.

## Deliberate limits

* Supported edits: regular, non-executable `.java` files under `src/main/java` and `src/test/java`, plus `docs/UserGuide.md` and `docs/DeveloperGuide.md`, already changed by the original PR. No workflow, build, dependency, permission, credential, or test-infrastructure edits; no new files, file deletions, or symlinks.
* Input: at most 16 complete supported files, 60 KB per file, 40 KB total source context, and 65 KB including instructions/findings. Larger inputs stop for human handling rather than silently truncating files. Cross-file problems requiring unavailable context are skipped.
* Output: one SoCLaaS request with high reasoning effort, up to 8 edited files and 24 exact replacements. There is no automatic retry or model fallback. Each finding needs a fix/skip explanation, and edits must match unique original text.
* A reserved attempt branch remains even after an error or a no-fix result. Rerunning cannot silently spend more tokens or overwrite a previous result. A maintainer can explicitly delete that generated branch to permit a fresh attempt; this workflow never deletes branches automatically.
* Successful tests are evidence, not proof that the patch is correct. The original and proposed code still require human review.

## Activation

Merge the `shizhen-review` prerequisite (PR #105), then this dependent PR. Both workflow files must exist on the default branch. No Copilot subscription, new API key, or access to teammates' fork branches is needed.

GitHub Actions must be allowed to create pull requests in **Settings → Actions → General → Workflow permissions → Allow GitHub Actions to create and approve pull requests**. That setting was disabled during implementation and has not been changed. Although GitHub combines those capabilities into one setting, this workflow never sends approval or merge requests. If creation is disabled or blocked by organization policy, the publisher fails and the proposal/test artifacts remain available for inspection.

To request a run against your PR after it has a successful review artifact:

```bash
gh workflow run shizhen-fix.yml --repo AY2627S1-CS2103T-F09-2a/tp -f pr_number=PR_NUMBER
```

Local verification:

```bash
python3 -m unittest discover -s .github/shizhen-fix -p 'test_*.py' -v
```

Local tests verify the author restriction, immutable review handoff, bounded exact edits, symlink/config rejection, stale-head protection, idempotency, same-repository/fork targets, and draft publication after failed checks. Live SoCLaaS calls, container execution on GitHub, and automatic draft creation remain unverified until an eligible run after activation.

## References

* [CS2103T PR review guidance](https://nus-cs2103-ay2627-s1.github.io/website/admin/appendixE-gitHub.html#tp-schedule-tracking-prs-reviewing)
* [SE-EDU review practices](https://se-education.org/guides/guidelines/PRs-reviewing.html)
* [GitHub: secure use of Actions](https://docs.github.com/en/actions/reference/security/secure-use)
* [GitHub: events that trigger workflows](https://docs.github.com/en/actions/reference/workflows-and-actions/events-that-trigger-workflows)
