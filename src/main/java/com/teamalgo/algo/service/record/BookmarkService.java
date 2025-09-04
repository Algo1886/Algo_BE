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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final RecordRepository recordRepository;

    // 북마크 추가
    @Transactional
    public void addBookmark(User user, Long recordId) {
        Record record = recordRepository.findByIdAndIsDraftFalse(recordId) // draft 제외
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));

        if (!bookmarkRepository.existsByUserAndRecordAndRecordIsDraftFalse(user, record)) {
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
        Record record = recordRepository.findByIdAndIsDraftFalse(recordId) // draft 제외
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));

        bookmarkRepository.findByUserAndRecordAndRecordIsDraftFalse(user, record)
                .ifPresent(bookmarkRepository::delete);
    }

    // 북마크 여부 확인
    public boolean isBookmarked(User user, Record record) {
        return bookmarkRepository.existsByUserAndRecordAndRecordIsDraftFalse(user, record);
    }

    // 북마크한 레코드 목록 조회 (카테고리 선택 X)
    public Page<RecordDTO> getBookmarkedRecords(User user, Pageable pageable) {
        return bookmarkRepository.findByUserAndRecordIsDraftFalse(user, pageable)
                .map(bookmark -> RecordDTO.from(bookmark.getRecord()));
    }

    // 북마크한 레코드 목록 조회 (카테고리 선택 O)
    public Page<RecordDTO> getBookmarkedRecords(User user, Pageable pageable, String category) {
        if (category == null || category.isBlank()) {
            return bookmarkRepository.findByUserAndRecordIsDraftFalse(user, pageable)
                    .map(bookmark -> RecordDTO.from(bookmark.getRecord()));
        }

        return bookmarkRepository.findByUserAndRecordIsDraftFalseAndRecord_RecordCategories_Category_Name(
                        user, category, pageable)
                .map(bookmark -> RecordDTO.from(bookmark.getRecord()));
    }
}



