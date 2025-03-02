package org.anas.bidderx_rest.service.dto;

public class LikeStatusDTO {
    private long totalLikes;
    private boolean hasLiked;

    public LikeStatusDTO() {
    }

    public LikeStatusDTO(long totalLikes, boolean hasLiked) {
        this.totalLikes = totalLikes;
        this.hasLiked = hasLiked;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }
}
