package com.rtvnewsnetwork.post.controller;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.post.model.PostModel;
import com.rtvnewsnetwork.post.model.PostsInsightModel;
import com.rtvnewsnetwork.post.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController implements AuthDetailsHelper {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("{postId}/like")
    public PostModel addLikeOrRemove(
            @PathVariable(required = true) String postId,
            @RequestParam(required = true) boolean add
    ) {
        String userId = getUserId();
        if (add) {
            return postService.incrementLikes(postId, userId);
        } else {
            return postService.decrementLikes(postId, userId);
        }
    }

    @PostMapping("/{postId}/comment")
    public CommentModel addComment(
            @PathVariable(required = true) String postId,
            @RequestBody(required = true) CommentModel commentModel
    ) {
        String userId = getUserId();
        String userName = getUser() != null ? getUser().getName() : null;
        return postService.addComment(commentModel, userId, postId, userName);
    }

    @PostMapping("/comment/{commentId}/reply")
    public CommentModel addReply(
            @PathVariable String commentId,
            @RequestBody CommentModel reply
    ) {
        String userId = getUserId();
        String userName = getUser() != null ? getUser().getName() : null;
        reply.setUserId(userId);
        reply.setUsername(userName);
        return postService.addReply(commentId, reply);
    }

    @GetMapping("/{postId}/comment")
    public List<CommentModel> getCommentsByPostId(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return postService.getCommentsByPostId(
                postId,
                PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending())
        );
    }

    @DeleteMapping("/comment/{commentId}")
    public PostsInsightModel deleteComment(@PathVariable String commentId) {
        return postService.deleteComment(commentId);
    }

    @PutMapping("/comment/{commentId}")
    public CommentModel updateComment(
            @PathVariable String commentId,
            @RequestBody String updatedComment
    ) {
        return postService.editComment(commentId, updatedComment);
    }

    @GetMapping("/{postId}")
    public PostModel getPostById(@PathVariable String postId) {
        String userId = getUserId();
        return postService.getPostById(postId, userId);
    }

    @GetMapping
    public List<PostModel> getAllPost(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        String userId = getUserId();
        return postService.getAllPost(userId, pageable);
    }

    @PostMapping
    public PostModel addPost(@RequestBody PostModel postModel) {
        return postService.addPost(postModel);
    }

}

