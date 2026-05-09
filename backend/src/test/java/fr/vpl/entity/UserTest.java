package fr.vpl.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link User} as a Spring Security principal.
 */
@DisplayName("User entity tests")
class UserTest {

    @Test
    @DisplayName("builder assigns USER role by default")
    void builder_shouldAssignUserRoleByDefault() {
        User user = User.builder()
                .username("john")
                .email("john@vpl.fr")
                .password("encoded")
                .build();

        assertThat(user.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    @DisplayName("getAuthorities exposes the role with Spring Security prefix")
    void getAuthorities_shouldExposeRoleAuthority() {
        User user = User.builder()
                .role(User.Role.ADMIN)
                .build();

        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Spring Security account flags are enabled")
    void accountFlags_shouldBeEnabled() {
        User user = new User();

        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
