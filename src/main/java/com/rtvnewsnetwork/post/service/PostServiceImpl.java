package com.rtvnewsnetwork.post.service;


import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.comment.model.TaggedUser;
import com.rtvnewsnetwork.comment.repository.CommentRepository;
import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;
import com.rtvnewsnetwork.common.model.CONTENT_TYPE;
import com.rtvnewsnetwork.event.service.EventPublisher;
import com.rtvnewsnetwork.event.service.EventService;
import com.rtvnewsnetwork.like.model.LikeModel;
import com.rtvnewsnetwork.like.repository.LikeRepository;
import com.rtvnewsnetwork.post.model.PostModel;
import com.rtvnewsnetwork.post.model.PostsInsightModel;
import com.rtvnewsnetwork.post.repository.PostRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
 //   private final UserPostRepository userPostRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EventService eventService;

    @Value("${cloudfront.url}")
    private String imageBaseUrl;

    public PostServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            EventService eventService,
            LikeRepository likeRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.likeRepository = likeRepository;
    }

    @Override
    public PostModel incrementLikes(String postId, String userId) {
        PostModel post = postRepository.findById(postId).orElse(null);
        if (post == null) return null;

        post.getInsights().setNoOfLikes(post.getInsights().getNoOfLikes() + 1);
        PostModel updatedPost = postRepository.save(post);
        LikeModel likeModel = new LikeModel(null, userId, postId, CONTENT_TYPE.POST);
        likeRepository.save(likeModel);
        updatedPost.setLiked(true);

//        Map<String, Object> data = EventDataUtil.createPostEventData(updatedPost.getId());
//        produceEventToKafka(Constants.POST_LIKE, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, data);
//
//        Map<String, Object> eventData = new HashMap<>();
//        eventData.put("title", "Congratulations! You’ve just won 100 coins! 🎉");
//        eventData.put("description", "Want to win even more? Keep engaging and liking posts in the app.");
//        eventData.put("path", "/posts");
//        produceEventToKafka(Constants.COIN_CREDIT, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, eventData);

        return updatedPost;
    }

    @Override
    public PostModel decrementLikes(String postId, String userId) {
        int noOfRow = likeRepository.deleteByUserIdAndContentId(userId, postId);
        if (noOfRow > 0) {
            PostModel post = postRepository.findById(postId).orElse(null);
            if (post == null) return null;

            if (post.getInsights().getNoOfLikes() > 0) {
                post.getInsights().setNoOfLikes(post.getInsights().getNoOfLikes() - 1);
            }
            PostModel updatedPost = postRepository.save(post);
            updatedPost.setLiked(false);

//            Map<String, Object> data = EventDataUtil.createPostEventData(updatedPost.getId());
//            produceEventToKafka(Constants.POST_DISLIKE, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, data);

            return updatedPost;
        } else {
            return null;
        }
    }

    @Override
    public CommentModel addComment(CommentModel commentModel, String userId, String postId, String userName) {
        CommentModel resolvedComment = resolveTaggedUsers(commentModel, userRepository);
        resolvedComment.setUserId(userId);
        resolvedComment.setUsername(userName);
        resolvedComment.setContentId(postId);
        CommentModel result = commentRepository.save(resolvedComment);
        PostModel post = postRepository.findById(commentModel.getContentId()).get();
        post.getInsights().setNoOfComments(post.getInsights().getNoOfComments() + 1);
        postRepository.save(post);
//
//        Map<String, Object> data = EventDataUtil.createCommentEventData(result.getId(), post.getId());
//        produceEventToKafka(Constants.COMMENT_ON_POST, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, data);

//        Map<String, Object> eventData = new HashMap<>();
//        eventData.put("title", "Congratulations! You’ve just won 50 coins! 🎉");
//        eventData.put("description", "Want to win even more? Keep engaging and commenting on posts in the app.");
//        eventData.put("path", "/posts");
//        produceEventToKafka(Constants.COIN_CREDIT, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, eventData);

        return result;
    }

    @Override
    public CommentModel addReply(String parentCommentId, CommentModel reply) {
        CommentModel parentComment = commentRepository.findById(parentCommentId).orElse(null);
        if (parentComment != null) {
            CommentModel resolvedReply = resolveTaggedUsers(reply, userRepository);
            if (resolvedReply.getId() == null) {
                resolvedReply.setId(ObjectId.get().toString());
            }
            resolvedReply.setContentId(parentComment.getContentId());

            List<CommentModel> updatedReplies = new ArrayList<>(parentComment.getReplies());
            updatedReplies.add(resolvedReply);
            parentComment.setReplies(updatedReplies);

            return commentRepository.save(parentComment);
        }
        return null;
    }

    @Override
    public List<CommentModel> getCommentsByPostId(String postId, Pageable pageable) {
        return commentRepository.findByContentId(postId, pageable);
    }


    @Override
    public PostModel getPost(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @Transactional
    @Override
    public PostsInsightModel deleteComment(String commentId) {
        CommentModel commentModel = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment with id " + commentId + " not found"));
        String postId = commentModel.getContentId();

        PostsInsightModel interaction = decrementComments(postId);
        commentRepository.deleteById(commentId);
//
//        Map<String, Object> data = EventDataUtil.createCommentEventData(commentModel.getId(), postId);
//        produceEventToKafka(Constants.DELETE_POST_COMMENT, userId, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, data);

        return interaction;
    }

    @Override
    public CommentModel editComment(String commentId, String updatedComment) {
        CommentModel existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment with id " + commentId + " not found"));
        existingComment.setComment(updatedComment);
        return commentRepository.save(existingComment);
    }

    @Transactional
    public PostsInsightModel decrementComments(String postId) {
        PostModel postModel = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post with id " + postId + " not found"));
        PostsInsightModel interactionCount = postModel.getInsights();

        interactionCount.setNoOfComments((interactionCount.getNoOfComments() !=0 ? interactionCount.getNoOfComments() : 0) - 1);
        if (interactionCount.getNoOfComments() < 0) {
            interactionCount.setNoOfComments(0);
        }

        postRepository.save(postModel);
        return interactionCount;
    }

    @Override
    public PostModel getPostById(String postId, String userId) {
        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not Found"));
        if (userId != null) {
            post.setLiked(isUserIdAndPostIdPresent(userId, postId));
        }
        return post;
    }

    @Override
    public List<PostModel> getAllPost(String userId, Pageable pageable) {
        List<PostModel> postList = postRepository.findAll(pageable).toList();
        if (userId == null) {
            return postList;
        } else {
            return postList.stream().map(post -> {
                boolean isMarked = likeRepository.existsByUserIdAndContentId(userId, post.getId());
                post.setLiked(isMarked);
                return post;
            }).collect(Collectors.toList());
        }
    }

    public boolean isUserIdAndPostIdPresent(String userId, String postId) {
        return likeRepository.existsByUserIdAndContentId(userId, postId);
    }

    @Override
    public PostModel addPost(PostModel postModel) {
        postModel.setTitle(removeHtmlTags(postModel.getTitle()));

        PostModel response = postRepository.save(postModel);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", postModel.getTitle());
        eventData.put("description", postModel.getDescription());
        String imgUrl = imageBaseUrl + (postModel.getBannerImage() != null && postModel.getBannerImage().getPath() != null
                ? postModel.getBannerImage().getPath()
                : postModel.getThumbnail());
        eventData.put("imageUrl", imgUrl);
        eventData.put("path", "/posts/" + response.getId());

//        produceEventToKafka(Constants.NEW_POST, null, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, eventData);
//
//        String deepLink = branchLinkService.createBranchLink(response.getId().toString(), "post");
//        if (deepLink != null) {
//            response.setShareUrl(deepLink);
//        } else {
//            System.out.println("Failed to create deep link for post ID: " + response.getId());
//        }

        postRepository.save(response);
        return response;
    }

    public String removeHtmlTags(String input) {
        return input.replaceAll("<[^>]*>", "").trim();
    }
//
//    public CommentModel resolveTaggedUsers(CommentModel comment, UserRepository userRepository) {
//        List<TaggedUser> taggedUsersWithNames = comment.getTaggedUsers().stream().map(taggedUser -> {
//                .map(taggedUser -> {
//                    User resolvedUser = userRepository.findById(taggedUser.getUserId()).get();
//                    String name = resolvedUser.getName() != null ? resolvedUser.getName() : "Unknown";
//                    return new TaggedUser(resolvedUser.getId(), name);
//                })
//                .collect(Collectors.toList());
//
//        comment.setTaggedUsers(taggedUsersWithNames);
//
//        List<String> userList = getUserIdList(taggedUsersWithNames);
//
////        Map<String, Object> eventData = new HashMap<>();
////        eventData.put("title", "You’ve been tagged 🔗 in a post.");
////        eventData.put("path", "/posts/" + comment.getPostId());
////        eventData.put("userlist", userList);
////
////        produceEventToKafka(Constants.OTHERS, null, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, eventData);
//
//        return comment;
//    }


    public CommentModel resolveTaggedUsers(CommentModel comment, UserRepository userRepository) {
        if (comment.getTaggedUsers() == null || comment.getTaggedUsers().isEmpty()) {
            return comment; // nothing to resolve
        }

        List<TaggedUser> resolvedTaggedUsers = comment.getTaggedUsers()
                .stream()
                .map(taggedUser -> {
                    return userRepository.findById(taggedUser.getUserId())
                            .map(user -> new TaggedUser(user.getId(),
                                    user.getName() != null ? user.getName() : "Unknown"))
                            .orElse(new TaggedUser(taggedUser.getUserId(), "Unknown"));
                })
                .collect(Collectors.toList());

        comment.setTaggedUsers(resolvedTaggedUsers);

        // If you still want the userId list for further processing:
        List<String> userList = resolvedTaggedUsers.stream()
                .map(TaggedUser::getUserId)
                .collect(Collectors.toList());

        // Example Kafka event code (commented out)
    /*
    Map<String, Object> eventData = new HashMap<>();
    eventData.put("title", "You’ve been tagged 🔗 in a post.");
    eventData.put("path", "/posts/" + comment.getContentId());
    eventData.put("userlist", userList);
    produceEventToKafka(Constants.OTHERS, null, KafkaTopicConfig.GENERIC_EVENT_CHANNEL, eventData);
    */

        return comment;
    }

    @Override
    public void deletePosts(List<String> postIds) {
        postRepository.deleteAllById(postIds);
    }

    public List<String> getUserIdList(List<TaggedUser> taggedUsersWithNames) {
        return taggedUsersWithNames.stream()
                .map(TaggedUser::getUserId)
                .collect(Collectors.toList());
    }
}
