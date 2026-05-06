package com.offnal.shifterz.global.util.encrypt;

import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.repository.MemberRepository;
import com.offnal.shifterz.domain.memo.domain.Memo;
import com.offnal.shifterz.domain.memo.repository.MemoRepository;
import com.offnal.shifterz.domain.todo.domain.Todo;
import com.offnal.shifterz.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"dev", "local"})
public class DevAesDataMigrationRunner implements org.springframework.boot.CommandLineRunner {

	private final EncryptUtil encryptUtil;
	private final MemberRepository memberRepository;
	private final TodoRepository todoRepository;
	private final MemoRepository memoRepository;

	@Value("${encrypt.migration.enabled:false}") // 기본 false
	private boolean enabled;

	@Override
	@Transactional
	public void run(String... args) {
		if (!enabled) {
			log.info("[AES MIGRATION] disabled (encrypt.migration.enabled=false)");
			return;
		}

		int memberUpdated = migrateMembers();
		int todoUpdated = migrateTodos();
		int memoUpdated = migrateMemos();

		log.info("[AES MIGRATION] DONE memberUpdated={}, todoUpdated={}, memoUpdated={}",
			memberUpdated, todoUpdated, memoUpdated);

	}

	private int migrateMembers() {
		List<Member> members = memberRepository.findAll();
		int updated = 0;

		for (Member m : members) {
			String emailEnc = encryptUtil.encryptAESOrNull(m.getEmail());
			String nameEnc = encryptUtil.encryptAESOrNull(m.getMemberName());
			String phoneEnc = encryptUtil.encryptAESOrNull(m.getPhoneNumber());
			String appleEnc = encryptUtil.encryptAESOrNull(m.getAppleRefreshToken());

			boolean changed =
				!Objects.equals(m.getEmail(), emailEnc) ||
					!Objects.equals(m.getMemberName(), nameEnc) ||
					!Objects.equals(m.getPhoneNumber(), phoneEnc) ||
					!Objects.equals(m.getAppleRefreshToken(), appleEnc);

			if (changed) {
				m.migrateSensitiveFields(emailEnc, nameEnc, phoneEnc, appleEnc);
				updated++;
			}
		}

		log.info("[AES MIGRATION] Members: total={}, updated={}", members.size(), updated);
		return updated;
	}

	private int migrateTodos() {
		List<Todo> todos = todoRepository.findAll();
		int updated = 0;

		for (Todo t : todos) {
			String contentEnc = encryptUtil.encryptAESOrNull(t.getContent());

			if (!Objects.equals(t.getContent(), contentEnc)) {
				t.migrateContent(contentEnc);
				updated++;
			}
		}

		log.info("[AES MIGRATION] Todos: total={}, updated={}", todos.size(), updated);
		return updated;
	}

	private int migrateMemos() {
		List<Memo> memos = memoRepository.findAll();
		int updated = 0;

		for (Memo m : memos) {
			String titleEnc = encryptUtil.encryptAESOrNull(m.getTitle());
			String contentEnc = encryptUtil.encryptAESOrNull(m.getContent());

			boolean changed =
				!Objects.equals(m.getTitle(), titleEnc) ||
					!Objects.equals(m.getContent(), contentEnc);

			if (changed) {
				m.migrateTitleContent(titleEnc, contentEnc);
				updated++;
			}
		}

		log.info("[AES MIGRATION] Memos: total={}, updated={}", memos.size(), updated);
		return updated;
	}
}
