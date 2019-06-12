package net.socialhub.define.action;

public enum OtherActionType implements ActionType {

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
