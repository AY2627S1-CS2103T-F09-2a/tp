"""Regression checks for author restrictions and safe fix publication."""

import base64
import copy
import io
import json
import os
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch
import zipfile

import fix


SHA = 'a' * 40
OTHER_SHA = 'b' * 40
PATH = 'src/main/java/example/Test.java'
ORIGINAL = 'class Test { int amount = -1; }\n'
EDIT = {'path': PATH, 'before': 'amount = -1', 'after': 'amount = 0'}


def pull_request():
    return {'number': 7, 'user': {'id': fix.AUTHOR_ID, 'login': 'shiverin'},
            'state': 'open', 'draft': False,
            'head': {'sha': SHA, 'ref': 'my-feature', 'repo': {'full_name': 'owner/repo'}},
            'base': {'ref': 'master'}, 'html_url': 'https://github.com/owner/repo/pull/7'}


class FakeGitHub:
    repository = 'owner/repo'
    token = 'dummy'
    prefix = 'https://api.github.com/repos/owner/repo/'

    def __init__(self):
        self.pr = pull_request()
        self.calls = []
        self.mode = '100644'
        self.reserved = False

    def pages(self, path):
        if path.endswith('/files'):
            return [{'filename': PATH, 'status': 'modified'}]
        if path.endswith('/commits'):
            return [{'commit': {'message': 'Add feature'}}]
        raise AssertionError(path)

    def api(self, path, data=None, method=None):
        self.calls.append((path, data, method))
        if path == 'pulls/7':
            return copy.deepcopy(self.pr)
        if path == 'git/commits/' + SHA:
            return {'tree': {'sha': OTHER_SHA}}
        if path == f'git/trees/{OTHER_SHA}?recursive=1':
            return {'tree': [{'path': PATH, 'mode': self.mode, 'type': 'blob'}]}
        if path.startswith('contents/'):
            return {'type': 'file', 'size': len(ORIGINAL),
                    'content': base64.b64encode(ORIGINAL.encode()).decode()}
        if path == 'git/refs':
            if self.reserved:
                raise fix.Stop('API request failed with HTTP 422.')
            self.reserved = True
            return {}
        if path.startswith('git/ref/heads/'):
            return {'object': {'sha': SHA}}
        if path in ('git/blobs', 'git/trees', 'git/commits'):
            return {'sha': OTHER_SHA}
        if path.startswith('git/refs/heads/'):
            return {}
        if path == 'pulls' and data:
            return {'html_url': 'https://github.com/owner/repo/pull/8'}
        raise AssertionError(path)


class FixTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.previous = Path.cwd()
        os.chdir(self.tmp.name)
        self.addCleanup(os.chdir, self.previous)
        self.env = patch.dict(os.environ, {
            'GITHUB_OUTPUT': str(Path('outputs').resolve()),
            'GITHUB_STEP_SUMMARY': str(Path('summary').resolve()),
            'GITHUB_EVENT_PATH': str(Path('event.json').resolve()),
            'GITHUB_EVENT_NAME': 'workflow_run', 'GITHUB_RUN_ID': '123',
            'VALIDATION_RESULT': 'failure', 'LLM_API_KEY': 'dummy', 'PR_NUMBER': '7',
        })
        self.env.start()
        self.addCleanup(self.env.stop)
        fix.save('event.json', {'workflow_run': {'id': 99}})
        self.gh = FakeGitHub()

    def proposal(self):
        return {'repository': 'owner/repo', 'pr_number': 7, 'head_sha': SHA,
                'branch': f'shizhen-fix/pr-7-{SHA[:12]}',
                'edits': [EDIT], 'original_hashes': {PATH: fix.digest(ORIGINAL)},
                'decisions': [{'finding': 1, 'decision': 'fix', 'reason': 'Reject negative initial value.'}]}

    def handoff(self):
        return {'repository': 'owner/repo', 'pr_number': 7, 'head_sha': SHA,
                'run_id': 99, 'run_attempt': 1,
                'review': {'review': {'key_issues_to_review': [{'issue_header': 'Bug'}]}}}

    def test_only_verified_numeric_author_id_is_allowed(self):
        for changed in [{'user': {'id': 9, 'login': 'shiverin'}},
                        {'state': 'closed'}, {'draft': True},
                        {'head': {'ref': 'shizhen-fix/pr-7', 'repo': {}, 'sha': SHA}}]:
            pr = pull_request()
            pr.update(changed)
            with self.subTest(changed=changed), self.assertRaises(fix.Stop):
                fix.eligible(pr)
        fix.eligible(pull_request())

    def test_reject_traversal_configs_and_ambiguous_edits(self):
        for path in ['../x.java', '/src/main/java/X.java', 'src/main/java/../X.java',
                     'src/main/java//X.java', '.github/workflows/a.yml', 'build.gradle',
                     'src/main/java/evil\\X.java']:
            with self.subTest(path=path):
                self.assertFalse(fix.allowed_path(path))
        self.assertEqual(fix.apply_edits([EDIT], {PATH: ORIGINAL}, {PATH})[PATH],
                         ORIGINAL.replace('amount = -1', 'amount = 0'))
        for edits in [[{**EDIT, 'before': ''}], [{**EDIT, 'before': 'not present'}],
                      [{**EDIT, 'path': 'build.gradle'}], [EDIT, EDIT]]:
            with self.subTest(edits=edits), self.assertRaises(fix.Stop):
                fix.apply_edits(edits, {PATH: ORIGINAL}, {PATH})

    def test_symlinks_and_executable_files_are_not_followed(self):
        for mode in ['120000', '100755']:
            gh = FakeGitHub()
            gh.mode = mode
            with self.assertRaises(fix.Stop):
                fix.file_at(gh, SHA, PATH)
            self.assertFalse(any(call[0].startswith('contents/') for call in gh.calls))

    def test_wrong_author_and_stale_reviews_never_call_model_or_reserve_branch(self):
        for changes in [{'user': {'id': 4}}, {'head': {**self.gh.pr['head'], 'sha': OTHER_SHA}}]:
            self.gh = FakeGitHub()
            self.gh.pr.update(changes)
            with patch.object(fix, 'handoff_from_run', return_value=self.handoff()), \
                    patch.object(fix, 'request_json') as model, self.assertRaises(fix.Stop):
                fix.generate(self.gh)
            model.assert_not_called()
            self.assertFalse(self.gh.reserved)

    def test_manual_runs_and_reviewer_handoffs_also_enforce_author_gate(self):
        self.gh.pr['user']['id'] = 4
        fix.save('event.json', {'inputs': {'pr_number': 7}})
        with patch.dict(os.environ, {'GITHUB_EVENT_NAME': 'workflow_dispatch'}), \
                patch.object(fix, 'manual_handoff') as lookup, self.assertRaises(fix.Stop):
            fix.generate(self.gh)
        lookup.assert_not_called()
        fix.handoff(self.gh)
        self.assertFalse(Path('review-head.json').exists())

    def test_no_findings_and_accepted_fixes_do_not_call_model(self):
        handoff = self.handoff()
        handoff['review']['review']['key_issues_to_review'] = []
        with patch.object(fix, 'handoff_from_run', return_value=handoff), \
                patch.object(fix, 'request_json') as model, self.assertRaises(fix.Stop):
            fix.generate(self.gh)
        model.assert_not_called()
        with patch.object(fix, 'handoff_from_run', return_value=self.handoff()), \
                patch.object(self.gh, 'pages', return_value=[{'commit': {'message': 'Apply shizhen-fix for PR #7'}}]), \
                patch.object(fix, 'request_json') as model, self.assertRaises(fix.Stop):
            fix.generate(self.gh)
        model.assert_not_called()

    def test_valid_proposal_and_duplicate_attempt_reservation(self):
        plan = {'decisions': self.proposal()['decisions'], 'edits': [EDIT]}
        response = {'choices': [{'finish_reason': 'stop', 'message': {'content': json.dumps(plan)}}]}
        with patch.object(fix, 'handoff_from_run', return_value=self.handoff()), \
                patch.object(fix, 'request_json', return_value=response) as model:
            fix.generate(self.gh)
            self.assertEqual(model.call_args.args[0], fix.ENDPOINT)
            self.assertEqual(model.call_args.args[2]['model'], 'x-test-1')
            self.assertTrue(Path('proposal.json').exists())
            with self.assertRaises(fix.Stop):
                fix.generate(self.gh)
            self.assertEqual(model.call_count, 1)
        fix.validate_proposal(self.gh, fix.load_json('proposal.json'))

    def test_publisher_rechecks_author_head_scope_and_hashes(self):
        proposal = self.proposal()
        for changed in [{'user': {'id': 9}}, {'head': {**self.gh.pr['head'], 'sha': OTHER_SHA}}]:
            gh = FakeGitHub()
            gh.pr.update(changed)
            with self.assertRaises(fix.Stop):
                fix.validate_proposal(gh, proposal)
        proposal['original_hashes'][PATH] = 'wrong'
        with self.assertRaises(fix.Stop):
            fix.validate_proposal(self.gh, proposal)

    def test_failed_checks_publish_only_draft_and_preserve_original_branch(self):
        for fork in [False, True]:
            gh = FakeGitHub()
            if fork:
                gh.pr['head']['repo']['full_name'] = 'shiverin/repo'
            fix.save('proposal.json', self.proposal())
            fix.publish(gh)
            created = [data for path, data, _ in gh.calls if path == 'pulls'][0]
            self.assertTrue(created['draft'])
            self.assertEqual(created['base'], 'master' if fork else 'my-feature')
            self.assertIn('**failure**', created['body'])
            writes = [path for path, data, _ in gh.calls if data]
            self.assertFalse(any(path.startswith('git/refs/heads/my-feature') for path in writes))
            self.assertFalse(any('/merge' in path or '/reviews' in path for path in writes))

    def test_changed_head_during_review_never_exports_handoff(self):
        fix.handoff(self.gh)
        self.gh.pr['head']['sha'] = OTHER_SHA
        with self.assertRaises(fix.Stop):
            fix.finish_handoff(self.gh)
        self.assertFalse(Path('review-handoff/handoff.json').exists())

    def test_artifact_run_identity_and_archive_contents_are_verified(self):
        run = {'path': '.github/workflows/shizhen-review.yml', 'name': 'shizhen-review',
               'conclusion': 'success', 'event': 'pull_request_target',
               'repository': {'full_name': 'owner/repo'}}
        artifacts = {'artifacts': [{'id': 1, 'name': 'shizhen-review-handoff-1'}]}
        buf = io.BytesIO()
        with zipfile.ZipFile(buf, 'w') as archive:
            archive.writestr('handoff.json', json.dumps(self.handoff()))
        with patch.object(self.gh, 'api', side_effect=[run, artifacts]), \
                patch.object(fix, 'open_request') as urlopen:
            urlopen.return_value.__enter__.return_value.read.return_value = buf.getvalue()
            self.assertEqual(fix.handoff_from_run(self.gh, 99)['head_sha'], SHA)
        for invalid in [{**run, 'conclusion': 'failure'}, {**run, 'path': 'evil.yml'},
                        {**run, 'repository': {'full_name': 'attacker/repo'}}]:
            with patch.object(self.gh, 'api', return_value=invalid), self.assertRaises(fix.Stop):
                fix.handoff_from_run(self.gh, 99)

    def test_other_authors_review_without_handoff_is_a_clean_skip(self):
        run = {'path': '.github/workflows/shizhen-review.yml', 'name': 'shizhen-review',
               'conclusion': 'success', 'event': 'pull_request_target',
               'repository': {'full_name': 'owner/repo'}}
        with patch.object(self.gh, 'api', side_effect=[run, {'artifacts': []}]), \
                self.assertRaisesRegex(fix.Stop, '^Skipped:'):
            fix.handoff_from_run(self.gh, 99)

    def test_redirects_drop_credentials_and_reject_non_https(self):
        request = fix.urllib.request.Request('https://api.github.com/resource',
                                            headers={'Authorization': 'Bearer dummy'})
        handler = fix.SafeRedirect()
        redirected = handler.redirect_request(request, None, 302, 'Found', {},
                                               'https://storage.example.com/artifact.zip')
        self.assertIsNone(redirected.get_header('Authorization'))
        with self.assertRaises(fix.Stop):
            handler.redirect_request(request, None, 302, 'Found', {}, 'http://unsafe.example.com')

    def test_incomplete_provider_response_never_writes_a_proposal(self):
        response = {'choices': [{'finish_reason': 'length', 'message': {'content': '{}'}}]}
        with patch.object(fix, 'handoff_from_run', return_value=self.handoff()), \
                patch.object(fix, 'request_json', return_value=response), self.assertRaises(fix.Stop):
            fix.generate(self.gh)
        self.assertFalse(Path('proposal.json').exists())


if __name__ == '__main__':
    unittest.main()
