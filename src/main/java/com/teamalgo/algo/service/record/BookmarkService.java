package com.teamalgo.algo.service.record;

import com.teamalgo.algo.domain.bookmark.Bookmark;
import com.teamalgo.algo.domain.record.Record;
import com.teamalgo.algo.domain.user.User;
import com.teamalgo.algo.dto.RecordDTO;
import com.teamalgo.algo.global.common.code.ErrorCode;
import com.teamalgo.algo.repository.BookmarkRepository;
import com.teamalgo.algo.repository.RecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final RecordRepository recordRepository;

    // 북마크 추가
    @Transactional
    public void addBookmark(User user, Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));

        if (!bookmarkRepository.existsByUserAndRecord(user, record)) {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .record(record)
                    .build();
            bookmarkRepository.save(bookmark);
        }
    }

    // 북마크 삭제
    @Transactional
    public void removeBookmark(User user, Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));

        bookmarkRepository.findByUserAndRecord(user, record)
                .ifPresent(bookmarkRepository::delete);
    }

    // 북마크 여부 확인
    public boolean isBookmarked(User user, Record record) {
        return bookmarkRepository.existsByUserAndRecord(user, record);
    }

    // 북마크한 레코드 목록 조회
    public List<RecordDTO> getBookmarkedRecords(User user) {
        return bookmarkRepository.findByUser(user).stream()
                .map(Bookmark::getRecord)
                .map(RecordDTO::from)
                .toList();
    }
}
