package com.teamalgo.algo.global.common.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UsernameGenerator {

    private final String[] ADJECTIVES = {
            "푸른","빨간","노란","하얀","검은","초록","보라","분홍","주황","은빛",
            "따뜻한","시원한","귀여운","예쁜","멋진","활발한","조용한","용감한","느린","빠른",
            "행복한","슬픈","기쁜","화난","친절한","상냥한","튼튼한","가벼운","무거운","작은"
    };

    private static final String[] NOUNS = {
            "호랑이","토끼","여우","곰","강아지","고양이","판다","돌고래","펭귄","독수리",
            "참새","공룡","사자","늑대","다람쥐","고래","상어","원숭이","두루미","개구리",
            "올빼미","부엉이","캥거루","코알라","너구리","치타","하마","기린","물개","바다사자",
            "비버","스컹크","고슴도치","청설모","족제비","두더지","미어캣","기러기","까마귀","거위",
            "개발자","코더"
    };

    private final Random random = new Random();

    public String generateRandomUsername() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];

        int number = random.nextInt(99) + 1;
        String formatted = String.format("%02d", number);

        return adjective + noun + formatted;
    }
}
