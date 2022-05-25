package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException

@DataJpaTest
class RemoveUserTest extends SpockTest {
    def user

    def setup() {
        user = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL)
        userRepository.save(user)
    }

    def "cannot remove normal active user"() {
        given:
        user.getAuthUser().setActive(true)

        when:
        user.remove()

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_IS_ACTIVE
        and:
        userRepository.findAll().size() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
