package com.rtvnewsnetwork.reel.controller;

import com.rtvnewsnetwork.comment.model.CommentModel;
import com.rtvnewsnetwork.reel.model.ReelInsightModel;
import com.rtvnewsnetwork.reel.model.ReelModel;
import com.rtvnewsnetwork.reel.model.ReviewStatus;
import com.rtvnewsnetwork.reel.service.ReelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reels")
public class ReelController {

    private final ReelService reelService;

    public ReelController(ReelService reelService) {
        this.reelService = reelService;
    }


    @PostMapping
    public ResponseEntity<ReelModel> addReel(@RequestBody ReelModel reelModel) {
        return new ResponseEntity<>(reelService.addReel(reelModel), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ReelModel> updateReel(
            @PathVariable String id,
            @RequestBody ReelModel reelModel
    ) {
        return ResponseEntity.ok(reelService.updateReel(id, reelModel));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ReelModel> deleteReel(@PathVariable String id) {
        return ResponseEntity.ok(reelService.deleteReel(id));
    }


    @GetMapping
    public ResponseEntity<List<ReelModel>> getAllReels(
            @RequestParam(value = "status", required = false) ReviewStatus status
    ) {
        return ResponseEntity.ok(reelService.getAllReels(status));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReelModel> getReelById(@PathVariable String id) {
        return ResponseEntity.ok(reelService.getReelById(id));
    }


    @PostMapping("/{id}/likes")
    public ResponseEntity<ReelModel> incrementLikes(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(reelService.incrementLikes(id, userId));
    }


    @DeleteMapping("/{id}/likes")
    public ResponseEntity<ReelModel> decrementLikes(
            @PathVariable String id,
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(reelService.decrementLikes(id, userId));
    }


    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentModel> addComment(
            @PathVariable String id,
            @RequestBody CommentModel commentModel
    ) {
        return new ResponseEntity<>(reelService.addComment(id, commentModel), HttpStatus.CREATED);
    }


    @PostMapping("/{id}/comments/{parentId}/replies")
    public ResponseEntity<CommentModel> addReply(
            @PathVariable String id,
            @PathVariable String parentId,
            @RequestBody CommentModel reply
    ) {
        return new ResponseEntity<>(reelService.addReply(id, parentId, reply), HttpStatus.CREATED);
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentModel>> getCommentsByReelId(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reelService.getCommentsByReelId(id, pageable));
    }


    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<ReelInsightModel> deleteComment(
            @PathVariable String id,
            @PathVariable String commentId
    ) {
        return ResponseEntity.ok(reelService.deleteComment(id, commentId));
    }


    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<CommentModel> editComment(
            @PathVariable String id,
            @PathVariable String commentId,
            @RequestParam String updatedComment
    ) {
        return ResponseEntity.ok(reelService.editComment(id, commentId, updatedComment));
    }
}
