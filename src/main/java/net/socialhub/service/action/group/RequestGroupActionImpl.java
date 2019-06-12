package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentGroupImpl;
import net.socialhub.model.group.RequestGroupImpl;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;

import java.util.HashMap;
import java.util.Map;

public class RequestGroupActionImpl implements RequestGroupAction {

    private RequestGroupImpl requestGroup;

    public RequestGroupActionImpl(RequestGroupImpl requestGroup) {
        this.requestGroup = requestGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentGroup getComments() {
        CommentGroupImpl model = new CommentGroupImpl();
        Map<Account, Pageable<Comment>> map = new HashMap<>();
        requestGroup.getActions().forEach((account, action) -> {

            Paging paging = new Paging();
            paging.setCount(200L);

            Pageable<Comment> comments = action.getComments(paging);
            map.put(account, comments);
        });

        model.setEntities(map);
        model.setSinceDateFromEntities();
        model.setActions(new HashMap<>(requestGroup.getActions()));
        return model;
    }
}
