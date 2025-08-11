package com.rtvnewsnetwork.post.service;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.post.model.PostModel;
import com.rtvnewsnetwork.post.model.PostsInsightModel;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostService {

    PostModel incrementLikes(String postId, String userId);

    PostModel decrementLikes(String postId, String userId);

    CommentModel addComment(CommentModel commentModel, String userId, String postId, String userName);

    CommentModel addReply(String parentId, CommentModel reply);

    List<CommentModel> getCommentsByPostId(String postId,  Pageable pageable);

    PostModel getPost(String postId);

    PostsInsightModel deleteComment(String commentId);

    CommentModel editComment(String commentId, String updatedComment);

    PostModel getPostById(String postId, String userId);

    List<PostModel> getAllPost(String userId, Pageable pageable);

    PostModel addPost(PostModel postModel);

    void deletePosts(List<String> postIds);
}

