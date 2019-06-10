package net.socialhub.define;

/**
 * SNS アクション一覧
 * SNS Actions
 */
public enum ActionType {

    // TimeLine
    HomeTimeLine,
    MentionTimeLine,
    UserCommentTimeLine,
    UserLikeTimeLine,


    // Comment
    GetComment,
    PostComment,
    DeleteComment,
    EditComment,
    LikeComment,
    UnlikeComment,
    ShareComment,
    UnShareComment,

    // User
    GetUser,
    GetUserMe,
    FollowUser,//
    UnfollowUser,
    MuteUser, //
    UnmuteUser, //
    BlockUser, //
    UnblockUser, //
    ;
}
