package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.support.ReactionCandidate;
import org.junit.Test;

import java.util.List;

public class ReactionCandidateTest extends AbstractApiTest {

    @Test
    public void testReactionCandidatesTwitter() {

        Account account = getTwitterAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesMastodon() {

        Account account = getMastodonAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesMisskey() {

        Account account = getMisskeyAccount();
        List<ReactionCandidate> candidates = account.action().getReactionCandidates();

        for (ReactionCandidate candidate : candidates) {
            printReactionCandidate(candidate);
        }
    }

    @Test
    public void testReactionCandidatesSlack() {

        Account account = getSlackAccount();
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
