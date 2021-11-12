package bmstu.iu9.tfl.lab3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NontermLeftmostDerivationTree {
    private enum RegularStatus {
        NOT_DETERMINED,
        REGULAR,
        PROBABLY_REGULAR,
        SUSPECT
    }

    private final TreeNode node;
    private RegularStatus status;

    public NontermLeftmostDerivationTree(String nonterm, Grammar rules) {
        status = RegularStatus.NOT_DETERMINED;
        node = new TreeNode(nonterm, rules, nonterm, false, new HashSet<>());
        if (!node.checkRootNontermFound()) {
            status = RegularStatus.PROBABLY_REGULAR;
        } else {
            String prefix = node.buildLeftMostDerivationToRootNonterm(nonterm);
            List<String> suffix = node.getNontermsAndTermsSuffixOfRootNontermDerivationTree();
            if (suffix.isEmpty()) {
                status = RegularStatus.PROBABLY_REGULAR;
            } else {
                if (checkRegularSubsetContainsSuffix && checkWordBelongsToSuffixIterLanguage()) {
                    Set<StringBuilder> words = node.getWords();
                    if (words.isEmpty()) {
                        status = RegularStatus.SUSPECT;
                        //leftmostDerivationTree may be not regular
                    } else {
                        for (StringBuilder word : words) {
                            if (!checkWordBelongsToSuffixIterLangugage()) {
                                status = RegularStatus.SUSPECT;
                                //leftmostDerivationTree may be not regular
                            }
                        }
                    }
                    if (status == RegularStatus.NOT_DETERMINED) {
                        status = (checkRegularSubsetContainsRootNonterm()) ? RegularStatus.REGULAR : RegularStatus.PROBABLY_REGULAR;
                    }
                } else {
                    status = RegularStatus.SUSPECT;
                }
            }
            //recursiveClosureOfAllNonterms that were created by NontermLeftmostDerivationTree
            // if (all nonterms are regular) then it is regular
            // if (all terms are regular and probably_regular) then it is probably_regular
            // if (all terms are probably_regular) then it is probably_regular
            // if it has all non-terms that are probably_regular then language regularity can not be determined
        }
    }
}
