package net.socialhub.core.define.action;

public enum OtherActionType implements ActionType {

    // Get Comment
    GetComment,
    GetContext,

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

    // Post Users
    FollowUser,//
    UnfollowUser,
    MuteUser, //
    UnmuteUser, //
    BlockUser, //
    UnblockUser, //
    GetRelationship, //

    // Channels
    GetChannels, //
    ;
}
