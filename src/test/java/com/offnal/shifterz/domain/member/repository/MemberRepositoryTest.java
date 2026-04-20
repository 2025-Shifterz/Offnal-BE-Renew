package com.offnal.shifterz.domain.member.repository;

import com.offnal.shifterz.core.config.QueryDslConfig;
import com.offnal.shifterz.domain.member.domain.Member;
import com.offnal.shifterz.domain.member.domain.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member saveMember(Provider provider, String providerId) {
        return memberRepository.save(
                Member.builder()
                        .provider(provider)
                        .providerId(providerId)
                        .build()
        );
    }

    @Test
    @DisplayName("provider와 providerId로 회원 조회")
    void findByProviderAndProviderId_success() {
        // given
        saveMember(Provider.KAKAO, "12345678");

        // when
        Optional<Member> result = memberRepository.findByProviderAndProviderId(Provider.KAKAO, "12345678");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(result.get().getProviderId()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("존재하지 않는 providerId로 조회하면 empty 반환")
    void findByProviderAndProviderId_notFound() {
        // when
        Optional<Member> result = memberRepository.findByProviderAndProviderId(Provider.KAKAO, "not-exist");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("provider가 다르면 조회되지 않는다")
    void findByProviderAndProviderId_wrongProvider() {
        // given
        saveMember(Provider.KAKAO, "12345678");

        // when
        Optional<Member> result = memberRepository.findByProviderAndProviderId(Provider.GOOGLE, "12345678");

        // then
        assertThat(result).isEmpty();
    }
}