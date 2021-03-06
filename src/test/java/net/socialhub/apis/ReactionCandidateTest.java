package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.support.ReactionCandidate;
import org.junit.Test;

import java.util.List;

public class ReactionCandidateTest extends AbstractApiTest {

    @Test
    public void testReactionCandidatesTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesMisskey() {

        Account account = SocialAuthUtil.getMisskeyAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    private void printReactionCandidate(ReactionCandidate candidate) {
        System.out.print(candidate.getName() + ": ");

        if (candidate.getEmoji() != null) {
            System.out.println("E: " + candidate.getEmoji());
        }
        if (candidate.getIconUrl() != null) {
            System.out.println("ICON: " + candidate.getIconUrl());
        }

        List<String> names = candidate.getAllNames();

        if (names.size() > 1) {
            for (String alias : names) {
                System.out.println("A: " + alias);
            }
        }
    }
}
