package fall24.swp391.g1se1868.koiauction.model.auction;

import fall24.swp391.g1se1868.koiauction.model.Auction;
import fall24.swp391.g1se1868.koiauction.model.KoiFish;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishMediaDTO;
import fall24.swp391.g1se1868.koiauction.model.koifishdto.KoiFishWithMediaAll;

import java.util.List;

public class KoiAuctionResponseDTO {
    private Auction auction;
    private List<Integer> koiFish; // Danh sách ID của KoiFish

    public KoiAuctionResponseDTO(Auction auction, List<Integer> koiFish) {
        this.auction = auction;
        this.koiFish = koiFish;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public List<Integer> getKoiFish() {
        return koiFish;
    }

    public void setKoiFish(List<Integer> koiFish) {
        this.koiFish = koiFish;
    }
}


