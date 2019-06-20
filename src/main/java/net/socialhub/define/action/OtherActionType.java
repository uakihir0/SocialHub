package net.socialhub.define.action;

public enum OtherActionType implements ActionType {

    // Get Comment
    GetComment,

    // Post Comment
    PostComment,
    DeleteComment,
    EditComment,
    LikeComment,
    UnlikeComment,
    ShareComment,
    UnShareComment,

    // Get Users
    GetUser,
    GetUserMe,
    GetFollowingUsers,
    GetFollowerUsers,

    // Post Users
    FollowUser,//
    UnfollowUser,
    MuteUser, //
    UnmuteUser, //
    BlockUser, //
    UnblockUser, //
    GetRelationship, //
    ;
}
