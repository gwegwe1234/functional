package coupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 3장 쿠폰 로직
 */
public class CouponService {

    // 계산 함수 -> 매번 같은 인자를 넣어주면 같은 응답 리턴
    public String subCouponRank(Subscriber subscriber) {
        if (subscriber.recCount >= 10) { // 계산
            return "best";
        } else {
            return "good";
        }
    }

    public List<String> selectCouponsByRank(List<Coupon> coupons, String rank) {
        List<String> returnList = new ArrayList<>();
        for (int i = 0; i < coupons.size(); i++) {
            var coupon = coupons.get(i);
            if (Objects.equals(coupon.rank, rank)) {
                returnList.add(coupon.code);
            }
        }

        return returnList;
    }

    public Email emailForSubscriber(Subscriber subscriber, String good, String  best) {
        var rank = subCouponRank(subscriber);
        if (rank == "best") {
            return new Email(
                    "newsLettter@coupon.co",
                    subscriber.email,
                    "Your best weekly",
                    "Here are the best coupon " + best
            );
        } else {
            return new Email(
                    "newsLettter@coupon.co",
                    subscriber.email,
                    "Your good weekly",
                    "Here are the best coupon " + good
            );
        }
    }

    public List<Email> emailsForSubscribers(List<Subscriber> subscribers, String good, String best) {
        List<Email> emails = new ArrayList<>();
        for (int i = 0; i< subscribers.size(); i++) {
            var subscriber = subscribers.get(i);
            var email = emailForSubscriber(subscriber, good, best);
            emails.add(email);
        }

        return emails;
    }

    // Action
    public void sendIssue() {
        var coupons = fetchCouponsFromDB();
        var goodCoupons = selectCouponsByRank(coupons, "good");
        var bestCoupons = selectCouponsByRank(coupons, "best");
        var subscribers = fetchSubscribersFromDB();
        var emails = emailsForSubscribers(subscribers, goodCoupons.get(0), bestCoupons.get(0));
        for (int i = 0; i < emails.size(); i++) {
            var email = emails.get(i);
            emailSystem.send(email);
        }
    }

    private List<Subscriber> fetchSubscribersFromDB() {
        return List.of(new Subscriber("email@ca.c", 15), new Subscriber("email2.c.c", 9));
    }

    private List<Coupon> fetchCouponsFromDB() {
        return List.of(new Coupon("code", "best"), new Coupon("code", "good"));
    }


    // Data 영역
    public static class Subscriber {
        private final String email;
        private final int recCount;

        public Subscriber(String email, int recCount) {
            this.email = email;
            this.recCount = recCount;
        }
    }

    public static class Coupon {
        private final String code;
        private final String rank;

        public Coupon(String code, String rank) {
            this.code = code;
            this.rank = rank;
        }
    }

    public static class Email {
        private final String from;
        private final String to;
        private final String subject;
        private final String body;

        public Email(String from, String to, String subject, String body) {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
    }
}
