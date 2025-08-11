package com.rtvnewsnetwork.reel.service;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReelService {
    ReelModel addReel(ReelModel reelModel);
    ReelModel updateReel(String reelId, ReelModel reelModel);
    ReelModel deleteReel(String reelId);
    List<ReelModel> getAllReels(ReviewStatus status);
    ReelModel getReelById(String reelId);
    ReelModel incrementLikes(String reelId,String userId);
    ReelModel decrementLikes(String reelId,String userId);
    CommentModel addComment(String reelId, CommentModel commentModel);
    CommentModel addReply(String reelId, String parentId, CommentModel reply);
    List<CommentModel> getCommentsByReelId(String reelId, Pageable pageable);
    ReelInsightModel deleteComment(String reelId, String commentId);
    CommentModel editComment(String reelId, String commentId, String updatedComment);
}
