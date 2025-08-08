package com.rtvnewsnetwork.reel.service;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.comment.model.TaggedUser;
import com.rtvnewsnetwork.comment.repository.CommentRepository;
import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import com.rtvnewsnetwork.like.model.LikeModel;
import com.rtvnewsnetwork.like.repository.LikeRepository;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import com.rtvnewsnetwork.reel.repository.ReelRepository;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReelServiceImpl implements ReelService {

    private final ReelRepository reelRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public ReelServiceImpl(
            ReelRepository reelRepository,
            CommentRepository commentRepository,
            LikeRepository likeRepository,
            UserRepository userRepository
    ) {
        this.reelRepository = reelRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReelModel addReel(ReelModel reelModel) {
        reelModel.setInsights(new ReelInsightModel(0, 0));
        reelModel.setStatus(ReviewStatus.UNDER_REVIEW);
        return reelRepository.save(reelModel);
    }

    @Override
    public ReelModel updateReel(String reelId, ReelModel reelModel) {
        ReelModel existing = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        existing.setTitle(reelModel.getTitle());
        existing.setThumbnail(reelModel.getThumbnail());
        existing.setVideoURL(reelModel.getVideoURL());
        existing.setStatus(reelModel.getStatus());
        return reelRepository.save(existing);
    }

    @Override
    public ReelModel deleteReel(String reelId) {
        ReelModel existing = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        reelRepository.delete(existing);
        return existing;
    }

    @Override
    public List<ReelModel> getAllReels(ReviewStatus status) {
        if (status != null) {
            return reelRepository.findAll()
                    .stream()
                    .filter(r -> status.equals(r.getStatus()))
                    .toList();
        }
        return reelRepository.findAll();
    }

    @Override
    public ReelModel getReelById(String reelId) {
        return reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
    }

    @Override
    public ReelModel incrementLikes(String reelId, String userId) {
        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        reel.getInsights().setNoOfLikes(reel.getInsights().getNoOfLikes() + 1);
        ReelModel updatedReel = reelRepository.save(reel);

        likeRepository.save(new LikeModel(null, userId, reelId, CONTENT_TYPE.REEL));
        updatedReel.setLiked(true);

        return updatedReel;
    }

    @Override
    public ReelModel decrementLikes(String reelId, String userId) {
        int deletedRows = likeRepository.deleteByUserIdAndContentId(userId, reelId);
        if (deletedRows > 0) {
            ReelModel reel = reelRepository.findById(reelId)
                    .orElseThrow(() -> new RuntimeException("Reel not found"));

            if (reel.getInsights().getNoOfLikes() > 0) {
                reel.getInsights().setNoOfLikes(reel.getInsights().getNoOfLikes() - 1);
            }
            ReelModel updatedReel = reelRepository.save(reel);
            updatedReel.setLiked(false);
            return updatedReel;
        }
        return null;
    }

    @Override
    public CommentModel addComment(String reelId, CommentModel commentModel) {
        CommentModel resolvedComment = resolveTaggedUsers(commentModel);
        resolvedComment.setContentId(reelId);
        resolvedComment.setTargetType(CONTENT_TYPE.REEL);

        CommentModel savedComment = commentRepository.save(resolvedComment);

        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        reel.getInsights().setNoOfComments(reel.getInsights().getNoOfComments() + 1);
        reelRepository.save(reel);

        return savedComment;
    }

    @Override
    public CommentModel addReply(String reelId, String parentId, CommentModel reply) {
        CommentModel parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        CommentModel resolvedReply = resolveTaggedUsers(reply);
        resolvedReply.setContentId(reelId);
        resolvedReply.setTargetType(CONTENT_TYPE.REEL);

        List<CommentModel> updatedReplies = new ArrayList<>(parent.getReplies());
        updatedReplies.add(resolvedReply);
        parent.setReplies(updatedReplies);

        commentRepository.save(parent);
        return resolvedReply;
    }

    @Override
    public List<CommentModel> getCommentsByReelId(String reelId, Pageable pageable) {
        return commentRepository.findByContentIdAndTargetType(reelId, CONTENT_TYPE.REEL, pageable);
    }

    @Override
    public ReelInsightModel deleteComment(String reelId, String commentId) {
        CommentModel comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.deleteById(commentId);

        ReelModel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));
        if (reel.getInsights().getNoOfComments() > 0) {
            reel.getInsights().setNoOfComments(reel.getInsights().getNoOfComments() - 1);
        }
        reelRepository.save(reel);

        return reel.getInsights();
    }

    @Override
    public CommentModel editComment(String reelId, String commentId, String updatedComment) {
        CommentModel comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setComment(updatedComment);
        return commentRepository.save(comment);
    }

    private CommentModel resolveTaggedUsers(CommentModel comment) {
        if (comment.getTaggedUsers() == null || comment.getTaggedUsers().isEmpty()) {
            return comment;
        }

        List<TaggedUser> resolved = comment.getTaggedUsers()
                .stream()
                .map(taggedUser -> userRepository.findById(taggedUser.getUserId())
                        .map(user -> new TaggedUser(user.getId(),
                                user.getName() != null ? user.getName() : "Unknown"))
                        .orElse(new TaggedUser(taggedUser.getUserId(), "Unknown"))
                )
                .collect(Collectors.toList());

        comment.setTaggedUsers(resolved);
        return comment;
    }
}
