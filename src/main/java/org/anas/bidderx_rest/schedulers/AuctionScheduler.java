package org.anas.bidderx_rest.schedulers;

import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.service.AuctionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;


@Service
public class AuctionScheduler {
    private final TaskScheduler taskScheduler;
    private final AuctionService auctionService;

    public AuctionScheduler(TaskScheduler taskScheduler, AuctionService auctionService) {
        this.taskScheduler = taskScheduler;
        this.auctionService = auctionService;
    }

    public void scheduleAuctionEnd(Auction auction) {
        Date endTime = Date.from(auction.getEndTime().atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(() -> endAuction(auction.getId()), endTime);
    }

    private void endAuction(UUID auctionId) {
        // Retrieve auction, determine winner, update status to ENDED, and set winner id
        auctionService.endAuction(auctionId);
    }
}
