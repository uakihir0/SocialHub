package net.socialhub.service.twitter;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.utils.MemoSupplier;
import twitter4j.ResponseList;
import twitter4j.Status;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TwitterMapper {

    /**
     * ユーザーマッピング
     */
    public static User user( //
                             twitter4j.User user, //
                             Service service) {

        User model = new User(service);

        model.setId(user.getId());
        model.setName(user.getName());
        model.setScreenName(user.getScreenName());
        model.setDescription(user.getDescription());
        model.setImageUrl(user.getOriginalProfileImageURLHttps());

        return model;
    }

    /**
     * コメントマッピング
     */
    public static Comment comment( //
                                   Status status,  //
                                   Service service) {

        Comment model = new Comment(service);

        model.setId(status.getId());
        model.setComment(status.getText());
        model.setCreateAt(status.getCreatedAt());
        model.setUser(MemoSupplier.of(() -> user(status.getUser(), service)));

        return model;
    }

    /**
     * ページングマッピング
     */
    public static Paging paging( //
                                 twitter4j.Paging paging) {

        Paging model = new Paging();

        model.setPage(((long) paging.getPage()));
        model.setCount(((long) paging.getCount()));
        model.setMaxId(paging.getMaxId());
        model.setSinceId(paging.getSinceId());

        return model;
    }

    public static twitter4j.Paging fromPaging( //
                                               Paging paging) {

        twitter4j.Paging model = new twitter4j.Paging();
        model.setPage(paging.getPage() == null ? -1 : paging.getPage().intValue());
        model.setCount(paging.getCount() == null ? -1 : paging.getCount().intValue());
        model.setMaxId(paging.getMaxId() == null ? -1 : paging.getMaxId());
        model.setSinceId(paging.getSinceId() == null ? -1 : paging.getSinceId());

        return model;
    }

    /**
     * タイムラインマッピング
     */
    public static Pageable<Comment> timeLine( //
                                              ResponseList<Status> statuses, //
                                              Service service, //
                                              Paging paging) {

        Pageable<Comment> model = new Pageable<>();
        model.setEntities(statuses.stream().map(e -> comment(e, service)) //
                .sorted(Comparator.comparing(Comment::getCreateAt).reversed()) //
                .collect(Collectors.toList()));

        if (paging != null) {
            model.setPaging(paging);

        } else {
            // make paging info from response
            Paging pg = new Paging();
            int count = statuses.size();

            pg.setCount((long) count);
            pg.setMaxId(model.getEntities().get(0).getNumberId());
            pg.setSinceId(model.getEntities().get(count - 1).getNumberId());
        }

        return model;
    }
}
